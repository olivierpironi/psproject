package com.fourcamp.sbanco.infra.exceptions;


@SuppressWarnings("serial")
public abstract class BOGPExceptions extends RuntimeException {

	
	public BOGPExceptions(String message) {
		super(message);
	}

}
