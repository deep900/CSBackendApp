/**
 * 
 */
package com.customer.service.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Pradheep
 *
 */
@Setter
@Getter
@NoArgsConstructor
public class ResponseModel {

	private int responseCode = 0;

	private Object message = "";
	
	private String errorMessage = "";

	public ResponseModel(int responseCode, Object message) {
		this.responseCode = responseCode;
		this.message = message;
	}
	
	public ResponseModel(int responseCode, Object message,Throwable throwable) {
		this.responseCode = responseCode;
		this.message = message;
		this.errorMessage = throwable.getMessage();
	}
}
