package com.customer.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.employee.Department;
import com.crm.employee.Employee;
import com.crm.organization.Organization;
import com.crm.security.EmployeePrevilege;
import com.crm.security.Previlege;
import com.customer.service.exception.DepartmentAlreadyExists;
import com.customer.service.exception.EmployeeAlreadyExistsException;
import com.customer.service.exception.OrganizationAlreadyExists;
import com.customer.service.repository.DepartmentRepository;
import com.customer.service.repository.EmployeePrevilegeRepository;
import com.customer.service.repository.EmployeeRepository;
import com.customer.service.repository.OrganizationRepository;
import com.customer.service.repository.PrevilegeRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pradheep
 *
 */
@Service
@Slf4j
public class AdminService {

	@Autowired
	private OrganizationRepository orgRepository;

	@Autowired
	private DepartmentRepository deptRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private PrevilegeRepository previlegeRepository;

	@Autowired
	private EmployeePrevilegeRepository employeePrevilegeRepository;

	public boolean createOrganization(Organization organization) throws OrganizationAlreadyExists {
		log.info("Creating a new organization " + organization);
		if (doesOrgAlreadyExists(organization)) {
			throw new OrganizationAlreadyExists("Organization already exists");
		} else {
			orgRepository.save(organization);
			return true;
		}
	}

	public Optional<Organization> getOrganizationById(Integer orgId) {
		log.info("Trying to fetch the details of organization:" + orgId);
		return orgRepository.findById(orgId);
	}

	public Optional<Department> getDepartmentById(Integer departmentId) {
		log.info("Trying to fetch the details of department:" + departmentId);
		return deptRepository.findById(departmentId);
	}

	public boolean doesOrgAlreadyExists(Organization organization) {
		return !orgRepository.findByOrganizationName(organization.getOrganizationName()).isEmpty();
	}

	public boolean createDepartment(Department department) throws DepartmentAlreadyExists {
		log.info("Creating a new department:" + department);
		if (doesDepartmentAlreadyExists(department)) {
			throw new DepartmentAlreadyExists(
					"Department already exists, try creating a new department with a different name.");
		} else {
			deptRepository.save(department);
			return true;
		}
	}

	public boolean doesDepartmentAlreadyExists(Department department) {
		return !deptRepository.findDepartmentByName(department.getName()).isEmpty();
	}

	/**
	 * The user has to reset the password for the first time to continue to use
	 * his account.
	 * 
	 * @param employeeObj
	 * @return
	 */
	public Employee createEmployee(Employee employeeObj) throws EmployeeAlreadyExistsException {		
			if (checkExistingUserEmail(employeeObj)) {
				throw new EmployeeAlreadyExistsException("User email already exists, cannot create account.");
			} else if (checkExistingContactNumber(employeeObj)) {
				throw new EmployeeAlreadyExistsException("User with same contact exists for same country code");
			}
			employeeObj.setEmailVerified(false);
			employeeObj.setGrantedAuthorities(Collections.emptyList());
			employeeObj.setPassword(UUID.randomUUID().toString());
			employeeObj.setSecretKey(UUID.randomUUID().toString().getBytes());
			Employee empObj = employeeRepository.save(employeeObj);
			log.debug("Created employee:" + empObj.toString());
			List<String> rolesAssigned = assignDefaultRoles(empObj);
			log.info("Assigned roles:" + rolesAssigned);
			return empObj;	
	}

	public boolean checkExistingUserEmail(Employee empObj) {
		return !this.employeeRepository.findByEmail(empObj.getEmail()).isEmpty();
	}

	public boolean checkExistingContactNumber(Employee empObj) {
		return !this.employeeRepository
				.findByContactNumberAndCountryCode(empObj.getContactNumber(), empObj.getCountryCode()).isEmpty();
	}

	/**
	 * Assigns the default role of 'ROLE_USER' and 'ROLE_CHANGE_PASSWORD'. The
	 * users with role 'ROLE_CHANGE_PASSWORD' has to change the password.
	 * 
	 * @param employee
	 * @return
	 */
	public List<String> assignDefaultRoles(Employee employee) {
		log.info("Assigning the default roles.");
		List<String> roles = new ArrayList<String>();
		Iterable<Previlege> previlegeIterator = previlegeRepository.findAll();
		previlegeIterator.forEach(previlege -> {
			if (previlege.getPrevilegeName().equalsIgnoreCase("ROLE_USER")
					|| previlege.getPrevilegeName().equalsIgnoreCase("ROLE_CHANGE_PASSWORD")) {
				roles.add(previlege.getPrevilegeName());
				employeePrevilegeRepository.save(new EmployeePrevilege(employee.getId(), previlege.getId()));
			}
		});
		return roles;
	}

	public Optional<Employee> getEmployee(int employeeId) {
		log.info("Printing the employee id:" + employeeId);
		if (employeeId == 0) {
			return Optional.empty();
		}
		return employeeRepository.findById(employeeId);
	}

	public Iterable<Organization> getAllOrganization() {
		return this.orgRepository.findAll();
	}

}
