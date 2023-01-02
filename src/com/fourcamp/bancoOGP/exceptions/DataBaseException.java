package exceptions;

import interfaces.BOGPExceptions;

@SuppressWarnings("serial")
public class DataBaseException extends RuntimeException implements BOGPExceptions{

	public DataBaseException(String message) {
		super(message);
	}

	@Override
	public void msg() {
		System.out.println(this.getMessage());
		
	}

}
