package Server;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

class Server_ReceiveLogout extends Thread
{
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private boolean logoutFlag = false;
	
	private boolean stopFlag = false;
	
	public Server_ReceiveLogout(ServerSocket serverSocket)
	{
		this.serverSocket = serverSocket;
	}
	
	public void run()
	{
		while(true){
			try {
				socket = serverSocket.accept();
				
				InputStream in = socket.getInputStream();
				DataInputStream dis = new DataInputStream(in);
				
				logoutFlag = dis.readBoolean();
				
				dis.close();
				in.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean getLogoutFlag()
	{
		return logoutFlag;
	}
	
	public void setLogoutFlag()
	{
		logoutFlag = false;
	}
}