package com.fourcamp.sbanco.infra.exceptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(SpringExtension.class)
@DisplayName("Testes para o handler")
class HandlerTest {

	@InjectMocks
	private Handler handler;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	@DisplayName("Tratar NoSuchElement.")
	void tratarNoSuchElement_TratarNoSuchElement_ComSucesso() {
		// cenário
		
		// ação
		ResponseEntity<Object> http = handler.tratarNoSuchElement();
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
	
	@Test
	@DisplayName("Tratar MethodArgumentNotValidException.")
	void tratarValidacao_TratarMethodArgumentNotValidException_ComSucesso() {
		// cenário
		MethodArgumentNotValidException e = mock(MethodArgumentNotValidException.class);
		List<DetalhamentoErros> lista = e.getFieldErrors().stream().map(DetalhamentoErros::new).toList();
		
		// ação
		ResponseEntity<List<DetalhamentoErros>> http = handler.tratarValidacao(e);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
		assertThat(http.getBody()).isEqualTo(lista);
	}
	
	@Test
	@DisplayName("Tratar CartaoNaoExisteException.")
	void tratarBOGPExceptions_TratarCartaoNaoExisteException_ComSucesso() {
		// cenário
		CartaoNaoExisteException e = mock(CartaoNaoExisteException.class);
		DetalhaBOGPException esperado = new DetalhaBOGPException(e);
		
		// ação
		ResponseEntity<DetalhaBOGPException> http = handler.tratarBOGPExceptions(e);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
		assertThat(http.getBody()).isEqualTo(esperado);
	}
	
	@Test
	@DisplayName("Tratar ClienteNaoExisteException.")
	void tratarBOGPExceptions_TratarClienteNaoExisteException_ComSucesso() {
		// cenário
		ClienteNaoExisteException e = mock(ClienteNaoExisteException.class);
		DetalhaBOGPException esperado = new DetalhaBOGPException(e);
		
		// ação
		ResponseEntity<DetalhaBOGPException> http = handler.tratarBOGPExceptions(e);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
		assertThat(http.getBody()).isEqualTo(esperado);
	}
	
	@Test
	@DisplayName("Tratar ContaNaoExisteException.")
	void tratarBOGPExceptions_TratarContaNaoExisteException_ComSucesso() {
		// cenário
		ContaNaoExisteException e = mock(ContaNaoExisteException.class);
		DetalhaBOGPException esperado = new DetalhaBOGPException(e);
		
		// ação
		ResponseEntity<DetalhaBOGPException> http = handler.tratarBOGPExceptions(e);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
		assertThat(http.getBody()).isEqualTo(esperado);
	}
	
	@Test
	@DisplayName("Tratar SenhaInvalidaException.")
	void tratarBOGPExceptions_TratarSenhaInvalidaException_ComSucesso() {
		// cenário
		SenhaInvalidaException e = mock(SenhaInvalidaException.class);
		DetalhaBOGPException esperado = new DetalhaBOGPException(e);
		
		// ação
		ResponseEntity<DetalhaBOGPException> http = handler.tratarBOGPExceptions(e);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
		assertThat(http.getBody()).isEqualTo(esperado);
	}
	
	@Test
	@DisplayName("Tratar TokenInvalidoOuNuloException.")
	void tratarBOGPExceptions_TratarTokenInvalidoOuNuloException_ComSucesso() {
		// cenário
		TokenInvalidoOuNuloException e = mock(TokenInvalidoOuNuloException.class);
		DetalhaBOGPException esperado = new DetalhaBOGPException(e);
		
		// ação
		ResponseEntity<DetalhaBOGPException> http = handler.tratarBOGPExceptions(e);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
		assertThat(http.getBody()).isEqualTo(esperado);
	}
	
	@Test
	@DisplayName("Tratar TransacaoInvalidaException.")
	void tratarBOGPExceptions_TratarTransacaoInvalidaException_ComSucesso() {
		// cenário
		TransacaoInvalidaException e = mock(TransacaoInvalidaException.class);
		DetalhaBOGPException esperado = new DetalhaBOGPException(e);
		
		// ação
		ResponseEntity<DetalhaBOGPException> http = handler.tratarBOGPExceptions(e);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
		assertThat(http.getBody()).isEqualTo(esperado);
	}
	
	@Test
	@DisplayName("Tratar CartaoBloqueadoException.")
	void tratarBOGPExceptions_TratarCartaoBloqueadoException_ComSucesso() {
		// cenário
		CartaoBloqueadoException e = mock(CartaoBloqueadoException.class);
		DetalhaBOGPException esperado = new DetalhaBOGPException(e);
		
		// ação
		ResponseEntity<DetalhaBOGPException> http = handler.tratarBOGPExceptions(e);
		
		// verificação
		assertThat(http.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
		assertThat(http.getBody()).isEqualTo(esperado);
	}

}
