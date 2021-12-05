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
		/*Employee emp = new Employee();
		emp.setContactNumber("8766123433");
		emp.setCountryCode("91");
		emp.setDesignation("Technical Manager");
		emp.setEmail("alex@example.com");
		emp.setFirstName("Alex");
		emp.setLastName("Henry");
		Gson gson = new Gson();
		System.out.println(gson.toJson(emp));*/

		SpringApplication.run(CustomerServiceApplication.class, args);
	}

}
