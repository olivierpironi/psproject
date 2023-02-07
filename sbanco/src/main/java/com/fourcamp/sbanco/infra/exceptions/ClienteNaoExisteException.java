package com.fourcamp.sbanco.infra.exceptions;

@SuppressWarnings("serial")
public class ClienteNaoExisteException extends BOGPExceptions{

	public ClienteNaoExisteException(String message) {
		super(message);
	}
}
