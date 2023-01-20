package com.fourcamp.sbanco.dto.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DepositoESaqueConta(
		
		@NotNull
		Long numeroDaConta, 
		
		@NotBlank
		String valor,
		
		@NotBlank
		String senha) {
	
}