package com.jtouzy.cv.api.errors.mappers;

import javax.ws.rs.NotAuthorizedException;
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
			if (exception instanceof NotAuthorizedException) {
				return Response.status(Response.Status.UNAUTHORIZED)
						       .type(MediaType.APPLICATION_JSON)
						       .entity(new ExceptionDescriptor(exception, build401Message(exception)))
						       .build();
			}
			return ((WebApplicationException)exception).getResponse();
		} else {
			return Response.status(500)
					       .type(MediaType.APPLICATION_JSON)
					       .entity(new ExceptionDescriptor(exception))
					       .build();
		}
	}
	
	private String build401Message(Exception exception) {
		if (exception.getMessage().startsWith("HTTP"))
			return "Vous devez être connecté pour accéder à cette page";
		return exception.getMessage();
	}
}
