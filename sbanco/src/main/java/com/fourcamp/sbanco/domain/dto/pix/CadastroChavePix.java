package com.fourcamp.sbanco.domain.dto.pix;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CadastroChavePix(
		
		@NotNull
		Long numeroDaConta, 
		
		@NotBlank
		String senha,
		
		@NotBlank
		String chavepix		) {
	
}