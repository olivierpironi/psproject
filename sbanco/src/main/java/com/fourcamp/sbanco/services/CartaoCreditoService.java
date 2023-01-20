package com.fourcamp.sbanco.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.dto.CartaoCreditoDTO;
import com.fourcamp.sbanco.dto.CartaoDTO;
import com.fourcamp.sbanco.dto.ContaCorrenteDTO;
import com.fourcamp.sbanco.dto.ContaDTO;
import com.fourcamp.sbanco.dto.TransacaoDTO;
import com.fourcamp.sbanco.enums.EnumSeguroCredito;
import com.fourcamp.sbanco.enums.EnumTransacao;
import com.fourcamp.sbanco.infra.exceptions.BOGPExceptions;
import com.fourcamp.sbanco.infra.exceptions.CartaoBloqueadoException;
import com.fourcamp.sbanco.infra.exceptions.SenhaInvalidaException;
import com.fourcamp.sbanco.infra.exceptions.TransacaoInvalidaException;
import com.fourcamp.sbanco.repository.CartaoCreditoRepository;

@Service
public class CartaoCreditoService {
	@Autowired
	private CartaoCreditoRepository repository;
	@Autowired
	private ContaService contaService;
	@Autowired
	private TransacaoService transacaoService;
	@Autowired
	private ExtratoBancarioService extratoService;
	@Autowired
	private FaturaService faturaService;

	public void salvar(CartaoCreditoDTO cartao) {
		repository.save(cartao);
	}

	public List<CartaoCreditoDTO> getAll() {
		return repository.findAll();
	}

	public CartaoCreditoDTO getById(Long numeroDoCartao) {
		return repository.findById(numeroDoCartao).get();
	}

	public CartaoCreditoDTO emitirCartaoDeCredito(Long numeroDaConta,EnumSeguroCredito seguro ,Integer senhaCartao) {
		ContaCorrenteDTO conta = (ContaCorrenteDTO) contaService.getByNumeroDaConta(numeroDaConta);
		try {
//			checaSenha(senhaDaConta);
			CartaoCreditoDTO cartao = new CartaoCreditoDTO(conta, seguro ,senhaCartao);
			salvar(cartao);
			conta.setCartaoDeCredito(cartao);
			return cartao;
		} catch (BOGPExceptions e) {
			e.msg();
			return null;
		}

	}

	public void desbloquearCartao(Long numeroDoCartao, Integer senha) {
		CartaoCreditoDTO cartao = repository.findById(numeroDoCartao).get();
		try {
			checaSenha(cartao, senha);
			
			cartao.setBloqueado(false);
			cartao.setLimiteCredito(cartao.getContaAssociada().getCliente().getCategoria().getLimiteCartaoDeCredito());
			cartao.getFatura().setLimiteDisponivel(cartao.getLimiteCredito());
		} catch (BOGPExceptions e) {
			e.getCause();
		}
	}

	public void bloquearCartao(Long numeroDoCartao) {
		CartaoCreditoDTO cartao = repository.findById(numeroDoCartao).get();
		cartao.setBloqueado(true);
	}
	
	
	public void pagarComCredito(Long numeroDoCartao, Long numeroDaContaDestino, String valorPagamento,
			Integer senhaCartao) {
		CartaoCreditoDTO cartao = repository.findById(numeroDoCartao).get();
		execMensalidadeSeguro(cartao);
		ContaDTO contaDestino = contaService.getByNumeroDaConta(numeroDaContaDestino);
		
		try {
			checaSenha(cartao, senhaCartao);
			checaBloqueio(cartao);
			BigDecimal bdPagamento = new BigDecimal(valorPagamento);
			BigDecimal bdTaxa = calculaTaxaOperacao(cartao,bdPagamento);
			contaService.checaSaidaSaldo(bdPagamento.add(bdTaxa));
			checaLimite(bdPagamento.add(bdTaxa), cartao.getFatura().getLimiteDisponivel());
			
			
			TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_CREDITO, cartao.getContaAssociada(), contaDestino, bdPagamento);
			TransacaoDTO taxaOperacao = new TransacaoDTO(EnumTransacao.TAXA_CARTAO_DE_CREDITO, cartao.getContaAssociada(), bdTaxa);
			transacaoService.salvar(transacao);
			transacaoService.salvar(taxaOperacao);
			
			faturaService.registraTransacao(cartao, transacao, taxaOperacao);
			contaService.receberTransacao(contaDestino, transacao);
			
		} catch (BOGPExceptions e) {
			e.msg();
		}

	}
	
	public List<TransacaoDTO> exibirFatura(Long numeroDoCartao) {
		CartaoCreditoDTO cartao = repository.findById(numeroDoCartao).get();
		execMensalidadeSeguro(cartao);
		faturaService.exibe(cartao);
		return cartao.getFatura().getHistoricoTransacoes();
		
	}

	public void pagarFaturaComSaldo(Long numeroDoCartao, String valorPagamento ,Integer senhaCartao) {
		CartaoCreditoDTO cartao = repository.findById(numeroDoCartao).get();
		try {
			checaSenha(cartao, senhaCartao);
			BigDecimal bdPagamento = new BigDecimal(valorPagamento);
			contaService.checaSaidaSaldo(bdPagamento);
			contaService.checaSaldoDisponivel(bdPagamento, cartao.getContaAssociada().getSaldo());
			
			TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_FATURA, cartao.getContaAssociada(), bdPagamento);
			transacaoService.salvar(transacao);
			
			cartao.getContaAssociada().setSaldo(cartao.getContaAssociada().getSaldo().subtract(transacao.getValor()));
			
			extratoService.registraTransacao(cartao.getContaAssociada().getExtrato(), transacao);
			faturaService.pagamentoFatura(cartao, transacao);
			
		} catch (BOGPExceptions e) {
			e.msg();
		}
		
	}
	
	public void execMensalidadeSeguro(CartaoCreditoDTO cartao) {
		if (cartao.getUltimoPagamentoSeguro().until(LocalDate.now(), ChronoUnit.DAYS) >= 30) {

			try {
				BigDecimal mensalidade = cartao.getContratoSeguro().getSeguradora().getValor();
				checaLimite(mensalidade, cartao.getFatura().getLimiteDisponivel());
				
				TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.MENSALIDADE_SEGURO, cartao.getContaAssociada(), mensalidade);
				faturaService.registraTransacao(cartao, transacao);
				cartao.setUltimoPagamentoSeguro(LocalDate.now());
			} catch (BOGPExceptions e) {
				e.msg();
			}
		}
	}

	private BigDecimal calculaTaxaOperacao(CartaoCreditoDTO cartao, BigDecimal bdPagamento) {
			return bdPagamento.multiply( cartao.getContaAssociada().getCliente().getCategoria().getTaxaUtilizacaoCartaoDeCredito());
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
