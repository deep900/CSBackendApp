/**
 * 
 */
package com.customer.service.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.crm.organization.Organization;

/**
 * @author Pradheep
 *
 */
public interface OrganizationRepository extends CrudRepository<Organization, Integer> {
	
	public List<Organization> findByOrganizationName(String organizationName);
}
