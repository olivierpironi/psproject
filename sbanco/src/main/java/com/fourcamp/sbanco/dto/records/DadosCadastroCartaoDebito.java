package com.fourcamp.sbanco.dto.records;

import jakarta.validation.constraints.NotNull;

public record DadosCadastroCartaoDebito(
		
		@NotNull
		Long numeroDaConta, 
		
		@NotNull
		Integer senhaCartao) {

}
