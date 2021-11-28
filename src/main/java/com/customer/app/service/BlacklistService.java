/**
 * 
 */
package com.customer.app.service;

import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.customer.app.entity.BlacklistedToken;
import com.customer.service.repository.BlacklistedTokenRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * This service blacklists the JWT token that is issued.
 * 
 * When a token is blacklisted it will no longer be valid in the system.
 * 
 * @author Pradheep
 *
 */
@Service
@Slf4j
public class BlacklistService {

	@Autowired
	private BlacklistedTokenRepository blacklistedTokenRepository;

	public boolean blackListToken(String token) {
		try {
			log.info("Black listed:" + token);
			BlacklistedToken blToken = new BlacklistedToken();
			blToken.setBlackListedToken(token);
			blToken.setEvictTime(getEvictionDate());
			blacklistedTokenRepository.save(blToken);
		} catch (Exception err) {
			log.error("Error while saving the blacklisted token", err);
			return false;
		}
		return true;
	}

	public Date getEvictionDate() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(GregorianCalendar.HOUR_OF_DAY, 24);
		return calendar.getTime();
	}
	
	public boolean isTokenBlackListed(String authToken) {
		return (null != blacklistedTokenRepository.findByBlackListedToken(authToken));
	}
}
