package com.fourcamp.sbanco.domain.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fourcamp.sbanco.domain.dto.cliente.ClienteDTO;
import com.fourcamp.sbanco.domain.dto.cliente.DadosAtualizarCliente;
import com.fourcamp.sbanco.domain.dto.cliente.DadosCadastroCliente;
import com.fourcamp.sbanco.domain.dto.cliente.DetalhaCliente;
import com.fourcamp.sbanco.domain.repository.ClienteRepository;

import jakarta.validation.Valid;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	public ClienteDTO salvar(@Valid DadosCadastroCliente dados) {
		return clienteRepository.save(new ClienteDTO(dados));
	}

	public ClienteDTO getByCpf(String cpf) {
		return clienteRepository.findById(cpf).get();
	}
	public DetalhaCliente consultaClienteByCPF(String cpf) {
		return new DetalhaCliente(clienteRepository.findById(cpf).get());
	}

	public void deletarById(String cpf) {
		clienteRepository.deleteById(cpf);

	}

	@Transactional
	public DetalhaCliente atualizarInformacoes(@Valid DadosAtualizarCliente dados) {
		ClienteDTO cliente = clienteRepository.findById(dados.cpf()).get();
		if (dados.nome() != null)
			cliente.setNome(dados.nome());
		if (dados.cpf() != null)
			cliente.setCpf(dados.cpf());
		if (dados.dataDeNascimento() != null)
			cliente.setDataDeNascimento(
					LocalDate.parse(dados.dataDeNascimento(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		if (dados.endereco() != null)
			cliente.setEndereco(dados.endereco());
		if (dados.tipoDeCliente() != null)
			cliente.setCategoria(dados.tipoDeCliente());
		return new DetalhaCliente(cliente);
	}

}
