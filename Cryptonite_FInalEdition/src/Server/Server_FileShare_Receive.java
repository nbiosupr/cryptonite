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
	// ������ �� �ʱ�ȭ���� �ν��Ͻ�
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private boolean stopFlag = false;
	
	// OTP�������� �ν��Ͻ�
	private boolean checkOTP = false;
	private Vector<String> OTP_List = new Vector<String>();
	private String OTP = "";
	
	// Ŭ���̾�Ʈ���� OTP���� ����
	private OutputStream out = null;
	private DataOutputStream dos = null;
	
	// Ŭ���̾�Ʈ�κ��� ���ϼ��� ����
	private File shareFile = null;
	private InputStream in = null;
	private BufferedInputStream bis = null;
	private ObjectInputStream ois = null;
	private DataInputStream dis = null;
	
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	
	// ��Ÿ �ν��Ͻ�
	private String[] fileNames = null;
	private byte[] buffer = null;
	private long totalSize = 0;
	private int bufferSize = 1024;
	
	// �������� �۽����� �÷��� �����ֱ�
	private boolean flag = true;			//	true�� ���ſ�,	false�� �۽ſ�
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
				System.out.println(getTime() + " ���ϰ��� ������ ������Դϴ�.");
				socket = serverSocket.accept();
				System.out.println(getTime() + " Ŭ���̾�Ʈ���Լ� ���ϰ��� ��ȣ�� �Խ��ϴ�.");
				
				buffer = new byte[bufferSize];
				
				in = socket.getInputStream();
				ois = new ObjectInputStream(in);
				dis = new DataInputStream(in);
				
				out = socket.getOutputStream();
				dos = new DataOutputStream(out);		
				
				System.out.println(getTime() + " OTP�� Ŭ���̾�Ʈ���� �����մϴ�.");
				this.OTP ="";
				this.OTP = makeOTP();
				dos.writeUTF(this.OTP);		// Ŭ���̾�Ʈ���� OTP�� �����մϴ�.	// 1��°
				dos.flush();
				
				try {
					fileNames = (String[]) ois.readObject();	//	Ŭ���̾�Ʈ�κ��� ��ü�� �޽��ϴ� // 2��°
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				while((goFlag = dis.readBoolean()) == false){}
				
				for(int i = 0; i < fileNames.length; i++){
					System.out.println(getTime() + " ���۹޴� ���� �̸� : " + fileNames[i]);
					shareFile = new File("C:\\Server\\Share\\" + this.OTP + "��" + fileNames[i]);	//	OTP ���̰�, ���ϻ���
					
					this.totalSize = dis.readLong();	// �ѿ뷮 ���� //	3��°
					
					fos = new FileOutputStream(shareFile);
					bos = new BufferedOutputStream(fos);
					bis = new BufferedInputStream(in);

					System.out.println(getTime() + " ���۹޴� ������ �뷮 : " + totalSize);
					int readCount = 0;
					while(totalSize > 0){		// 5
						readCount = bis.read(buffer);
						bos.write(buffer, 0, readCount);
						totalSize -= readCount;
					}
					bos.close();
					fos.close();
					System.out.println(getTime() + " " + fileNames[i] + "������ ���� �Ǿ����ϴ�.");
					
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