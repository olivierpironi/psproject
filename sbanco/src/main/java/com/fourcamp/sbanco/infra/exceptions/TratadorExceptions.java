package com.fourcamp.sbanco.infra.exceptions;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorExceptions {
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Object> tratarNoSuchElement(){
		return ResponseEntity.notFound().build();
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> tratarValidacao(MethodArgumentNotValidException e){
		var erros = e.getFieldErrors();
		return ResponseEntity.badRequest().body(erros.stream().map(DetalhamentoErros::new).toList());
	}
	
	@ExceptionHandler(TokenInvalidoOuNuloException.class)
	public ResponseEntity<Object> tratarToken(TokenInvalidoOuNuloException e){
		System.err.println(e.getMessage());
		return ResponseEntity.badRequest().build();
	}//TODO NAO FUNCIONA
	
	private record DetalhamentoErros(String field, String msg) {
		public DetalhamentoErros(FieldError erro) {
			this(erro.getField(), erro.getDefaultMessage());
		}
	}

}
