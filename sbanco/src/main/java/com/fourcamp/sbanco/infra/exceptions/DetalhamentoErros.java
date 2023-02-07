package com.fourcamp.sbanco.infra.exceptions;

import org.springframework.validation.FieldError;

public record DetalhamentoErros(String field, String msg) {
	public DetalhamentoErros(FieldError erro) {
		this(erro.getField(), erro.getDefaultMessage());
	}

}