package com.jtouzy.cv.api.resources;

public class MatchTeamLoginParameters extends UserLoginParameters {
	private Integer matchId;
	private Integer teamId;

	public Integer getTeamId() {
		return teamId;
	}
	public void setTeamId(Integer teamId) {
		this.teamId = teamId;
	}
	public Integer getMatchId() {
		return matchId;
	}
	public void setMatchId(Integer matchId) {
		this.matchId = matchId;
	}
}
