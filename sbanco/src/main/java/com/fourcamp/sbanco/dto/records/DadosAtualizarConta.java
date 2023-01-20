package com.fourcamp.sbanco.dto.records;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizarConta(
		
		@NotNull
		Long numeroDaConta, 
		
		String novaSenha) {

}
