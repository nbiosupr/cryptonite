package Server;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.io.*;
import java.util.*;
import java.net.*;
import java.text.SimpleDateFormat;

import User.User;

public class Server_ReceivePrivacy
{
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	
	private File userFolder = null;
	private File userPrivacy = null;
	
	private File userObject = null;
	private FileOutputStream fos = null;
	private ObjectOutputStream oos = null;
	
	private String name = null;
	private String id = null;
	private String password = null;
	private String email = null;
	private String AES_Key = null;
	private String salt = null;
	private int iteration = 0;
	private int loginCount = 0;
	
	private User user = null;
	
	private String sumString = null;
	
	public Server_ReceivePrivacy(ServerSocket serverSocket, Socket socket)
	{
		this.serverSocket = serverSocket;
		this.socket = socket;
	}
	
	public void getPrivacy()
	{
		try{
			System.out.println(getTime() + " [SignUP] 회원가입으로 이동");
			System.out.println(getTime() + " [SignUP] 클라이언트와 연결되었습니다.");
			InputStream in = socket.getInputStream();
			DataInputStream dis = new DataInputStream(in);
			
			name = dis.readUTF();
			id = dis.readUTF();
			password = dis.readUTF();
			email = dis.readUTF();
			AES_Key = dis.readUTF();		// 바이트
			salt = dis.readUTF();			// 바이트
			iteration = dis.readInt();
			ObjectInputStream ois = new ObjectInputStream(in);
			user = (User)ois.readObject();
			
			userFolder = new File("C:\\Server\\Backup", id);
			userFolder.mkdir();
			
			sumString = name + "★" + id + "★" + password + "★" + email + "★" + AES_Key + "★" + salt + "★" + iteration + "★" + loginCount;
			userPrivacy = new File("C:\\Server\\Privacy",(id + ".txt"));
			System.out.println(getTime() + " [SignUP] 생성완료");
			
			FileWriter fw = new FileWriter(userPrivacy);
			fw.write(sumString);
			fw.flush();	//
			
			userObject = new File("C:\\Server\\UserObject", (id + ".cnob"));
			fos = new FileOutputStream(userObject);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(user);
			oos.flush();
			oos.close();
			fos.close();
			
			dis.close();
			in.close();
			fw.close();
			//회원가입 완료
		}
		catch(IOException e){
			e.printStackTrace();
		} 
		 catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static String getTime()
	{
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	}
}