package com.fourcamp.bancoOGP.model;

import java.math.BigDecimal;

import com.fourcamp.bancoOGP.enums.EnumCartao;
import com.fourcamp.bancoOGP.exceptions.BOGPExceptions;
import com.fourcamp.bancoOGP.exceptions.CartaoBloqueadoException;
import com.fourcamp.bancoOGP.exceptions.SenhaInvalidaException;
import com.fourcamp.bancoOGP.exceptions.TransacaoInvalidaException;

/**
 * Classe mãe de qualquer tipo de cartão no Banco OGP, conta com as funções: -
 * Bloqueio e desbloqueio de cartão; 
 * - Troca de senha; 
 * - Calcula taxa de utilização do cartão.
 * 
 * Os diversos tipos de checagens feitas antes que qualquer operação utiliza um
 * sistema de exceções, é uma regra de negócio.
 * 
 * @author Olivier Gomes Pironi
 */
public abstract class Cartao {
	private static Long contaNumero = 0L;
	protected Conta contaAssociada;
	protected EnumCartao tipoDeCartao;
	private Long numero;
	private Integer senha;
	private boolean bloqueado;

	protected Cartao(Conta contaAssociada, Integer senha) {
		super();
		contaNumero += 1L;
		this.contaAssociada = contaAssociada;
		this.numero = 100000000000L + contaNumero;
		this.senha = senha;
		this.bloqueado = true;
	}
	
	protected BigDecimal calculaTaxaOperacao(BigDecimal bdPagamento) {
		if (this.tipoDeCartao.equals(EnumCartao.CREDITO))
			return bdPagamento.multiply(contaAssociada.getCliente().getCategoria().getTaxaUtilizacaoCartaoDeCredito());

		if (this.tipoDeCartao.equals(EnumCartao.DEBITO))
			return bdPagamento.multiply(contaAssociada.getCliente().getCategoria().getTaxaUtilizacaoCartaoDeDebito());
		return new BigDecimal("0");
	}

	@Override
	public String toString() {
		return "\nBANCO OGP" + 
				"\nTitular: " + contaAssociada.getCliente().getNome() + 
				"\nCategoria: " + contaAssociada.getCliente().getCategoria().getDescricao() + 
				"\n" + tipoDeCartao.getDescricao() + 
				"\nNº" + getNumero();
	}

	protected void checaSenha(Integer senha) {
		if (!senha.equals(this.senha)) {
			throw new SenhaInvalidaException("\nSenha inválida.");
		}
	}

	protected void checaLimite(BigDecimal bdPagamento, BigDecimal limite) {
		if (limite.compareTo(bdPagamento) < 0) {
			throw new TransacaoInvalidaException("\nNão há limite disponível para essa transação.");
		}
	}

	protected void checaBloqueio() {
		if (this.bloqueado) {
			throw new CartaoBloqueadoException("\nCartão bloqueado, operação cancelada.");
		}
	}

	public boolean isBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(boolean bloqueado, Integer senha) {
		try {
			checaSenha(senha);
			this.bloqueado = bloqueado;
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	public void setSenha(Integer novaSenha, Integer senhaAntiga) {
		try {
			checaSenha(senhaAntiga);
			this.senha = novaSenha;
		} catch (BOGPExceptions e) {
			e.msg();
		}
	}

	public Conta getContaAssociada() {
		return contaAssociada;
	}

	public EnumCartao getTipoDeCartao() {
		return tipoDeCartao;
	}

	public Long getNumero() {
		return numero;
	}

}
