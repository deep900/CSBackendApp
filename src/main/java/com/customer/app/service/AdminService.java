package com.customer.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.employee.Department;
import com.crm.employee.Employee;
import com.crm.organization.Organization;
import com.customer.service.exception.DepartmentAlreadyExists;
import com.customer.service.exception.OrganizationAlreadyExists;
import com.customer.service.repository.DepartmentRepository;
import com.customer.service.repository.EmployeeRepository;
import com.customer.service.repository.OrganizationRepository;

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

	public boolean createEmployee(Employee employeeObj) {
		try {
			Employee empObj = employeeRepository.save(employeeObj);
			log.debug("Created employee:" + empObj.toString());
			return true;
		} catch (Exception err) {
			log.error("Error while creating the employee", err);
			return false;
		}
	}
	
	public Optional<Employee> getEmployee(int employeeId) {
		log.info("Printing the employee id:" + employeeId);
		if(employeeId == 0) {
			return Optional.empty();
		}
		return employeeRepository.findById(employeeId);
	}
	
	public Iterable<Organization> getAllOrganization(){
		return this.orgRepository.findAll();
	}
	
}
