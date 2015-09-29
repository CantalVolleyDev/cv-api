package com.jtouzy.cv.api.resources.beanview;

import java.time.LocalDateTime;

import com.jtouzy.cv.model.classes.News;
import com.owlike.genson.BeanView;

public class NewsView implements BeanView<News> {
	public NewsView() {
	}
	
	public String getTitle(News object) {
		return object.getTitle();
	}
	public String getContent(News object) {
		return object.getContent();
	}
	public String getAuthorFullName(News object) {
		return object.getAuthor().getFirstName() + " " + object.getAuthor().getName();
	}
	public LocalDateTime getPublishDate(News object) {
		return object.getPublishDate();
	}
	public String getCategory(News object) {
		return object.getCategory();
	}
	public String getImagePath(News object) {
		return "news" + object.getIdentifier() + ".jpg";
	}
}
