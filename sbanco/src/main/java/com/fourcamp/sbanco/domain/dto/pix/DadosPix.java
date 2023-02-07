package com.fourcamp.sbanco.domain.dto.pix;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosPix(
		
		@NotNull
		Long numeroDaContaOrigem, 
		
		@NotNull
		Long numeroDaContaDestino, 
		
		@NotBlank
		String senha, 
		
		@NotBlank
		String valorPix) {
	

}
