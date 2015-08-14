package com.jtouzy.cvapi.dao;

import java.util.Map;

import com.jtouzy.cvapi.errors.DAOObjectInstanciationException;

public interface DAO<T> {
	public T create(T object);
	public T createFromValues(Map<String, Object> values) throws DAOObjectInstanciationException;
	public T update(T object);
	public void delete(T object);
	public T createOrUpdate(T object);
}
