package com.jtouzy.cv.api.lifecycle;

import java.time.LocalDateTime;

import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class APIRequestEventListener implements RequestEventListener {
	private long startTime;
	
	public APIRequestEventListener() {
		this.startTime = System.currentTimeMillis();
	}
	@Override
	public void onEvent(RequestEvent event) {
		StringBuilder log = new StringBuilder();
		log.append(LocalDateTime.now())
		   .append(" - ");
		StringBuilder eventLog = new StringBuilder();
		switch (event.getType()) {
			case RESOURCE_METHOD_START:
				eventLog.append("Lancement de la méthode de la ressource");
				break;
			case RESOURCE_METHOD_FINISHED:
				eventLog.append("Fin de la méthode de la ressource");
				break;
			case ON_EXCEPTION:
				eventLog.append("Exception levée pendant la requête");
				break;
			case FINISHED:
				eventLog.append("Fin de l'exécution de la requête")
				        .append(" - ")
				        .append("Durée ")
				        .append(System.currentTimeMillis() - startTime)
				        .append("ms");
				break;
			default:
				break;
		}
		if (eventLog.length() > 0) {
			System.out.println(log.append(eventLog)
					              .append(" [")
					              .append(event.getType())
					              .append("]"));
		}
	}
}
