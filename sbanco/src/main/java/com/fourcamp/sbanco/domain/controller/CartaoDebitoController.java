package com.fourcamp.sbanco.domain.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fourcamp.sbanco.domain.dto.cartao.DadosAtualizarCartao;
import com.fourcamp.sbanco.domain.dto.cartao.PagarComCartao;
import com.fourcamp.sbanco.domain.dto.cartaodebito.CartaoDebitoDTO;
import com.fourcamp.sbanco.domain.dto.cartaodebito.DadosCadastroCartaoDebito;
import com.fourcamp.sbanco.domain.dto.cartaodebito.DetalhamentoDadosCartaoDebito;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.service.CartaoDebitoService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cartaodebito")
public class CartaoDebitoController {
	
	@Autowired
	private CartaoDebitoService cartaoService;
	
	@PostMapping("/emitir")
	@Transactional
	public ResponseEntity<DetalhamentoDadosCartaoDebito> emitirCartaoDeCredito(@RequestBody @Valid DadosCadastroCartaoDebito dados) {
		CartaoDebitoDTO cartao = cartaoService.emitirCartaoDeDebito(dados);
		return ResponseEntity.created(URI.create("/cartaocredito/"+cartao.getNumero())).body(new DetalhamentoDadosCartaoDebito(cartao));
	}
	
	@GetMapping("/{numerodocartao}")
	public ResponseEntity<DetalhamentoDadosCartaoDebito> detalhar(@PathVariable("numerodocartao") Long numeroDoCartao) {
		return ResponseEntity.ok(cartaoService.getById(numeroDoCartao));
	}
	
	@PutMapping("/desbloquear")
	@Transactional
	public ResponseEntity<DetalhamentoDadosCartaoDebito> desbloquear(@RequestBody @Valid DadosAtualizarCartao dados) {
		return ResponseEntity.ok(cartaoService.desbloquearCartao(dados));
	}
	
	@PutMapping("/bloquear")
	@Transactional
	public ResponseEntity<DetalhamentoDadosCartaoDebito> bloquear(@RequestBody @Valid DadosAtualizarCartao dados) {
		return ResponseEntity.ok(cartaoService.bloquearCartao(dados));
	}
	
	@PutMapping("/renovarlimitedisponivel/{numerodocartao}")
	@Transactional
	public ResponseEntity<DetalhamentoDadosCartaoDebito> renovarLimite(@PathVariable("numerodocartao") Long numeroDoCartao) {
		return ResponseEntity.ok(cartaoService.renovarLimite(numeroDoCartao));
	}
	
	@PutMapping("/pagar")
	@Transactional
	public ResponseEntity<List<DetalhamentoTransacao>> pagarComDebito(@RequestBody @Valid PagarComCartao dados) {
		return ResponseEntity.ok(cartaoService.pagarComDebito(dados));
	}
	
	

}
