package com.fourcamp.sbanco.dto.records;

import org.hibernate.validator.constraints.br.CPF;

import com.fourcamp.sbanco.enums.EnumCliente;

import jakarta.validation.constraints.NotBlank;

public record DadosAtualizarCliente(
		
		String nome, 
		
		@NotBlank
		@CPF
		String cpf, 
		
		String dataDeNascimento, 
		
		String endereco, 
		
		EnumCliente tipoDeCliente) {

}
