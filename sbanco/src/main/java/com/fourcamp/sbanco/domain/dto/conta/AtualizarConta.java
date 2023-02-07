package com.fourcamp.sbanco.domain.dto.conta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarConta(
		
		@NotNull
		Long numeroDaConta, 
		
		@NotBlank
		String senhaAntiga, 
		
		String novaSenha) {

}
