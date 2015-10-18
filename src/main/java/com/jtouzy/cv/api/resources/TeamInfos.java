package com.jtouzy.cv.api.resources;

import java.util.List;

import com.jtouzy.cv.model.classes.Comment;
import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.classes.SeasonTeam;
import com.jtouzy.cv.model.classes.SeasonTeamPlayer;

public class TeamInfos {
	private SeasonTeam seasonTeam;
	private List<SeasonTeamPlayer> players;
	private List<Comment> comments;
	private List<Match> lastMatchs;
	
	public SeasonTeam getSeasonTeam() {
		return seasonTeam;
	}
	public void setSeasonTeam(SeasonTeam seasonTeam) {
		this.seasonTeam = seasonTeam;
	}
	public List<SeasonTeamPlayer> getPlayers() {
		return players;
	}
	public void setPlayers(List<SeasonTeamPlayer> players) {
		this.players = players;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public List<Match> getLastMatchs() {
		return lastMatchs;
	}
	public void setLastMatchs(List<Match> lastMatchs) {
		this.lastMatchs = lastMatchs;
	}
}
