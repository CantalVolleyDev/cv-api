package com.jtouzy.cv.api.security;

import java.security.Principal;

public class Client implements Principal {
	public static final String AUTHENTIFICATION_COOKIE_NAME = "X-CvAuth";
	private String name;
	
	public Client(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
}
