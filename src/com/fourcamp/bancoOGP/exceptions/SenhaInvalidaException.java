package exceptions;

import interfaces.BOGPExceptions;

@SuppressWarnings("serial")
public class SenhaInvalidaException extends RuntimeException implements BOGPExceptions {
	
	public SenhaInvalidaException(String message) {
		super(message);
	}

	@Override
	public void msg() {
		System.out.println(this.getMessage());

	}

}
