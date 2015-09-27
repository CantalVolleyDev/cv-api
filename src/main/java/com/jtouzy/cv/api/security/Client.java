package com.jtouzy.cv.api.security;

import java.security.Principal;

import javax.ws.rs.core.NewCookie;

import com.jtouzy.cv.model.classes.User;

public class Client implements Principal {
	public static final String AUTHENTIFICATION_COOKIE_NAME = "X-CvAuth";
	public static final String AUTHENTIFICATION_VALIDATION_COOKIE_NAME = "X-CvAuthValidation";
	
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
	
	public static NewCookie createAuthCookie() {
		return createAuthCookie(null);
	}
	
	public static NewCookie createAuthCookie(User user) {
		return new NewCookie(Client.AUTHENTIFICATION_COOKIE_NAME, 
				             user == null ? "deleted" : TokenHelper.getUserToken(user), 
				             "/", "", "", 
				             user == null ? 0 : NewCookie.DEFAULT_MAX_AGE, false, true);
	}
	
	public static NewCookie createAuthValidationCookie() {
		return createAuthValidationCookie(null);
	}
	
	public static NewCookie createAuthValidationCookie(User user) {
		return new NewCookie(Client.AUTHENTIFICATION_VALIDATION_COOKIE_NAME, 
	                         user == null ? "deleted" : TokenHelper.getUserToken(user), 
	                         "/", "", "", 
	                         user == null ? 0 : NewCookie.DEFAULT_MAX_AGE, false, true);		
	}
}
