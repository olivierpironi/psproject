package com.fourcamp.sbanco.infra.exceptions;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<List<DetalhamentoErros>> tratarValidacao(MethodArgumentNotValidException e) {
		var erros = e.getFieldErrors();
		return ResponseEntity.badRequest().body(erros.stream().map(DetalhamentoErros::new).toList());
	}

	@ExceptionHandler(BOGPExceptions.class)
	public ResponseEntity<DetalhaBOGPException> tratarBOGPExceptions(BOGPExceptions e) {
		return ResponseEntity.badRequest().body(new DetalhaBOGPException(e));
	}

}
