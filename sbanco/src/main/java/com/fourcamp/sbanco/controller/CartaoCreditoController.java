package com.fourcamp.sbanco.controller;

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
import org.springframework.web.util.UriComponentsBuilder;

import com.fourcamp.sbanco.dto.CartaoCreditoDTO;
import com.fourcamp.sbanco.dto.TransacaoDTO;
import com.fourcamp.sbanco.dto.records.DadosAtualizarCartao;
import com.fourcamp.sbanco.dto.records.DadosCadastroCartaoCredito;
import com.fourcamp.sbanco.dto.records.DetalhamentoDadosCartaoCredito;
import com.fourcamp.sbanco.dto.records.DetalhamentoTransacao;
import com.fourcamp.sbanco.dto.records.NumeroDoCartao;
import com.fourcamp.sbanco.dto.records.PagarComCartao;
import com.fourcamp.sbanco.dto.records.PagarFatura;
import com.fourcamp.sbanco.services.CartaoCreditoService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cartaocredito")
public class CartaoCreditoController { 
	
	@Autowired
	private CartaoCreditoService cartaoService;
	
	@PostMapping("/emitir")
	@Transactional
	public ResponseEntity<DetalhamentoDadosCartaoCredito> emitirCartaoDeCredito(@RequestBody @Valid DadosCadastroCartaoCredito dados, UriComponentsBuilder uriBuilder) {
		CartaoCreditoDTO cartao = cartaoService.emitirCartaoDeCredito(dados.numeroDaConta(),dados.seguradora() ,dados.senhaCartao());
		var uri = uriBuilder.path("cartaocredito/{numerocartao}").buildAndExpand(cartao.getNumero()).toUri();
		return ResponseEntity.created(uri).body(new DetalhamentoDadosCartaoCredito(cartao));
	}
	
	@GetMapping("/{numerodocartao}")
	public ResponseEntity<DetalhamentoDadosCartaoCredito> detalhar(@PathVariable("numerodocartao") Long numeroDoCartao) {
		CartaoCreditoDTO cartao = cartaoService.getById(numeroDoCartao);
		return ResponseEntity.ok(new DetalhamentoDadosCartaoCredito(cartao));
	}
	
	@PutMapping("/desbloquear")
	@Transactional
	public void desbloquear(@RequestBody @Valid DadosAtualizarCartao dados) {
		cartaoService.desbloquearCartao(dados.numeroDoCartao(), dados.senhaCartao());
	}
	
	@PutMapping("/bloquear")
	@Transactional
	public void bloquear(@RequestBody @Valid DadosAtualizarCartao dados) {
		cartaoService.bloquearCartao(dados.numeroDoCartao());
	}
	
	//TODO necessário implementar retorno http quando o cartão estiver bloqueado ou sem limite de crédito;
	@PutMapping("/pagar")
	@Transactional
	public void pagarComCredito(@RequestBody @Valid PagarComCartao dados) {
		cartaoService.pagarComCredito(dados.numeroDoCartao(),dados.numeroDaContaDestino(),dados.valorPagamento() ,dados.senhaCartao());
	}
	
	@GetMapping("/exibirfatura")
	@Transactional
	public ResponseEntity<List<DetalhamentoTransacao>> exibirFatura(@RequestBody @Valid NumeroDoCartao dados) {
		List<TransacaoDTO> listaDeTransacoes = cartaoService.exibirFatura(dados.numeroDoCartao());
		return ResponseEntity.ok().body(listaDeTransacoes.stream().map(DetalhamentoTransacao::new).toList());
	}
	
	@PutMapping("/pagarfatura")
	@Transactional
	public void pagarFaturaComSaldo(@RequestBody @Valid PagarFatura dados) {
		cartaoService.pagarFaturaComSaldo(dados.numeroDoCartao(), dados.valorPagamento() ,dados.senhaCartao());
	}
	

}
