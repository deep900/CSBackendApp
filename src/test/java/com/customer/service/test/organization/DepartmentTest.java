/**
 * 
 */
package com.customer.service.test.organization;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import com.crm.employee.Department;
import com.customer.service.configuration.ApplicationConfiguration;

/**
 * @author Pradheep
 *
 */
@SpringBootTest(classes = ApplicationConfiguration.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class DepartmentTest extends BasicTestData {

	private Department testData;

	public void init() {
		try {
			super.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Initializing the department test.");
		testData = createAdminDepartment();
	}

	@Test
	public void createDeptTest() throws Exception {
		init();
		this.mockMvc.perform(
				post("/admin/createDepartment").content(convertObjectToString(testData)).accept("application/json")
						.header("Authorization", authHeader).header("Content-Type", "application/json"))
				.andExpect(status().isOk());
	}

	//@Test
	public void createDeptAuthTest() throws Exception {
		init();
		this.mockMvc
				.perform(post("/admin/createDepartment").content(convertObjectToString(testData))
						.accept("application/json").header("Authorization", generateSampleToken("ROLE_USER"))
						.header("Content-Type", "application/json"))
				.andExpect(result -> Assert
						.assertTrue(result.getResolvedException().getMessage().equals("Access is denied")));
	}
	
	@Test
	public void testDeleteDept() throws Exception {
		init();
		this.mockMvc.perform(
				get("/admin/deleteDepartment?deptId=1").content(convertObjectToString(testData)).accept("application/json")
						.header("Authorization", authHeader).header("Content-Type", "application/json"))
				.andExpect(status().isOk());
	}
}
