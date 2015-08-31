package com.jtouzy.cv.api.lifecycle;

import java.time.LocalDateTime;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class APIEventListener implements ApplicationEventListener {
	private Integer requestCount = 0;
	
	@Override
	public void onEvent(ApplicationEvent event) {
		StringBuilder log = new StringBuilder();
		log.append(LocalDateTime.now())
		   .append(" - ");
		switch (event.getType()) {
			case INITIALIZATION_START:
				log.append("Démarrage de l'application...");
				break;
			case INITIALIZATION_APP_FINISHED:
				log.append("Fin d'initialisation de l'application");
				break;
			case INITIALIZATION_FINISHED:
				log.append("Fin du démarrage de l'application... En attente de requêtes");
				break;
			default:
				break;
		}
		System.out.println(log);
	}
	@Override
	public RequestEventListener onRequest(RequestEvent requestEvent) {
		StringBuilder log = new StringBuilder();
		requestCount++;
		log.append(LocalDateTime.now())
		   .append(" - ")
		   .append("Requête n°")
		   .append(requestCount)
		   .append(" - ")
		   .append(requestEvent.getContainerRequest().getMethod())
		   .append(" ")
		   .append(requestEvent.getContainerRequest().getUriInfo().getRequestUri());
		System.out.println(log);
		return new APIRequestEventListener();
	}
}
