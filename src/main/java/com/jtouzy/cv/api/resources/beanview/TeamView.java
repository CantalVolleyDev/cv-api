package com.jtouzy.cv.api.resources.beanview;

import java.time.LocalDateTime;

import com.jtouzy.cv.model.classes.Gym;
import com.jtouzy.cv.model.classes.Season;
import com.jtouzy.cv.model.classes.SeasonTeam;
import com.jtouzy.cv.model.classes.Team;

public class TeamView extends TeamSimpleView {
	public Integer getIdentifier(SeasonTeam seasonTeam) {
		return seasonTeam.getIdentifier();
	}
	public Team getTeam(SeasonTeam seasonTeam) {
		return seasonTeam.getTeam();
	}
	public Season getSeason(SeasonTeam seasonTeam) {
		return seasonTeam.getSeason();
	}
	public Gym getGym(SeasonTeam seasonTeam) {
		return seasonTeam.getGym();
	}
	public LocalDateTime getDate(SeasonTeam seasonTeam) {
		return seasonTeam.getDate();
	}
	public String getLabel(SeasonTeam seasonTeam) {
		return seasonTeam.getLabel();
	}
	public Integer getPlayersNumber(SeasonTeam seasonTeam) {
		return seasonTeam.getPlayersNumber();
	}
}
