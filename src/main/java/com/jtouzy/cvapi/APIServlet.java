package com.jtouzy.cvapi;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import javax.servlet.ServletException;

import org.glassfish.jersey.servlet.ServletContainer;

import com.jtouzy.cvapi.dao.DAOManager;
import com.jtouzy.cvapi.model.Season;

public class APIServlet extends ServletContainer {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() 
	throws ServletException {
		super.init();
		
		try {
			PropertyDescriptor pd = new PropertyDescriptor("identifier", Season.class);
			System.out.println(pd.getReadMethod().toString());
			System.out.println(pd.getWriteMethod().toString());
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		
		
		// Premier appel au DAOManager : Initialisation
		//DAOManager.get();
	}
}
