package com.fourcamp.sbanco.dto.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroChavePix(
		
		@NotNull
		Long numeroDaConta, 
		
		@NotBlank
		String chavepix) {
	
}