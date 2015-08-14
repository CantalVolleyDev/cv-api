package com.jtouzy.cvapi;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jtouzy.cvapi.errors.MethodInvokationException;

public class ReflectionInvoker {

	public static void invokeSetter(String property, Object object, Object value)
	throws MethodInvokationException {
		try {
			PropertyDescriptor descriptor = new PropertyDescriptor(property, object.getClass());
			Method setter = descriptor.getWriteMethod();
			setter.invoke(object, value);
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new MethodInvokationException(property, e);
		}
	}
}
