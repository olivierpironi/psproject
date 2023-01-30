package com.fourcamp.sbanco.domain.dto.cartao;

import jakarta.validation.constraints.NotNull;

public record NumeroDoCartao(
		
		@NotNull
		Long numeroDoCartao) {

}
