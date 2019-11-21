package Client;

/*
 * Writed by Sangwoon Park (Dept. Information Security at Suwon University)
 */

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PBE {
	
	//private Charset charset = Charset.forName("UTF-8");
	
	private byte[] salt;
	private int iterationCount;
	
	public PBE(byte[] salt, int iterationCount){
		this.salt = salt;
		this.iterationCount = iterationCount;
	}
	
	private SecretKey generateKey(String strPassword) throws GeneralSecurityException {
		//패스워드를 이용한 secretKey 생성
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec keySpec = new PBEKeySpec(strPassword.toCharArray(), salt, iterationCount, 256);
		SecretKey secretKey = new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), "AES");
		return secretKey;
	}
	
	public byte[] encrypt (String password, byte[] aesKey) throws GeneralSecurityException {
		SecretKey secretKey = generateKey(password);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptData = cipher.doFinal(aesKey);
		return encryptData;
	}
	
	public SecretKey decrypt(String password, byte[] aesKey) throws GeneralSecurityException {
		SecretKey secretKey = generateKey(password);
		byte[] plainData = null;
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		try{
			plainData = cipher.doFinal(aesKey);
		} catch(BadPaddingException e) { e.printStackTrace(); } 
		SecretKey decryptAesKey = new SecretKeySpec(plainData, "AES");
		
		return decryptAesKey;
	}

}
