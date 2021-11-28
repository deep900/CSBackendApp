/**
 * 
 */
package com.customer.service.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.customer.service.controller.ErrorCodes;
import com.customer.service.controller.ResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pradheep
 *
 */
@Slf4j
public class CustomerServiceEntryPoint implements AuthenticationEntryPoint {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {		
		authException.printStackTrace();
		try (ServletServerHttpResponse res = new ServletServerHttpResponse(response)) {
			res.setStatusCode(HttpStatus.UNAUTHORIZED);
			res.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			res.getBody().write(mapper.writeValueAsString(new ResponseModel(ErrorCodes.FAILURE, "Authentication error"))
					.getBytes());
		} catch (Exception err) {
			log.error("Exception in writing the output error", err);
		}

	}

}
