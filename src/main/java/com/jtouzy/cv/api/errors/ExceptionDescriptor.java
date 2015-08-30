package com.jtouzy.cv.api.errors;

public class ExceptionDescriptor {
	private Throwable cause;
	private String message;
	
	public ExceptionDescriptor(Throwable cause, String message) {
		super();
		this.cause = cause;
		this.message = message;
	}

	public ExceptionDescriptor(Throwable cause) {
		this(cause, null);
	}

	public ExceptionDescriptor(String message) {
		this(null, message);
	}

	public Throwable getCause() {
		return cause;
	}

	public String getMessage() {
		return message;
	}
}
