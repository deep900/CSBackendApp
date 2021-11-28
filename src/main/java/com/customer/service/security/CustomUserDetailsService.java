package com.customer.service.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.crm.employee.AdminUser;
import com.crm.employee.Employee;
import com.crm.employee.User;
import com.crm.security.AdminUserPrevilege;
import com.crm.security.Previlege;
import com.customer.service.repository.AdminUserPrevilegeRepository;
import com.customer.service.repository.AdminUserRepository;
import com.customer.service.repository.EmployeeRepository;
import com.customer.service.repository.PrevilegeRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private AdminUserRepository adminUserRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private AdminUserPrevilegeRepository adminUserPrevilegeRepository;

	@Autowired
	private PrevilegeRepository previlegeRepository;

	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		log.info("Trying to find the user with email: " + userEmail);
		List<AdminUser> adminUserList = adminUserRepository.findByUserEmail(userEmail);
		if (null == adminUserList || adminUserList.isEmpty()) {
			log.debug("User not found in admin user list");
			List<Employee> employeeList = employeeRepository.findByEmail(userEmail);
			if (null != employeeList && !employeeList.isEmpty()) {
				Employee employeeObj = employeeList.get(0);
				byte[] key = employeeObj.getSecretKey();
				if (null != key) {
					SecretKey secretKey = SecurityUtility.convertByteToObject(key);
					String decryptedPassword = SecurityUtility.getDecryptedPassword(employeeObj.getPassword(),
							secretKey, SecurityUtility.generateIv());
					employeeObj.setPassword(decryptedPassword);
				}
				log.info("Found user in employee list " + employeeObj.toString());
				return employeeObj;
			}
		} else {
			AdminUser adminUserObj = adminUserList.get(0);
			byte[] key = adminUserObj.getSecretKey();
			if (null != key) {
				SecretKey secretKey = SecurityUtility.convertByteToObject(key);
				String decryptedPassword = SecurityUtility.getDecryptedPassword(adminUserObj.getPassword(), secretKey,
						SecurityUtility.generateIv());
				adminUserObj.setPassword(decryptedPassword);
			}
			adminUserObj.setGrantedAuthorities(getAdminUserAuthorities(adminUserObj.getId()));			
			User userObjj = (User) adminUserObj;
			log.info("Printing the user details:" + userObjj.isAccountNonLocked());
			log.info("Found the admin user details:" + adminUserObj.toString());
			return adminUserObj;
		}
		throw new UsernameNotFoundException("User with email " + userEmail + " is not found");
	}

	public List<GrantedAuthority> getAdminUserAuthorities(Integer adminUserId) {
		log.info("Trying to fetch the admin user previleges:" + adminUserId);
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		List<AdminUserPrevilege> adminUserPrevilegeList = adminUserPrevilegeRepository.findByAdminUserId(adminUserId);
		Iterator<AdminUserPrevilege> adminUserPrevilegeIterator = adminUserPrevilegeList.iterator();
		while (adminUserPrevilegeIterator.hasNext()) {
			Integer id = adminUserPrevilegeIterator.next().getPrevilegeId();
			log.info("Trying to fetch the previlege by id" + id);
			Optional<Previlege> previlege = previlegeRepository.findById(id);
			if (previlege.isPresent()) {
				authorities.add(new SimpleGrantedAuthority(previlege.get().getPrevilegeName()));
			}
		}
		return authorities;
	}
}
