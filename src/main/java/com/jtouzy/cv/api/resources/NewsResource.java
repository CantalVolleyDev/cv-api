package com.jtouzy.cv.api.resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.model.classes.News;
import com.jtouzy.cv.model.dao.NewsDAO;
import com.jtouzy.dao.DAOManager;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;

@Path("/news")
public class NewsResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<News> getAll()
	throws APIException {
		try {
			Connection connection = DriverManager.getConnection("jdbc:postgresql://5.135.146.110:5432/jto_cvapi_dvt", "postgres", "jtogri%010811sqladmin");
			return DAOManager.getDAO(connection, NewsDAO.class).queryAll();
		} catch (SQLException | DAOInstantiationException | QueryException ex) {
			throw new APIException(ex);
		}
	}
}
