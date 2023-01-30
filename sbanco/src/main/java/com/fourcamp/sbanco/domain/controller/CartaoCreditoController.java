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
import com.fourcamp.sbanco.domain.dto.cartao.DadosNumeroESenhaCartao;
import com.fourcamp.sbanco.domain.dto.cartao.PagarComCartao;
import com.fourcamp.sbanco.domain.dto.cartaocredito.CartaoCreditoDTO;
import com.fourcamp.sbanco.domain.dto.cartaocredito.DadosCadastroCartaoCredito;
import com.fourcamp.sbanco.domain.dto.cartaocredito.DetalhamentoDadosCartaoCredito;
import com.fourcamp.sbanco.domain.dto.fatura.PagarFatura;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.service.CartaoCreditoService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cartaocredito")
public class CartaoCreditoController {

	@Autowired
	private CartaoCreditoService cartaoService;

	@PostMapping("/emitir")
	@Transactional
	public ResponseEntity<DetalhamentoDadosCartaoCredito> emitirCartaoDeCredito(@RequestBody @Valid DadosCadastroCartaoCredito dados) {
		CartaoCreditoDTO cartao = cartaoService.emitirCartaoDeCredito(dados);
		return ResponseEntity.created(URI.create("/cartaocredito/"+cartao.getNumero())).body(new DetalhamentoDadosCartaoCredito(cartao));
	}

	@GetMapping("/{numerodocartao}")
	public ResponseEntity<DetalhamentoDadosCartaoCredito> detalhar(@PathVariable("numerodocartao") Long numeroDoCartao) {
		return ResponseEntity.ok(cartaoService.getById(numeroDoCartao));
	}

	@PutMapping("/desbloquear")
	@Transactional
	public ResponseEntity<DetalhamentoDadosCartaoCredito> desbloquear(@RequestBody @Valid DadosAtualizarCartao dados) {
		return ResponseEntity.ok(cartaoService.desbloquearCartao(dados));
	}

	@PutMapping("/bloquear")
	@Transactional
	public ResponseEntity<DetalhamentoDadosCartaoCredito> bloquear(@RequestBody @Valid DadosAtualizarCartao dados) {
		return ResponseEntity.ok(cartaoService.bloquearCartao(dados));
	}

	@PutMapping("/pagar")
	@Transactional
	public ResponseEntity<Object> pagarComCredito(@RequestBody @Valid PagarComCartao dados) {
		return ResponseEntity.ok().body(cartaoService.pagarComCredito(dados));
		
	}

	@GetMapping("/exibirfatura")
	@Transactional
	public ResponseEntity<List<DetalhamentoTransacao>> exibirFatura(@RequestBody @Valid DadosNumeroESenhaCartao dados) {
		return ResponseEntity.ok().body(cartaoService.exibirFatura(dados));
	}

	@PutMapping("/pagarfatura")
	@Transactional
	public ResponseEntity<DetalhamentoTransacao> pagarFaturaComSaldo(@RequestBody @Valid PagarFatura dados) {
		return ResponseEntity.ok().body(cartaoService.pagarFaturaComSaldo(dados));
	}

}
