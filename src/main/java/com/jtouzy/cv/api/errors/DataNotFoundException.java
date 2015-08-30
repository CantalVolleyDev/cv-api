package com.jtouzy.cv.api.errors;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class DataNotFoundException extends LogicException {
	private static final long serialVersionUID = 1L;

	public DataNotFoundException(Status status, String message) {
		super(Response.Status.NOT_FOUND, message);
	}
}
