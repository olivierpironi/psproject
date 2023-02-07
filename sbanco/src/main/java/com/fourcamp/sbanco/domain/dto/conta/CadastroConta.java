package com.fourcamp.sbanco.domain.dto.conta;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.NotBlank;

public record CadastroConta(
		
		@NotBlank
		@CPF
		String cpfCliente, 
		
		@NotBlank
		String senha) {

}
