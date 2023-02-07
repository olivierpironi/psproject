package com.fourcamp.sbanco.domain.dto.conta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NumeroESenhaConta(
		
		@NotNull
		Long numeroDaConta, 
		
		@NotBlank
		String senha) {

}
