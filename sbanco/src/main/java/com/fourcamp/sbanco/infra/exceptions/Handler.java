package com.fourcamp.sbanco.infra.exceptions;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class Handler {
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Object> tratarNoSuchElement() {
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> tratarValidacao(MethodArgumentNotValidException e) {
		var erros = e.getFieldErrors();
		return ResponseEntity.badRequest().body(erros.stream().map(DetalhamentoErros::new).toList());
	}

	@ExceptionHandler(BOGPExceptions.class)
	public ResponseEntity<Object> tratarToken(BOGPExceptions e) {
		return ResponseEntity.badRequest().body(new DetalhamentoBOGPException(e));
	}

	private record DetalhamentoErros(String field, String msg) {
		public DetalhamentoErros(FieldError erro) {
			this(erro.getField(), erro.getDefaultMessage());
		}

	}

	private record DetalhamentoBOGPException(String msg) {
		public DetalhamentoBOGPException(BOGPExceptions e) {
			this(e.getMessage());
		}
	}

}
