/**
 * 
 */
package com.customer.service.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.customer.app.service.UserCreationBean;
import com.customer.service.events.CSApplicationEventPublisher;
import com.customer.service.security.CustomerServiceSecurityConfig;
import com.customer.service.security.TokenUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Pradheep
 *
 */
@Configuration
@Import(CustomerServiceSecurityConfig.class)
@ComponentScan(basePackages = "com.customer")
@EnableJpaRepositories(basePackages = "com.customer.service.repository")
@EntityScan(basePackages = { "com.crm", "com.customer.app.entity" })
public class ApplicationConfiguration {

	@Bean
	public CSApplicationEventPublisher getCSApplicationEventPublisher() {
		return new CSApplicationEventPublisher();
	}

	@Bean
	public TokenUtility getTokenUtility() {
		return new TokenUtility();
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public UserCreationBean getUserCreationBean() {
		return new UserCreationBean();
	}
}
