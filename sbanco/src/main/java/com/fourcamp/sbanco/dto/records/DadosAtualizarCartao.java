package com.fourcamp.sbanco.dto.records;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizarCartao(
		
		@NotNull
		Long numeroDoCartao, 
		
		@NotNull
		Integer senhaCartao,
		
		String novoLimiteDiario) {

}
