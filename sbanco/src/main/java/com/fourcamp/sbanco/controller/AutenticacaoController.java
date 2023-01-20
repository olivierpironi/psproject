package com.fourcamp.sbanco.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fourcamp.sbanco.dto.ClienteDTO;
import com.fourcamp.sbanco.dto.ContaCorrenteDTO;
import com.fourcamp.sbanco.dto.ContaDTO;
import com.fourcamp.sbanco.dto.ContaPoupancaDTO;
import com.fourcamp.sbanco.dto.records.DadosCadastroCliente;
import com.fourcamp.sbanco.dto.records.DadosCadastroConta;
import com.fourcamp.sbanco.dto.records.DetalhamentoDadosCliente;
import com.fourcamp.sbanco.dto.records.DetalhamentoDadosConta;
import com.fourcamp.sbanco.infra.security.DadosAutenticacao;
import com.fourcamp.sbanco.infra.security.DadosToken;
import com.fourcamp.sbanco.infra.security.TokenService;
import com.fourcamp.sbanco.services.ClienteService;
import com.fourcamp.sbanco.services.ContaService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {
	
	@Autowired
	private AuthenticationManager manager;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private ClienteService clienteService;
	@Autowired
	private ContaService contaService;
	

	@PostMapping
	public  ResponseEntity<Object> efetuarLogin(@RequestBody DadosAutenticacao dados){
		var token = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
		var autenticacao = manager.authenticate(token);
		String tokenJWT = tokenService.gerarToken((ContaDTO) autenticacao.getPrincipal());
		return ResponseEntity.ok(new DadosToken(tokenJWT));
	}
	
	@PostMapping("/cliente/cadastrar")
	@Transactional 
	public ResponseEntity<DetalhamentoDadosCliente> cadastrar(@RequestBody @Valid DadosCadastroCliente dados, UriComponentsBuilder uriBuilder) {
		ClienteDTO cliente = new ClienteDTO(dados);
		clienteService.salvar(cliente);
		var uri = uriBuilder.path("/cliente/{id}").buildAndExpand(cliente.getCpf()).toUri();
		return ResponseEntity.created(uri).body(new DetalhamentoDadosCliente(cliente));
	}
	
	@PostMapping("/conta/cadastrarcp")
	@Transactional
	public ResponseEntity<DetalhamentoDadosConta> cadastrarCP(@RequestBody @Valid DadosCadastroConta dados, UriComponentsBuilder uriBuilder) {
		ClienteDTO cliente = clienteService.getByCpf(dados.cpfCliente());
		ContaPoupancaDTO conta = new ContaPoupancaDTO(cliente, dados.senha());
		contaService.salvar(conta);
		var uri = uriBuilder.path("/conta/{numerodaconta}").buildAndExpand(conta.getNumeroDaConta()).toUri();
		return ResponseEntity.created(uri).body(new DetalhamentoDadosConta(conta));
	}
	
	@PostMapping("/conta/cadastrarcc")
	@Transactional
	public ResponseEntity<DetalhamentoDadosConta> cadastrarCC(@RequestBody @Valid DadosCadastroConta dados, UriComponentsBuilder uriBuilder) {
		ClienteDTO cliente = clienteService.getByCpf(dados.cpfCliente());
		ContaCorrenteDTO conta = new ContaCorrenteDTO(cliente, dados.senha());
		contaService.salvar(conta);
		var uri = uriBuilder.path("/conta/{numerodaconta}").buildAndExpand(conta.getNumeroDaConta()).toUri();
		return ResponseEntity.created(uri).body(new DetalhamentoDadosConta(conta));
	}
}
