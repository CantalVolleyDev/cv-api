package com.jtouzy.cv.api.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.jtouzy.cv.api.errors.mappers.APIExceptionMapper;
import com.jtouzy.cv.api.errors.mappers.BeanValidationExceptionMapper;
import com.jtouzy.cv.api.filters.AuthFilter;
import com.jtouzy.cv.api.lifecycle.APIEventListener;

public class APIResourceConfig extends ResourceConfig {
	public APIResourceConfig() {
		// Fonctionnalités
		register(RolesAllowedDynamicFeature.class);
		// Ecouteurs
		register(APIEventListener.class);
		// Filtres
		register(AuthFilter.class);
		// Transformations d'exceptions
		register(APIExceptionMapper.class);
		register(BeanValidationExceptionMapper.class);
	}
}
