package com.fourcamp.sbanco.enums;

public enum EnumConta {
	
	CONTA_POUPANCA("CONTA POUPANÇA"),
	CONTA_CORRENTE("CONTA CORRENTE");
	
	private String descricao;

	private EnumConta(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	

}
