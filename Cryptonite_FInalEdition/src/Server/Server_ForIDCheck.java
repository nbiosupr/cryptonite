package Server;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server_ForIDCheck
{
	// 서버 연결관련
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	
	// 이름관련 인스턴스
	private String[] filenames = null;
	private File privacyFolder = null;
	
	// 아이디 관련 인스턴스
	private String id = null;
	private boolean endIDCheck = false;
	
	// MessageDigest 보여주기용
	private MessageDigest md = null;
	private byte[] encryptedID = null;
	
	public Server_ForIDCheck(ServerSocket serverSocket, Socket socket)
	{
		this.serverSocket = serverSocket;
		this.socket = socket;
	}
	
	public boolean getEndIDCheck()
	{
		return endIDCheck;
	}
	
	public void compareID()
	{
		boolean goSignUP = true;
		try 
		{
			privacyFolder = new File("C:\\Server\\Privacy");
			filenames = privacyFolder.list();
			System.out.println(getTime() + " [ID_CHECK] 아이디 중복체크를 합니다.");
			
			InputStream in = socket.getInputStream();
			DataInputStream dis = new DataInputStream(in);
			
			this.id = dis.readUTF();
			
			md = MessageDigest.getInstance("SHA-256");
			md.update(this.id.getBytes());
			encryptedID = md.digest();
			System.out.println(getTime() + " [ID_CHECK] 클라이언트로부터 전달받은 ID : " + new String(Server_Base64Coder.encode(encryptedID)));
			
			OutputStream out = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			
			for(int i = 0; i < filenames.length; i++){
				if((id+".txt").equals(filenames[i]) == true){
					System.out.println(getTime() + " [ID_CHECK] 아이디가 중복됩니다.");
					goSignUP = false;
					dos.writeBoolean(false);
					break;
				}
			}
			if(goSignUP == true){
				dos.writeBoolean(true); //
				dos.flush(); //
			}
			
			dos.close();
			out.close();
			
			dis.close();
			in.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
	}
	
	static String getTime()
	{
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	}
}