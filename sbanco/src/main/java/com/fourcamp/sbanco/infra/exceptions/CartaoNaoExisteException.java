package com.fourcamp.sbanco.infra.exceptions;

@SuppressWarnings("serial")
public class CartaoNaoExisteException extends BOGPExceptions{

	public CartaoNaoExisteException(String message) {
		super(message);
	}

}
