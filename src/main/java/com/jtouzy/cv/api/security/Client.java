package com.jtouzy.cv.api.security;

import java.security.Principal;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.NewCookie;

import com.jtouzy.cv.api.request.RequestUtils;
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
	
	public static NewCookie createAuthCookie(ContainerRequestContext requestContext) {
		return createAuthCookie(requestContext, null);
	}
	
	public static NewCookie createAuthCookie(ContainerRequestContext requestContext, User user) {
		return new NewCookie(Client.AUTHENTIFICATION_COOKIE_NAME, 
				             user == null ? "deleted" : TokenHelper.getUserToken(user), 
				             "/", RequestUtils.getDomainOnlyOriginHeader(requestContext), "", 
				             user == null ? 0 : NewCookie.DEFAULT_MAX_AGE, false, false);
	}
	
	public static NewCookie createAuthValidationCookie(ContainerRequestContext requestContext) {
		return createAuthValidationCookie(requestContext, null);
	}
	
	public static NewCookie createAuthValidationCookie(ContainerRequestContext requestContext, User user) {
		return new NewCookie(Client.AUTHENTIFICATION_VALIDATION_COOKIE_NAME, 
	                         user == null ? "deleted" : TokenHelper.getUserToken(user), 
	                         "/", RequestUtils.getDomainOnlyOriginHeader(requestContext), "",
	                         user == null ? 0 : NewCookie.DEFAULT_MAX_AGE, false, false);		
	}
}
