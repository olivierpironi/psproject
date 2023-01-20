package com.fourcamp.sbanco.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.dto.CartaoDTO;
import com.fourcamp.sbanco.dto.CartaoDebitoDTO;
import com.fourcamp.sbanco.dto.ContaCorrenteDTO;
import com.fourcamp.sbanco.dto.ContaDTO;
import com.fourcamp.sbanco.dto.TransacaoDTO;
import com.fourcamp.sbanco.enums.EnumTransacao;
import com.fourcamp.sbanco.infra.exceptions.BOGPExceptions;
import com.fourcamp.sbanco.infra.exceptions.CartaoBloqueadoException;
import com.fourcamp.sbanco.infra.exceptions.SenhaInvalidaException;
import com.fourcamp.sbanco.infra.exceptions.TransacaoInvalidaException;
import com.fourcamp.sbanco.repository.CartaoDebitoRepository;

@Service
public class CartaoDebitoService {
	@Autowired
	private CartaoDebitoRepository repository;
	@Autowired
	private ContaService contaService;
	@Autowired
	private TransacaoService transacaoService;
	@Autowired
	private ExtratoBancarioService extratoService;

	public void salvar(CartaoDebitoDTO cartao) {
		repository.save(cartao);
	}

	public CartaoDebitoDTO getById(Long numeroDoCartao) {
		return repository.findById(numeroDoCartao).get();
	}
	public List<CartaoDebitoDTO> getAll() {
		return repository.findAll();
	}

	public CartaoDebitoDTO emitirCartaoDeDebito(Long numeroDaConta, Integer senhaCartao) {
		ContaCorrenteDTO conta = (ContaCorrenteDTO) contaService.getByNumeroDaConta(numeroDaConta);
		try {
//			checaSenha(senhaDaConta);
			CartaoDebitoDTO cartao = new CartaoDebitoDTO(conta, senhaCartao);
			salvar(cartao);
			conta.setCartaoDeDebito(cartao);

			return cartao;
		} catch (BOGPExceptions e) {
			e.msg();
			return null;
		}
	}

	public void desbloquearCartao(Long numeroDoCartao, Integer senha) {
		CartaoDebitoDTO cartao = repository.findById(numeroDoCartao).get();
		checaSenha(cartao, senha);
		setLimiteDiario(cartao);
		cartao.setBloqueado(false);
	}

	public void bloquearCartao(Long numeroDoCartao, Integer senha) {
		CartaoDebitoDTO cartao = repository.findById(numeroDoCartao).get();
		cartao.setBloqueado(true);
	}
	
	private void setLimiteDiario(CartaoDebitoDTO cartao) {
		cartao.setLimiteDiario(cartao.getContaAssociada().getCliente().getCategoria().getLimiteDiarioTransacaoDebito());
		renovarLimite(cartao.getNumero());
	}

	public void renovarLimite(Long numeroDoCartao) {
		CartaoDebitoDTO cartao = repository.findById(numeroDoCartao).get();
		if (cartao.getUltimaRenovacaoLimiteDisponivel().until(LocalDate.now(), ChronoUnit.DAYS) >= 1) {
			cartao.setLimiteDisponivel(cartao.getLimiteDiario());
			cartao.setUltimaRenovacaoLimiteDisponivel(LocalDate.now()); 
		}
		
	}
	
	public void pagarComDebito(Long numeroDoCartao, Long numeroDaContaDestino, String valorPagamento,
			Integer senhaCartao) {
		renovarLimite(numeroDoCartao);
		CartaoDebitoDTO cartao = repository.findById(numeroDoCartao).get();
		ContaCorrenteDTO contaOrigem = cartao.getContaAssociada();
		ContaDTO contaDestino = contaService.getByNumeroDaConta(numeroDaContaDestino);
		
		try {
			checaSenha(cartao, senhaCartao);
			checaBloqueio(cartao);
			BigDecimal bdPagamento = new BigDecimal(valorPagamento);
			BigDecimal bdTaxa = calculaTaxaOperacao(cartao,bdPagamento);
			BigDecimal valorTotal = bdPagamento.add(bdTaxa);
			
			checaLimite(valorTotal, cartao.getLimiteDisponivel());
			contaService.checaSaidaSaldo(valorTotal);
			contaService.checaSaldoDisponivel(valorTotal, contaOrigem.getSaldo());
			
			TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_DEBITO, contaOrigem, contaDestino, bdPagamento);
			TransacaoDTO taxaOperacao = new TransacaoDTO(EnumTransacao.TAXA_CARTAO_DE_DEBITO, contaOrigem, bdTaxa);
			transacaoService.salvar(transacao);
			transacaoService.salvar(taxaOperacao);
			
			cartao.setLimiteDisponivel(cartao.getLimiteDisponivel().subtract(valorTotal));
			contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valorTotal));
			
			contaService.receberTransacao(contaDestino, transacao);
			extratoService.registraTransacao(contaOrigem.getExtrato(), transacao, taxaOperacao);
			
		} catch (BOGPExceptions e) {
			e.msg();
		}

	}
	
	
	
	private BigDecimal calculaTaxaOperacao(CartaoDebitoDTO cartao, BigDecimal bdPagamento) {
			return bdPagamento.multiply(cartao.getContaAssociada().getCliente().getCategoria().getTaxaUtilizacaoCartaoDeDebito());
	}

	private void checaSenha(CartaoDTO cartao, Integer senha) {
		if (!senha.equals(cartao.getSenha())) {
			throw new SenhaInvalidaException("\nSenha inválida.");
		}
	}

	private void checaLimite(BigDecimal bdPagamento, BigDecimal limite) {
		if (limite.compareTo(bdPagamento) < 0) {
			throw new TransacaoInvalidaException("\nNão há limite disponível para essa transação.");
		}
	}

	private void checaBloqueio(CartaoDTO cartao) {
		if (cartao.isBloqueado()) {
			throw new CartaoBloqueadoException("\nCartão bloqueado, operação cancelada.");
		}
	}
}
