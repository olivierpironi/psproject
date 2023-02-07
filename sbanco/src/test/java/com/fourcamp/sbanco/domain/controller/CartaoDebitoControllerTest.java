package com.fourcamp.sbanco.domain.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
import com.fourcamp.sbanco.domain.dto.cartao.PagarComCartao;
import com.fourcamp.sbanco.domain.dto.cartaodebito.CadastroCartaoDebito;
import com.fourcamp.sbanco.domain.dto.cartaodebito.CartaoDebitoDTO;
import com.fourcamp.sbanco.domain.dto.cartaodebito.DetalhaCartaoDebito;
import com.fourcamp.sbanco.domain.dto.transacao.DetalhamentoTransacao;
import com.fourcamp.sbanco.domain.service.CartaoDebitoService;
import com.fourcamp.sbanco.util.ContaFactory;

@ExtendWith(SpringExtension.class)
@DisplayName("Teste para o controller de cartoes de débito.")
class CartaoDebitoControllerTest {

	@InjectMocks
	private CartaoDebitoController controller;
	@Mock
	private static CartaoDebitoService cartaoService;
	private static ContaFactory c1;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		c1 = new ContaFactory().fabricarC1();
	}
	
	@Test
	@DisplayName("Emitir cartao de débito com sucesso.")
	void emitirCartaoDeDebito_EmitirCartaoDebito_ComSucesso() {
		// cenário
		CadastroCartaoDebito dados = new CadastroCartaoDebito(c1.cc.getNumeroDaConta(), "123", 456);
		CartaoDebitoDTO cartao = c1.cartaoDebito;
		DetalhaCartaoDebito detalhamento = new DetalhaCartaoDebito(cartao);
		when(cartaoService.emitirCartaoDeDebito(dados)).thenReturn(cartao);
		
		// ação
		ResponseEntity<DetalhaCartaoDebito> http = controller.emitirCartaoDeDebito(dados);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
	}

	@Test
	@DisplayName("Desbloquear cartao de débito com sucesso.")
	void desbloquear_DesbloquearCartaoDebito_ComSucesso() {
		// cenário
		AtualizarCartao dados = c1.atualizarCartao;
		c1.cartaoDebito.setBloqueado(false);
		DetalhaCartaoDebito detalhamento = new DetalhaCartaoDebito(c1.cartaoDebito);
		when(cartaoService.desbloquearCartao(dados)).thenReturn(detalhamento);

		// ação
		ResponseEntity<DetalhaCartaoDebito> http = controller.desbloquear(dados);

		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(http.getBody()).isEqualTo(detalhamento);
	}
	
	@Test
	@DisplayName("Bloquear cartao de débito com sucesso.")
	void bloquear_BloquearCartaoDebito_ComSucesso() {
		// cenário
		AtualizarCartao dados = c1.atualizarCartao;
		c1.cartaoDebito.setBloqueado(true);
		DetalhaCartaoDebito detalhamento = new DetalhaCartaoDebito(c1.cartaoDebito);
		when(cartaoService.bloquearCartao(dados)).thenReturn(detalhamento);
		
		// ação
		ResponseEntity<DetalhaCartaoDebito> http = controller.bloquear(dados);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
	}
	
	@Test
	@DisplayName("Detalhar cartao de débito com sucesso.")
	void detalhar_DetalharCartaoDebito_ComSucesso() {
		// cenário
		CartaoDebitoDTO cartao = c1.cartaoDebito;
		DetalhaCartaoDebito detalhamento = new DetalhaCartaoDebito(cartao);
		when(cartaoService.getById(cartao.getNumero())).thenReturn(detalhamento);
		
		// ação
		ResponseEntity<DetalhaCartaoDebito> http = controller.detalhar(cartao.getNumero());
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
	}
	
	@Test
	@DisplayName("Renovar limite diário cartao de débito com sucesso.")
	void renovarLimite_RenovarLimiteDiarioCartaoDebito_ComSucesso() {
		// cenário
		CartaoDebitoDTO cartao = c1.cartaoDebito;
		DetalhaCartaoDebito detalhamento = new DetalhaCartaoDebito(cartao);
		when(cartaoService.renovarLimite(cartao.getNumero())).thenReturn(detalhamento);
		
		// ação
		ResponseEntity<DetalhaCartaoDebito> http = controller.renovarLimite(cartao.getNumero());
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(detalhamento);
	}
	
	@Test
	@DisplayName("Pagar com cartao de débito com sucesso.")
	void pagarComDebito_PagarComCartaoDebito_ComSucesso() {
		// cenário
		PagarComCartao dados = new PagarComCartao(c1.cartaoDebito.getNumero(), c1.cc.getNumeroDaConta(), "100", 456);
		List<DetalhamentoTransacao> lista = new ArrayList<>();
		when(cartaoService.pagarComDebito(dados)).thenReturn(lista);
		
		// ação
		ResponseEntity<List<DetalhamentoTransacao>> http = controller.pagarComDebito(dados);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(http.getBody()).isEqualTo(lista);
	}
	
}
