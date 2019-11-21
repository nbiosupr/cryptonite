package User;

import java.io.Serializable;

public class User implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6680318455176874746L;
	private String id;
	private String pw;
	private byte[] aesKey;
	private byte[] salt;
	private int iterationCount;
	
	public User(String id, String pw, byte[] aeskey, byte[] salt, int iterationCount){
		this.id = id;
		this.pw = pw;
		this.aesKey = aeskey;
		this.salt = salt;
		this.iterationCount = iterationCount;
	}
	
	public byte[] getSalt() {
		return salt;
	}
	
	public int getIterationCount() {
		return iterationCount;
	}

	public String getId() {
		return id;
	}

	public String getPw() {
		return pw;
	}

	public byte[] getAesKey() {
		return aesKey;
	}	
}
