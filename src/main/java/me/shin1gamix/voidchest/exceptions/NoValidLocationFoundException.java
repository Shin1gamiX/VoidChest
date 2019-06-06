package me.shin1gamix.voidchest.exceptions;

public class NoValidLocationFoundException extends RuntimeException {

	private static final long serialVersionUID = 790672485710507101L;

	public NoValidLocationFoundException(String errorMessage) {
		super(errorMessage);
	}

	public NoValidLocationFoundException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

}
