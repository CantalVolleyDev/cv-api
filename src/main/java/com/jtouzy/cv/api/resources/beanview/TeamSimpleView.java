package com.jtouzy.cv.api.resources.beanview;

import com.jtouzy.cv.model.classes.SeasonTeam;
import com.owlike.genson.BeanView;

public class TeamSimpleView implements BeanView<SeasonTeam> {
	public String getImagePath(SeasonTeam seasonTeam) {
		String extension = seasonTeam.getImage();
		if (extension != null) {
			return "images/team" + seasonTeam.getIdentifier() + "." + extension.toLowerCase(); 
		}
		return "images/team-default.png";
	}
	
	public String getImagePlayersPath(SeasonTeam seasonTeam) {
		String extension = seasonTeam.getImagePlayers();
		if (extension != null) {
			return "images/teamPlayers" + seasonTeam.getIdentifier() + "." + extension.toLowerCase(); 
		}
		return "images/teamPlayers-default.png";
	}
}
