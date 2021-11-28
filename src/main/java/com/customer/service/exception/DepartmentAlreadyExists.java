/**
 * 
 */
package com.customer.service.exception;

/**
 * @author deep90
 *
 */
public class DepartmentAlreadyExists extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public DepartmentAlreadyExists(String message) {
		super(message);
	}
}
