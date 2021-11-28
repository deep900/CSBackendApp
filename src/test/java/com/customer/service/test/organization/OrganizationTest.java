package com.customer.service.test.organization;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import com.customer.service.configuration.ApplicationConfiguration;

/**
 * @author Pradheep
 *
 */
@SpringBootTest(classes = ApplicationConfiguration.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class OrganizationTest extends BasicTestData {	
	
	//@Test
	public void testAuthorization2() throws Exception {
		init();
		this.mockMvc.perform(
				post("/admin/createOrg").content(convertObjectToString(createSampleOrg())).accept("application/json")
						.header("Authorization", "fake_header").header("Content-Type", "application/json"))
				.andExpect(status().isForbidden());
	}

	@Test
	public void testCreateOrg1() throws Exception {
		init();
		this.mockMvc.perform(
				post("/admin/createOrg").content(convertObjectToString(createSampleOrg())).accept("application/json")
						.header("Authorization", authHeader).header("Content-Type", "application/json"))
				.andExpect(status().isOk());
	}

	@Test
	public void testCreateOrg2() throws Exception {
		init();
		System.out.println("Performing duplicate organization creation test.");
		this.mockMvc
				.perform(post("/admin/createOrg").content(convertObjectToString(createSampleOrg()))
						.accept("application/json").header("Authorization", authHeader).header("Content-Type",
								"application/json"))
				.andExpect(content().string(
						"{\"errorCode\":1000,\"message\":\"Organization already exists\",\"errorMessage\":\"Organization already exists\"}"));
	}

	@Test
	public void testDeleteOrg() throws Exception {
		init();
		this.mockMvc.perform(get("/admin/deleteOrg?orgId=1").header("Authorization", authHeader).header("Content-Type",
				"application/json")).andExpect(status().isOk());
	}
}
