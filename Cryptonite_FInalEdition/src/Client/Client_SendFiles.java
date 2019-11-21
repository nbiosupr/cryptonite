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
	// 서버연결관련 및 버퍼배열
	private String serverIP = FINAL_CONSTANCE.serverIP;
	private int serverPort = 8001;
	private byte[] buffer;
	private Socket socket = null;
	
	// 파일클래스
	private File file = null;
	private File forName = null;
	
	// 보호폴더 주소 불러오기 관련
	private File protectedFolder = null;
	private FileReader fr = null;
	private BufferedReader br = null;
	private String cryptoFolder = "";
	
	// 파일이름, 패스, 페런트
	private String fileName = null;
	private String filePath = null;
	private String fileParent = null;
	
	// 보호폴더
	//private String protectedFolder = null;
	
	// 벡터관련
	private Vector<String> tempVector = null;
	private Vector<String> checkVector = null;
	private Vector<String> checkVector2 = null;
	
	// 파일 전송수신 관련
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	private ObjectInputStream ois = null;
	
	private OutputStream out = null;
	private FileOutputStream fos = null;
	private ObjectOutputStream oos = null;
	private DataOutputStream dos = null;
	private BufferedOutputStream bos = null;
	
	// 플래그
	private boolean sendFlag = false;
	private boolean stopFlag = false;
	private boolean goCheck = false;
	private boolean sendedFile = false;
	
	// 파일백업 UI 인스턴스
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
					//complete.setVisible(false);	// 버튼 안보이게
					//sending.setVisible(true);	// 버튼 보이게
					
					forName = new File(FileED.filenameVector.get(0));
					filePath = forName.getPath();
					fileParent = forName.getParent();
					fileName = forName.getName();
					
					//filenameField.setText(fileName);
					//filenameField.setVisible(true);
					
					try {
						File inVectorFile = new File("C:\\cryptonite\\Filelist", "filenames.ser");
						fis = new FileInputStream(inVectorFile);	//	오류나면 이름 바꾸기
						ois = new ObjectInputStream(fis);
						checkVector = (Vector<String>)ois.readObject();
					} 
					catch(FileNotFoundException fe){
						System.out.println("서버에 업로드된 내용이 없습니다.");
					}
					catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					try{
						if(checkVector.isEmpty() == false){
							for(int i = 0; i < checkVector.size(); i++){
								if(filePath.equals(checkVector.get(i)) == true){
										System.out.println(getTime() + " " + filePath + "는 이미 전송된 파일입니다.");
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
							System.out.println(getTime() + " 전송할 파일이름 : " + fileName);
						else if(file.isDirectory() == true)
							System.out.println(getTime() + " 전송할 디렉터리 이름 : " + fileName);
						
						if(file.isDirectory() == false){
							fis = new FileInputStream(file);
							bis = new BufferedInputStream(fis);
														
							System.out.println(getTime() + " " + fileName +"파일을 전송 중입니다....");
							int read = 0;
							while((read = bis.read(buffer)) > 0){
								bos.write(buffer, 0 , read);
							}
							System.out.println(getTime() + " 파일 전송이 완료되었습니다.");
							
												
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
							System.out.println("파일이 없습니다.");
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
							System.out.println("무시");
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