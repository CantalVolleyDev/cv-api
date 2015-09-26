package com.jtouzy.cv.api.resources;

import java.util.List;

import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.classes.MatchPlayer;

public class MatchInfos {
	private Match match;
	private List<MatchPlayer> firstTeamMatchPlayers;
	private List<MatchPlayer> secondTeamMatchPlayers;
	
	public Match getMatch() {
		return match;
	}
	public void setMatch(Match match) {
		this.match = match;
	}
	public List<MatchPlayer> getFirstTeamMatchPlayers() {
		return firstTeamMatchPlayers;
	}
	public void setFirstTeamMatchPlayers(List<MatchPlayer> firstTeamMatchPlayers) {
		this.firstTeamMatchPlayers = firstTeamMatchPlayers;
	}
	public List<MatchPlayer> getSecondTeamMatchPlayers() {
		return secondTeamMatchPlayers;
	}
	public void setSecondTeamMatchPlayers(List<MatchPlayer> secondTeamMatchPlayers) {
		this.secondTeamMatchPlayers = secondTeamMatchPlayers;
	}
}
