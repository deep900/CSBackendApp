/**
 * 
 */
package com.customer.service.exception;

/**
 * @author Pradheep
 *
 */
public class InvalidRefreshTokenException extends RuntimeException {

	public InvalidRefreshTokenException(String message) {
		super(message);
	}
}
