package com.jtouzy.cv.api.resources;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.model.classes.News;
import com.jtouzy.cv.model.dao.NewsDAO;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;

@Path("/news")
@PermitAll
public class NewsResource extends BasicResource<News, NewsDAO> {
	@QueryParam("published")
	protected Boolean published;
	@QueryParam("limitTo")
	protected Integer limitTo;
	@QueryParam("page")
	protected Integer page;
	
	public NewsResource() {
		super(News.class, NewsDAO.class);
	}
	
	@Override
	public List<News> getAll() 
	throws APIException {
		try {
			if (published != null && published) {
				return getDAO().getAllPublished(limitTo, page);
			}
			return getDAO().getAllWithDetails(limitTo, page);
		} catch (QueryException | DAOInstantiationException ex) {
			throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, ex);
		}
	}
}
