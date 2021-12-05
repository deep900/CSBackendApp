/**
 * 
 */
package com.customer.service.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;

import com.crm.approvals.Approval;
import com.crm.security.JwtUser;
import com.customer.service.events.CSApplicationEventPublisher;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pradheep
 *
 */
@Slf4j
public class BaseController {
	
	public Gson gson = new Gson();

	@Autowired
	private CSApplicationEventPublisher eventPublisher;

	ResponseModel model = new ResponseModel();

	public ResponseModel getSuccessResponseModel() {
		return new ResponseModel(ErrorCodes.SUCCESS, ApplicationMessages.SUCCESS_MSG);
	}
	
	public ResponseModel getSuccessResponseModel(Object message) {
		return new ResponseModel(ErrorCodes.SUCCESS, message);
	}	

	public ResponseModel getFailureMessage(String message) {
		return new ResponseModel(ErrorCodes.FAILURE,message);
	}

	public ResponseModel getFailureMessage(String message, Throwable throwable) {
		return new ResponseModel(ErrorCodes.FAILURE, message, throwable);
	}

	public JwtUser extractUserInfo(HttpServletRequest request) {
		return null;
	}

	public void handleApproval(Approval approval) {
		log.info("Handle approval workflow " + approval.toString());
		this.eventPublisher.publishEvent(new MyApplicationEvent(approval));
	}

	class MyApplicationEvent extends ApplicationEvent {

		public MyApplicationEvent(Object source) {
			super(source);
		}
	}
	
	class EmployeeCreatedEvent extends ApplicationEvent {
		public EmployeeCreatedEvent(Object source) {
			super(source);
		}
	}
}
