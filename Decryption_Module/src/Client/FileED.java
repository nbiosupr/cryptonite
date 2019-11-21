package Client;
/*
 * Writed by Sangwoon Park (Dept. Information Security at Suwon University)
 */

import java.io.*;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class FileED {
	
	static final String algorithm = "AES"; 
	private static final String transformation = algorithm + "/ECB/PKCS5Padding";
	public static final String EXTENDED_FILE_NAME = ".cnec";
	
	/*
	 * AES Encryption
	 * 블록암호화 운용모드 : ECB 
	 * 패딩: PKCS5Padding
	 */
	 
	private SecretKey key;
	
	public FileED(SecretKey key) {
		this.key = key;
	}
	
	/*
	 * 원본 파일을 암호화해서 대상 파일을 만든다.
	 * 
	 * source 원본 파일
	 * dest 타켓 파일
	 */
	
	public void encrypt(File source) throws Exception {
		File dest = new File(source.getPath()+EXTENDED_FILE_NAME);
		crypt(Cipher.ENCRYPT_MODE, source, dest);
		source.delete();
	}
	
	/*
	 * 원본 파일을 복호화해서 대상 파일을 만든다.
	 * 
	 * source 원본 파일
	 * dest 타켓 파일
	 */
	public void decrypt(File source, File dest, boolean fileDelite) throws Exception {
		//String[] destpath = source.getPath().split(EXTENDED_FILE_NAME);
		//File dest = new File(destpath[0]);
		System.out.println("fileEd decrypt 파일주소: " + dest.getPath());
		crypt(Cipher.DECRYPT_MODE, source, dest);
		if(fileDelite)
			source.delete();
	}
	
	/*
	 * 원본 파일을 암/복호화해서 대상 파일을 만든다.
	 * 
	 * mode 암/복호화 모드
	 * source 원본 파일
	 * dest 타겟 파일
	 */
	private void crypt(int mode, File source, File dest) throws Exception {
		Cipher cipher = Cipher.getInstance(transformation);
		cipher.init(mode, key);
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {
			//System.out.println("crypt에서 source네임 : " + source.getName());
			input = new BufferedInputStream(new FileInputStream(source));
			output = new BufferedOutputStream(new FileOutputStream(dest));
			byte[] buffer = new byte[1024];
			int read = -1;
			while ((read = input.read(buffer)) != -1) {
				output.write(cipher.update(buffer, 0, read));
			}
			output.write(cipher.doFinal());
		} finally {
			if (output != null) {
				try { output.close(); } catch(IOException ie) {}
			}
			if (input != null) {
				try { input.close(); } catch(IOException ie) {}
			}
		}
	}
}
