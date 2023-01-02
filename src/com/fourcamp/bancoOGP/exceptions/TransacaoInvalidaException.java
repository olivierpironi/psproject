package exceptions;

import interfaces.BOGPExceptions;

@SuppressWarnings("serial")
public class TransacaoInvalidaException extends RuntimeException implements BOGPExceptions{

	public TransacaoInvalidaException(String message) {
		super(message);
	}

	@Override
	public void msg() {
		System.out.println(this.getMessage());
		
	}

}
