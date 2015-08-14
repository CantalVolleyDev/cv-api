package com.jtouzy.cvapi.dao;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import com.jtouzy.cvapi.ReflectionInvoker;
import com.jtouzy.cvapi.errors.DAOObjectInstanciationException;
import com.jtouzy.cvapi.errors.InvalidColumnNameException;
import com.jtouzy.cvapi.errors.MethodInvokationException;

public abstract class AbstractDAO<T> implements DAO<T> {
	private Class<T> daoClass;
	private DAOCache<T> cache;
	
	public AbstractDAO(Class<T> daoClass) {
		this.daoClass = daoClass;
		this.cache = new DAOCache<T>(daoClass);
	}

	@Override
	public T create(T object) {
		return object;
	}

	@Override
	public T update(T object) {
		return null;
	}

	@Override
	public void delete(T object) {
	}

	@Override
	public T createOrUpdate(T object) {
		return null;
	}
	
	@Override
	public T createFromValues(Map<String, Object> values)
	throws DAOObjectInstanciationException {
		TableContext tableContext = getTableContext();
		T object = newObject();
		Iterator<String> it = values.keySet().iterator();
		String propertyName;
		Field field;
		while (it.hasNext()) {
			propertyName = it.next();
			field = tableContext.getFieldForProperty(propertyName);
			if (field == null) {
				throw new DAOObjectInstanciationException(new InvalidColumnNameException(propertyName, tableContext.getTableName()));
			}
			try {
				ReflectionInvoker.invokeSetter(field.getName(), object, values.get(propertyName));
			} catch (MethodInvokationException ex) {
				throw new DAOObjectInstanciationException(ex);
			}
		}
		return object;
	}
	
	protected TableContext getTableContext() {
		return DAOManager.get().getTableContext(daoClass);
	}
	
	private T newObject()
	throws DAOObjectInstanciationException {
		try {
			return daoClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new DAOObjectInstanciationException(e);
		}
	}
}
