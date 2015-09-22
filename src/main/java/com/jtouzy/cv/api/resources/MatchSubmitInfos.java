package com.jtouzy.cv.api.resources;

import java.util.List;

import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.classes.MatchPlayer;
import com.jtouzy.cv.model.classes.SeasonTeamPlayer;

public class MatchSubmitInfos {
	private Match match;
	private List<SeasonTeamPlayer> firstTeamPlayers;
	private List<SeasonTeamPlayer> secondTeamPlayers;
	private List<MatchPlayer> firstTeamMatchPlayers;
	private List<MatchPlayer> secondTeamMatchPlayers;
	private List<Integer> userTeams;

	public Match getMatch() {
		return match;
	}
	public void setMatch(Match match) {
		this.match = match;
	}
	public List<SeasonTeamPlayer> getFirstTeamPlayers() {
		return firstTeamPlayers;
	}
	public void setFirstTeamPlayers(List<SeasonTeamPlayer> firstTeamPlayers) {
		this.firstTeamPlayers = firstTeamPlayers;
	}
	public List<SeasonTeamPlayer> getSecondTeamPlayers() {
		return secondTeamPlayers;
	}
	public void setSecondTeamPlayers(List<SeasonTeamPlayer> secondTeamPlayers) {
		this.secondTeamPlayers = secondTeamPlayers;
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
	public List<Integer> getUserTeams() {
		return userTeams;
	}
	public void setUserTeams(List<Integer> userTeams) {
		this.userTeams = userTeams;
	}
}
