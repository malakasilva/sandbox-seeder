package org.absi.sandbox.download;

public class DownloadException extends Exception {

	public DownloadException (String massage) {
		super(massage);
	}
	
	public DownloadException (String massage, Throwable t) {
		super(massage, t);
	}
	
}