package Client;

/*
 * Module Designed by Sangwoon Park (Dept. Information Security at Suwon University)
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.nio.file.WatchEvent.Kind;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.crypto.SecretKey;

import Crypto.FileED;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;

public class Client_FolderScan extends Thread
{
	// 워치서비스 관련 인스턴스
	WatchService watchService = null;
	private Vector<String> stringVector = new Vector<String>();
	private File forDirectory = null;
	private Client_EncryptManagement CEM = null;
	private Path newFolder = null;
	private Client_FolderScan CFS = null;
	
	// 보호폴더 불러오기
	private File protectedFolder = null;
	private FileReader fr = null;
	private BufferedReader br = null;
	private String cryptoFolder = null;	// 여기선 이게 String address로 대체됩니다.
   
	// 파일이름, 절대경로 만들기
	private String fileName = null;
	private String address = null;
	private String realAddress = null;
   
	private File startFolder = null;
	private File startFolder2 = null;
   
	// 워치키 및 플래그
	private WatchKey watchKey = null;
	private String[] filenames = null;
	private boolean firstStart = true;
	private boolean stopFlag = false;
   
	// AES_KEY
	public static SecretKey AES_Key = null;
	
	StringTokenizer st = null;
	private String filter = "init";
   
	public Client_FolderScan()
	{
		try {
			protectedFolder = new File("C:\\cryptonite", "protected.adr");
			fr = new FileReader(protectedFolder);
			br = new BufferedReader(fr);
			this.address = br.readLine();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
  
	public Client_FolderScan(String address)
	{	
		this.address = address;
		this.start();
	}
   
	public Client_FolderScan(String address, String realAddress)
	{
		this.address = address;
		this.realAddress = realAddress;
		FileED.filenameVector.add(realAddress);
		this.start();
	}
	
	public void setAES_Key(SecretKey AES_Key)
	{
		Client_FolderScan.AES_Key = AES_Key;
	}
	
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	public synchronized void run(){
		
		try{
			watchService = FileSystems.getDefault().newWatchService();
		    Path directory = Paths.get(address);
		    directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
		                                  StandardWatchEventKinds.ENTRY_DELETE);
		         
		    startFolder = new File(address);
		    filenames = startFolder.list();
		         
		    if(firstStart == true){
		    	for(int i = 0; i < filenames.length; i++){
			        forDirectory = new File(address + "\\" + filenames[i]);
				        if(forDirectory.isDirectory() == true){
				        	fileName = filenames[i];
				            realAddress = address + "\\" + fileName;
				        	CFS = new Client_FolderScan(forDirectory.getPath(), realAddress);
				        }
				        else{
				             fileName = filenames[i];
				             realAddress = address + "\\" + fileName;
				             stringVector.add(realAddress);
				             CEM = new Client_EncryptManagement(realAddress, AES_Key);
				       }
			        }
		    	}
		        firstStart = false;
		        

		        while(stopFlag == false){
        	 
		        	watchKey = watchService.take();
		        	List<WatchEvent<?>> list = watchKey.pollEvents();
            
		        	for(WatchEvent watchEvent : list){
		        		Kind kind = watchEvent.kind();
		        		Path path = (Path)watchEvent.context();
                  
		        		if(kind == StandardWatchEventKinds.ENTRY_CREATE){
		        			forDirectory = new File(address + "\\" + path.getFileName().toString());
		        			if(forDirectory.isDirectory() == true){
		        				FileED.filenameVector.add(forDirectory.getPath());
		        				CFS = new Client_FolderScan(forDirectory.getPath());
		        			}
		        			else{
		        				fileName = path.getFileName().toString();
		        				System.out.println("파일생성 >> " + fileName);
		        				realAddress = address + "\\" + fileName;
		        				stringVector.add(realAddress);
		        				CEM = new Client_EncryptManagement(realAddress, AES_Key);
		        			}
		        		}
		        		else if(kind == StandardWatchEventKinds.ENTRY_DELETE){
		        			for(int i=0; i<stringVector.size(); i++){
		        				if(path.getFileName().toString().equals(stringVector.get(i))){
		        					stringVector.remove(i);
		        					break;
		        				}
		        			}
		        		}
		        		else if(kind == StandardWatchEventKinds.OVERFLOW){
		        			System.out.println("디렉터리가 사라졌습니다.");
		        			break;
		        		}
		        	}
               
		        	boolean valid = watchKey.reset();   // watchKey를 한번썼으니깐 리셋을 해줘야합니다.
                                       // reset()성공시 valid에 true를 리턴합니다.
		        	if(!valid)
		        		break;                     // 실패시 false를 리턴받아 폴더스캔이 중지됩니다.
		        }
		}
		catch(ClosedWatchServiceException cwe){
			this.interrupt();
		}
		catch(IOException e){
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
		}
	}
   
	public void stopThread()
	{
		try {
			watchService.close();
			stopFlag = true;
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	  
	}
}