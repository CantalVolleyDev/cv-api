package com.jtouzy.cv.api.resources;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.jtouzy.cv.api.resources.params.ToolsParameters;
import com.jtouzy.cv.tools.ToolLauncher;
import com.jtouzy.cv.tools.errors.ToolsException;
import com.jtouzy.cv.tools.model.ToolsList;

@Path("/tools")
public class ToolsResource extends GenericResource {
	@POST
	@Path("/updateTeamInformations")
	public void executeUpdateTeamInformations(ToolsParameters parameters) {
		commonLaunchTool(ToolsList.UPD_TEAM_INFOS, parameters);
	}
	
	public void commonLaunchTool(ToolsList target, ToolsParameters parameters) {
		try {
			ToolLauncher.build()
						.useConnection(this.getRequestContext().getConnection())
						.target(target)
						.addParameters(parameters.getParameters())
						.run();
		} catch (ToolsException ex) {
			throw new InternalServerErrorException(ex);
		}
	}
}
