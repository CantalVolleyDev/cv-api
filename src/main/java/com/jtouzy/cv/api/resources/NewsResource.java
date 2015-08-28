package com.jtouzy.cv.api.resources;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jtouzy.cv.api.config.AppConfig;
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
			Connection connection = AppConfig.getDataSource().getConnection();
			List<News> news = DAOManager.getDAO(connection, NewsDAO.class).queryAll();
			connection.close();
			return news;
		} catch (SQLException | DAOInstantiationException | QueryException ex) {
			throw new APIException(ex);
		}
	}
}
