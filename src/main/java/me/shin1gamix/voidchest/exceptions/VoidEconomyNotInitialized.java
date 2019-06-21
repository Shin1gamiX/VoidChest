package me.shin1gamix.voidchest.exceptions;

public class VoidEconomyNotInitialized extends RuntimeException {

	private static final long serialVersionUID = 792063761470507101L;

	public VoidEconomyNotInitialized(String errorMessage) {
		super(errorMessage);
	}

	public VoidEconomyNotInitialized(String... errorMessage) {
		super(String.join(" ", errorMessage));
	}

	public VoidEconomyNotInitialized(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

}
