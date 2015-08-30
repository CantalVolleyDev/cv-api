package com.jtouzy.cv.api.security;

import javax.ws.rs.core.SecurityContext;

public class RequestSecurityContext implements SecurityContext {
	private Client clientInfos;
	
	public RequestSecurityContext() {
	}
	
	public RequestSecurityContext(Client client) {
		this.clientInfos = client;
	}
	
	@Override
	public Client getUserPrincipal() {
		return clientInfos;
	}

	@Override
	public boolean isUserInRole(String role) {
		if (role.equals("admin") && clientInfos != null && clientInfos.getName().equals("JTO")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public String getAuthenticationScheme() {
		return null;
	}	
}
