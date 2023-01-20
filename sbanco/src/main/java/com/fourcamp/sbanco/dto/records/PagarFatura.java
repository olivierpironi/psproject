package com.fourcamp.sbanco.dto.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PagarFatura(
		
		@NotNull
		Long numeroDoCartao, 
		
		@NotBlank
		String valorPagamento,
		
		@NotNull
		Integer senhaCartao ) {

}
