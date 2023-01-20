package com.fourcamp.sbanco.dto.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosPix(
		
		@NotNull
		Long numeroDaContaOrigem, 
		
		@NotNull
		Long numeroDaContaDestino, 
		
		@NotBlank
		String valorPix) {

}
