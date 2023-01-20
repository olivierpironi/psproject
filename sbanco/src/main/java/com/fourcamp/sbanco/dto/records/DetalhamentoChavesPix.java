package com.fourcamp.sbanco.dto.records;

import com.fourcamp.sbanco.dto.ChavePixDTO;

public record DetalhamentoChavesPix(String chavepix) {

	public DetalhamentoChavesPix(ChavePixDTO chave) {
		this(chave.getChave());
	}
}
