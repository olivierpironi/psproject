package com.fourcamp.sbanco.domain.dto.cartaocredito;

import com.fourcamp.sbanco.domain.enums.EnumSeguroCredito;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroCartaoCredito(
		
		@NotNull
		Long numeroDaConta, 
		
		@NotNull
		Integer senhaCartao,
		
		@NotNull
		EnumSeguroCredito seguradora,
		
		@NotBlank
		String senhaDaConta ) {

}
