package com.fourcamp.sbanco.domain.dto.cliente;

import org.hibernate.validator.constraints.br.CPF;

import com.fourcamp.sbanco.domain.enums.EnumCliente;

import jakarta.validation.constraints.NotBlank;

public record AtualizarCliente(
		
		String nome, 
		
		@NotBlank
		@CPF
		String cpf, 
		
		String dataDeNascimento, 
		
		String endereco, 
		
		EnumCliente tipoDeCliente) {

}
