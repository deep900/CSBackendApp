/**
 * 
 */
package com.customer.service.controller;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crm.approvals.Approval;
import com.crm.approvals.ApprovalReasons;
import com.crm.employee.Department;
import com.crm.employee.Employee;
import com.crm.organization.Organization;
import com.crm.security.JwtUser;
import com.crm.security.JwtUser.JwtUserType;
import com.customer.app.service.AdminService;
import com.customer.app.service.ApprovalService;
import com.customer.service.exception.OrganizationAlreadyExists;
import com.customer.service.security.SecurityUtility;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides the Admin functions for Customer Service.
 * 
 * @author Pradheep
 *
 */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController extends BaseController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private ApprovalService approvalService;

	@PostMapping("/createOrg")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody ResponseModel createOrganization(@RequestBody Organization organization) {
		log.info("Creating a new organization:" + organization.toString());
		try {
			if (adminService.createOrganization(organization)) {
				return getSuccessResponseModel("Successfully created the organization");
			} else {
				return getFailureMessage("Unable to create an organization");
			}
		} catch (OrganizationAlreadyExists exp) {
			log.error("Organization already exists", exp);
			return getFailureMessage("Organization already exists", exp);
		}
	}

	@GetMapping("/deleteOrg")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody ResponseModel deleteOrganization(HttpServletRequest request) {
		String orgId = request.getParameter("orgId");
		log.info("Deletion of organization request: " + orgId);
		if (null == orgId) {
			return getFailureMessage("Invalid organization id, cannot delete");
		}
		Integer organizationId = null;
		try {
			organizationId = Integer.parseInt(orgId);
		} catch (Exception err) {
			log.error("Error in converting to integer, invalid organization id", err);
			return getFailureMessage("Invalid organization id, expected a integer.");
		}
		Optional<Organization> organization = adminService.getOrganizationById(organizationId);
		if (!organization.isPresent()) {
			log.info("Unable to find the organization for id:" + orgId);
			return getFailureMessage("Unable to find the organization");
		}
		JwtUser userObj = this.extractUserInfo(request);
		int headOfOrg = organization.get().getHeadOfOrganizationId();
		if (headOfOrg == 0) {
			ResponseModel responseModel = getFailureMessage("Couldn't find the head of the organization to delete.");
			return responseModel;
		}
		Optional<Employee> approver = adminService.getEmployee(headOfOrg);
		Approval approval = createApprovalRequest(userObj, approver.get(), organization,
				ApprovalReasons.DELETE_ORGANIZATION);
		handleApproval(approval);
		ResponseModel model = getSuccessResponseModel();
		model.setMessage("Request for deletion of organization is given, will be deleted after approval.");
		return model;
	}

	@PostMapping("/createDepartment")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody ResponseModel createDepartment(@RequestBody Department department) {
		log.info("Creating a department :" + department.toString());
		try {
			if (null == department.getName() || department.getName().isEmpty()) {
				return getFailureMessage("Department name is invalid.");
			}
			if (this.adminService.createDepartment(department)) {
				log.info("Created the department successfully.");
				return getSuccessResponseModel();
			} else {
				return getFailureMessage("Unable to create the department.");
			}
		} catch (Exception err) {
			return getFailureMessage("Unable to create the department", err);
		}
	}

	@GetMapping("/deleteDepartment")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody ResponseModel deleteDepartment(HttpServletRequest request) {
		String departmentId = request.getParameter("deptId");
		if (null == departmentId || departmentId.isEmpty()) {
			log.error("Invalid department id, check the request parameter.");
			return getFailureMessage("Invalid department ID");
		}
		try {
			Integer deptId = Integer.parseInt(departmentId);
			Optional<Department> departmentObj = adminService.getDepartmentById(deptId);
			if (departmentObj.isPresent()) {
				Department department = departmentObj.get();
				Employee approver = department.getDepartmentLead();
				if (null == approver) {
					log.error("Unable to delete the department, no approver found.");
					return getFailureMessage("Unable to find the approver for the department");
				} else {
					JwtUser userObj = this.extractUserInfo(request);
					Approval approval = createApprovalRequest(userObj, approver, department,
							ApprovalReasons.DELETE_DEPARTMENT);
					handleApproval(approval);
					log.info("Approval request for deletion of department is submitted.");
					return getSuccessResponseModel();
				}
			} else {
				log.error("No such department found");
				return getFailureMessage("No such department found");
			}
		} catch (NumberFormatException exp) {
			log.error("Invalid department id", exp);
			return getFailureMessage("Expected an integer for the department id field.");
		}
	}

	@PostMapping("/createEmployee")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
	public ResponseModel createEmployee(@RequestBody Employee employeeObj) {
		log.info("Trying to create a new employee" + employeeObj.toString());
		if (adminService.createEmployee(employeeObj)) {
			return getSuccessResponseModel("Successfully created the employee");
		} else {
			return getFailureMessage("Unable to create the employee, check the server logs");
		}
	}

	private Approval createApprovalRequest(JwtUser userObj, Employee approver, Object obj, ApprovalReasons reason) {
		Approval approval = null;
		if (userObj.getUserType().equals(JwtUser.JwtUserType.ADMIN_USER.name())) {
			approval = this.approvalService.createApprovalRequest(obj, reason, userObj.getUserId(), -1,
					approver.getId());
		} else if (userObj.getUserType().equals(JwtUserType.EMPLOYEE.name())) {
			approval = this.approvalService.createApprovalRequest(obj, reason, -1, userObj.getUserId(),
					approver.getId());
		}
		return approval;
	}

	@GetMapping("/getAllOrg")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseModel getAllOrganization() {
		Iterable<Organization> orgIter = adminService.getAllOrganization();
		Map<String, String> orgMap = new HashMap<String, String>();
		orgIter.forEach(org -> {
			orgMap.put(getEncryptedId(SecurityUtility.addPaddingToEncryptString(String.valueOf(org.getId()))),
					org.getOrganizationName());
		});
		return this.getSuccessResponseModel(orgMap);
	}

	private String getEncryptedId(String arg) {
		try {
			return SecurityUtility.encrypt(SecurityUtility.AESAlgorithm, arg, SecurityUtility.getApplicationKey(),
					SecurityUtility.generateIv());
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return null;
	}
}
