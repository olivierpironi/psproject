package com.fourcamp.sbanco.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fourcamp.sbanco.dto.ClienteDTO;
import com.fourcamp.sbanco.dto.records.DadosAtualizarCliente;
import com.fourcamp.sbanco.dto.records.DetalhamentoDadosCliente;
import com.fourcamp.sbanco.services.ClienteService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cliente") 
public class ClienteController {
	
	@Autowired
	ClienteService clienteService;

	@PutMapping("/atualizar")
	@Transactional
	public ResponseEntity<DetalhamentoDadosCliente> atualizarDados(@RequestBody @Valid DadosAtualizarCliente dados) {
		ClienteDTO cliente = clienteService.getByCpf(dados.cpf());
		clienteService.atualizarInformacoes(dados, cliente);
		return ResponseEntity.ok(new DetalhamentoDadosCliente(cliente));
		
	}
	
	@GetMapping("/{cpf}")
	public ResponseEntity<DetalhamentoDadosCliente> detalhar(@PathVariable String cpf) {
		ClienteDTO cliente = clienteService.getByCpf(cpf);
		return ResponseEntity.ok(new DetalhamentoDadosCliente(cliente));
	}
}
