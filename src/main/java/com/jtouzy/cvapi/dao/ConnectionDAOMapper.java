package com.jtouzy.cvapi.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ConnectionDAOMapper {

	private List<ConnectionDAOMapping> mappings;
	
	public ConnectionDAOMapper() {
		mappings = new ArrayList<>();
	}
	
	public ConnectionDAOMapping register(Connection connection) {
		ConnectionDAOMapping mapping = new ConnectionDAOMapping(connection);
		mappings.add(mapping);
		return mapping;
	}
}
