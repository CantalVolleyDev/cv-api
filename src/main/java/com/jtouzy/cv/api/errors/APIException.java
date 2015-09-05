package com.jtouzy.cv.api.errors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class APIException extends WebApplicationException {
	private static final long serialVersionUID = 1L;
	
	public APIException(Response.Status status, Throwable cause) {
		this(status, new ExceptionDescriptor(cause));
	}
	
	public APIException(Response.Status status, String message) {
		this(status, new ExceptionDescriptor(message));
	}
	
	public APIException(Response.Status status, String message, Throwable cause) {
		this(status, new ExceptionDescriptor(cause, message));
	}
	
	private APIException(Response.Status status, ExceptionDescriptor descriptor) {
		super(descriptor.getCause(), Response.status(status)
				                             .type(MediaType.APPLICATION_JSON)
			                                 .entity(descriptor)
			                                 .build());
	}
}
