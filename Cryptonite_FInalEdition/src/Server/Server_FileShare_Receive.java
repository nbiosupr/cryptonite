package Server;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Server_FileShare_Receive extends Thread
{
	// 생성자 및 초기화관련 인스턴스
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private boolean stopFlag = false;
	
	// OTP생성관련 인스턴스
	private boolean checkOTP = false;
	private Vector<String> OTP_List = new Vector<String>();
	private String OTP = "";
	
	// 클라이언트에게 OTP전송 관련
	private OutputStream out = null;
	private DataOutputStream dos = null;
	
	// 클라이언트로부터 파일수신 관련
	private File shareFile = null;
	private InputStream in = null;
	private BufferedInputStream bis = null;
	private ObjectInputStream ois = null;
	private DataInputStream dis = null;
	
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	
	// 기타 인스턴스
	private String[] fileNames = null;
	private byte[] buffer = null;
	private long totalSize = 0;
	private int bufferSize = 1024;
	
	// 수신인지 송신인지 플래그 나눠주기
	private boolean flag = true;			//	true면 수신용,	false면 송신용
	private boolean goFlag = false;
	
	//
	
	public Server_FileShare_Receive(ServerSocket serverSocket)
	{
		this.serverSocket = serverSocket;
	}
	
	public synchronized void run()
	{
		while(!stopFlag)
		{
			try
			{
				System.out.println(getTime() + " 파일공유 접속을 대기중입니다.");
				socket = serverSocket.accept();
				System.out.println(getTime() + " 클라이언트에게서 파일공유 신호가 왔습니다.");
				
				buffer = new byte[bufferSize];
				
				in = socket.getInputStream();
				ois = new ObjectInputStream(in);
				dis = new DataInputStream(in);
				
				out = socket.getOutputStream();
				dos = new DataOutputStream(out);		
				
				System.out.println(getTime() + " OTP를 클라이언트에게 전송합니다.");
				this.OTP ="";
				this.OTP = makeOTP();
				dos.writeUTF(this.OTP);		// 클라이언트에게 OTP를 전송합니다.	// 1번째
				dos.flush();
				
				try {
					fileNames = (String[]) ois.readObject();	//	클라이언트로부터 객체를 받습니다 // 2번째
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				while((goFlag = dis.readBoolean()) == false){}
				
				for(int i = 0; i < fileNames.length; i++){
					System.out.println(getTime() + " 전송받는 파일 이름 : " + fileNames[i]);
					shareFile = new File("C:\\Server\\Share\\" + this.OTP + "★" + fileNames[i]);	//	OTP 붙이고, 파일생성
					
					this.totalSize = dis.readLong();	// 총용량 수신 //	3번째
					
					fos = new FileOutputStream(shareFile);
					bos = new BufferedOutputStream(fos);
					bis = new BufferedInputStream(in);

					System.out.println(getTime() + " 전송받는 파일의 용량 : " + totalSize);
					int readCount = 0;
					while(totalSize > 0){		// 5
						readCount = bis.read(buffer);
						bos.write(buffer, 0, readCount);
						totalSize -= readCount;
					}
					bos.close();
					fos.close();
					System.out.println(getTime() + " " + fileNames[i] + "파일이 수신 되었습니다.");
					
					if(fileNames.length >= 2){
							dos.writeBoolean(true);
					}
				}
				goFlag = false;
			} 
			catch (SocketException se){
				
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public String makeOTP()
	{
		int temp;
		
		try 
		{
			try
			{
				FileInputStream fis = new FileInputStream("C:\\Server\\OTP\\OTP_List.ser");
				ObjectInputStream ois = new ObjectInputStream(fis);
				OTP_List = (Vector<String>)ois.readObject();
			}
			catch(EOFException e){}
			
			do{
				for(int i = 0; i < 6; i++){
					temp = (int)(Math.random()*10);
					this.OTP += Integer.toString(temp);
				}
				checkOTP = true;
				
				for(int j = 0; j < OTP_List.size(); j++){
					if(OTP.equals(OTP_List.get(j)) == true){
						checkOTP = false;
						break;
					}
				}
				
			} while (checkOTP == false);
			
			OTP_List.add(OTP);
			FileOutputStream fos = new FileOutputStream("C:\\Server\\OTP\\OTP_List.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(OTP_List);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		return OTP;
	}
	
	public void stopThread()
	{
		try 
		{
			this.serverSocket.close();
			this.stopFlag = true;
		} 
		catch (IOException e) 
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