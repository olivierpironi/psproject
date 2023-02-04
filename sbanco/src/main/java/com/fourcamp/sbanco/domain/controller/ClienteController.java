package com.fourcamp.sbanco.domain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fourcamp.sbanco.domain.dto.cliente.DadosAtualizarCliente;
import com.fourcamp.sbanco.domain.dto.cliente.DetalhaCliente;
import com.fourcamp.sbanco.domain.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cliente") 
public class ClienteController {
	
	@Autowired
	ClienteService clienteService;

	@PatchMapping("/atualizar")
	public ResponseEntity<DetalhaCliente> atualizarDados(@RequestBody @Valid DadosAtualizarCliente dados) {
		return ResponseEntity.ok(clienteService.atualizarInformacoes(dados));
		
	}
	
	@GetMapping("/{cpf}")
	public ResponseEntity<DetalhaCliente> detalhar(@PathVariable String cpf) {
		return ResponseEntity.ok(clienteService.consultaClienteByCPF(cpf));
	}
}
