package com.fourcamp.bancoOGP.exceptions;


@SuppressWarnings("serial")
public abstract class BOGPExceptions extends RuntimeException {

	
	protected BOGPExceptions(String message) {
		super(message);
	}

	public void msg() {
		System.out.println(this.getMessage());

	}
}
