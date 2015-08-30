package com.jtouzy.cv.api.resources;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;
import com.jtouzy.cv.model.classes.News;
import com.jtouzy.cv.model.dao.NewsDAO;

@Path("/news")
@PermitAll
public class NewsResource extends BasicResource<News, NewsDAO> {
	public NewsResource() {
		super(NewsDAO.class);
	}
}
