package com.fourcamp.sbanco.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fourcamp.sbanco.domain.dto.cartao.AtualizarCartao;
import com.fourcamp.sbanco.domain.dto.cartao.CartaoDTO;
import com.fourcamp.sbanco.domain.dto.cartao.PagarComCartao;
import com.fourcamp.sbanco.domain.dto.cartaodebito.CadastroCartaoDebito;
import com.fourcamp.sbanco.domain.dto.cartaodebito.CartaoDebitoDTO;
import com.fourcamp.sbanco.domain.dto.cartaodebito.DetalhaCartaoDebito;
import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;
import com.fourcamp.sbanco.domain.dto.contacorrente.ContaCorrenteDTO;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.dto.transacao.TransacaoDTO;
import com.fourcamp.sbanco.domain.enums.EnumTransacao;
import com.fourcamp.sbanco.domain.repository.CartaoDebitoRepository;
import com.fourcamp.sbanco.infra.exceptions.CartaoBloqueadoException;
import com.fourcamp.sbanco.infra.exceptions.CartaoNaoExisteException;
import com.fourcamp.sbanco.infra.exceptions.SenhaInvalidaException;
import com.fourcamp.sbanco.infra.exceptions.TransacaoInvalidaException;

@Service
@Transactional
public class CartaoDebitoService {
	private static final CartaoNaoExisteException CARTAO_NAO_EXISTE_EXCEPTION = new CartaoNaoExisteException("Não existe um cartão com este número.");
	@Autowired
	private CartaoDebitoRepository cartaoRepository;
	@Autowired
	private ContaService contaService;
	@Autowired
	private TransacaoService transacaoService;
	@Autowired
	private ExtratoBancarioService extratoService;

	public DetalhaCartaoDebito getById(Long numeroDoCartao) {
		return new DetalhaCartaoDebito(cartaoRepository.findById(numeroDoCartao).orElseThrow(() -> CARTAO_NAO_EXISTE_EXCEPTION));
	}

	public CartaoDebitoDTO emitirCartaoDeDebito(CadastroCartaoDebito dados) {
		ContaCorrenteDTO conta = (ContaCorrenteDTO) contaService.getByNumeroDaConta(dados.numeroDaConta());
		contaService.checaSenha(conta, dados.senhaDaConta());

		CartaoDebitoDTO cartao = new CartaoDebitoDTO(conta, dados.senhaCartao());
		cartaoRepository.save(cartao);
		conta.setCartaoDeDebito(cartao);
		return cartao;
	}

	public DetalhaCartaoDebito desbloquearCartao(AtualizarCartao dados) {
		CartaoDebitoDTO cartao = cartaoRepository.findById(dados.numeroDoCartao()).orElseThrow(() -> CARTAO_NAO_EXISTE_EXCEPTION);
		checaSenha(cartao, dados.senhaCartao());
		setLimiteDiario(cartao);
		cartao.setBloqueado(false);
		return new DetalhaCartaoDebito(cartao);
	}

	public DetalhaCartaoDebito bloquearCartao(AtualizarCartao dados) {
		CartaoDebitoDTO cartao = cartaoRepository.findById(dados.numeroDoCartao()).orElseThrow(() -> CARTAO_NAO_EXISTE_EXCEPTION);
		checaSenha(cartao, dados.senhaCartao());
		cartao.setBloqueado(true);
		return new DetalhaCartaoDebito(cartao);
	}

	private void setLimiteDiario(CartaoDebitoDTO cartao) {
		cartao.setLimiteDiario(cartao.getContaAssociada().getCliente().getCategoria().getLimiteDiarioTransacaoDebito());
		renovarLimite(cartao.getNumero());
	}

	public DetalhaCartaoDebito renovarLimite(Long numeroDoCartao) {
		CartaoDebitoDTO cartao = cartaoRepository.findById(numeroDoCartao).orElseThrow(() -> CARTAO_NAO_EXISTE_EXCEPTION);
		if (cartao.getUltimaRenovacaoLimiteDisponivel().until(LocalDate.now(), ChronoUnit.DAYS) >= 1) {
			cartao.setLimiteDisponivel(cartao.getLimiteDiario());
			cartao.setUltimaRenovacaoLimiteDisponivel(LocalDate.now());
		}
		return new DetalhaCartaoDebito(cartao);
	}

	public List<DetalhamentoTransacao> pagarComDebito(PagarComCartao dados) {
		//Checagens
		renovarLimite(dados.numeroDoCartao());
		CartaoDebitoDTO cartao = cartaoRepository.findById(dados.numeroDoCartao()).orElseThrow(() -> CARTAO_NAO_EXISTE_EXCEPTION);
		ContaCorrenteDTO contaOrigem = cartao.getContaAssociada();
		ContaDTO contaDestino = contaService.getByNumeroDaConta(dados.numeroDoCartao());

		BigDecimal bdPagamento = new BigDecimal(dados.valorPagamento());
		BigDecimal bdTaxa = calculaTaxaOperacao(cartao, bdPagamento);
		BigDecimal valorTotal = bdPagamento.add(bdTaxa);
		checaSenha(cartao, dados.senhaCartao());
		checaBloqueio(cartao);
		checaLimite(valorTotal, cartao.getLimiteDisponivel());
		contaService.checaSaidaSaldo(valorTotal);
		contaService.checaSaldoDisponivel(valorTotal, contaOrigem.getSaldo());
		//Operação
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_DEBITO, contaOrigem, contaDestino,
				bdPagamento);
		TransacaoDTO taxaOperacao = new TransacaoDTO(EnumTransacao.TAXA_CARTAO_DE_DEBITO, contaOrigem, bdTaxa);
		transacaoService.salvar(transacao);
		transacaoService.salvar(taxaOperacao);

		cartao.setLimiteDisponivel(cartao.getLimiteDisponivel().subtract(valorTotal));
		contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valorTotal));

		contaService.receberTransacao(contaDestino, transacao);
		extratoService.registraTransacao(contaOrigem.getExtrato(), transacao, taxaOperacao);

		return List.of(transacao, taxaOperacao).stream().map(DetalhamentoTransacao::new).toList(); 
	}

	private BigDecimal calculaTaxaOperacao(CartaoDebitoDTO cartao, BigDecimal bdPagamento) {
		return bdPagamento.multiply(cartao.getContaAssociada().getCliente().getCategoria().getTaxaUtilizacaoCartaoDeDebito());
	}

	private void checaSenha(CartaoDTO cartao, Integer senha) {
		if (!senha.equals(cartao.getSenha())) {
			throw new SenhaInvalidaException("Senha inválida.");
		}
	}

	private void checaLimite(BigDecimal bdPagamento, BigDecimal limite) {
		if (limite.compareTo(bdPagamento) < 0) {
			throw new TransacaoInvalidaException("Não há limite disponível para essa transação.");
		}
	}

	private void checaBloqueio(CartaoDTO cartao) {
		if (cartao.isBloqueado()) {
			throw new CartaoBloqueadoException("Cartão bloqueado, operação cancelada.");
		}
	}
}
