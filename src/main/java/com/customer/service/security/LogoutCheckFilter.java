/**
 * 
 */
package com.customer.service.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.customer.app.service.BlacklistService;
import com.customer.app.service.UrlSkipService;
import com.customer.service.exception.TokenBlacklistedException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pradheep
 *
 */
@Slf4j
public class LogoutCheckFilter extends OncePerRequestFilter {

	@Autowired
	private BlacklistService blackListService;

	@Autowired
	private UrlSkipService urlSkipService;	

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {		
		log.info("####Checking for token in black list.###$");		
		if (!urlSkipService.canSkipUrl(request)) {
			log.info("Checking for blacklisted token");
			String authHeader = request.getHeader("Authorization");
			if (null == authHeader || authHeader.isEmpty()) {
				log.info("Unable to check for blacklisted token filter. Check the authorization header.");
				filterChain.doFilter(request, response);
			} else {
				if (this.blackListService.isTokenBlackListed(authHeader)) {
					throw new TokenBlacklistedException("User logged out already.");
				} else {
					filterChain.doFilter(request, response);
				}
			}
		} else {
			log.info("Skipped the logout check filter");
			filterChain.doFilter(request, response);
		}
	}

}
