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
import com.fourcamp.sbanco.domain.dto.cartao.NumeroESenhaCartao;
import com.fourcamp.sbanco.domain.dto.cartao.PagarComCartao;
import com.fourcamp.sbanco.domain.dto.cartaocredito.AtualizarCartaoCredito;
import com.fourcamp.sbanco.domain.dto.cartaocredito.CadastroCartaoCredito;
import com.fourcamp.sbanco.domain.dto.cartaocredito.CartaoCreditoDTO;
import com.fourcamp.sbanco.domain.dto.cartaocredito.DetalhaCartaoCredito;
import com.fourcamp.sbanco.domain.dto.fatura.PagarFatura;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.service.CartaoCreditoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cartaocredito")
public class CartaoCreditoController {

	@Autowired
	private CartaoCreditoService cartaoService;

	@PostMapping("/emitir")
	public ResponseEntity<DetalhaCartaoCredito> emitirCartaoDeCredito(@RequestBody @Valid CadastroCartaoCredito dados) {
		CartaoCreditoDTO cartao = cartaoService.emitirCartaoDeCredito(dados);
		return ResponseEntity.created(URI.create("/cartaocredito/"+cartao.getNumero())).body(new DetalhaCartaoCredito(cartao));
	}

	@GetMapping("/{numerodocartao}")
	public ResponseEntity<DetalhaCartaoCredito> detalhar(@PathVariable("numerodocartao") Long numeroDoCartao) {
		return ResponseEntity.ok(cartaoService.getById(numeroDoCartao));
	}

	@PatchMapping("/desbloquear")
	public ResponseEntity<DetalhaCartaoCredito> desbloquear(@RequestBody @Valid AtualizarCartao dados) {
		return ResponseEntity.ok(cartaoService.desbloquearCartao(dados));
	}

	@PatchMapping("/bloquear")
	public ResponseEntity<DetalhaCartaoCredito> bloquear(@RequestBody @Valid AtualizarCartao dados) {
		return ResponseEntity.ok(cartaoService.bloquearCartao(dados));
	}
	
	@PatchMapping("/atualizar-dados")
	public ResponseEntity<DetalhaCartaoCredito> atualizarCartao(@RequestBody @Valid AtualizarCartaoCredito dados) {
		return ResponseEntity.ok(cartaoService.atualizarCartao(dados));
	}

	@PatchMapping("/pagar")
	public ResponseEntity<List<DetalhamentoTransacao>> pagarComCredito(@RequestBody @Valid PagarComCartao dados) {
		return ResponseEntity.ok().body(cartaoService.pagarComCredito(dados));
		
	}

	@GetMapping("/exibirfatura")
	public ResponseEntity<List<DetalhamentoTransacao>> exibirFatura(@RequestBody @Valid NumeroESenhaCartao dados) {
		return ResponseEntity.ok().body(cartaoService.exibirFatura(dados));
	}

	@PatchMapping("/pagarfatura")
	public ResponseEntity<DetalhamentoTransacao> pagarFaturaComSaldo(@RequestBody @Valid PagarFatura dados) {
		return ResponseEntity.ok().body(cartaoService.pagarFaturaComSaldo(dados));
	}

}
