package com.fourcamp.sbanco.dto.records;

import org.hibernate.validator.constraints.br.CPF;

import com.fourcamp.sbanco.enums.EnumCliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroCliente(
		
		@NotBlank
		String nome, 
		
		@NotBlank
		@CPF
		String cpf, 
		
		@NotBlank
		String dataDeNascimento, 
		
		String endereco, 
		
		@NotNull
		EnumCliente tipoDeCliente) {

}
