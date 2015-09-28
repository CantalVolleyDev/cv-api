package com.jtouzy.cv.api.resources.data.min;

import javax.ws.rs.core.Response;

public interface DataMinimizer<T> {
	public Response minimize(T object);
}
