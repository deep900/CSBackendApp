/**
 * 
 */
package com.customer.service.exception;

/**
 * @author Pradheep
 *
 */
public class OrganizationAlreadyExists extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public OrganizationAlreadyExists(String message) {
		super(message);
	}
}
