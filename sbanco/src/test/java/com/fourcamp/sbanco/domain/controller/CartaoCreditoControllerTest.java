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

import com.fourcamp.sbanco.domain.dto.cartao.AtualizarCartao;
import com.fourcamp.sbanco.domain.dto.cartao.NumeroESenhaCartao;
import com.fourcamp.sbanco.domain.dto.cartao.PagarComCartao;
import com.fourcamp.sbanco.domain.dto.cartaocredito.AtualizarCartaoCredito;
import com.fourcamp.sbanco.domain.dto.cartaocredito.CadastroCartaoCredito;
import com.fourcamp.sbanco.domain.dto.cartaocredito.CartaoCreditoDTO;
import com.fourcamp.sbanco.domain.dto.cartaocredito.DetalhaCartaoCredito;
import com.fourcamp.sbanco.domain.dto.fatura.PagarFatura;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.dto.transacao.TransacaoDTO;
import com.fourcamp.sbanco.domain.enums.EnumSeguroCredito;
import com.fourcamp.sbanco.domain.enums.EnumTransacao;
import com.fourcamp.sbanco.domain.service.CartaoCreditoService;
import com.fourcamp.sbanco.util.ContaFactory;

@ExtendWith(SpringExtension.class)
@DisplayName("Teste para o controller de cartoes de crédito.")
class CartaoCreditoControllerTest {

	@InjectMocks
	private CartaoCreditoController controller;
	@Mock
	private static CartaoCreditoService cartaoService;
	private static ContaFactory c1;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		c1 = new ContaFactory().fabricarC1();
	}
	
	@Test
	@DisplayName("Emitir cartao de crédito com sucesso.")
	void emitirCartaoDeCredito_EmitirCartaoCredito_ComSucesso() {
		// cenário
		CadastroCartaoCredito dados = new CadastroCartaoCredito(c1.cc.getNumeroDaConta(), 456,EnumSeguroCredito.CREDITO_SEGURO ,"123");
		CartaoCreditoDTO cartao = c1.cartaoCredito;
		DetalhaCartaoCredito detalhamento = new DetalhaCartaoCredito(cartao);
		when(cartaoService.emitirCartaoDeCredito(dados)).thenReturn(cartao);
		
		// ação
		ResponseEntity<DetalhaCartaoCredito> http = controller.emitirCartaoDeCredito(dados);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
	}

	@Test
	@DisplayName("Desbloquear cartao de crédito com sucesso.")
	void desbloquear_DesbloquearCartaoCredito_ComSucesso() {
		// cenário
		AtualizarCartao dados = c1.atualizarCartao;
		c1.cartaoDebito.setBloqueado(false);
		DetalhaCartaoCredito detalhamento = new DetalhaCartaoCredito(c1.cartaoCredito);
		when(cartaoService.desbloquearCartao(dados)).thenReturn(detalhamento);

		// ação
		ResponseEntity<DetalhaCartaoCredito> http = controller.desbloquear(dados);

		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(http.getBody()).isEqualTo(detalhamento);
	}
	
	@Test
	@DisplayName("Bloquear cartao de crédito com sucesso.")
	void bloquear_BloquearCartaoCredito_ComSucesso() {
		// cenário
		AtualizarCartao dados = c1.atualizarCartao;
		c1.cartaoDebito.setBloqueado(false);
		DetalhaCartaoCredito detalhamento = new DetalhaCartaoCredito(c1.cartaoCredito);
		when(cartaoService.bloquearCartao(dados)).thenReturn(detalhamento);
		
		// ação
		ResponseEntity<DetalhaCartaoCredito> http = controller.bloquear(dados);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
	}
	
	@Test
	@DisplayName("Detalhar cartao de crédito com sucesso.")
	void detalhar_DetalharCartaoCredito_ComSucesso() {
		// cenário
		CartaoCreditoDTO cartao = c1.cartaoCredito;
		DetalhaCartaoCredito detalhamento = new DetalhaCartaoCredito(cartao);
		when(cartaoService.getById(cartao.getNumero())).thenReturn(detalhamento);
		
		// ação
		ResponseEntity<DetalhaCartaoCredito> http = controller.detalhar(cartao.getNumero());
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
	}
	
	@Test
	@DisplayName("Atualizar informações cartao de crédito com sucesso.")
	void atualizarCartao_AtualizarInformacoesCartaoCredito_ComSucesso() {
		// cenário
		CartaoCreditoDTO cartao = c1.cartaoCredito;
		AtualizarCartaoCredito dados = new AtualizarCartaoCredito(cartao.getNumero(), 456, EnumSeguroCredito.SEGURIDADE_TOTAL, "9999");
		DetalhaCartaoCredito detalhamento = new DetalhaCartaoCredito(cartao);
		when(cartaoService.atualizarCartao(dados)).thenReturn(detalhamento);
		
		// ação
		ResponseEntity<DetalhaCartaoCredito> http = controller.atualizarCartao(dados);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
	}
	
	@Test
	@DisplayName("Pagar com cartao de crédito com sucesso.")
	void pagarComCredito_PagarComCartaoCredito_ComSucesso() {
		// cenário
		PagarComCartao dados = new PagarComCartao(c1.cartaoDebito.getNumero(), c1.cc.getNumeroDaConta(), "100", 456);
		List<DetalhamentoTransacao> lista = new ArrayList<>();
		when(cartaoService.pagarComCredito(dados)).thenReturn(lista);
		
		// ação
		ResponseEntity<List<DetalhamentoTransacao>> http = controller.pagarComCredito(dados);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(lista);
	}
	
	@Test
	@DisplayName("Exibir fatura do cartão de crédito com sucesso.")
	void exibirFatura_ExibirFaturaCartaoCredito_ComSucesso() {
		// cenário
		NumeroESenhaCartao dados = new NumeroESenhaCartao(c1.cartaoCredito.getNumero(), 456);
		List<DetalhamentoTransacao> lista = new ArrayList<>();
		when(cartaoService.exibirFatura(dados)).thenReturn(lista);
		
		// ação
		ResponseEntity<List<DetalhamentoTransacao>> http = controller.exibirFatura(dados);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(lista);
	}
	
	@Test
	@DisplayName("Pagar fatura com saldo da conta corrente.")
	void pagarFaturaComSaldo_PagarFaturaComSaldoDaContaCorrente_ComSucesso() {
		// cenário
		PagarFatura dados = new PagarFatura(c1.cartaoCredito.getNumero(), "100", "123");
		TransacaoDTO transacao = new TransacaoDTO(EnumTransacao.PAGAMENTO_FATURA, c1.cc, new BigDecimal(dados.valorPagamento()));
		DetalhamentoTransacao detalhamento = new DetalhamentoTransacao(transacao);
		when(cartaoService.pagarFaturaComSaldo(dados)).thenReturn(detalhamento);
		
		// ação
		ResponseEntity<DetalhamentoTransacao> http = controller.pagarFaturaComSaldo(dados);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
	}

}
