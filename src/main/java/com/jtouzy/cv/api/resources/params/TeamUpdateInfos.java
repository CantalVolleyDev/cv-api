package com.jtouzy.cv.api.resources.params;

import com.jtouzy.cv.model.classes.SeasonTeam;

public class TeamUpdateInfos extends GymUpdateInfos {
	private SeasonTeam seasonTeam;
	
	public SeasonTeam getSeasonTeam() {
		return seasonTeam;
	}
	public void setSeasonTeam(SeasonTeam seasonTeam) {
		this.seasonTeam = seasonTeam;
	}
}
