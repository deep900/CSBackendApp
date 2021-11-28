/**
 * 
 */
package com.customer.service.test.organization;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.customer.service.configuration.ApplicationConfiguration;
import com.customer.service.security.LoginDTO;
import com.google.gson.Gson;

/**
 * @author Pradheep Test methods of Login Controller.
 *
 */
@SpringBootTest(classes = ApplicationConfiguration.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class LoginControllerTest extends BasicTestData {

	private Gson gson = new Gson();
	
	private final String loginEmail = "ben@example.com";

	@Test
	public void testLogin() throws Exception {
		System.out.println("Testubg the login functionality.");
		init(loginEmail);
		this.mockMvc
				.perform(post("/api/authenticate").content(gson.toJson(new LoginDTO(loginEmail, "12345")))
						.accept("application/json").header("Content-Type", "application/json"))
				.andDo(MockMvcResultHandlers.print()).andExpect(status().isOk());
	}
}
