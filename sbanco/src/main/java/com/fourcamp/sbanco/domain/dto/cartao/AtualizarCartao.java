package com.fourcamp.sbanco.domain.dto.cartao;

import jakarta.validation.constraints.NotNull;

public record AtualizarCartao(
		
		@NotNull
		Long numeroDoCartao, 
		
		@NotNull
		Integer senhaCartao,
		
		String novoLimiteDiario) {

}
