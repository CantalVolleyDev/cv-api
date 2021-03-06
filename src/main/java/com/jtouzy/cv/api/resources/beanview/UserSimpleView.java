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
			return "images/user/" + user.getIdentifier() + "-" + user.getImageVersion() + "." + extension.toLowerCase(); 
		}
		if (user.getGender() == User.Gender.F)
			return "images/user/female-default-20160320-000000.png";
		return "images/user/male-default-20160320-000000.png";
	}
}
