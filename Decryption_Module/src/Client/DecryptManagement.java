package Client;

/*
 * Writed by Sangwoon Park (Dept. Information Security at Suwon University)
 */

import java.io.*;
import User.User;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.crypto.SecretKey;

public class DecryptManagement{
	
	private String password= null;
	private SecretKey Key = null;
	private User findedUser =null;
	private String[] args = null;
	private List<File> fileList = null;
	private String protectedAdr = null;
	
	public DecryptManagement(String[] args) {
		this.args = args;
	}
	
	public boolean isHereProtected(){
		for(File target : fileList){
			if(target.getPath().indexOf(protectedAdr) != -1) {
				System.out.println(protectedAdr);
				return true;
			}
		}
		return false;
	}
	
	public boolean getUserObject(){
		File userObject = new File("C:/cryptonite/User.cnob");
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userObject));
			findedUser = (User)ois.readObject();
		} catch (ClassNotFoundException | IOException e) {	findedUser = null; return false;}
		
		File adrText = new File("C:/cryptonite/protected.adr");
		try{
			BufferedReader br = new BufferedReader(new FileReader(adrText));
			protectedAdr = br.readLine();
		} catch(IOException e) {e.printStackTrace();}
		return true;
	}

	public void getFilePath(){
		fileList = new Vector<File>(0);
		if(args.length == 0) {
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			fileList.add(new File(scanner.nextLine()));
		} else {
			for(String a: args){
				System.out.println(a);
				if(a.indexOf(".cnec") != -1){
					fileList.add(new File(a));
				}
			}
		}
	}

	public String getFileName(){
		String firstFileName = fileList.get(0).getName();
		int fileCount = fileList.size();
		if(fileCount < 2)
			return (firstFileName);
		else
			return (firstFileName + " 외 "+(fileCount-1)+" 개의 파일");
	}
	
	public boolean checkPassword(String password) {
		//비밀번호 해싱화
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			return false;
		}
		String hashPassword = null;
		
		md.update(password.getBytes());
		hashPassword = new String(Base64Coder.encode(md.digest()));//

		System.out.println(hashPassword);
		//비밀번호 일치 확인
		boolean result = findedUser.getPw().equals(hashPassword); 
		if(result == true) this.password = password;
		
		return result;
	}
	
	private void autoMove(File target,String OriginPath){
		Thread checkThread = new Thread() {
			@Override
			public synchronized void run(){
				try {
					int readCount = 0;
					System.out.println("파일열림");
					String address = "\"" + target.getParent() + "\\" + target.getName() + "\"";
					Runtime test = Runtime.getRuntime();
					System.out.println("어드레스 : " + address);
					Process p = test.exec("cmd /c" + address);
					p.waitFor();
					System.out.println("파일닫힘");
					byte[] buffer = new byte[1024];
					File Origin = new File(OriginPath +"/"+ target.getName());
					System.out.println("protectedFolder : " + OriginPath);
					FileInputStream fis = new FileInputStream(target);
					BufferedInputStream bis = new BufferedInputStream(fis);
					FileOutputStream fos = new FileOutputStream(Origin);
					BufferedOutputStream bos = new BufferedOutputStream(fos);

					while((readCount = bis.read(buffer, 0, buffer.length)) != -1){
						bos.write(buffer,0,readCount);
					}
					bis.close(); bos.flush(); bos.close();
					target.delete();
				} catch (IOException e) {
					e.printStackTrace();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		};
		checkThread.start();
	}
	public void decrypt(int flag) {
		//PBKDF로 AES키 생성		
		PBE pbe = new PBE(findedUser.getSalt(), findedUser.getIterationCount());
		//암호화된 비밀키 복호화
		try {
			Key = pbe.decrypt(password, findedUser.getAesKey());
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		FileED fileED = new FileED(Key);
		for(File target: fileList) {
			try {
				String[] destpath = target.getName().split(".cnec");
				File dest = null;
				switch (flag) {
					case 1:
						if(target.getPath().indexOf(protectedAdr) != -1) {
							System.out.println("for filelist : destPath[0] : "+ destpath[0]);
							dest = new File("C:/cryptonite/temp/running/" + destpath[0]);
							dest.getParentFile().mkdirs();
							System.out.println("복호화 대상 주소: " + dest.getPath());
							fileED.decrypt(target, dest, false);
							autoMove(dest, target.getParent());
						} else {
							dest = new File(target.getParent() +"/"+ destpath[0]);
							fileED.decrypt(target, dest, true);
							Runtime.getRuntime().exec("cmd.exe /c " + dest.getPath());
						}
						break;
					case 2:
						dest = new File(target.getParent()+ "/"+ destpath[0]);
						fileED.decrypt(target, dest, true);
						break;
				}

				//테스트용 암호화
				//fileED.encrypt(target);
				//복호화


			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
