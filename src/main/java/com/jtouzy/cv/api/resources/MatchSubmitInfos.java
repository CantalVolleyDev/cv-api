package com.jtouzy.cv.api.resources;

import java.util.List;

import com.jtouzy.cv.model.classes.Comment;
import com.jtouzy.cv.model.classes.SeasonTeamPlayer;

public class MatchSubmitInfos extends MatchInfos {
	private List<SeasonTeamPlayer> firstTeamPlayers;
	private List<SeasonTeamPlayer> secondTeamPlayers;
	private List<Integer> userTeams;
	private Comment submitterComment;

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
	public List<Integer> getUserTeams() {
		return userTeams;
	}
	public void setUserTeams(List<Integer> userTeams) {
		this.userTeams = userTeams;
	}
	public Comment getSubmitterComment() {
		return submitterComment;
	}
	public void setSubmitterComment(Comment submitterComment) {
		this.submitterComment = submitterComment;
	}
}
