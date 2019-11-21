package Client;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.awt.Button;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import FINAL_CONSTANCE.FINAL_CONSTANCE;

public class Client_FileShare_Send extends Thread
{
	// ������ ���� �ν��Ͻ�
	private Socket socket = null;
	private String serverIP = FINAL_CONSTANCE.serverIP;
	private int serverPort = 7001;
	
	// ���� ����� ���� �ν��Ͻ�
	private InputStream in = null;
	private DataInputStream dis = null;
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	
	private OutputStream out = null;
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	private ObjectOutputStream oos = null;
	private DataOutputStream dos = null;
	
	// ���ϰ������� �ν��Ͻ�
	private String OTP = "";
	private String[] filePaths = null;
	private String[] fileNames = null;
	private FileFinder fileChooser = null;
	
	private byte[] buffer = null;
	private long totalSize = 0;
	private boolean sendFlag = true;
	
	// ���ϰ������� UI �ν��Ͻ�
	private Client_FileShare_Send_UI cfssUI = null;
		
	public Client_FileShare_Send() { }
	
	public synchronized void run()
	{
		try {	
			fileChooser = new FileFinder();
			cfssUI = new Client_FileShare_Send_UI();
			 
			while(fileChooser.getSelectionFinish() ==false){
				try {
					sleep(10);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			buffer = new byte[1024];
			
			filePaths = fileChooser.getFilePaths();		// ������ ������
			fileNames = fileChooser.getFileNames();		// ������ �̸�
			fileChooser.dispose();
			
			socket = new Socket(serverIP, serverPort);
			in = socket.getInputStream();
			dis = new DataInputStream(in);
			
			out = socket.getOutputStream();
			oos = new ObjectOutputStream(out);
			dos = new DataOutputStream(out);
			
			this.OTP = dis.readUTF();		//	OTP�� �޾ƿɴϴ�.	// 1��°
			System.out.println(getTime() + " �����κ��� �޾ƿ� OTP : " + this.OTP);
			cfssUI.getOTPLabel().setText(this.OTP);		// UI�� OTPǥ��
			
			oos.writeObject(fileNames);		//	�����̸� ��ü�� �����մϴ�. // 2��°
			
			cfssUI.getFileCountLabel().setText(fileNames.length  + " Files");
			cfssUI.fileShare_Send_UI_ON();
			
			while(cfssUI.getSendFlag() == false){
				try 
				{
					sleep(1);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			dos.writeBoolean(true);
			dos.flush();
			
			cfssUI.sendToSending();
			
			for(int i = 0; i < fileNames.length; i++){
				if(fileNames.length >= 2){
					if(i >= 1){
						while((sendFlag = dis.readBoolean()) == false) { }
					}
				}
				
				File sendFile = new File(filePaths[i]);
				System.out.println(getTime() + " ������ ���� �̸� : " + sendFile.getName());
				
				totalSize = sendFile.length();
				dos.writeLong(totalSize);			// �ѿ뷮 ���� // 3��°
				dos.flush();

				fis = new FileInputStream(sendFile);
				bis = new BufferedInputStream(fis);
				bos = new BufferedOutputStream(out);
				
				System.out.println(getTime() + " " + fileNames[i] + "������ �������Դϴ�.....");
				
				int readCount = 0;
				while((readCount = bis.read(buffer)) != -1){
					bos.write(buffer, 0, readCount);		// ������ �����մϴ� // 4��°
				}
				this.sendFlag = false;
				bos.flush();
				
				System.out.println(getTime() + " " + fileNames[i] + "���� ���� �Ϸ�!");	
			}
			
			cfssUI.sendingToComplete();
			cfssUI.getFileCountLabel().setText("Nothing");
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

class FileFinder extends Frame
{
	private File[] selectedFiles = null;
	private String[] fileNames = null;
	private String[] filePaths = null;
	private boolean selectionFinish = false;
	
	 private Button b1 = new Button("���� ����");
	
	 public FileFinder()
	 {
		/* this.setLayout( new FlowLayout() );
		 this.add(b1);
		 this.b1.addActionListener(this);*/
		 this.fileFinderON();
	  }
	 /**
	  * @param args
	  */
	 public void fileFinderON()
	 {
		  FileDialog fd = new FileDialog( this, "���� ����", FileDialog.LOAD);
		  fd.setMultipleMode(true);
		  fd.setVisible(true);
		  selectedFiles = fd.getFiles();					// ���ϰ�ü �޾ƿ��°�
		  
		  fileNames = new String[selectedFiles.length];		// �����̸� ��Ʈ���迭 ����
		  filePaths = new String[selectedFiles.length];
		  
		  for(int i = 0; i < selectedFiles.length; i++){
			  filePaths[i] = selectedFiles[i].getPath();
			  fileNames[i] = selectedFiles[i].getName();
		  }
		  
		  if(selectedFiles.length != 0){
			  this.selectionFinish = true;
		  }
	 }
	 
	 public String[] getFileNames()
	 {
		 return fileNames;
	 }
	 
	 public String[] getFilePaths()
	 {
		 this.selectionFinish = false;
		 return filePaths;
	 }
	 
	 public boolean getSelectionFinish()
	 {
		 return this.selectionFinish;
	 }
			 
	private void showMessage(String title, String message) 
	{
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}