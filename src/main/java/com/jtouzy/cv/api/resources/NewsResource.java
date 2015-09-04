package com.jtouzy.cv.api.resources;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.jtouzy.cv.model.classes.News;
import com.jtouzy.cv.model.dao.NewsDAO;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.model.ColumnContextNotFoundException;
import com.jtouzy.dao.query.Query;

@Path("/news")
@PermitAll
public class NewsResource extends BasicResource<News, NewsDAO> {
	@QueryParam("published")
	protected Boolean published;
	
	public NewsResource() {
		super(NewsDAO.class);
	}
	
	@Override
	public Query<News> query()
	throws DAOInstantiationException, QueryException {
		try {
			Query<News> query = super.query();
			if (published != null) {
				News.State state = News.State.C;
				if (published) {
					state = News.State.V;
				}
				query.context().addEqualsCriterion(News.STATE_FIELD, state);
			}
			query.context().orderBy(News.PUBLISH_DATE_FIELD, false);
			return query;
		} catch (ColumnContextNotFoundException ex) {
			throw new QueryException(ex);
		}
	}
}
