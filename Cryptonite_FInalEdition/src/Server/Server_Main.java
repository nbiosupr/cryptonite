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

			// ���Ϻ��� ��Ʈ : 11000
			// �α��� ��Ʈ : 10000
			// �α׾ƿ� ��Ʈ : 9000
			// ���Ϲ�� ��Ʈ : 8000
			// ���ϰ���(�������忡�� ����) ��Ʈ : 7000
			// ���ϰ���(�������忡�� ����) ��Ʈ : 6000
			
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
						// �ϴ� �̰� �߻���  ����1
					}
					
					if(rcl.getLogoutFlag() == true){
						rf.stopThread();
						sfsr.stopThread();
						sfss.stopThread();
						firstTime = true;
						rcl.setLogoutFlag();
					}
					
					if(firstTime == true){
							System.out.println(getTime() + " ������ ����Ǿ����ϴ�.");
							socket = loginSocket.accept();
							InputStream in = socket.getInputStream();
							DataInputStream dis = new DataInputStream(in);
							
							flag = dis.readInt();
							System.out.println("���޹��� �÷��� : " + flag);
							
						switch(flag){
						case 0:		//	�α��� ���� �޾ƿ���
							Server_ReceiveLogin rl = new Server_ReceiveLogin(loginSocket,socket);
							id = rl.compareLogin();
							break;
						
						case 1:		//	ȸ������ ���� �޾ƿ���
							Server_ReceivePrivacy rp = new Server_ReceivePrivacy(loginSocket,socket);
							rp.getPrivacy();
							break;
							
						case 2:		//	�ߺ� Ȯ��
							Server_ForIDCheck fic = new Server_ForIDCheck(loginSocket,socket);
							fic.compareID();
							break;
							
						default:
							break;
						}
					}
					
					if(Server_ReceiveLogin.nextLogin == true){
						if(firstTime == true){
							shareReceiveSocket = new ServerSocket(7000);	//	���ϰ���(Ŭ���̾�Ʈ ���忡�� ����) ���Ͽ���
							shareSendSocket = new ServerSocket(6000);	// ���ϰ���(Ŭ���̾�Ʈ ���忡�� ����) ���Ͽ���
							fileSocket = new ServerSocket(8000);	// ���Ϲ�� ���Ͽ���
							restoreSocket = new ServerSocket(11000); //���� ���� ����
							sfsr = new Server_FileShare_Receive(shareReceiveSocket);	// ���ϰ���(����) ������ ����
							sfss = new Server_FileShare_Send(shareSendSocket);// ���ϰ���(����) ������ ����
							rf = new Server_ReceiveFiles(fileSocket);
							sfr = new Server_FileRestore(restoreSocket);	//���Ϻ��� ������ ����
							sfsr.start();	//	���ϰ���_���ú� start
							sfss.start();
							rf.setID(id);	//	ReceiveFiles�� ���̵� ����
							rf.start();		//	ReceiveFiles�� start
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
