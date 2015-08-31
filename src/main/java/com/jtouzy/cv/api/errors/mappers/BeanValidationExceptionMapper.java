package com.jtouzy.cv.api.errors.mappers;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.jtouzy.cv.api.errors.ExceptionDescriptor;

@Provider
public class BeanValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
	@Override
	public Response toResponse(ConstraintViolationException exception) {
		return Response.status(500)
					   .entity(new ExceptionDescriptor(exception))
				       .type(MediaType.APPLICATION_JSON)
				       .build();
	}
}
