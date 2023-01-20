package com.fourcamp.sbanco.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fourcamp.sbanco.dto.ChavePixDTO;
import com.fourcamp.sbanco.dto.ContaDTO;
import com.fourcamp.sbanco.dto.TransacaoDTO;
import com.fourcamp.sbanco.dto.records.DadosAtualizarConta;
import com.fourcamp.sbanco.dto.records.DadosCadastroChavePix;
import com.fourcamp.sbanco.dto.records.DadosPix;
import com.fourcamp.sbanco.dto.records.DepositoESaqueConta;
import com.fourcamp.sbanco.dto.records.DetalhamentoChavesPix;
import com.fourcamp.sbanco.dto.records.DetalhamentoDadosConta;
import com.fourcamp.sbanco.dto.records.DetalhamentoTransacao;
import com.fourcamp.sbanco.dto.records.NumeroDaConta;
import com.fourcamp.sbanco.services.ClienteService;
import com.fourcamp.sbanco.services.ContaService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/conta")
public class ContaController {

	@Autowired
	ContaService contaService;
	@Autowired
	ClienteService clienteService; 

	@GetMapping("/{numerodaconta}")
	public ResponseEntity<DetalhamentoDadosConta> detalhar(@PathVariable("numerodaconta") Long numeroDaConta) {
		 ContaDTO conta = contaService.getByNumeroDaConta(numeroDaConta);
		return ResponseEntity.ok(new DetalhamentoDadosConta(conta));
	}

	@PutMapping("/deposito")
	@Transactional
	public void deposito(@RequestBody @Valid DepositoESaqueConta dados) {
		ContaDTO conta = contaService.getByNumeroDaConta(dados.numeroDaConta());
		contaService.efetuarDeposito(conta, dados.valor());
	}

	@PutMapping("/saque")
	@Transactional
	public void saque(@RequestBody @Valid DepositoESaqueConta dados) {
		ContaDTO conta = contaService.getByNumeroDaConta(dados.numeroDaConta());
		contaService.efetuarSaque(conta, dados.valor());
	}
	
	@GetMapping("/exibirextrato/{numerodaconta}")
	public ResponseEntity<List<DetalhamentoTransacao>> exibirExtrato(@PathVariable("numerodaconta") Long numeroDaConta) {
		ContaDTO conta = contaService.getByNumeroDaConta(numeroDaConta);
		 List<TransacaoDTO> listaDeTransacoes = contaService.exibirExtrato(conta);
		return ResponseEntity.ok().body(listaDeTransacoes.stream().map(DetalhamentoTransacao::new).toList());
	}
	
	@PutMapping("/trocarsenha")
	@Transactional
	public void trocarSenha(@RequestBody @Valid DadosAtualizarConta dados) {
		ContaDTO conta = contaService.getByNumeroDaConta(dados.numeroDaConta());
		contaService.trocarSenha(conta, dados.novaSenha());
	}
	
	@PutMapping("/pix/cadastrarchave")
	@Transactional
	public void cadastrarChavePix(@RequestBody @Valid DadosCadastroChavePix dados) {
		contaService.cadastrarChavePix(dados.numeroDaConta(), dados.chavepix());
	}

	@GetMapping("/pix/exibirchavespix")
	public ResponseEntity<List<DetalhamentoChavesPix>> exibirChavesPix(@RequestBody NumeroDaConta dados) {
		List<ChavePixDTO> todasAsChaves = contaService.exibirChavesPix(dados.numeroDaConta());
		return ResponseEntity.ok().body(todasAsChaves.stream().map(DetalhamentoChavesPix::new).toList());
	}
	
	@PutMapping("/pix/enviarpix")
	@Transactional
	public void cadastrarChavePix(@RequestBody @Valid DadosPix dados) {
		contaService.enviarPix(dados.numeroDaContaOrigem(), dados.numeroDaContaDestino(), dados.valorPix());
	}
	
}
