package Client;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.util.*;

import javax.swing.JOptionPane;

import FINAL_CONSTANCE.FINAL_CONSTANCE;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;

public class Client_FileShare_Receive extends Thread
{
	// ���� ������� �ν��Ͻ�
	private Socket socket = null;
	private String serverIP = FINAL_CONSTANCE.serverIP;
	private int serverPort = 6001;
	
	// About I.O Stream
	private InputStream in = null;
	private DataInputStream dis = null;
	private ObjectInputStream ois = null;
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	
	private OutputStream out = null;
	private DataOutputStream dos = null;
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	
	// ���� ���۰��� �ν��Ͻ�
	private byte[] buffer = null;
	private String OTP = null;
	private String selectedPath = null;
	private Vector<String> filenames = null;
	private File receiveFile = null;
	private long totalSize = 0;
	private boolean sendFlag = false;
	private boolean OTP_Check_Flag = false;
	
	// UI���� �ν��Ͻ�
	private Client_FileShare_Receive_UI cfsrUI = null;
	
	//
	Scanner scanner = new Scanner(System.in);	//	UI������ �������� (�ؽ�Ʈ�Է¹ڽ��� �ٲ����)
	//
	
	public void run()
	{	
		try {
			do{
				cfsrUI = new Client_FileShare_Receive_UI();
				cfsrUI.fileShare_Receive_UI_ON();
				cfsrUI.completeToCheck();
				//		�� ������ ������ �ѳ��� 30�ʵ��� �ƹ��͵� ���ϸ� �����尡 �����Ե˴ϴ�.
				int test = 0;
				while(cfsrUI.getFolderSelectionEnd() == false)
				{
					try 
					{
						sleep(1);
						test++;
						if(test >= 30000)
							break;
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				if(test >= 30000){
					break;
				}
				//
				selectedPath = cfsrUI.getRoute().getText();
				while(cfsrUI.getReceiveFlag() == false)
				{
					try 
					{
						sleep(1);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				OTP = cfsrUI.getOTP();
				
				socket = new Socket(serverIP, serverPort);
				out = socket.getOutputStream();
				dos = new DataOutputStream(out);
				
				in = socket.getInputStream();
				ois = new ObjectInputStream(in);
				dis = new DataInputStream(in);
				
				dos.writeUTF(OTP);		//	OTP�� ������ �����մϴ�.	// 1��°
				dos.flush();
				
				OTP_Check_Flag = dis.readBoolean();
				
				if(OTP_Check_Flag == true){
					filenames = (Vector<String>)ois.readObject();	//	�����κ��� �����̸���� ���͸� �޾ƿɴϴ�	// 2��°
					cfsrUI.getFileCount().setText(filenames.size() + " Files");
					
					buffer = new byte[1024];
					cfsrUI.checkToDownloading();
					for(int i = 0; i < filenames.size(); i++){
						totalSize = dis.readLong();					// �����κ��� ���۹��� ������ �뷮�� �޾ƿɴϴ�. // 3��°
						receiveFile = new File(selectedPath,filenames.get(i));
						fos = new FileOutputStream(receiveFile);
						bos = new BufferedOutputStream(fos);
						System.out.println(getTime() + " " + receiveFile.getName() + "�� ���� �޴� ���Դϴ�....");
						
						bis = new BufferedInputStream(in);
						int readCount = 0;
						while(totalSize > 0){
							readCount = bis.read(buffer);
							bos.write(buffer, 0, readCount);
							totalSize -= readCount;
						}
						bos.close();
						fos.close();
						
						System.out.println(getTime() + " " + receiveFile.getName() + "������ ������ �Ϸ�Ǿ����ϴ�.");
						
						if(filenames.size() >= 2){
							dos.writeBoolean(true);
						}
					}
					cfsrUI.downloadingToComplete();
					
					dos.close();
					ois.close();
					dos.close();
					out.close();
					in.close();
					
					cfsrUI.getFileCount().setText("Nothing");
				}
				else if(OTP_Check_Flag == false){
					showMessage("OTP ����", "������ �ش��ϴ� OTP�� ���õ� ������ �����ϴ�.");
					cfsrUI.dispose();
				}
			}while(OTP_Check_Flag == false);
		} 
		catch (UnknownHostException e) 
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
	}
	
	static String getTime()
	{
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	}
	
	private static void showMessage(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}
