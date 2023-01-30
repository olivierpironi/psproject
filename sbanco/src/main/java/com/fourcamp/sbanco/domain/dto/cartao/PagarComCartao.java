package com.fourcamp.sbanco.domain.dto.cartao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PagarComCartao(
		
		@NotNull
		Long numeroDoCartao, 
		
		@NotNull
		Long numeroDaContaDestino, 
		
		@NotBlank
		String valorPagamento,
		
		@NotNull
		Integer senhaCartao ) {

}
