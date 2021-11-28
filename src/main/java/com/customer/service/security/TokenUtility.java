/**
 * 
 */
package com.customer.service.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.customer.service.exception.InvalidRefreshTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Pradheep
 *
 */
public class TokenUtility {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.timeToLiveInSecs}")
	private int seconds;

	private static final String ROLES = "ROLES";

	private static final String ISSUER = "Customer Service";

	private static final String REFRESH = "REFRESH_TOKEN";

	public Token generateToken(User userObj) {
		Claims claims = Jwts.claims().setSubject(userObj.getUsername());
		StringBuffer roles = new StringBuffer();
		Iterator<GrantedAuthority> rolesIterator = userObj.getAuthorities().iterator();
		while (rolesIterator.hasNext()) {
			roles.append(rolesIterator.next().getAuthority());
			if (rolesIterator.hasNext()) {
				roles.append(",");
			}
		}
		claims.put(ROLES, roles.toString());
		claims.setIssuer(ISSUER);
		Optional<Date> expirationDate = getExpirationDate();
		if (expirationDate.isPresent()) {
			claims.setExpiration(expirationDate.get());
		}
		String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
		Token tokenObj = new Token();
		tokenObj.setToken(token);
		tokenObj.setRefreshToken(generateRefreshToken(userObj.getUsername(), expirationDate.get(),roles.toString()));
		return tokenObj;
	}

	/**
	 * Refresh token is valid for one minute from the expiry of token.
	 * 
	 * @param email
	 * @param tokenExpiryDate
	 * @return
	 */
	public String generateRefreshToken(String email, Date tokenExpiryDate,String roles) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(tokenExpiryDate);
		calendar.add(GregorianCalendar.MINUTE, 1);
		Claims claims = Jwts.claims().setSubject(email);
		claims.setExpiration(calendar.getTime());
		claims.put(ROLES,roles);
		claims.setNotBefore(tokenExpiryDate);
		claims.setAudience(REFRESH);
		claims.setIssuer(ISSUER);
		String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
		return token;
	}

	public Optional<Date> getExpirationDate() {
		if (seconds <= 0) {
			return Optional.empty();
		}
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(GregorianCalendar.SECOND, seconds);
		return Optional.of(calendar.getTime());
	}

	public User parseToken(String jwtToken) throws JwtException {
		Claims body = (Claims) Jwts.parser().setSigningKey(secret).parse(jwtToken).getBody();
		String userEmail = body.getSubject();
		String roles = body.get(ROLES).toString();
		System.out.println(roles);
		User userObj = new User(userEmail, "", getGrantedAuthorities(roles));
		return userObj;
	}

	public User parseRefreshToken(String jwtToken) throws JwtException, InvalidRefreshTokenException {
		Claims body = (Claims) Jwts.parser().setSigningKey(secret).parse(jwtToken).getBody();
		if (!body.getAudience().equalsIgnoreCase(REFRESH) || !body.getIssuer().equalsIgnoreCase(ISSUER)) {
			throw new InvalidRefreshTokenException("Not a valid refresh token");
		}
		User userObj = parseToken(jwtToken);
		return userObj;
	}

	private List<GrantedAuthority> getGrantedAuthorities(String roles) {
		String[] authorities = roles.split(",");
		List<GrantedAuthority> grantedAuthoritiesList = new ArrayList<GrantedAuthority>();
		if (roles.length() > 0 && authorities.length == 0) {
			System.out.println(roles);
			grantedAuthoritiesList.add(new SimpleGrantedAuthority(roles));
			return grantedAuthoritiesList;
		}
		for (int i = 0; i < authorities.length; i++) {
			String role = authorities[i];
			SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
			grantedAuthoritiesList.add(authority);
		}
		System.out.println(grantedAuthoritiesList.toString());
		return grantedAuthoritiesList;
	}

	public boolean checkIfTokenExpired(String jwtToken) {
		try {
			Claims body = (Claims) Jwts.parser().setSigningKey(secret).parse(jwtToken).getBody();
		} catch (ExpiredJwtException err) {
			return true;
		}
		return false;
	}

}
