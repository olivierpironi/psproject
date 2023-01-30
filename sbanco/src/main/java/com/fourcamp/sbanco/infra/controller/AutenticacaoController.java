package com.fourcamp.sbanco.infra.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fourcamp.sbanco.domain.dto.cliente.ClienteDTO;
import com.fourcamp.sbanco.domain.dto.cliente.DadosCadastroCliente;
import com.fourcamp.sbanco.domain.dto.cliente.DetalhaCliente;
import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;
import com.fourcamp.sbanco.domain.dto.conta.DadosCadastroConta;
import com.fourcamp.sbanco.domain.dto.conta.DetalhamentoDadosConta;
import com.fourcamp.sbanco.domain.dto.contacorrente.ContaCorrenteDTO;
import com.fourcamp.sbanco.domain.dto.contapoupanca.ContaPoupancaDTO;
import com.fourcamp.sbanco.domain.service.ClienteService;
import com.fourcamp.sbanco.domain.service.ContaService;
import com.fourcamp.sbanco.infra.security.DadosAutenticacao;
import com.fourcamp.sbanco.infra.security.DadosToken;
import com.fourcamp.sbanco.infra.security.TokenService;

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
	public ResponseEntity<DetalhaCliente> cadastrar(@RequestBody @Valid DadosCadastroCliente dados, UriComponentsBuilder uriBuilder) {
		ClienteDTO cliente = clienteService.salvar(dados);
		return ResponseEntity.created(URI.create("/cliente/"+cliente.getCpf())).body(new DetalhaCliente(cliente));
	}
	
	@PostMapping("/conta/cadastrarcp")
	@Transactional
	public ResponseEntity<DetalhamentoDadosConta> cadastrarCP(@RequestBody @Valid DadosCadastroConta dados, UriComponentsBuilder uriBuilder) {
		ContaPoupancaDTO cp = contaService.cadastrarCP(dados);
		return ResponseEntity.created(URI.create("/conta/"+cp.getNumeroDaConta().toString())).body(new DetalhamentoDadosConta(cp));
	}
	
	@PostMapping("/conta/cadastrarcc")
	@Transactional
	public ResponseEntity<DetalhamentoDadosConta> cadastrarCC(@RequestBody @Valid DadosCadastroConta dados, UriComponentsBuilder uriBuilder) {
		ContaCorrenteDTO cc = contaService.cadastrarCC(dados);
		return ResponseEntity.created(URI.create("/conta/"+cc.getNumeroDaConta().toString())).body(new DetalhamentoDadosConta(cc));
	}
}
