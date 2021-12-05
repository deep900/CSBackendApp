/**
 * 
 */
package com.customer.app.service;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.beans.factory.annotation.Autowired;

import com.crm.employee.AdminUser;
import com.customer.service.repository.AdminUserRepository;
import com.customer.service.security.SecurityUtility;

/**
 * @author deep90
 *
 */
public class UserCreationBean  {

	@Autowired
	private AdminUserRepository adminUserRepository;
	
	public void createAdminUser() {
		AdminUser adminUser = new AdminUser();
		adminUser.setFirstName("Pradheep");
		adminUser.setLastName("Ponnuswamy");
		adminUser.setUserEmail("pradheep@gmail.com");
		String originalInput = "12345";
		String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
		System.out.println("Encoded passwordStr:" + encodedString);
		
		try {
			SecretKey key = SecurityUtility.generateKey();
			adminUser.setSecretKey(SecurityUtility.convertObjectToByteArray(key));
			try {
				IvParameterSpec spec =  SecurityUtility.generateIv();
				adminUser.setPassword(SecurityUtility.encrypt(SecurityUtility.AESAlgorithm, encodedString, key, spec));				
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		adminUserRepository.save(adminUser);
	}

	public void createNormalUser() {

	}

}
