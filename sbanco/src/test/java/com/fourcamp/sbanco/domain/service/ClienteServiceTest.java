package com.fourcamp.sbanco.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fourcamp.sbanco.domain.dto.cliente.CadastroCliente;
import com.fourcamp.sbanco.domain.dto.cliente.ClienteDTO;
import com.fourcamp.sbanco.domain.dto.cliente.AtualizarCliente;
import com.fourcamp.sbanco.domain.dto.cliente.DetalhaCliente;
import com.fourcamp.sbanco.domain.repository.ClienteRepository;
import com.fourcamp.sbanco.infra.exceptions.ClienteNaoExisteException;
import com.fourcamp.sbanco.util.ContaFactory;

@ExtendWith(SpringExtension.class)
@DisplayName("Testes para o serviço de pessoas")
class ClienteServiceTest {
	
	@InjectMocks
	private ClienteService clienteService;
	@Mock
	private ClienteRepository clienteRepository;
	
	private static ContaFactory c1;
	private static ContaFactory c2;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		c1 = new ContaFactory().fabricarC1();
		c2 = new ContaFactory().fabricarC2();
	}

	@Test
	@DisplayName("Salvar cliente com sucesso.")
	void salvar_SalvarCliente_ComSucesso() {
		// cenário
		CadastroCliente dados = c1.cadastroCliente;
		ClienteDTO clienteEsperado = c1.cliente;
		when(clienteRepository.save(new ClienteDTO(dados))).thenReturn(clienteEsperado);
		
		// ação
		ClienteDTO cliente = clienteService.salvar(dados);

		//verificação
		assertThat(cliente).isNotNull();
				
		assertThat(cliente.getCpf()).isEqualTo(clienteEsperado.getCpf());
	}
	
	@Test
	@DisplayName("Buscar cliente pelo CPF com sucesso.")
	void getByCpf_BuscarClientePeloCpf_ComSucesso() {
		// cenário
		ClienteDTO clienteEsperado = c1.cliente;
		String cpf = clienteEsperado.getCpf();
		when(clienteRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(clienteEsperado));
		
		// ação
		ClienteDTO cliente = clienteService.getByCpf(cpf);
		
		//verificação
		assertThat(cliente).isNotNull();
		
		assertThat(cliente.getCpf()).isEqualTo(clienteEsperado.getCpf());
	}
	
	@Test
	@DisplayName("Buscar cliente pelo CPF, cliente não cadastrado.")
	void getByCpf_BuscarClientePeloCpf_ClienteNaoCadastrado() {
		// cenário
		ClienteDTO clienteEsperado = c1.cliente;
		String cpf = clienteEsperado.getCpf();
		when(clienteRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		
		 Throwable e = catchThrowable(() -> clienteService.getByCpf(cpf));
		
		//verificação
		 assertThat(e).isInstanceOf(ClienteNaoExisteException.class).hasMessageContaining("CPF não cadastrado.");
	}
	
	@Test
	@DisplayName("Consultar cliente pelo CPF com sucesso.")
	void consultaClienteByCPF_ConsultarClientePeloCpf_ComSucesso() {
		// cenário
		ClienteDTO clienteEsperado = c1.cliente;
		String cpf = clienteEsperado.getCpf();
		DetalhaCliente esperado = new DetalhaCliente(clienteEsperado);
		when(clienteRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(clienteEsperado));
		
		// ação
		DetalhaCliente retorno = clienteService.consultaClienteByCPF(cpf);
		
		//verificação
		assertThat(retorno).isEqualTo(esperado);
	}
	
	@Test
	@DisplayName("Consultar cliente pelo CPF, cliente não cadastrado.")
	void consultaClienteByCPF_ConsultarClientePeloCpf_ClienteNaoCadastrado() {
		// cenário
		ClienteDTO clienteEsperado = c1.cliente;
		String cpf = clienteEsperado.getCpf();
		when(clienteRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> clienteService.consultaClienteByCPF(cpf));
		
		//verificação
		assertThat(e).isInstanceOf(ClienteNaoExisteException.class).hasMessageContaining("CPF não cadastrado.");
	}
	
	@Test
	@DisplayName("Atualizar informações de cadastro com sucesso.")
	void atualizarInformacoes_AtualizarInformacoes_ComSucesso() {
		// cenário
		AtualizarCliente atualizarCliente = c1.atualizarCliente;
		DetalhaCliente esperado = c2.detalhamentoCliente;
		when(clienteRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cliente));
		
		// ação
		DetalhaCliente retorno = clienteService.atualizarInformacoes(atualizarCliente);
		
		//verificação
		assertThat(retorno).isEqualTo(esperado);
	}
	
	@Test
	@DisplayName("Atualizar informações de cadastro, cliente não cadastrado.")
	void atualizarInformacoes_AtualizarInformacoes_ClienteNaoCadastrado() {
		// cenário
		AtualizarCliente atualizarCliente = c1.atualizarCliente;
		when(clienteRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
		
		// ação
		Throwable e = catchThrowable(() -> clienteService.atualizarInformacoes(atualizarCliente));
		
		//verificação
		assertThat(e).isInstanceOf(ClienteNaoExisteException.class).hasMessageContaining("CPF não cadastrado.");
	}
	
	@Test
	@DisplayName("Atualizar informações de cadastro, nenhuma alteração.")
	void atualizarInformacoes_AtualizarInformacoes_NenhumaAlteração() {
		// cenário
		AtualizarCliente atualizarCliente = new AtualizarCliente(null, null, null, null, null);
		DetalhaCliente esperado = c1.detalhamentoCliente;
		when(clienteRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(c1.cliente));
		
		// ação
		DetalhaCliente retorno = clienteService.atualizarInformacoes(atualizarCliente);
		
		//verificação
		assertThat(retorno).isEqualTo(esperado);
	}

}
