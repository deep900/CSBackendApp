/**
 * 
 */
package com.customer.app.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.crm.approvals.Approval;
import com.crm.approvals.ApprovalReasons;
import com.crm.approvals.ApprovalStatus;
import com.crm.employee.Department;
import com.crm.organization.Organization;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pradheep
 *
 */
@Service
@Slf4j
public class ApprovalService {

	private Gson gson = new Gson();

	public Approval createApprovalRequest(Object obj, ApprovalReasons reason, Integer adminUserId,
			Integer requestEmployeeId, Integer approverId) {
		log.debug("Creating the approval request." + obj.toString());
		Approval approval = getBasicApproval(adminUserId, requestEmployeeId, approverId);
		switch (reason) {
		case DELETE_ORGANIZATION:
			Organization org = (Organization) obj;
			approval.setStatus(ApprovalStatus.PENDING.name());
			approval.setDescription("Organization " + org.getOrganizationName() + " marked for deletion");
			approval.setUpdatedDate(new Date());
			approval.setReferencedObject(gson.toJson(obj));
			return approval;
		case DELETE_DEPARTMENT:
			Department department = (Department) obj;
			approval.setStatus(ApprovalStatus.PENDING.name());
			approval.setDescription("Department " + department.getName() + " marked for deletion");
			approval.setUpdatedDate(new Date());
			approval.setReferencedObject(gson.toJson(obj));
			return approval;
		default:
		}
		return null;
	}

	public Approval getBasicApproval(Integer adminUserId, Integer requestEmpId, Integer approverId) {
		Approval approval = new Approval();
		approval.setRequestDate(new Date());
		return approval;
	}
}
