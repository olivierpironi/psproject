package com.fourcamp.sbanco.domain.dto.cartao;

import jakarta.validation.constraints.NotNull;

public record NumeroESenhaCartao(
		
		@NotNull
		Long numeroDoCartao, 
		
		@NotNull
		Integer senhaDoCartao) {

}
