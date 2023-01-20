package com.fourcamp.sbanco.dto.records;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroConta(
		
		@NotBlank
		@CPF
		String cpfCliente, 
		
		@NotBlank
		String senha) {

}
