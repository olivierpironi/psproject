package com.fourcamp.sbanco.infra.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fourcamp.sbanco.domain.dto.cliente.CadastroCliente;
import com.fourcamp.sbanco.domain.dto.cliente.ClienteDTO;
import com.fourcamp.sbanco.domain.dto.cliente.DetalhaCliente;
import com.fourcamp.sbanco.domain.dto.conta.CadastroConta;
import com.fourcamp.sbanco.domain.dto.conta.ContaDTO;
import com.fourcamp.sbanco.domain.dto.conta.DetalhaConta;
import com.fourcamp.sbanco.domain.service.ClienteService;
import com.fourcamp.sbanco.domain.service.ContaService;
import com.fourcamp.sbanco.infra.security.DadosAutenticacao;
import com.fourcamp.sbanco.infra.security.DadosToken;
import com.fourcamp.sbanco.infra.security.TokenService;
import com.fourcamp.sbanco.util.ContaFactory;
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Teste para o controller de autenticação.")
class AutenticacaoControllerTest {

		@InjectMocks
		private AutenticacaoController controller;
		@Mock
		private AuthenticationManager manager;
		@Mock
		private TokenService tokenService;
		@Mock
		private ClienteService clienteService;
		@Mock
		private ContaService contaService;
		
		private static ContaFactory c1;

		@BeforeEach
		public void setup() {
			MockitoAnnotations.openMocks(this);
			c1 = new ContaFactory().fabricarC1();
		}
		
		@Test
		@DisplayName("Efetuar login.")
		void efetuarLogin_EfetuarLogin_ComSucesso() {
			// cenário
			DadosAutenticacao dados = new DadosAutenticacao("usuario", "senha");
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
			ContaDTO conta = c1.cp;
			String tokenJWT = "tokenJWT";
			
			var authentication = mock(Authentication.class);
			when(manager.authenticate(token)).thenReturn(authentication);
			when(authentication.getPrincipal()).thenReturn(conta);
			when(tokenService.gerarToken(conta)).thenReturn(tokenJWT);
			
			// ação
			ResponseEntity<DadosToken> http = controller.efetuarLogin(dados);
			
			// verificação
			assertThat(http.getStatusCode()).isEqualTo(HttpStatus.OK);
			
			assertThat(http.getBody()).isEqualTo(new DadosToken(tokenJWT));
		}
		
		@Test
		@DisplayName("Cadastrar conta poupança com sucesso.")
		void cadastrarCP_CadastrarCP_ComSucesso() {
			// cenário
			CadastroConta dados = c1.cadastroConta;
			c1.cp.setNumeroDaConta(1l);
			DetalhaConta detalhamento = new DetalhaConta(c1.cp);
			when(contaService.cadastrarCP(dados)).thenReturn(c1.cp);
			
			// ação
			ResponseEntity<DetalhaConta> http = controller.cadastrarCP(dados);
			
			// verificação
			assertThat(http.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			
			assertThat(http.getBody()).isEqualTo(detalhamento);
			
			assertThat(http.getHeaders().getLocation().toString()).hasToString("/conta/"+c1.cp.getNumeroDaConta());
		}
		
		@Test
		@DisplayName("Cadastrar conta corrente com sucesso.")
		void cadastrarCC_CadastrarCC_ComSucesso() {
			// cenário
			CadastroConta dados = c1.cadastroConta;
			c1.cc.setNumeroDaConta(1l);
			DetalhaConta detalhamento = new DetalhaConta(c1.cc);
			when(contaService.cadastrarCC(dados)).thenReturn(c1.cc);
			
			// ação
			ResponseEntity<DetalhaConta> http = controller.cadastrarCC(dados);
			
			// verificação
			assertThat(http.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			
			assertThat(http.getBody()).isEqualTo(detalhamento);
			
			assertThat(http.getHeaders().getLocation().toString()).hasToString("/conta/"+c1.cc.getNumeroDaConta());
		}
		
		@Test
		@DisplayName("Cadastrar cliente com sucesso.")
		void cadastrar_CadastrarCliente_ComSucesso() {
			// cenário
			CadastroCliente dados = c1.cadastroCliente;
			ClienteDTO cliente = c1.cliente;
			DetalhaCliente detalhamento = new DetalhaCliente(cliente);
			when(clienteService.salvar(dados)).thenReturn(cliente);
			
			// ação
			ResponseEntity<DetalhaCliente> http = controller.cadastrar(dados);
			
			// verificação
			assertThat(http.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			
			assertThat(http.getBody()).isEqualTo(detalhamento);
			
			assertThat(http.getHeaders().getLocation().toString()).hasToString("/cliente/"+cliente.getCpf());
		}

}
