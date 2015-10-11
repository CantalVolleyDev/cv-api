package com.jtouzy.cv.api.resources.params;

import java.util.List;

import com.jtouzy.cv.model.classes.Gym;
import com.jtouzy.cv.model.classes.SeasonTeam;

public class TeamUpdateInfos {
	private SeasonTeam seasonTeam;
	private List<Gym> gyms;
	
	public SeasonTeam getSeasonTeam() {
		return seasonTeam;
	}
	public void setSeasonTeam(SeasonTeam seasonTeam) {
		this.seasonTeam = seasonTeam;
	}
	public List<Gym> getGyms() {
		return gyms;
	}
	public void setGyms(List<Gym> gyms) {
		this.gyms = gyms;
	}
}
