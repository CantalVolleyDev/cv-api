package com.jtouzy.cv.api.resources.params;

import com.jtouzy.cv.model.classes.Match;

public class MatchUpdateInfos extends GymUpdateInfos {
	private Match match;

	public Match getMatch() {
		return match;
	}
	public void setMatch(Match match) {
		this.match = match;
	}	
}
