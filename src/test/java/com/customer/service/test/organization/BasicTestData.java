package com.customer.service.test.organization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.crm.employee.AdminUser;
import com.crm.employee.Department;
import com.crm.employee.Employee;
import com.crm.organization.Organization;
import com.customer.service.repository.AdminUserRepository;
import com.customer.service.security.CustomerServiceAuthorizationFilter;
import com.customer.service.security.SecurityUtility;
import com.customer.service.security.Token;
import com.customer.service.security.TokenUtility;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pradheep
 *
 */
@Slf4j
public class BasicTestData {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private CustomerServiceAuthorizationFilter customerServiceAuthFilter;

	public MockMvc mockMvc;

	public String authHeader;

	@Autowired
	private TokenUtility tokenUtility;

	private Gson gson = new Gson();

	public final String testEmail = "admin.user@sampleorg.com";

	@Autowired
	private AdminUserRepository adminUserRepository;

	public void init() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilter(customerServiceAuthFilter, "/admin/*")
				.build();
		createAdminUserAndCredentials(testEmail);
		authHeader = generateSampleToken("ROLE_ADMIN").getToken();
	}
	
	public void init(String email) throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilter(customerServiceAuthFilter, "/admin/*")
				.build();
		createAdminUserAndCredentials(email);
		authHeader = generateSampleToken("ROLE_ADMIN").getToken();
	}

	public Organization createSampleOrg() {
		Organization org = new Organization();
		org.setId(1);
		org.setMarkedForDelete(false);
		org.setOrganizationName("MySampleOrg");
		org.setDepartmentList(createDepartmentList());
		org.setEmployeeList(createEmployeeList());
		return org;
	}

	public List<Employee> createEmployeeList() {
		ArrayList empList = new ArrayList();
		empList.add(createQAEmployee());
		return empList;
	}

	public List<Department> createDepartmentList() {
		List departmentList = new ArrayList();
		departmentList.add(createTechnicalDepartment());
		departmentList.add(createAdminDepartment());
		return departmentList;
	}

	public Employee createQAEmployee() {
		Employee employee = new Employee();
		employee.setId(1);
		employee.setContactNumber("698941250");
		employee.setCountryCode("+91");
		employee.setDesignation("QA-Engineer");
		employee.setEmail("sampleemail-qa@MySampleOrg.com");
		employee.setEmailVerified(true);
		return employee;
	}

	public Employee createManagerEmployee() {
		Employee employee = new Employee();
		employee.setId(1);
		employee.setContactNumber("422001250");
		employee.setCountryCode("+91");
		employee.setDepartment(createTechnicalDepartment());
		employee.setDesignation("QA-Manager");
		employee.setEmail("sampleemail-manager@MySampleOrg.com");
		employee.setEmailVerified(true);
		return employee;
	}

	public Department createTechnicalDepartment() {
		Department department = new Department();
		department.setId(1);
		department.setName("Technical");
		return department;
	}

	public Department createAdminDepartment() {
		Department department = new Department();
		department.setId(2);
		department.setName("Admin");
		department.setDepartmentLead(createManagerEmployee());
		return department;
	}

	public String convertObjectToString(Object obj) {
		return gson.toJson(obj);
	}

	public List<GrantedAuthority> createGrantedAuthority(String role) {
		GrantedAuthority grantedAuth = new SimpleGrantedAuthority(role);
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(grantedAuth);
		return authorities;
	}

	public Token generateSampleToken(String role) {
		User userObj = new User(testEmail, "12345", createGrantedAuthority(role));
		return tokenUtility.generateToken(userObj);
	}	

	public void createAdminUserAndCredentials(String email) {
		AdminUser adminUser = new AdminUser();
		SecretKey testKey = null;
		try {
			testKey = SecurityUtility.generateKey();
			adminUser.setSecretKey(SecurityUtility.convertObjectToByteArray(testKey));
		} catch (NoSuchAlgorithmException e) {
			log.error("No such algorithm exception", e);
		} catch (Exception e) {
			log.error("Unable to create admin user", e);
		}
		adminUser.setFirstName("Admin");
		adminUser.setLastName("User");
		adminUser.setUserEmail(email);
		try {
			String encryptedPassword = SecurityUtility.encrypt(SecurityUtility.AESAlgorithm, "12345", testKey,
					SecurityUtility.generateIv());
			System.out.println("Printing the encrypted passwordStr:" + encryptedPassword);
			adminUser.setPassword(encryptedPassword);
		} catch (InvalidKeyException e) {
			log.error("Invalid key exception", e);
		} catch (NoSuchPaddingException e) {
			log.error("No such padding exception", e);
		} catch (NoSuchAlgorithmException e) {
			log.error("No such algorithm exception", e);
		} catch (InvalidAlgorithmParameterException e) {
			log.error("Invalid algorithm parameter exception", e);
		} catch (BadPaddingException e) {
			log.error("Invalid key exception", e);
		} catch (IllegalBlockSizeException e) {
			log.error("Illegal block size exception", e);
		}
		adminUser.setEmailVerified(true);
		log.info(adminUser.toString());
		AdminUser adminUserObj = this.adminUserRepository.save(adminUser);
		log.info("Printing the saved entity:" + adminUserObj.toString());
	}
}
