package com.fourcamp.sbanco.domain.dto.fatura;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PagarFatura(
		
		@NotNull
		Long numeroDoCartao, 
		
		@NotBlank
		String valorPagamento,
		
		@NotBlank
		String senhaConta ) {

}
