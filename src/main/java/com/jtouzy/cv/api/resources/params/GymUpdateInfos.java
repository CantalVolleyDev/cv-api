package com.jtouzy.cv.api.resources.params;

import java.util.List;

import com.jtouzy.cv.model.classes.Gym;

public class GymUpdateInfos {
	private List<Gym> gyms;
	
	public List<Gym> getGyms() {
		return gyms;
	}
	public void setGyms(List<Gym> gyms) {
		this.gyms = gyms;
	}
}
