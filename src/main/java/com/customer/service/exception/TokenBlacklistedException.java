/**
 * 
 */
package com.customer.service.exception;

/**
 * @author Pradheep
 *
 */
public class TokenBlacklistedException extends RuntimeException {

	public TokenBlacklistedException(String message) {
		super(message);
	}
}
