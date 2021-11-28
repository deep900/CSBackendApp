/**
 * 
 */
package com.customer.service.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.customer.app.service.BlacklistService;
import com.customer.app.service.UserCreationBean;
import com.customer.service.security.LoginDTO;
import com.customer.service.security.Token;
import com.customer.service.security.TokenUtility;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pradheep
 *
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class LoginController extends BaseController {

	@Autowired
	private BlacklistService blackListService;

	@Autowired
	private TokenUtility tokenUtility;

	@Autowired
	private UserCreationBean userCreationBean;

	@Autowired
	private org.springframework.security.authentication.AuthenticationProvider authenticationProvider;

	@PostMapping("/authenticate")
	public ResponseModel loginUser(@Valid @RequestBody LoginDTO loginDTO) throws Exception {
		try {
			// userCreationBean.createAdminUser();
			log.info("Trying to login the user :" + loginDTO.getUserName());
			Authentication authentication = authenticationProvider.authenticate(loginDTO);
			if (authentication.isAuthenticated()) {
				Token token = issueNewToken(authentication);
				ResponseModel response = new ResponseModel(ErrorCodes.SUCCESS, token);
				return response;
			} else {
				return getFailureMessage("Authentication failed, check the user credentials");
			}
		} catch (UsernameNotFoundException err) {
			log.error("No such user found", err);
			return getFailureMessage("Unable to find the user details");
		} catch (Exception err) {
			log.error("Authentication exception", err);
			return getFailureMessage("Unable to authenticate the user.");
		}
	}

	@GetMapping("/logout")
	public ResponseModel logoutUser(HttpServletRequest request) throws Exception {
		String authHeader = request.getHeader("Authorization");
		if (null == authHeader || authHeader.isEmpty()) {
			return getFailureMessage("Invalid authorization header");
		}
		if (blackListService.blackListToken(authHeader)) {
			return getSuccessResponseModel("User logged out successfully");
		} else {
			return getFailureMessage("Unable to logout the user");
		}
	}

	@GetMapping("/refreshToken")
	public ResponseModel refreshToken(HttpServletRequest request) throws Exception {
		String authHeader = request.getHeader("Authorization");
		String rToken = request.getHeader("RefreshToken");
		if (null == authHeader || authHeader.isEmpty()) {
			return getFailureMessage("Invalid authorization header, cannot refresh token.");
		}
		if (blackListService.isTokenBlackListed(authHeader)) {
			return getFailureMessage("User already logged out, cannot refresh token. Login again");
		}
		try {
			if (checkAuthHeaderInRefreshToken(authHeader)) {
				log.info("Issuing a new token as auth header expired.");
				Token token = issueNewToken(refreshToken(rToken));
				blackListService.blackListToken(authHeader);
				blackListService.blackListToken(rToken);
				return new ResponseModel(ErrorCodes.SUCCESS, token);
			} else {
				return getFailureMessage("Authorization token not yet expired");
			}
		} catch (Exception err) {
			log.error("Error while refresh token", err);
			return getFailureMessage("Unable to refresh token. Login again",err);
		}
	}

	private Token issueNewToken(Authentication authentication) {
		log.info("Found " + authentication.getAuthorities().size() + " authorities");
		User userObj = new User(authentication.getName(), "", authentication.getAuthorities());
		return tokenUtility.generateToken(userObj);
	}

	private boolean checkAuthHeaderInRefreshToken(String authHeader) {
		return tokenUtility.checkIfTokenExpired(authHeader);
	}

	private Authentication refreshToken(String token) {
		log.info("Refresh token:" + token);
		User userObj = tokenUtility.parseRefreshToken(token);
		PreAuthenticatedAuthenticationToken auth = new PreAuthenticatedAuthenticationToken(userObj.getUsername(), "",
				userObj.getAuthorities());
		return auth;
	}
}
