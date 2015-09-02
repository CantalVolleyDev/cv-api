package com.jtouzy.cv.api.lifecycle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class APIEventListener implements ApplicationEventListener {
	private Integer requestCount = 0;
	private static final Logger logger = LogManager.getLogger(APIEventListener.class); 
	
	@Override
	public void onEvent(ApplicationEvent event) {
		switch (event.getType()) {
			case INITIALIZATION_START:
				logger.trace("Démarrage de l'application Jersey...");
				break;
			case INITIALIZATION_FINISHED:
				logger.trace("Fin du démarrage de Jersey");
				break;
			default:
				break;
		}
	}
	@Override
	public RequestEventListener onRequest(RequestEvent requestEvent) {
		StringBuilder log = new StringBuilder();
		requestCount++;
		log.append("Requête n°")
		   .append(requestCount)
		   .append(" - ")
		   .append(requestEvent.getContainerRequest().getMethod())
		   .append(" ")
		   .append(requestEvent.getContainerRequest().getUriInfo().getRequestUri());
		logger.trace(log);
		return new APIRequestEventListener();
	}
}
