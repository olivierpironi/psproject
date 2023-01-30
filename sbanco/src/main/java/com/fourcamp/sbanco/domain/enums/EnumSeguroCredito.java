package com.fourcamp.sbanco.domain.enums;

import java.math.BigDecimal;

public enum EnumSeguroCredito {

	CREDITO_SEGURO("Crédito Seguro", new BigDecimal("100"), "condicoes1"), 
	RISCO_ZERO("Risco Zero", new BigDecimal("70"), "condicoes2"),
	SEGURIDADE_TOTAL("Seguridade Total", new BigDecimal("50"), "condicoes3");

	private EnumSeguroCredito(String nome, BigDecimal valor, String condicoesAcionamento) {
		this.nomeSeguradora = nome;
		this.valor = valor;
		this.condicoesAcionamento = condicoesAcionamento;
	}

	private String nomeSeguradora;
	private BigDecimal valor;
	private String condicoesAcionamento;

	public String getNomeSeguradora() {
		return nomeSeguradora;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public String getCondicoesAssionamento() {
		return condicoesAcionamento;
	}

}
