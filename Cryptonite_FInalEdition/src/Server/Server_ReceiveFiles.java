package Server;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;

public class Server_ReceiveFiles extends Thread
{
	private ServerSocket serverSocket = null;
	private boolean checkLogin = true;	// 훗날 온라인과 오프라인 비교
	private boolean what_is_it;
	private boolean forDirectory;
	
	private String protectedFolder = null;
	private String[] tempProtectedFolder = null;
	private String[] forUpperFolder = null;
	private String fileParent = null;
	
	private String userFolder = null;
	private String id = null;
	private String serverFolder = "C:\\Server\\Backup";
	private String filename = null;
	private int tokenCount = 0;
	
	private byte[] buffer = null;
	private Socket socket = null;
	private StringTokenizer st = null;
	
	private FileWriter fw = null;
	private FileReader fr = null;
	
	private String nameCheck = null;
	private boolean receiveFlag = false;
	
	private boolean stopFlag = false;
	
	public Server_ReceiveFiles(ServerSocket serverSocket)
	{
		this.serverSocket = serverSocket;
	}
	
	public void setID(String id)
	{
		this.id = id;
	}
	
	public synchronized void run()
	{
		try{
			System.out.println(getTime() + " [File_Backup] 클라이언트의 접속을 기다리는 중입니다...");
			while(true){
				
				
				socket = serverSocket.accept();
				System.out.println(getTime() + " [File_Backup] 연결완료 !! 연결된 IP : " + socket.getInetAddress());
				
				if(stopFlag == true){
					this.interrupt();
					wait();
				}
				
				if(checkLogin == true){
					buffer = new byte[512];
					InputStream in = socket.getInputStream();
					DataInputStream dis = new DataInputStream(in);
					BufferedInputStream bis = new BufferedInputStream(in);
					
					protectedFolder = dis.readUTF(); // 보호중인 폴더의 이름을 받는다. 1
					
					fileParent = dis.readUTF();	// 2
					st = new StringTokenizer(fileParent, "\\");
					tempProtectedFolder = new String[st.countTokens()];
					
					int i = 0;
					tokenCount = st.countTokens();
					while(tokenCount != 0){
						tempProtectedFolder[i] = st.nextToken();
							if(tempProtectedFolder[i].equals(protectedFolder)){
								if(tokenCount >= 1){
									forUpperFolder = new String[tokenCount-1];
									for(int j=0; j < forUpperFolder.length; j++){
										forUpperFolder[j] = st.nextToken();
									}
									tokenCount = 0;
								}
							}
						i++;
						if(tokenCount != 0)
							tokenCount--;
					}
					
					userFolder = serverFolder + "\\" + id;
					for(int a=0; a < forUpperFolder.length; a++){
						userFolder += "\\";
						userFolder += forUpperFolder[a];
					}
					
					what_is_it = dis.readBoolean();				// 3
					if(what_is_it == true){ forDirectory = true; }
					else{ forDirectory = false; }
					
					filename = dis.readUTF();	// 파일 및 디렉터리 이름을 받아옵니다.	4
					System.out.println(getTime() + " [File_Backup] 파일 이름 : " + filename);
					
					/*
					 * newFile은 파일이 저장될 경로입니다.
					 */
					
					File newFile = new File(userFolder, filename);
					if(forDirectory  == true){
						newFile.mkdir();
						System.out.println(getTime() + " [File_Backup] 디렉터리 송신 완료!");
					}
					else if(forDirectory == false){
						FileOutputStream fout = new FileOutputStream(newFile);
						BufferedOutputStream bout = new BufferedOutputStream(fout);
						System.out.println(getTime() + " [File_Backup] 파일을 송신중입니다. 잠시만 기다려주세요......");
						int readCount = 0;
						while((readCount = bis.read(buffer)) != -1){		// 5
							bout.write(buffer, 0, readCount);
						}
						
						System.out.println(getTime() + " [File_Backup] 송신 완료!");	
						bout.close();
						fout.close();
						bis.close();
						dis.close();
						in.close();
					}
				}
			}
		}
		catch(SocketException se)
		{
			this.interrupt();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
		}
		}
	
	public void stopThread()
	{
		try {
			this.serverSocket.close();
			stopFlag = true;
		} catch (IOException e) {
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