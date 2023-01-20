package com.fourcamp.sbanco.infra.exceptions;

@SuppressWarnings("serial")
public class TransacaoInvalidaException extends BOGPExceptions {

	public TransacaoInvalidaException(String message) {
		super(message);
	}

}
