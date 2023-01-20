package com.fourcamp.sbanco.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fourcamp.sbanco.dto.CartaoDebitoDTO;
import com.fourcamp.sbanco.dto.records.DadosAtualizarCartao;
import com.fourcamp.sbanco.dto.records.DadosCadastroCartaoDebito;
import com.fourcamp.sbanco.dto.records.DetalhamentoDadosCartaoDebito;
import com.fourcamp.sbanco.dto.records.PagarComCartao;
import com.fourcamp.sbanco.services.CartaoDebitoService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cartaodebito")
public class CartaoDebitoController {
	
	@Autowired
	private CartaoDebitoService cartaoService;
	
	@PostMapping("/emitir")
	@Transactional
	public ResponseEntity<DetalhamentoDadosCartaoDebito> emitirCartaoDeDebito(@RequestBody @Valid DadosCadastroCartaoDebito dados, UriComponentsBuilder uriBuilder) {
		CartaoDebitoDTO cartao = cartaoService.emitirCartaoDeDebito(dados.numeroDaConta(), dados.senhaCartao());
		var uri = uriBuilder.path("/cartaodebito/{numerocartao}").buildAndExpand(cartao.getNumero()).toUri();
		return ResponseEntity.created(uri).body(new DetalhamentoDadosCartaoDebito(cartao)); 
	}
	
	@GetMapping("/{numerodocartao}")
	public ResponseEntity<DetalhamentoDadosCartaoDebito> detalhar(@PathVariable("numerodocartao") Long numeroDoCartao) {
		CartaoDebitoDTO cartao = cartaoService.getById(numeroDoCartao);
		return ResponseEntity.ok(new DetalhamentoDadosCartaoDebito(cartao));
	}
	
	@PutMapping("/desbloquear")
	@Transactional
	public void desbloquear(@RequestBody @Valid DadosAtualizarCartao dados) {
		cartaoService.desbloquearCartao(dados.numeroDoCartao(), dados.senhaCartao());
	}
	
	@PutMapping("/bloquear")
	@Transactional
	public void bloquear(@RequestBody @Valid DadosAtualizarCartao dados) {
		cartaoService.bloquearCartao(dados.numeroDoCartao(), dados.senhaCartao());
	}
	
	@PutMapping("/renovarlimitedisponivel/{numerodocartao}")
	@Transactional
	public void renovarLimite(@PathVariable("numerodocartao") Long numeroDoCartao) {
		cartaoService.renovarLimite(numeroDoCartao);
	}
	
	@PutMapping("/pagar")
	@Transactional
	public void pagarComDebito(@RequestBody @Valid PagarComCartao dados) {
		cartaoService.pagarComDebito(dados.numeroDoCartao(),dados.numeroDaContaDestino(),dados.valorPagamento() ,dados.senhaCartao());
	}
	
	

}
