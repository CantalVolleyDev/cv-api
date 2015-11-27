package com.jtouzy.cv.api.errors;

import javax.ws.rs.core.Response;

public class DataNotFoundException extends LogicException {
	private static final long serialVersionUID = 1L;

	public DataNotFoundException(String message) {
		super(Response.Status.NOT_FOUND, message);
	}
}
