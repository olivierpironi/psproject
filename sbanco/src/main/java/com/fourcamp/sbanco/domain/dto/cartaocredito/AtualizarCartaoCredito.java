package com.fourcamp.sbanco.domain.dto.cartaocredito;

import com.fourcamp.sbanco.domain.enums.EnumSeguroCredito;

import jakarta.validation.constraints.NotNull;

public record AtualizarCartaoCredito(
		
		@NotNull
		Long numeroDoCartao, 
		
		@NotNull
		Integer senhaCartao,
		
		EnumSeguroCredito seguradora, 
		
		String novoLimite) {

}
