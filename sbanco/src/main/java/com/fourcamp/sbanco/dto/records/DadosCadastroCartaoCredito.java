package com.fourcamp.sbanco.dto.records;

import com.fourcamp.sbanco.enums.EnumSeguroCredito;

import jakarta.validation.constraints.NotNull;

public record DadosCadastroCartaoCredito(
		
		@NotNull
		Long numeroDaConta, 
		
		@NotNull
		Integer senhaCartao,
		
		@NotNull
		EnumSeguroCredito seguradora) {

}
