package com.fourcamp.sbanco.domain.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fourcamp.sbanco.domain.dto.cartao.AtualizarCartao;
import com.fourcamp.sbanco.domain.dto.cartao.PagarComCartao;
import com.fourcamp.sbanco.domain.dto.cartaodebito.CartaoDebitoDTO;
import com.fourcamp.sbanco.domain.dto.cartaodebito.CadastroCartaoDebito;
import com.fourcamp.sbanco.domain.dto.cartaodebito.DetalhaCartaoDebito;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.service.CartaoDebitoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cartaodebito")
public class CartaoDebitoController {
	
	@Autowired
	private CartaoDebitoService cartaoService;
	
	@PostMapping("/emitir")
	public ResponseEntity<DetalhaCartaoDebito> emitirCartaoDeDebito(@RequestBody @Valid CadastroCartaoDebito dados) {
		CartaoDebitoDTO cartao = cartaoService.emitirCartaoDeDebito(dados);
		return ResponseEntity.created(URI.create("/cartaocredito/"+cartao.getNumero())).body(new DetalhaCartaoDebito(cartao));
	}
	
	@GetMapping("/{numerodocartao}")
	public ResponseEntity<DetalhaCartaoDebito> detalhar(@PathVariable("numerodocartao") Long numeroDoCartao) {
		return ResponseEntity.ok(cartaoService.getById(numeroDoCartao));
	}
	
	@PatchMapping("/desbloquear")
	public ResponseEntity<DetalhaCartaoDebito> desbloquear(@RequestBody @Valid AtualizarCartao dados) {
		return ResponseEntity.ok(cartaoService.desbloquearCartao(dados));
	}
	
	@PatchMapping("/bloquear")
	public ResponseEntity<DetalhaCartaoDebito> bloquear(@RequestBody @Valid AtualizarCartao dados) {
		return ResponseEntity.ok(cartaoService.bloquearCartao(dados));
	}
	
	@PatchMapping("/renovarlimitedisponivel/{numerodocartao}")
	public ResponseEntity<DetalhaCartaoDebito> renovarLimite(@PathVariable("numerodocartao") Long numeroDoCartao) {
		return ResponseEntity.ok(cartaoService.renovarLimite(numeroDoCartao));
	}
	
	@PatchMapping("/pagar")
	public ResponseEntity<List<DetalhamentoTransacao>> pagarComDebito(@RequestBody @Valid PagarComCartao dados) {
		return ResponseEntity.ok(cartaoService.pagarComDebito(dados));
	}
	
	

}
