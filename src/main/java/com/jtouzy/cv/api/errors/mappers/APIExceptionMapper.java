package com.jtouzy.cv.api.errors.mappers;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
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
			} else if (exception instanceof NotFoundException) {
				return Response.status(Response.Status.NOT_FOUND)
						       .type(MediaType.APPLICATION_JSON)
						       .entity(new ExceptionDescriptor(exception, build404Message(exception)))
						       .build();
			} else if (exception instanceof ForbiddenException) {
				return Response.status(Response.Status.FORBIDDEN)
					       	   .type(MediaType.APPLICATION_JSON)
					       	   .entity(new ExceptionDescriptor(exception, build401Message(exception)))
					       	   .build();
			} else if (exception instanceof BadRequestException) {
				return Response.status(Response.Status.BAD_REQUEST)
						       .type(MediaType.APPLICATION_JSON)
						       .entity(new ExceptionDescriptor(exception, build400Message(exception)))
						       .build();
			}
			return ((WebApplicationException)exception).getResponse();
		} else {
			return Response.status(500)
					       .type(MediaType.APPLICATION_JSON)
					       .entity(new ExceptionDescriptor(exception, exception.getMessage()))
					       .build();
		}
	}
	
	private String build401Message(Exception exception) {
		if (exception.getMessage().startsWith("HTTP"))
			return "Vous devez être connecté pour accéder à cette page";
		return exception.getMessage();
	}
	
	private String build404Message(Exception exception) {
		if (exception.getMessage().startsWith("HTTP"))
			return "La page que vous demandez n'existe pas";
		return exception.getMessage();
	}
	
	private String build400Message(Exception exception) {
		if (exception.getMessage().startsWith("HTTP"))
			return "La requête pour la page demandée est incorrecte";
		return exception.getMessage();
	}
}
