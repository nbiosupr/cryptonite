package Client;

/*
 * Module Designed by Sangwoon Park (Dept. Information Security at Suwon University)
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

import Crypto.FileED;
import FINAL_CONSTANCE.FINAL_CONSTANCE;

public class Client_SendFiles extends Thread
{
	// ����������� �� ���۹迭
	private String serverIP = FINAL_CONSTANCE.serverIP;
	private int serverPort = 8001;
	private byte[] buffer;
	private Socket socket = null;
	
	// ����Ŭ����
	private File file = null;
	private File forName = null;
	
	// ��ȣ���� �ּ� �ҷ����� ����
	private File protectedFolder = null;
	private FileReader fr = null;
	private BufferedReader br = null;
	private String cryptoFolder = "";
	
	// �����̸�, �н�, �䷱Ʈ
	private String fileName = null;
	private String filePath = null;
	private String fileParent = null;
	
	// ��ȣ����
	//private String protectedFolder = null;
	
	// ���Ͱ���
	private Vector<String> tempVector = null;
	private Vector<String> checkVector = null;
	private Vector<String> checkVector2 = null;
	
	// ���� ���ۼ��� ����
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	private ObjectInputStream ois = null;
	
	private OutputStream out = null;
	private FileOutputStream fos = null;
	private ObjectOutputStream oos = null;
	private DataOutputStream dos = null;
	private BufferedOutputStream bos = null;
	
	// �÷���
	private boolean sendFlag = false;
	private boolean stopFlag = false;
	private boolean goCheck = false;
	private boolean sendedFile = false;
	
	// ���Ϲ�� UI �ν��Ͻ�
	//private Client_FileBackup_UI cfbUI = null;
	//private JButton sending = null;
	//private JButton complete = null;
	//private JTextField filenameField = null;
	
	StringTokenizer st = null;
	
	public Client_SendFiles()
	{
		try {
			protectedFolder = new File("C:\\cryptonite", "protected.adr");
			fr = new FileReader(protectedFolder);
			br = new BufferedReader(fr);
			File tempFile = new File(br.readLine());
			this.cryptoFolder = tempFile.getName();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void run()
	{
		try{
			/*cfbUI = new Client_FileBackup_UI();
			sending  = cfbUI.getSendingButton();
			complete = cfbUI.getCompleteButton();
			filenameField = cfbUI.getFilenameField();*/
			tempVector = new Vector<String>();
			
			while(true){
				if(stopFlag == true){
					FileED.filenameVector.removeAllElements();
					this.interrupt();
					wait();
				}
				
				//complete.setVisible(true);
				//sending.setVisible(false);
				//filenameField.setVisible(false);
				if(FileED.filenameVector.isEmpty() == false){
					//cfbUI.fileBackupUI_ON();
					//complete.setVisible(false);	// ��ư �Ⱥ��̰�
					//sending.setVisible(true);	// ��ư ���̰�
					
					forName = new File(FileED.filenameVector.get(0));
					filePath = forName.getPath();
					fileParent = forName.getParent();
					fileName = forName.getName();
					
					//filenameField.setText(fileName);
					//filenameField.setVisible(true);
					
					try {
						File inVectorFile = new File("C:\\cryptonite\\Filelist", "filenames.ser");
						fis = new FileInputStream(inVectorFile);	//	�������� �̸� �ٲٱ�
						ois = new ObjectInputStream(fis);
						checkVector = (Vector<String>)ois.readObject();
					} 
					catch(FileNotFoundException fe){
						System.out.println("������ ���ε�� ������ �����ϴ�.");
					}
					catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					try{
						if(checkVector.isEmpty() == false){
							for(int i = 0; i < checkVector.size(); i++){
								if(filePath.equals(checkVector.get(i)) == true){
										System.out.println(getTime() + " " + filePath + "�� �̹� ���۵� �����Դϴ�.");
										sendedFile = true;
									break;
								}
							}
						}
					}
					catch(NullPointerException npe){}
					
					if(sendedFile == false){
						file = new File(fileParent, fileName);
						buffer = new byte[1024];
						
						socket = new Socket(serverIP, serverPort);					
						out = socket.getOutputStream();
						dos = new DataOutputStream(out);
						bos = new BufferedOutputStream(out);
						
						dos.writeUTF(cryptoFolder);
						dos.flush();
						
						dos.writeUTF(fileParent);
						dos.flush();
						
						dos.writeBoolean(file.isDirectory());
						dos.flush();
						
						dos.writeUTF(fileName);
						dos.flush();
										
						if(file.isDirectory() == false)
							System.out.println(getTime() + " ������ �����̸� : " + fileName);
						else if(file.isDirectory() == true)
							System.out.println(getTime() + " ������ ���͸� �̸� : " + fileName);
						
						if(file.isDirectory() == false){
							fis = new FileInputStream(file);
							bis = new BufferedInputStream(fis);
														
							System.out.println(getTime() + " " + fileName +"������ ���� ���Դϴ�....");
							int read = 0;
							while((read = bis.read(buffer)) > 0){
								bos.write(buffer, 0 , read);
							}
							System.out.println(getTime() + " ���� ������ �Ϸ�Ǿ����ϴ�.");
							
												
							fis.close();
							bis.close();
							bos.close();		
							dos.close();
							out.close();
							}
						
						try{
						File inVectorFile = new File("C:\\cryptonite\\Filelist", "filenames.ser");
						fis = new FileInputStream(inVectorFile);
						ois = new ObjectInputStream(fis);
						checkVector2 = (Vector<String>)ois.readObject();
						checkVector2.add(filePath);
						tempVector = checkVector2;
						}
						catch(FileNotFoundException fe){
							System.out.println("������ �����ϴ�.");
							tempVector.add(filePath);
						}
						
						File outVectorFile = new File("C:\\cryptonite\\Filelist", "filenames.ser");
						fos = new FileOutputStream(outVectorFile);
						oos = new ObjectOutputStream(fos);
						oos.writeObject(tempVector);
						}
						try{
							FileED.filenameVector.remove(0);
						}
						catch(ArrayIndexOutOfBoundsException aioe){
							System.out.println("����");
						} 
						sendedFile = false;
					
						//fos.close();
						//oos.close();
					}
				}
			}
			catch(IOException | ClassNotFoundException e){
				e.printStackTrace();
			} 
			catch (InterruptedException e) {
			}
		}
	
	public void stopThread()
	{
		stopFlag = true;
	}
	
	static String getTime()
	{
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	}
}