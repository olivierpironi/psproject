package com.fourcamp.sbanco.domain.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fourcamp.sbanco.domain.dto.cliente.ClienteDTO;
import com.fourcamp.sbanco.domain.dto.cliente.AtualizarCliente;
import com.fourcamp.sbanco.domain.dto.cliente.CadastroCliente;
import com.fourcamp.sbanco.domain.dto.cliente.DetalhaCliente;
import com.fourcamp.sbanco.domain.repository.ClienteRepository;
import com.fourcamp.sbanco.infra.exceptions.ClienteNaoExisteException;

import jakarta.validation.Valid;

@Service
@Transactional
public class ClienteService {

	private static final ClienteNaoExisteException CLIENTE_NAO_EXISTE_EXCEPTION = new ClienteNaoExisteException("CPF não cadastrado.");
	
	@Autowired
	private ClienteRepository clienteRepository;

	public ClienteDTO salvar(@Valid CadastroCliente dados) {
		return clienteRepository.save(new ClienteDTO(dados));
	}

	public ClienteDTO getByCpf(String cpf) {
		return clienteRepository.findById(cpf).orElseThrow(() -> CLIENTE_NAO_EXISTE_EXCEPTION);
	}
	public DetalhaCliente consultaClienteByCPF(String cpf) {
		ClienteDTO cliente = clienteRepository.findById(cpf).orElseThrow(() -> CLIENTE_NAO_EXISTE_EXCEPTION);
		return new DetalhaCliente(cliente);
	}

	public DetalhaCliente atualizarInformacoes(@Valid AtualizarCliente dados) {
		ClienteDTO cliente = clienteRepository.findById(dados.cpf()).orElseThrow(() -> CLIENTE_NAO_EXISTE_EXCEPTION);
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
