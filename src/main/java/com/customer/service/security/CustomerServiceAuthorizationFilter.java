/**
 * 
 */
package com.customer.service.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import com.customer.app.service.UrlSkipService;
import com.customer.service.exception.AuthorizationException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pradheep
 *
 */
@Slf4j
public class CustomerServiceAuthorizationFilter extends OncePerRequestFilter {

	@Autowired
	private TokenUtility tokenUtility;

	@Autowired
	private UrlSkipService urlSkipService;

	public Authentication translateUserToAuthentication(User userObj) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userObj.getUsername(), userObj.getAuthorities());
		log.info("Size of authorities:" + userObj.getAuthorities().size());
		return authentication;
	}

	public void filter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.info("Intercepting the request at Customer Service Authorization Filter.");
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		
		if (urlSkipService.canSkipUrl(httpServletRequest)) {
			log.info("Skip the filter :" + this.getClass().getName());
			chain.doFilter(httpServletRequest, response);
			return;
		}
		String jwtToken = httpServletRequest.getHeader("Authorization");
		log.info("Printing the token:" + jwtToken);
		try {
			User userObj = tokenUtility.parseToken(jwtToken);			
			PreAuthenticatedAuthenticationToken auth = new PreAuthenticatedAuthenticationToken(userObj.getUsername(),
					"", userObj.getAuthorities());
			log.info("Size" + auth.getAuthorities().size());
			SecurityContextHolder.getContext().setAuthentication(auth);
			log.info("Set the auth in security context.");
		} catch (Exception err) {
			log.error("Error while authorizing the token", err);
			throw new AuthorizationException(err.getMessage());
		}
		chain.doFilter(request, response);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		filter(request, response, filterChain);
	}

}
