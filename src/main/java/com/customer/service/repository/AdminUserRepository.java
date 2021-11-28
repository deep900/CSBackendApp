/**
 * 
 */
package com.customer.service.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.crm.employee.AdminUser;

/**
 * @author Pradheep
 *
 */
public interface AdminUserRepository extends CrudRepository<AdminUser,Integer> {
	
	public List<AdminUser> findByUserEmail(String userEmail);
}
