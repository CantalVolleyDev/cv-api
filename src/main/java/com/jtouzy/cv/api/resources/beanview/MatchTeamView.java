package com.jtouzy.cv.api.resources.beanview;

import com.jtouzy.cv.model.classes.Gym;
import com.jtouzy.cv.model.classes.SeasonTeam;
import com.owlike.genson.BeanView;

/**
 * Vue utilisée pour : 
 * La visualisation des matchs (Page match)
 * La recherche d'informations pour la soumission d'un score (Page score)
 * @author JTO
 *
 */
public class MatchTeamView implements BeanView<SeasonTeam> {
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
	public String getImagePath(SeasonTeam seasonTeam) {
		String extension = seasonTeam.getImage();
		if (extension != null) {
			return "images/team" + seasonTeam.getIdentifier() + "." + extension.toLowerCase(); 
		}
		return "images/team-default.png";
	}
}
