package com.fourcamp.sbanco.domain.enums;

public enum EnumCartao {
	CREDITO("CARTÃO DE CRÉDITO"),
	DEBITO("CARTÃO DE DÉBITO");
	
	private String descricao;

	private EnumCartao(String tipoDeCartao) {
		this.descricao = tipoDeCartao;
	}

	public String getDescricao() {
		return descricao;
	}
	
}
