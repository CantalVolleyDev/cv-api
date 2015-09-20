package com.jtouzy.cv.api.resources;

import java.util.List;

import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.classes.SeasonTeamPlayer;

public class AccountInfos {
	private List<SeasonTeamPlayer> teamPlayers;
	private boolean uploadImage;
	private List<Match> matchs;
	
	public List<SeasonTeamPlayer> getTeams() {
		return teamPlayers;
	}
	public void setTeams(List<SeasonTeamPlayer> teams) {
		this.teamPlayers = teams;
	}
	public List<Match> getMatchs() {
		return matchs;
	}
	public void setMatchs(List<Match> matchs) {
		this.matchs = matchs;
	}
	public boolean isUploadImage() {
		return uploadImage;
	}
	public void setUploadImage(boolean uploadImage) {
		this.uploadImage = uploadImage;
	}
}
