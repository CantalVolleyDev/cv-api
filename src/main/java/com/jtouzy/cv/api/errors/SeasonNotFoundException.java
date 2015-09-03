package com.jtouzy.cv.api.errors;

import javax.ws.rs.core.Response;

public class SeasonNotFoundException extends LogicException {
	private static final long serialVersionUID = 1L;

	public SeasonNotFoundException() {
		super(Response.Status.NOT_FOUND, "Impossible de retrouver la saison demandée");
	}
}
