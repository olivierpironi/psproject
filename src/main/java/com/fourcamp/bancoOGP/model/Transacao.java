package com.fourcamp.bancoOGP.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fourcamp.bancoOGP.enums.EnumTransacao;
import com.fourcamp.bancoOGP.services.Formatadores;

/**
 * Todas as movimentações financeiras do Banco OGP se dão através do objeto
 * transação, o qual conta com várias informações relevantes referentes a
 * movimentação em questão.
 * 
 * @author Olivier Gomes Pironi
 */

public class Transacao {
	private EnumTransacao enumTransacao;
	private Conta contaOrigem;
	private Conta contaDestino;
	private LocalDateTime dataDaTransacao;
	private BigDecimal valor;

	public Transacao(EnumTransacao enumTransacao, Conta contaOrigem, Conta contaDestino, LocalDateTime dataDaTransacao,
			BigDecimal valor) {
		this.enumTransacao = enumTransacao;
		this.contaOrigem = contaOrigem;
		this.contaDestino = contaDestino;
		this.dataDaTransacao = dataDaTransacao;
		this.valor = valor;
	}

	public Transacao(EnumTransacao enumTransacao, Conta contaOrigem, Conta contaDestino, BigDecimal valor) {
		this.enumTransacao = enumTransacao;
		this.contaOrigem = contaOrigem;
		this.contaDestino = contaDestino;
		this.dataDaTransacao = LocalDateTime.now();
		this.valor = valor;
	}

	public Transacao(EnumTransacao enumTransacao, Conta contaOrigem, BigDecimal valor) {
		this.enumTransacao = enumTransacao;
		this.contaOrigem = contaOrigem;
		this.contaDestino = null;
		this.dataDaTransacao = LocalDateTime.now();
		this.valor = valor;
	}

	private String info() {
	
		return enumTransacao.equals(EnumTransacao.PIX_RECEBIDO) ? "\nDe: " + this.contaOrigem.getCliente().getNome()
				: enumTransacao.equals(EnumTransacao.PIX_ENVIADO) ? "\nPara: " + this.contaDestino.getCliente().getNome()
				: enumTransacao.equals(EnumTransacao.PAGAMENTO_RECEBIDO) ? "\nDe: " + this.contaOrigem.getCliente().getNome()
				: enumTransacao.equals(EnumTransacao.PAGAMENTO_CREDITO) ? "\nPara: " + this.contaDestino.getCliente().getNome()
				: enumTransacao.equals(EnumTransacao.PAGAMENTO_DEBITO) ? "\nPara: " + this.contaDestino.getCliente().getNome()
				: " ";
	
	}

	@Override
	public String toString() {
	
		return "\n" + enumTransacao.getTipo() + info() + "............................ " + enumTransacao.getSinal()
				+ " R$" + Formatadores.arredonda(this.valor) + "\n" + this.dataDaTransacao.format(Formatadores.formato);
	}

	public EnumTransacao getEnumTransacao() {
		return enumTransacao;
	}

	public LocalDateTime getDataDaTransacao() {
		return dataDaTransacao;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public Conta getContaOrigem() {
		return contaOrigem;
	}
}
