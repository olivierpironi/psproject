package com.fourcamp.sbanco.domain.dto.cliente;

import com.fourcamp.sbanco.domain.enums.EnumCliente;

public record DetalhaCliente(String nome, String cpf, String dataDeNascimento, String endereco,
		EnumCliente tipoDeCliente) {

	public DetalhaCliente(ClienteDTO cliente) {
		this(cliente.getNome(), cliente.getCpf(), cliente.getDataDeNascimento().toString(),cliente.getEndereco(), cliente.getCategoria());
	}
}
