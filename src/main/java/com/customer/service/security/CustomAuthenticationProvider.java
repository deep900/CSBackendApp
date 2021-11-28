/**
 * 
 */
package com.customer.service.security;

import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.crm.employee.AdminUser;
import com.crm.employee.Employee;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pradheep
 *
 */
@Component
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		log.info("Inside authentication provider.");
		String userEmail = authentication.getPrincipal().toString();
		log.info("Trying to authenticate the user:" + userEmail);
		String userPassword = authentication.getCredentials().toString();
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);		
		log.info("Found the user details:" + userDetails.getPassword());		
		if (userDetails.getPassword().equals(userPassword)) {
			log.info("Authentication was successful");
			LoginDTO authObj = new LoginDTO();
			authObj.setAuthenticated(true);
			authObj.setUserName(userEmail);
			authObj.setPassword(userPassword);
			List<GrantedAuthority> authorities = (List<GrantedAuthority>) userDetails.getAuthorities();
			authObj.setAuthorities(authorities);
			authentication = authObj;
		} else {
			log.error("Authentication failed, user name or password was not matching.");
			authentication.setAuthenticated(false);
		}
		return authentication;
	}

	public SecretKey getSecretKey(UserDetails userDetails) {
		byte[] bytes = null;
		if (userDetails instanceof Employee) {
			Employee employee = (Employee) userDetails;
			bytes = employee.getSecretKey();
		} else if (userDetails instanceof AdminUser) {
			AdminUser adminUser = (AdminUser) userDetails;
			bytes = adminUser.getSecretKey();
		}
		if (null == bytes) {
			return null;
		} else {
			return SecurityUtility.convertByteToObject(bytes);
		}
	}

	public boolean supports(Class<?> authentication) {
		return true;
	}

}
