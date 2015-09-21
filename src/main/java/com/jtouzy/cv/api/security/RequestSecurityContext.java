package com.jtouzy.cv.api.security;

import java.sql.Connection;
import java.sql.SQLException;

import javax.ws.rs.core.SecurityContext;

import com.jtouzy.cv.api.config.AppConfig;

public class RequestSecurityContext implements SecurityContext {
	private Client clientInfos;
	private Connection connection;
	
	public RequestSecurityContext() {
	}
	
	public RequestSecurityContext(Client client)
	throws SQLException {
		this.clientInfos = client;
		this.connection = AppConfig.getDataSource().getConnection();
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	@Override
	public Client getUserPrincipal() {
		return clientInfos;
	}

	@Override
	public boolean isUserInRole(String role) {
		if (clientInfos != null) {
			if (role.equals(Roles.CONNECTED))
				return true;
			else if (role.equals(Roles.ADMIN) && clientInfos.getUser().isAdministrator())
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
