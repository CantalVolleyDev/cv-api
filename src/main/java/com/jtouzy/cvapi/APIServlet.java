package com.jtouzy.cvapi;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletException;

import org.glassfish.jersey.servlet.ServletContainer;

import com.jtouzy.cvapi.dao.DAOManager;
import com.jtouzy.cvapi.dao.SeasonDAO;
import com.jtouzy.cvapi.model.Season;

public class APIServlet extends ServletContainer {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() 
	throws ServletException {
		super.init();		
		try {
			Connection connection = DriverManager.getConnection("jdbc:postgresql://5.135.146.110:5432/jto_cvapi_dvt", "upublic", "jtogri%010811sqlpublic");
			DAOManager.get()
	          		  .registerForCnx(connection)
	          		  .add(new SeasonDAO(Season.class));
		} catch (Exception e) {
			// TODO gérer l'exception correctement
			e.printStackTrace();
		}
	}
}
