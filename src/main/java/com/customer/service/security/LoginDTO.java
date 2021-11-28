/**
 * 
 */
package com.customer.service.security;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Pradheep
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class LoginDTO implements Authentication {

	public LoginDTO(String userName, String password) {
		this.password = password;
		this.userName = userName;
	}

	public LoginDTO(String userName, String password, String refreshToken) {
		this.userName = userName;
		this.password = password;
		this.refreshToken = refreshToken;
	}
	
	@NotEmpty
	private String userName;
	
	@NotEmpty
	private String password;

	private String refreshToken;

	private List<GrantedAuthority> authorities;

	private boolean authenticated;

	@Override
	public String getName() {
		return userName;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public Object getCredentials() {
		return password;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return userName;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		this.authenticated = isAuthenticated;
	}

}
