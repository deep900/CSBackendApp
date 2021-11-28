/**
 * 
 */
package com.customer.service.repository;

import org.springframework.data.repository.CrudRepository;

import com.customer.app.entity.BlacklistedToken;

/**
 * @author Pradheep
 *
 */
public interface BlacklistedTokenRepository extends CrudRepository<BlacklistedToken, Integer>{

	public BlacklistedToken findByBlackListedToken(String blackListedToken);
}
