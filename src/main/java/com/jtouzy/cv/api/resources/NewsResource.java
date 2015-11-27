package com.jtouzy.cv.api.resources;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.resources.beanview.NewsView;
import com.jtouzy.cv.model.classes.News;
import com.jtouzy.cv.model.dao.NewsDAO;
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
	public Response getAllAsResponse()
	throws APIException {
		try {
			List<News> news;
			if (published != null && published) {
				news = getDAO(NewsDAO.class).getAllPublished(limitTo, page);
			} else {
				news = getDAO(NewsDAO.class).getAllWithDetails(limitTo, page);
			}	
			return buildViewResponse(news, NewsView.class);
		} catch (QueryException ex) {
			throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, ex);
		}
	}
}
