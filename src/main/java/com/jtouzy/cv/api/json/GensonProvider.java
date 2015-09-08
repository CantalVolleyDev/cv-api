package com.jtouzy.cv.api.json;

import java.time.LocalDateTime;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.jtouzy.cv.model.classes.User;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

@Provider
public class GensonProvider implements ContextResolver<Genson> {
	private final Genson genson = 
			new GensonBuilder().withConverter(new DateConverter(), LocalDateTime.class)
			                   .exclude("password", User.class)
			                   .create();
	@Override
	public Genson getContext(Class<?> type) {
		return genson;
	}
}
