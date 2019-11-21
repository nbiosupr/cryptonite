package Crypto;

/*
 * Developed by Park Sang Woon (Dept. Information Security at Suwon University)
 * */

import java.io.*;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class FileED {
	
	static final String algorithm = "AES"; 
	private static final String transformation = algorithm + "/ECB/PKCS5Padding";
	public static Vector<String> filenameVector = new Vector<String>(); // 
	public static Vector<String> encryptedVector = new Vector<String>();	// �ִϸ��̼ǿ�
	static final String EXTENDED_FILE_NAME = ".cnec";
	private File dest = null;
	private boolean bigFileFlag = false;
	
	/*
	 * AES Encryption
	 * ��Ͼ�ȣȭ ����� : ECB 
	 * �е�: PKCS5Padding
	 */
	 
	private SecretKey key;
	
	public FileED(SecretKey key) {
		this.key = key;
	}
	
	/*
	 * ���� ������ ��ȣȭ�ؼ� ��� ������ �����.
	 * 
	 * source ���� ����
	 * dest Ÿ�� ����
	 */
	
	public void encrypt(File source) throws Exception {
		if(source.isDirectory() == true){
			crypt1(Cipher.ENCRYPT_MODE, source, dest);
		}
		else{
			dest = new File(source.getPath()+EXTENDED_FILE_NAME);
			crypt1(Cipher.ENCRYPT_MODE, source, dest);
			source.delete();
			}
	}
	
	/*
	 * ���� ������ ��ȣȭ�ؼ� ��� ������ �����.
	 * 
	 * source ���� ����
	 * dest Ÿ�� ����
	 */
	/*public void decrypt(File source) throws Exception {
		String[] destpath = source.getPath().split(EXTENDED_FILE_NAME);
		System.out.println(destpath[0]);
		File dest = new File(destpath[0]);
		crypt1(Cipher.DECRYPT_MODE, source, dest);
		//source.delete();
	}*/
	
	/*
	 * ���� ������ ��/��ȣȭ�ؼ� ��� ������ �����.
	 * 
	 * mode ��/��ȣȭ ���
	 * source ���� ����
	 * dest Ÿ�� ����
	 */
	private void crypt1(int mode, File source, File dest) throws Exception {
		encryptedVector.add(dest.getPath());
		Cipher cipher = Cipher.getInstance(transformation);
		cipher.init(mode, key);
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		
		if(source.isDirectory() == false){
			try {
				input = new BufferedInputStream(new FileInputStream(source));
				output = new BufferedOutputStream(new FileOutputStream(dest));
				byte[] buffer = new byte[1024];
				int read = -1;
				while ((read = input.read(buffer)) != -1) {
					output.write(cipher.update(buffer, 0, read));
				}
				output.write(cipher.doFinal());
				//synchronized (this){
					filenameVector.add(dest.getPath()); //
					encryptedVector.remove(0);
				//}
			}
			catch(FileNotFoundException e){
				do{
					try{
						input = new BufferedInputStream(new FileInputStream(source));
						output = new BufferedOutputStream(new FileOutputStream(dest));
						byte[] buffer = new byte[1024];
						int read = -1;
						while ((read = input.read(buffer)) != -1) {
							output.write(cipher.update(buffer, 0, read));
						}
						output.write(cipher.doFinal());
						filenameVector.add(dest.getPath()); // 
						encryptedVector.remove(0);
						bigFileFlag = true;
					}
					catch(FileNotFoundException e2){}
					finally {
						if (output != null) {
							try { output.close(); } catch(IOException ie) {}
						}
						if (input != null) {
							try { input.close(); } catch(IOException ie) {}
						}
					}
				}while(bigFileFlag != true);
			}
			finally {
				if (output != null) {
					try { output.close(); } catch(IOException ie) {}
				}
				if (input != null) {
					try { input.close(); } catch(IOException ie) {}
				}
			}
		}
		else
			filenameVector.add(source.getPath());
	}
}
