/**
 * 
 */
package com.customer.service.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.crm.employee.Employee;

/**
 * @author Pradheep
 *
 */
public interface EmployeeRepository extends CrudRepository<Employee,Integer> {
	
	public List<Employee> findByEmail(String email);

}
