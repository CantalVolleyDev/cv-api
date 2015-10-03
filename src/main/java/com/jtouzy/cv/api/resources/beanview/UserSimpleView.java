package com.jtouzy.cv.api.resources.beanview;

import com.jtouzy.cv.model.classes.User;
import com.owlike.genson.BeanView;

public class UserSimpleView implements BeanView<User> {
	public UserSimpleView() {
	}
	
	public Integer getIdentifier(User user) {
		return user.getIdentifier();
	}
	public String getName(User user) {
		return user.getName();
	}
	public String getFirstName(User user) {
		return user.getFirstName();
	}
	public String getImagePath(User user) {
		String extension = user.getImage();
		if (extension != null) {
			return "images/user" + user.getIdentifier() + "." + extension.toLowerCase(); 
		}
		if (user.getGender() == User.Gender.M)
			return "images/user-male-default.png";
		else return "images/user-female-default.png";
	}
}
