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
	// 서버 연결 관련
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
	
	// 정지 플래그 관련
	private boolean stopFlag = false;
	
	// 파일 전송관련 인스턴스
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
		while(!stopFlag){	// close해줄것들 : dis, in, 
			try 
			{
				System.out.println(getTime() + " [FileShare] 클라이언트로부터 파일수신 신호를 기다리는 중입니다.");
				socket = serverSocket.accept();
				System.out.println(getTime() + " [FileShare] 클라이언트로부터 파일수신 신호가 들어왔습니다.");
				
				in = socket.getInputStream();
				dis = new DataInputStream(in);
				
				out = socket.getOutputStream();
				oos = new ObjectOutputStream(out);
				
				OTP = dis.readUTF();		//	클라이언트로부터 OTP를 받아옵니다.	// 1번째
				System.out.println(getTime() + " [FileShare] 클라이언트로부터 받아온 OTP : " + OTP);
				shareFolder = new File("C:\\Server\\Share");
				filelist = shareFolder.list();
				filenames = new Vector<String>();
				for(int i = 0; i < filelist.length; i++){
					st = new StringTokenizer(filelist[i], "★");
					token = st.nextToken();
					if(token.equals(OTP) == true){
						token = st.nextToken();
						File forRename = new File("C:\\Server\\Share", filelist[i]);
						forRename.renameTo(new File("C:\\Server\\Share", token));
						filenames.add(token);
						OTP_Check_Flag = true;
					}
				}	// 여기까지 파일앞에 붙은 OTP 분류과정
				
				if(OTP_Check_Flag == false){
					dos = new DataOutputStream(out);
					dos.writeBoolean(false);
					dos.flush();
				}
				else if(OTP_Check_Flag == true){
					dos = new DataOutputStream(out);
					dos.writeBoolean(true);
					dos.flush();
					
					oos.writeObject(filenames);						// 클라이언트로 파일이름목록 벡터를 전송합니다 // 2번째
					
					buffer = new byte[1024];
					
					for(int j = 0 ; j < filenames.size(); j++){
						if(filenames.size() >= 2){
							if(j >= 1){
								while((sendFlag = dis.readBoolean()) == false) { }
							}
						}
						
						sendFile = new File("C:\\Server\\Share", filenames.get(j));
						dos.writeLong(sendFile.length());			// 클라이언트로 전송할 파일의 용량을 전송합니다. // 3번째
						System.out.println(getTime() + " [FileShare] 전송할 파일 이름 : " + sendFile.getName());
						
						fis = new FileInputStream(sendFile);
						bis = new BufferedInputStream(fis);
						bos = new BufferedOutputStream(out);
						
						int readCount = 0;
						while((readCount = bis.read(buffer)) != -1){
							bos.write(buffer, 0, readCount);		//	클라이언트로 파일을 전송합니다	// 4번째
						}
						this.sendFlag = false;
						bos.flush();
						System.out.println(getTime() + " [FileShare] " + sendFile.getName() + "파일을 성공적으로 전송하였습니다.");
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
				// 로그아웃시 원래 SocketException떴지만 빈칸 둠으로써 해결
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
