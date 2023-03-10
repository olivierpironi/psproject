package com.fourcamp.sbanco.domain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fourcamp.sbanco.domain.dto.conta.AtualizarConta;
import com.fourcamp.sbanco.domain.dto.conta.DepositoESaqueConta;
import com.fourcamp.sbanco.domain.dto.conta.DetalhaConta;
import com.fourcamp.sbanco.domain.dto.conta.NumeroESenhaConta;
import com.fourcamp.sbanco.domain.dto.pix.CadastroChavePix;
import com.fourcamp.sbanco.domain.dto.pix.DadosPix;
import com.fourcamp.sbanco.domain.dto.pix.DetalhamentoChavesPix;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.service.ClienteService;
import com.fourcamp.sbanco.domain.service.ContaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/conta")
public class ContaController {

	@Autowired
	ContaService contaService;
	@Autowired
	ClienteService clienteService;

	@GetMapping("/{numerodaconta}")
	public ResponseEntity<DetalhaConta> detalhar(@PathVariable("numerodaconta") Long numeroDaConta) {
		return ResponseEntity.ok(contaService.consultaByNumeroDaConta(numeroDaConta));
	}

	@PatchMapping("/deposito")
	public ResponseEntity<DetalhamentoTransacao> deposito(@RequestBody @Valid DepositoESaqueConta dados) {
		return ResponseEntity.ok(contaService.efetuarDeposito(dados));
	}

	@PatchMapping("/saque")
	public ResponseEntity<DetalhamentoTransacao> saque(@RequestBody @Valid DepositoESaqueConta dados) {
		return ResponseEntity.ok(contaService.efetuarSaque(dados));
	}

	@GetMapping("/exibirextrato/")
	public ResponseEntity<List<DetalhamentoTransacao>> exibirExtrato(@RequestBody @Valid NumeroESenhaConta dados) {
		return ResponseEntity.ok().body(contaService.exibirExtrato(dados));
	}

	@PatchMapping("/trocarsenha")
	public ResponseEntity<DetalhaConta> trocarSenha(@RequestBody @Valid AtualizarConta dados) {
		return ResponseEntity.ok().body(contaService.trocarSenha(dados));
	}

	@PatchMapping("/pix/cadastrarchave")
	public ResponseEntity<DetalhamentoChavesPix> cadastrarChavePix(@RequestBody @Valid CadastroChavePix dados) {
		return ResponseEntity.ok().body(contaService.cadastrarChavePix(dados));
	}

	@GetMapping("/pix/exibirchavespix")
	public ResponseEntity<List<DetalhamentoChavesPix>> exibirChavesPix(@RequestBody @Valid NumeroESenhaConta dados) {
		return ResponseEntity.ok().body(contaService.exibirChavesPix(dados));
	}

	@PatchMapping("/pix/enviarpix")
	public ResponseEntity<DetalhamentoTransacao> enviarPix(@RequestBody @Valid DadosPix dados) {
		return ResponseEntity.ok().body(contaService.enviarPix(dados));
	}

}
