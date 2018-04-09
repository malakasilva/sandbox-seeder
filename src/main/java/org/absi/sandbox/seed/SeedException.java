package org.absi.sandbox.seed;


public class SeedException extends Exception {

	public SeedException (String massage) {
		super(massage);
	}
	
	public SeedException (String massage, Throwable t) {
		super(massage, t);
	}
	
}