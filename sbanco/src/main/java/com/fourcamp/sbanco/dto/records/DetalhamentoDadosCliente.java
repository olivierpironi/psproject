package com.fourcamp.sbanco.dto.records;

import com.fourcamp.sbanco.dto.ClienteDTO;
import com.fourcamp.sbanco.enums.EnumCliente;

public record DetalhamentoDadosCliente(String nome, String cpf, String dataDeNascimento, String endereco,
		EnumCliente tipoDeCliente) {

	public DetalhamentoDadosCliente(ClienteDTO cliente) {
		this(cliente.getNome(), cliente.getCpf(), cliente.getDataDeNascimento().toString(),cliente.getEndereco(), cliente.getCategoria());
	}
}
