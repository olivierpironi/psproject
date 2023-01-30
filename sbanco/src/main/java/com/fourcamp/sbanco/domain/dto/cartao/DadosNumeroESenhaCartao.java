package com.fourcamp.sbanco.domain.dto.cartao;

import jakarta.validation.constraints.NotNull;

public record DadosNumeroESenhaCartao(
		
		@NotNull
		Long numeroDoCartao, 
		
		@NotNull
		Integer senhaDoCartao) {

}
