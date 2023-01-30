package com.fourcamp.sbanco.domain.dto.pix;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroChavePix(
		
		@NotNull
		Long numeroDaConta, 
		
		@NotBlank
		String senha,
		
		@NotBlank
		String chavepix		) {
	
}