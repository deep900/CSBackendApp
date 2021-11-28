/**
 * 
 */
package com.customer.service.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.customer.service.configuration.ApplicationConfiguration;

/**
 * @author Pradheep
 *
 */
@SpringBootApplication
@Import(value = ApplicationConfiguration.class)
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

}
