package com.jtouzy.cvapi;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jtouzy.cvapi.errors.MethodInvokationException;

/**
 * Classe de regroupement de méthodes utilitaires pour gérer la reflection
 * @author jtouzy
 */
public class ReflectionInvoker {
	/**
	 * Invocation d'un setter sur une propriété d'un objet
	 * @param property Nom de la propriété
	 * @param object Objet sur lequel invoquer la méthode
	 * @param value Valeur à passer au setter de l'objet
	 * @throws MethodInvokationException Si un problème survient lors de l'invocation
	 */
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
	/**
	 * Invocation d'un getter sur la propriété d'un objet
	 * @param property Nom de la propriété
	 * @param object Objet sur lequel invoquer la méthode
	 * @return Retour de la méthode GET invoquée
	 * @throws MethodInvokationException Si un problème survient lors de l'invocation
	 */
	public static Object invokeGetter(String property, Object object) 
	throws MethodInvokationException {
		try {
			PropertyDescriptor descriptor = new PropertyDescriptor(property, object.getClass());
			Method setter = descriptor.getReadMethod();
			return setter.invoke(object);
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new MethodInvokationException(property, e);
		}
	}
}
