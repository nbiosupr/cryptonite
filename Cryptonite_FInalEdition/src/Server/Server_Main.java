package Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server_Main
{
	public static void main(String[] args)
	{
			ServerSocket logoutSocket;
			ServerSocket loginSocket;
			ServerSocket fileSocket;
			ServerSocket shareSendSocket;
			ServerSocket shareReceiveSocket;
			ServerSocket restoreSocket;

			// 파일복원 포트 : 11000
			// 로그인 포트 : 10000
			// 로그아웃 포트 : 9000
			// 파일백업 포트 : 8000
			// 파일공유(서버입장에서 수신) 포트 : 7000
			// 파일공유(서버입장에서 전송) 포트 : 6000
			
			boolean firstTime = true;
			String id = null;
			Socket socket = null;
			int flag = 0;
			boolean logoutFlag = false;
			
			try {
				loginSocket = new ServerSocket(10000);
				logoutSocket = new ServerSocket(9000);
				
				Server_ReceiveLogout rcl = new Server_ReceiveLogout(logoutSocket);
				Server_ReceiveFiles rf = null;
				Server_FileShare_Receive sfsr = null;
				Server_FileShare_Send sfss = null;
				Server_FileRestore sfr = null;
			
				rcl.start();
				
				while(true){
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// 일단 이게 발생함  수정1
					}
					
					if(rcl.getLogoutFlag() == true){
						rf.stopThread();
						sfsr.stopThread();
						sfss.stopThread();
						firstTime = true;
						rcl.setLogoutFlag();
					}
					
					if(firstTime == true){
							System.out.println(getTime() + " 서버가 실행되었습니다.");
							socket = loginSocket.accept();
							InputStream in = socket.getInputStream();
							DataInputStream dis = new DataInputStream(in);
							
							flag = dis.readInt();
							System.out.println("전달받은 플래그 : " + flag);
							
						switch(flag){
						case 0:		//	로그인 정보 받아오기
							Server_ReceiveLogin rl = new Server_ReceiveLogin(loginSocket,socket);
							id = rl.compareLogin();
							break;
						
						case 1:		//	회원가입 정보 받아오기
							Server_ReceivePrivacy rp = new Server_ReceivePrivacy(loginSocket,socket);
							rp.getPrivacy();
							break;
							
						case 2:		//	중복 확인
							Server_ForIDCheck fic = new Server_ForIDCheck(loginSocket,socket);
							fic.compareID();
							break;
							
						default:
							break;
						}
					}
					
					if(Server_ReceiveLogin.nextLogin == true){
						if(firstTime == true){
							shareReceiveSocket = new ServerSocket(7000);	//	파일공유(클라이언트 입장에서 전송) 소켓열기
							shareSendSocket = new ServerSocket(6000);	// 파일공유(클라이언트 입장에서 수신) 소켓열기
							fileSocket = new ServerSocket(8000);	// 파일백업 소켓열기
							restoreSocket = new ServerSocket(11000); //파일 복원 소켓
							sfsr = new Server_FileShare_Receive(shareReceiveSocket);	// 파일공유(전송) 생성자 선언
							sfss = new Server_FileShare_Send(shareSendSocket);// 파일공유(수신) 생성자 선언
							rf = new Server_ReceiveFiles(fileSocket);
							sfr = new Server_FileRestore(restoreSocket);	//파일복원 생성자 선언
							sfsr.start();	//	파일공유_리시브 start
							sfss.start();
							rf.setID(id);	//	ReceiveFiles에 아이디 설정
							rf.start();		//	ReceiveFiles을 start
							sfr.Start();
							firstTime = false;
						}
						Server_ReceiveLogin.nextLogin = false;
					}
				}
					
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	static String getTime()
	{
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	}
}
