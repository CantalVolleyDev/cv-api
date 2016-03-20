package com.jtouzy.cv.api.json;

import java.time.LocalDateTime;

import com.jtouzy.cv.model.classes.User;
import com.owlike.genson.GensonBuilder;

public class JsonMapper {
	public static final GensonBuilder getDefaultBuilder() {
		return new GensonBuilder()
				.withConverter(new DateConverter(), LocalDateTime.class)
	            .useBeanViews(true)
	            .exclude("password", User.class);
	}
}
