package com.jtouzy.cv.api.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.jtouzy.cv.api.errors.mappers.APIExceptionMapper;
import com.jtouzy.cv.api.errors.mappers.BeanValidationExceptionMapper;
import com.jtouzy.cv.api.filters.AuthFilter;
import com.jtouzy.cv.api.filters.OptionMethodFilter;
import com.jtouzy.cv.api.filters.ResponseFilter;
import com.jtouzy.cv.api.filters.SecurityInitializationFilter;
import com.jtouzy.cv.api.json.GensonProvider;
import com.jtouzy.cv.api.lifecycle.APIEventListener;

public class APIResourceConfig extends ResourceConfig {
	public APIResourceConfig() {
		// Fonctionnalités
		register(RolesAllowedDynamicFeature.class);
		register(GensonProvider.class);
		// Ecouteurs
		register(APIEventListener.class);
		// Filtres
		register(SecurityInitializationFilter.class);
		register(OptionMethodFilter.class);
		register(AuthFilter.class);
		register(ResponseFilter.class);
		// Transformations d'exceptions
		register(APIExceptionMapper.class);
		register(BeanValidationExceptionMapper.class);
	}
}
