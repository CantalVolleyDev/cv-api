package com.jtouzy.cv.api.security;

import java.security.Principal;

import com.jtouzy.cv.model.classes.User;

public class Client implements Principal {
	public static final String AUTHENTIFICATION_COOKIE_NAME = "X-CvAuth";
	private User user;
	
	public Client(User user) {
		this.user = user;
	}
	
	@Override
	public String getName() {
		return this.user.getFirstName() + this.user.getName();
	}
	
	public User getUser() {
		return this.user;
	}
}
