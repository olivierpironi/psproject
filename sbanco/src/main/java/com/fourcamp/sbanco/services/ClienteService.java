package com.fourcamp.sbanco.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fourcamp.sbanco.dto.ClienteDTO;
import com.fourcamp.sbanco.dto.records.DadosAtualizarCliente;
import com.fourcamp.sbanco.repository.ClienteRepository;

import jakarta.validation.Valid;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repository;

	public void salvar(ClienteDTO cliente) {
		repository.save(cliente);
	}

	public ClienteDTO getByCpf(String cpf) {
		return repository.findById(cpf).get();
	}

	public void deletarById(String cpf) {
		repository.deleteById(cpf);

	}

	public void atualizarInformacoes(@Valid DadosAtualizarCliente dados, ClienteDTO cliente) {
		if (dados.nome() != null)
			cliente.setNome(dados.nome());
		if (dados.nome() != null)
			cliente.setCpf(dados.cpf());
		if (dados.dataDeNascimento() != null)
			cliente.setDataDeNascimento(
					LocalDate.parse(dados.dataDeNascimento(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		if (dados.endereco() != null)
			cliente.setEndereco(dados.endereco());
		if (dados.tipoDeCliente() != null)
			cliente.setCategoria(dados.tipoDeCliente());
	}

}
