package com.fourcamp.sbanco.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.domain.dto.cartao.CartaoDTO;
import com.fourcamp.sbanco.domain.dto.cartao.DadosAtualizarCartao;
import com.fourcamp.sbanco.domain.dto.cartao.DadosNumeroESenhaCartao;
import com.fourcamp.sbanco.domain.dto.cartao.PagarComCartao;
import com.fourcamp.sbanco.domain.dto.cartaocredito.CartaoCreditoDTO;
import com.fourcamp.sbanco.domain.dto.cartaocredito.DadosCadastroCartaoCredito;
import com.fourcamp.sbanco.domain.dto.cartaocredito.DetalhamentoDadosCartaoCredito;
import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;
import com.fourcamp.sbanco.domain.dto.contacorrente.ContaCorrenteDTO;
import com.fourcamp.sbanco.domain.dto.fatura.PagarFatura;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.dto.transacao.TransacaoDTO;
import com.fourcamp.sbanco.domain.enums.EnumTransacao;
import com.fourcamp.sbanco.domain.repository.CartaoCreditoRepository;
import com.fourcamp.sbanco.infra.exceptions.CartaoBloqueadoException;
import com.fourcamp.sbanco.infra.exceptions.SenhaInvalidaException;
import com.fourcamp.sbanco.infra.exceptions.TransacaoInvalidaException;

@Service
public class CartaoCreditoService {
	@Autowired
	private CartaoCreditoRepository cartaoRepository;
	@Autowired
	private ContaService contaService;
	@Autowired
	private TransacaoService transacaoService;
	@Autowired
	private ExtratoBancarioService extratoService;
	@Autowired
	private FaturaService faturaService;

	public List<CartaoCreditoDTO> getAll() {
		return cartaoRepository.findAll();
	}

	public DetalhamentoDadosCartaoCredito getById(Long numeroDoCartao) {
		return new DetalhamentoDadosCartaoCredito(cartaoRepository.findById(numeroDoCartao).get());
	}

	public CartaoCreditoDTO emitirCartaoDeCredito(DadosCadastroCartaoCredito dados) {
		ContaCorrenteDTO conta = (ContaCorrenteDTO) contaService.getByNumeroDaConta(dados.numeroDaConta());
		contaService.checaSenha(conta, dados.senhaDaConta());

		CartaoCreditoDTO cartao = new CartaoCreditoDTO(conta, dados.seguradora(), dados.senhaCartao());
		cartaoRepository.save(cartao);
		conta.setCartaoDeCredito(cartao);
		return cartao;
	}

	public DetalhamentoDadosCartaoCredito desbloquearCartao(DadosAtualizarCartao dados) {
		CartaoCreditoDTO cartao = cartaoRepository.findById(dados.numeroDoCartao()).get();
		checaSenha(cartao, dados.senhaCartao());
		cartao.setBloqueado(false);
		cartao.setLimiteCredito(cartao.getContaAssociada().getCliente().getCategoria().getLimiteCartaoDeCredito());
		cartao.getFatura().setLimiteDisponivel(cartao.getLimiteCredito());
		return new DetalhamentoDadosCartaoCredito(cartao);
	}

	public DetalhamentoDadosCartaoCredito bloquearCartao(DadosAtualizarCartao dados) {
		CartaoCreditoDTO cartao = cartaoRepository.findById(dados.numeroDoCartao()).get();
		checaSenha(cartao, dados.senhaCartao());
		cartao.setBloqueado(true);
		return new DetalhamentoDadosCartaoCredito(cartao);
	}

	public List<DetalhamentoTransacao> pagarComCredito(PagarComCartao dados) {
		// Checagens
		CartaoCreditoDTO cartao = cartaoRepository.findById(dados.numeroDoCartao()).get();
		execMensalidadeSeguro(cartao);
		ContaDTO contaDestino = contaService.getByNumeroDaConta(dados.numeroDaContaDestino());

		checaSenha(cartao, dados.senhaCartao());
		checaBloqueio(cartao);
		BigDecimal bdPagamento = new BigDecimal(dados.valorPagamento());
		BigDecimal bdTaxa = calculaTaxaOperacao(cartao, bdPagamento);
		contaService.checaSaidaSaldo(bdPagamento.add(bdTaxa));
		checaLimite(bdPagamento.add(bdTaxa), cartao.getFatura().getLimiteDisponivel());
		// Operação
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_CREDITO, cartao.getContaAssociada(),
				contaDestino, bdPagamento);
		TransacaoDTO taxaOperacao = new TransacaoDTO(EnumTransacao.TAXA_CARTAO_DE_CREDITO, cartao.getContaAssociada(),
				bdTaxa);
		transacaoService.salvar(transacao);
		transacaoService.salvar(taxaOperacao);

		faturaService.registraTransacao(cartao, transacao, taxaOperacao);
		contaService.receberTransacao(contaDestino, transacao);
		return List.of(transacao, taxaOperacao).stream().map(DetalhamentoTransacao::new).toList(); 
	}

	public List<DetalhamentoTransacao> exibirFatura(DadosNumeroESenhaCartao dados) {
		CartaoCreditoDTO cartao = cartaoRepository.findById(dados.numeroDoCartao()).get();
		checaSenha(cartao, dados.senhaDoCartao());
		execMensalidadeSeguro(cartao);
		faturaService.exibe(cartao);
		return cartao.getFatura().getHistoricoTransacoes().stream().map(DetalhamentoTransacao::new).toList();

	}

	public DetalhamentoTransacao pagarFaturaComSaldo(PagarFatura dados) {
		CartaoCreditoDTO cartao = cartaoRepository.findById(dados.numeroDoCartao()).get();
		ContaCorrenteDTO contaAssociada = cartao.getContaAssociada();

		contaService.checaSenha(contaAssociada, dados.senhaConta());
		BigDecimal bdPagamento = new BigDecimal(dados.valorPagamento());
		contaService.checaSaidaSaldo(bdPagamento);
		contaService.checaSaldoDisponivel(bdPagamento, contaAssociada.getSaldo());

		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_FATURA, contaAssociada, bdPagamento);
		transacaoService.salvar(transacao);

		contaAssociada.setSaldo(contaAssociada.getSaldo().subtract(transacao.getValor()));

		extratoService.registraTransacao(contaAssociada.getExtrato(), transacao);
		faturaService.pagamentoFatura(cartao, transacao);
		
		return new DetalhamentoTransacao(transacao);

	}

	public void execMensalidadeSeguro(CartaoCreditoDTO cartao) {
		if (cartao.getUltimoPagamentoSeguro().until(LocalDate.now(), ChronoUnit.DAYS) >= 30) {

			BigDecimal mensalidade = cartao.getContratoSeguro().getSeguradora().getValor();
			checaLimite(mensalidade, cartao.getFatura().getLimiteDisponivel());

			TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.MENSALIDADE_SEGURO, cartao.getContaAssociada(),
					mensalidade);
			faturaService.registraTransacao(cartao, transacao);
			cartao.setUltimoPagamentoSeguro(LocalDate.now());
		}
	}

	private BigDecimal calculaTaxaOperacao(CartaoCreditoDTO cartao, BigDecimal bdPagamento) {
		return bdPagamento
				.multiply(cartao.getContaAssociada().getCliente().getCategoria().getTaxaUtilizacaoCartaoDeCredito());
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
