/**
 * 
 */
package com.customer.service.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pradheep
 *
 */
@Slf4j
public class SecurityUtility {

	public static String AESAlgorithm = "AES/CBC/PKCS5Padding";

	private static String DELIMITER = "#$%~*(";

	private static SecretKey appKey = null;

	public static IvParameterSpec generateIv() {
		byte[] iv = { 2, 5, 1, 2, 0, 1, 3, 4, 5, 3, 7, 8, 8, 7, 5, 6 };
		return new IvParameterSpec(iv);
	}

	public static SecretKey generateKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		return keyGenerator.generateKey();
	}

	public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
		return new String(plainText);		
	}

	public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] cipherText = cipher.doFinal(input.getBytes());
		return Base64.getUrlEncoder().encodeToString(cipherText);
	}

	public static SecretKey convertByteToObject(byte[] key) {
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(key)) {
			ObjectInputStream oins = new ObjectInputStream(inputStream);
			SecretKey secretKey = (SecretKey) oins.readObject();
			return secretKey;
		} catch (Exception err) {
			log.error("Error while reading bytes to object", err);
		}
		return null;
	}

	public static String getDecryptedPassword(String encryptedString, SecretKey key, IvParameterSpec spec) {
		log.info("Printing the encrypted passwordStr:" + encryptedString);
		try {
			return SecurityUtility.decrypt(SecurityUtility.AESAlgorithm, encryptedString, key, spec);
		} catch (InvalidKeyException e) {
			log.error("Invalid key exception ", e);
		} catch (NoSuchPaddingException e) {
			log.error("No such method exception ", e);
		} catch (NoSuchAlgorithmException e) {
			log.error("No such algorith exception", e);
		} catch (InvalidAlgorithmParameterException e) {
			log.error("Invalid algorithm parameter exception", e);
		} catch (BadPaddingException e) {
			log.error("Bad padding exception ", e);
		} catch (IllegalBlockSizeException e) {
			log.error("Illegal block size exception", e);
		}
		return null;
	}

	public static byte[] convertObjectToByteArray(Object obj) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		byte[] byteArray = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(obj);
			out.flush();
			byteArray = bos.toByteArray();
		} catch (Exception err) {
			log.error("Error in converting the object to byte array.", err);
		} finally {
			try {
				bos.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return byteArray;
	}	
	

	public static void main(String args[]) {
		IvParameterSpec ivParamSpec = generateIv();
		SecretKey key = null;
		try {
			key = generateKey();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		FileOutputStream stream;
		try {
			File f = File.createTempFile("app", "key");
			System.out.println(f.getAbsolutePath());
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(key);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String addPaddingToEncryptString(String originalValue) {
		UUID uid = UUID.randomUUID();
		return uid.toString().substring(0, 8) + DELIMITER + originalValue;
	}

	public static Optional<String> stripOriginalDecryptedString(String decryptedString) {
		if (decryptedString.contains(DELIMITER)) {
			String[] arg = decryptedString.split(DELIMITER);
			if (arg.length == 2) {
				return Optional.of(arg[1]);
			} else {
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}

	public static SecretKey getApplicationKey() {
		if (null == appKey) {
			loadApplicationSecretKey();
		}
		return appKey;
	}

	private static void loadApplicationSecretKey() {
		Resource resource = new ClassPathResource("app.key");
		try (ObjectInputStream ois = new ObjectInputStream(resource.getInputStream())) {
			appKey = (SecretKey) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
