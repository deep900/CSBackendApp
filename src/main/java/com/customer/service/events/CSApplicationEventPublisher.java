/**
 * 
 */
package com.customer.service.events;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pradheep
 *
 */
@Slf4j
public class CSApplicationEventPublisher implements ApplicationEventPublisherAware {
	
	private ApplicationEventPublisher eventPublisher;
	
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}
	
	public void publishEvent(ApplicationEvent event) {
		this.eventPublisher.publishEvent(event);
	}
}
