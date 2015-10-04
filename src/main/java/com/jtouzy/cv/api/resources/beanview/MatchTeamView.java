package com.jtouzy.cv.api.resources.beanview;

import com.jtouzy.cv.model.classes.Gym;
import com.jtouzy.cv.model.classes.SeasonTeam;

/**
 * Vue utilisée pour : 
 * La visualisation des matchs (Page match)
 * @author JTO
 *
 */
public class MatchTeamView extends TeamSimpleView {
	public MatchTeamView() {
	}
	
	public Integer getIdentifier(SeasonTeam seasonTeam) {
		return seasonTeam.getIdentifier();
	}
	public Gym getGym(SeasonTeam seasonTeam) {
		return seasonTeam.getGym();
	}
	public String getLabel(SeasonTeam seasonTeam) {
		return seasonTeam.getLabel();
	}
}
