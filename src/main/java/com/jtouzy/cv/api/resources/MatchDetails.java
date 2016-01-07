package com.jtouzy.cv.api.resources;

import java.util.List;

import com.jtouzy.cv.model.classes.Comment;

public class MatchDetails extends MatchInfos {
	private List<Comment> comments;

	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
}
