package com.jtouzy.cvapi.dao.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DAOTableField {
	String fieldName();
}
