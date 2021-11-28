/**
 * 
 */
package com.customer.service.exception;

/**
 * This is thrown when the user authorization is failed.
 * 
 * @author Pradheep
 *
 */
public class AuthorizationException extends RuntimeException {

	public AuthorizationException(String message) {
		super(message);
	}

}
