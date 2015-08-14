package com.jtouzy.cvapi.dao;

import java.util.HashSet;
import java.util.Set;

public class DAOCache<T> {
	private Class<T> daoClass;
	private Set<T> storage;
	
	public DAOCache(Class<T> daoClass) {
		this.storage = new HashSet<>();
		this.daoClass = daoClass;
	}
}
