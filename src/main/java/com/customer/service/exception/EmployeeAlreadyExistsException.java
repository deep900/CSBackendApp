/**
 * 
 */
package com.customer.service.exception;

/**
 * @author deep90
 *
 */
public class EmployeeAlreadyExistsException extends RuntimeException {
	
	public EmployeeAlreadyExistsException(String message) {
		super(message);
	}
}
