package Server;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;

public class Server_FileShare_Send extends Thread
{
	// ���� ���� ����
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	
	// About I.O Stream
	private InputStream in = null;
	private DataInputStream dis = null;
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	
	private OutputStream out = null;
	private DataOutputStream dos = null;
	private ObjectOutputStream oos = null;
	private BufferedOutputStream bos = null;
	
	// ���� �÷��� ����
	private boolean stopFlag = false;
	
	// ���� ���۰��� �ν��Ͻ�
	private byte[] buffer = null;
	private String OTP = null;
	private File shareFolder = null;
	private String[] filelist = null;
	private StringTokenizer st = null;
	private String token = null;
	private Vector<String> filenames = null;
	private File sendFile = null;
	private boolean sendFlag = false;
	private boolean OTP_Check_Flag = false;
	
	public Server_FileShare_Send(ServerSocket serverSocket)
	{
		this.serverSocket = serverSocket;
	}
	
	public void run()
	{
		while(!stopFlag){	// close���ٰ͵� : dis, in, 
			try 
			{
				System.out.println(getTime() + " [FileShare] Ŭ���̾�Ʈ�κ��� ���ϼ��� ��ȣ�� ��ٸ��� ���Դϴ�.");
				socket = serverSocket.accept();
				System.out.println(getTime() + " [FileShare] Ŭ���̾�Ʈ�κ��� ���ϼ��� ��ȣ�� ���Խ��ϴ�.");
				
				in = socket.getInputStream();
				dis = new DataInputStream(in);
				
				out = socket.getOutputStream();
				oos = new ObjectOutputStream(out);
				
				OTP = dis.readUTF();		//	Ŭ���̾�Ʈ�κ��� OTP�� �޾ƿɴϴ�.	// 1��°
				System.out.println(getTime() + " [FileShare] Ŭ���̾�Ʈ�κ��� �޾ƿ� OTP : " + OTP);
				shareFolder = new File("C:\\Server\\Share");
				filelist = shareFolder.list();
				filenames = new Vector<String>();
				for(int i = 0; i < filelist.length; i++){
					st = new StringTokenizer(filelist[i], "��");
					token = st.nextToken();
					if(token.equals(OTP) == true){
						token = st.nextToken();
						File forRename = new File("C:\\Server\\Share", filelist[i]);
						forRename.renameTo(new File("C:\\Server\\Share", token));
						filenames.add(token);
						OTP_Check_Flag = true;
					}
				}	// ������� ���Ͼտ� ���� OTP �з�����
				
				if(OTP_Check_Flag == false){
					dos = new DataOutputStream(out);
					dos.writeBoolean(false);
					dos.flush();
				}
				else if(OTP_Check_Flag == true){
					dos = new DataOutputStream(out);
					dos.writeBoolean(true);
					dos.flush();
					
					oos.writeObject(filenames);						// Ŭ���̾�Ʈ�� �����̸���� ���͸� �����մϴ� // 2��°
					
					buffer = new byte[1024];
					
					for(int j = 0 ; j < filenames.size(); j++){
						if(filenames.size() >= 2){
							if(j >= 1){
								while((sendFlag = dis.readBoolean()) == false) { }
							}
						}
						
						sendFile = new File("C:\\Server\\Share", filenames.get(j));
						dos.writeLong(sendFile.length());			// Ŭ���̾�Ʈ�� ������ ������ �뷮�� �����մϴ�. // 3��°
						System.out.println(getTime() + " [FileShare] ������ ���� �̸� : " + sendFile.getName());
						
						fis = new FileInputStream(sendFile);
						bis = new BufferedInputStream(fis);
						bos = new BufferedOutputStream(out);
						
						int readCount = 0;
						while((readCount = bis.read(buffer)) != -1){
							bos.write(buffer, 0, readCount);		//	Ŭ���̾�Ʈ�� ������ �����մϴ�	// 4��°
						}
						this.sendFlag = false;
						bos.flush();
						System.out.println(getTime() + " [FileShare] " + sendFile.getName() + "������ ���������� �����Ͽ����ϴ�.");
						fis.close();
						sendFile.delete();
					}
					
					bos.close();
					bis.close();
					fis.close();
					oos.close();
					dis.close();
					out.close();
					in.close();
				}
			}
			catch(SocketException se)
			{
				// �α׾ƿ��� ���� SocketException������ ��ĭ �����ν� �ذ�
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			
		}
	}
	
	public void stopThread()
	{
		try {
			this.serverSocket.close();
			stopFlag = true;
		} 
		catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	static String getTime()
	{
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	}
}
