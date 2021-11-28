package com.customer.service.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.crm.security.AdminUserPrevilege;

public interface AdminUserPrevilegeRepository extends CrudRepository<AdminUserPrevilege, Integer> {
	
	public List<AdminUserPrevilege> findByAdminUserId(Integer adminUserId);

}
