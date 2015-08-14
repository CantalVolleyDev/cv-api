package com.jtouzy.cvapi.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ConnectionDAOMapping {
	private Connection connection;
	private List<DAO<?>> daoList;
	
	public ConnectionDAOMapping(Connection connection) {
		this.daoList = new ArrayList<>();
		this.connection = connection;
	}
	
	public ConnectionDAOMapping add(DAO<?> dao) {
		daoList.add(dao);
		return this;
	}
}
