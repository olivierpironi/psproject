package com.fourcamp.sbanco.infra.exceptions;

@SuppressWarnings("serial")
public class ContaNaoExisteException extends BOGPExceptions{

	public ContaNaoExisteException(String message) {
		super(message);
	}

}
