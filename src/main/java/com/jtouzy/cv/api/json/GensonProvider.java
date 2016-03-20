package com.jtouzy.cv.api.json;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.owlike.genson.Genson;

@Provider
public class GensonProvider implements ContextResolver<Genson> {
	private final Genson genson = JsonMapper.getDefaultBuilder().create();
	
	@Override
	public Genson getContext(Class<?> type) {
		return genson;
	}
}
