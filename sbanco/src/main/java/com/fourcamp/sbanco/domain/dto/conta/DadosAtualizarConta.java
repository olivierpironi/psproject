package com.fourcamp.sbanco.domain.dto.conta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizarConta(
		
		@NotNull
		Long numeroDaConta, 
		
		@NotBlank
		String senhaAntiga, 
		
		String novaSenha) {

}
