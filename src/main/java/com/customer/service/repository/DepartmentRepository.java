/**
 * 
 */
package com.customer.service.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.crm.employee.Department;

/**
 * @author deep90
 *
 */
public interface DepartmentRepository extends CrudRepository<Department, Integer> {
	
	public List<Department> findDepartmentByName(String name);
}
