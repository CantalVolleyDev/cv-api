package com.jtouzy.cv.api.resources;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.resources.beanview.NewsView;
import com.jtouzy.cv.model.classes.News;
import com.jtouzy.cv.model.dao.NewsDAO;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;

@Path("/news")
@PermitAll
public class NewsResource extends GenericResource {
	@QueryParam("published")
	protected Boolean published;
	@QueryParam("limitTo")
	protected Integer limitTo;
	@QueryParam("page")
	protected Integer page;
	
	@GET
	public Response getAllAsResponse(@Context Providers providers)
	throws APIException {
		try {
			List<News> news;
			if (published != null && published) {
				news = getDAO(NewsDAO.class).getAllPublished(limitTo, page);
			} else {
				news = getDAO(NewsDAO.class).getAllWithDetails(limitTo, page);
			}	
			return buildViewResponse(NewsView.class, news);
		} catch (QueryException | DAOInstantiationException ex) {
			throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, ex);
		}
	}
}
