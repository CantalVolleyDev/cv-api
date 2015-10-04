package com.jtouzy.cv.api.resources.beanview;

import java.time.LocalDate;

import com.jtouzy.cv.model.classes.User;
import com.jtouzy.cv.model.classes.User.Gender;

public class UserView extends UserSimpleView {
	public UserView() {
	}

	public String getMail(User user) {
		return user.getMail();
	}
	public String getPassword(User user) {
		return user.getPassword();
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
	public Gender getGender(User user) {
		return user.getGender();
	}
}
