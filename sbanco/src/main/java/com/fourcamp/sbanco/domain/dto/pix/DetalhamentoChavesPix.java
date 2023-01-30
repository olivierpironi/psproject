package com.fourcamp.sbanco.domain.dto.pix;

public record DetalhamentoChavesPix(String chavepix) {

	public DetalhamentoChavesPix(ChavePixDTO chave) {
		this(chave.getChave());
	}
}
