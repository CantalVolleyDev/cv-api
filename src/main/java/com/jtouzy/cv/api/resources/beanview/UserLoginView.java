package com.jtouzy.cv.api.resources.beanview;

import java.time.LocalDate;

import com.jtouzy.cv.model.classes.User;

public class UserLoginView extends UserSimpleView {
	public UserLoginView() {
	}

	public String getMail(User user) {
		return user.getMail();
	}
	public LocalDate getBirthDate(User user) {
		return user.getBirthDate();
	}
	public String getPhone(User user) {
		return user.getPhone();
	}
	public Boolean isAdministrator(User user) {
		return user.isAdministrator();
	}
}
