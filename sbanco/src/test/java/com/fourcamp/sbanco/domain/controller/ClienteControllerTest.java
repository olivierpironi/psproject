package com.fourcamp.sbanco.domain.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
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

import com.fourcamp.sbanco.domain.dto.cliente.AtualizarCliente;
import com.fourcamp.sbanco.domain.dto.cliente.DetalhaCliente;
import com.fourcamp.sbanco.domain.enums.EnumCliente;
import com.fourcamp.sbanco.domain.service.ClienteService;
import com.fourcamp.sbanco.infra.exceptions.ClienteNaoExisteException;
import com.fourcamp.sbanco.util.ContaFactory;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Teste para o controller de clientes.")
class ClienteControllerTest {

	@InjectMocks
	private ClienteController controller;
	@Mock
	private static ClienteService clienteService;
	private static ContaFactory c1;
	private static ContaFactory c2;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		c1 = new ContaFactory().fabricarC1();
		c2 = new ContaFactory().fabricarC2();
	}

	@Test
	@DisplayName("Detalhar cliente.")
	void detalhar_DetalharCliente_ComSucesso() {
		// cenário
		String cpf = c1.cliente.getCpf();
		DetalhaCliente detalhamento = c1.detalhamentoCliente;
		when(clienteService.consultaClienteByCPF(cpf)).thenReturn(detalhamento);

		// ação
		ResponseEntity<DetalhaCliente> http = controller.detalhar(cpf);

		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(http.getBody()).isEqualTo(detalhamento);
	}

	@Test
	@DisplayName("Detalhar cliente, CPF solicitado não cadastrado.")
	void detalhar_DetalharCliente_CpfNaoCadastrado() {
		// cenário
		when(clienteService.consultaClienteByCPF("123")).thenThrow(new ClienteNaoExisteException("CPF não cadastrado."));

		// ação
		Throwable e = Assertions.catchThrowable(() -> controller.detalhar("123"));

		// verificação
		assertThat(e).isInstanceOf(ClienteNaoExisteException.class).hasMessage("CPF não cadastrado.");
	}

	@Test
	@DisplayName("Atualizar cliente com sucesso.")
	void atualizarDados_AtualizarCliente_ComSucesso() {
		// cenário
		AtualizarCliente dadosAtualizar = c1.atualizarCliente;
		DetalhaCliente detalhamento = c2.detalhamentoCliente;
		when(clienteService.atualizarInformacoes(dadosAtualizar)).thenReturn(detalhamento);

		// ação
		ResponseEntity<DetalhaCliente> http = controller.atualizarDados(dadosAtualizar);

		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(http.getBody()).isEqualTo(detalhamento);
	}

	@Test
	@DisplayName("Atualizar cliente, CPF solicitado não cadastrado.")
	void atualizarDados_AtualizarCliente_CpfNaoExiste() {
		// cenário
		AtualizarCliente dadosAtualizar = new AtualizarCliente("Ana", "123", "02/02/1900", "endereco",
				EnumCliente.PREMIUM);
		when(clienteService.atualizarInformacoes(dadosAtualizar)).thenThrow(new ClienteNaoExisteException("CPF não cadastrado."));

		// ação
		Throwable e = Assertions.catchThrowable(() -> controller.atualizarDados(dadosAtualizar));

		// verificação
		assertThat(e).isInstanceOf(ClienteNaoExisteException.class).hasMessage("CPF não cadastrado.");
	}

}
