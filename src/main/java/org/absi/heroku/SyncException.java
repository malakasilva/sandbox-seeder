package org.absi.heroku;

public class SyncException extends Exception {

	public SyncException (String massage) {
		super(massage);
	}
	
	public SyncException (String massage, Throwable t) {
		super(massage, t);
	}
	
}
