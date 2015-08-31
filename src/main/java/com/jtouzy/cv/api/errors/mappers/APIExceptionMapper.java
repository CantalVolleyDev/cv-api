package com.jtouzy.cv.api.errors.mappers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.jtouzy.cv.api.errors.ExceptionDescriptor;

@Provider
public class APIExceptionMapper implements ExceptionMapper<Exception> {
	@Override
	public Response toResponse(Exception exception) {
		if (exception instanceof WebApplicationException) {
			return ((WebApplicationException)exception).getResponse();
		} else {
			return Response.status(500)
					       .type(MediaType.APPLICATION_JSON)
					       .entity(new ExceptionDescriptor(exception))
					       .build();
		}
	}
}
