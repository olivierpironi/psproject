package com.fourcamp.sbanco.domain.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fourcamp.sbanco.domain.dto.conta.AtualizarConta;
import com.fourcamp.sbanco.domain.dto.conta.DepositoESaqueConta;
import com.fourcamp.sbanco.domain.dto.conta.DetalhaConta;
import com.fourcamp.sbanco.domain.dto.conta.NumeroESenhaConta;
import com.fourcamp.sbanco.domain.dto.pix.CadastroChavePix;
import com.fourcamp.sbanco.domain.dto.pix.ChavePixDTO;
import com.fourcamp.sbanco.domain.dto.pix.DadosPix;
import com.fourcamp.sbanco.domain.dto.pix.DetalhamentoChavesPix;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.dto.transacao.TransacaoDTO;
import com.fourcamp.sbanco.domain.enums.EnumTransacao;
import com.fourcamp.sbanco.domain.service.ContaService;
import com.fourcamp.sbanco.util.ContaFactory;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Teste para o controller de contas.")
class ContaControllerTest {

	@InjectMocks
	private ContaController controller;
	@Mock
	private ContaService contaService;
	
	private static ContaFactory c1;
	private static ContaFactory c2;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		c1 = new ContaFactory().fabricarC1();
		c2 = new ContaFactory().fabricarC2();
	}

	@Test
	@DisplayName("Efetuar deposito com sucesso.")
	void deposito_Deposito_ComSucesso() {
		// cen??rio
		DepositoESaqueConta dados = new DepositoESaqueConta(c1.cp.getNumeroDaConta(), "123", "100");
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.DEPOSITO, c1.cp, new BigDecimal(dados.valor()));
		DetalhamentoTransacao detalhamento = new DetalhamentoTransacao(transacao);
		when(contaService.efetuarDeposito(dados)).thenReturn(detalhamento);

		// a????o
		ResponseEntity<DetalhamentoTransacao> http = controller.deposito(dados);

		// verifica????o
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(http.getBody()).isEqualTo(detalhamento);
	}
	
	@Test
	@DisplayName("Efetuar saque com sucesso.")
	void saque_Deposito_ComSucesso() {
		// cen??rio
		DepositoESaqueConta dados = new DepositoESaqueConta(c1.cp.getNumeroDaConta(), "123", "100");
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.SAQUE, c1.cp, new BigDecimal(dados.valor()));
		DetalhamentoTransacao detalhamento = new DetalhamentoTransacao(transacao);
		when(contaService.efetuarSaque(dados)).thenReturn(detalhamento);
		
		// a????o
		ResponseEntity<DetalhamentoTransacao> http = controller.saque(dados);
		
		// verifica????o
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
	}
	
	@Test
	@DisplayName("Trocar senha da conta.")
	void trocarSenha_TrocarSenhaDaConta_ComSucesso() {
		// cen??rio
		AtualizarConta dadosAtualizarConta = c1.dadosAtualizarConta;
		DetalhaConta detalhamento = c1.detalhamentoConta;
		when(contaService.trocarSenha(dadosAtualizarConta)).thenReturn(detalhamento);
		
		// a????o
		ResponseEntity<DetalhaConta> http = controller.trocarSenha(dadosAtualizarConta);
		
		// verifica????o
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
		
	}
	
	@Test
	@DisplayName("Exibir extrato banc??rio.")
	void exibirExtrato_ExibirExtratoBancario_ComSucesso() {
		// cen??rio
		NumeroESenhaConta dados = c1.numeroESenhaConta;
		List<DetalhamentoTransacao> lista = new ArrayList<>();
		when(contaService.exibirExtrato(dados)).thenReturn(lista);
		
		// a????o
		ResponseEntity<List<DetalhamentoTransacao>> http = controller.exibirExtrato(dados);
		
		// verifica????o
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(lista);
		
	}
	
	@Test
	@DisplayName("Cadastrar chave pix com sucesso.")
	void cadastrarChavePix_CadastrarChavePix_ComSucesso() {
		// cen??rio
		CadastroChavePix dados = new CadastroChavePix(c1.cp.getNumeroDaConta(), "123", "chave");
		DetalhamentoChavesPix detalhamento = new DetalhamentoChavesPix(new ChavePixDTO(c1.cp,"chave"));
		when(contaService.cadastrarChavePix(dados)).thenReturn(detalhamento);
		
		// a????o
		ResponseEntity<DetalhamentoChavesPix> http = controller.cadastrarChavePix(dados);
		
		// verifica????o
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
		
	}
	
	@Test
	@DisplayName("Exibir chave pix com sucesso.")
	void exibirChavesPix_ExibirChavesPix_ComSucesso() {
		// cen??rio
		NumeroESenhaConta dados = c1.numeroESenhaConta;
		List<DetalhamentoChavesPix> lista = new ArrayList<>();
		when(contaService.exibirChavesPix(dados)).thenReturn(lista);
		
		// a????o
		ResponseEntity<List<DetalhamentoChavesPix>> http = controller.exibirChavesPix(dados);
		
		// verifica????o
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(lista);
		
	}
	
	@Test
	@DisplayName("Enviar pix com sucesso.")
	void enviarPix_EnviarPix_ComSucesso() {
		// cen??rio
		DadosPix dados = new DadosPix(c1.cp.getNumeroDaConta(), c2.cp.getNumeroDaConta(), "123", "100");
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PIX_ENVIADO, c1.cp, c2.cp, new BigDecimal(dados.valorPix()));
		DetalhamentoTransacao detalhamento = new DetalhamentoTransacao(transacao);
		when(contaService.enviarPix(dados)).thenReturn(detalhamento);
		
		// a????o
		ResponseEntity<DetalhamentoTransacao> http = controller.enviarPix(dados);
		
		// verifica????o
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
		
	}
	
	@Test
	@DisplayName("Detalhar conta com sucesso.")
	void detalhar_DetalharConta_ComSucesso() {
		// cen??rio
		Long id = c1.cp.getNumeroDaConta();
		DetalhaConta detalhamento = new DetalhaConta(c1.cp);
		when(contaService.consultaByNumeroDaConta(id)).thenReturn(detalhamento);
		
		// a????o
		ResponseEntity<DetalhaConta> http = controller.detalhar(id);
		
		// verifica????o
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
		
	}
	
}
