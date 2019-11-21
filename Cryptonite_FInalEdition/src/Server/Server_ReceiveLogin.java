package Server;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class Server_ReceiveLogin
{
	//
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	
	//
	private boolean forLogin = false;
	public static boolean nextLogin = false;	
	
	//
	private StringTokenizer st = null;
	private String sumString = "";
	private String token = null;
	
	//
	private File privacyFolder = null;
	private String[] filenames = null;
	
	//
	private String id;
	private String password;
	private int tempLoginCount;
	private String loginCount;
	
	//
	private File sendCnob = null;
	private byte[] buffer = null;
	
	// �� �ּ� ����
	private String macAddress = null;
	
	// Ŭ���̾�Ʈ ������ �� ��ġ���� ����
	private boolean pathMac = false;
	private String clientIP = null;
	private Server_LocationTrack slt = null;
	private String userAddress = null;
	private boolean exceptionOccurence = false;
	
	// MessageDigest ����
	MessageDigest md = null;
	
	public Server_ReceiveLogin(){}
	
	
	public Server_ReceiveLogin(ServerSocket serverSocket, Socket socket)
	{
		this.serverSocket = serverSocket;
		this.socket = socket;
	}
	
	public String compareLogin()
	{
		try {
			InputStream in = socket.getInputStream();
			DataInputStream dis = new DataInputStream(in);
			
			OutputStream out = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			BufferedOutputStream bos = new BufferedOutputStream(out);
			
			md = MessageDigest.getInstance("SHA-256");
			this.id = dis.readUTF(); 			// ù��°
			md.update(this.id.getBytes());
			System.out.println(getTime() + " [Login] ���޹��� ID : " + new String(Server_Base64Coder.encode(md.digest())));
			this.password = dis.readUTF();		// �ι�°
			System.out.println(getTime() + " [Login] ���޹��� Password : " + this.password);
			
			privacyFolder = new File("C:\\Server\\Privacy");
			filenames = privacyFolder.list();
			
			int read = 0;
			char temp;
			for(int i = 0; i < filenames.length; i++){
				if((this.id + ".txt").equals(filenames[i]) == true){
					System.out.println(getTime() + " [Login] ��ġ�ϴ� ���̵� �����մϴ�.");
					FileReader fr = new FileReader("C:\\Server\\Privacy" + "\\" + filenames[i]);
					while((read = fr.read()) != -1){
						temp = (char)read;
						sumString += temp;
					}
					st = new StringTokenizer(sumString, "��");
					while(st.hasMoreTokens()){
						token = st.nextToken();
						if(token.equals(this.password) == true){
							System.out.println(getTime() + " [Login] ��й�ȣ���� ��ġ�մϴ�.");
							forLogin = true;
							break;
						}
					}
					if(forLogin == true)
						break;
				}
			}
			if(forLogin == false){
				System.out.println(getTime() + " [Login] ��ġ�ϴ� ���̵� �����ϴ�.");
			}
			
			dos.writeBoolean(forLogin); // ����°
			dos.flush();
				
			if(forLogin == true){
				// ���⼭ �ƾ�巹���� �޾Ƽ� �񱳸� ���ݴϴ�.
				macAddress = dis.readUTF();
				try
				{
					File forMac = new File("C:\\Server\\MacAddress", (this.id + ".madr"));
					FileReader fr = new FileReader(forMac);
					BufferedReader br = new BufferedReader(fr);
					
					if(macAddress.equals(br.readLine()) == true){
						pathMac = true;
					}
				}
				catch(FileNotFoundException e)
				{
					dos.writeInt(2);
					dos.flush();
					System.out.println(getTime() + " [Login] ���� ��ǻ�� ����� �������� �����̽��ϴ�.");
					exceptionOccurence = true;
				}
					System.out.println("pathMac : " + pathMac);
					
					if(pathMac == true && exceptionOccurence == false){
						System.out.println(getTime() + " [Login] �� ��巹���� ��ġ�մϴ�. ��ǻ�� ������ ���� �Ͽ����ϴ�.");
						dos.writeInt(1);
						dos.flush();
					}
					else if(pathMac == false && exceptionOccurence == false){
						System.out.println(getTime() + " [Login] �� ��彺�� ����ġ�մϴ�. ��ǻ�� ������ ���� �Ͽ����ϴ�.");
						dos.writeInt(0);
						dos.flush();
						
						File tempIP = new File("C:\\Server\\ClientIP", (this.id + ".ipadr"));
						FileReader ipfr = new FileReader(tempIP);
						BufferedReader ipbr = new BufferedReader(ipfr);
						slt = new Server_LocationTrack(ipbr.readLine());
						ipbr.close();
						ipfr.close();
						userAddress = slt.getUserAddress();
						
						dos.writeUTF(userAddress);
						dos.flush();
					}
				
				tempLoginCount = dis.readInt();
				while(st.hasMoreTokens()){
					loginCount = st.nextToken();
				}
				tempLoginCount += Integer.parseInt(loginCount);		// �α���ī���� �߰�
				String[] forSplit = sumString.split("��");
				sumString = "";			// sumString�� �ٽú���ְ�..
				for(int i = 0 ; i < forSplit.length; i++){
					if(i == forSplit.length-1){
						forSplit[i] = Integer.toString(tempLoginCount);
					}
					sumString += forSplit[i];
					if(i != forSplit.length-1){
						sumString += "��";
					}
				}
				
				FileWriter newPrivacy = new FileWriter("C:\\Server\\Privacy"+ "\\" + id + ".txt");
				newPrivacy.write(sumString);
				newPrivacy.close();				// �α��� ī���� �߰��Ѱͱ��� �ٽ� privacy���� ����
				
				FileReader cnobFile = new FileReader("C:\\Server\\Privacy"+ "\\" + id + ".txt");
				int cnobTemp = 0;
				String cnobString = "";
				while((cnobTemp = cnobFile.read()) != -1){
					cnobString += (char)cnobTemp;
				}
				dos.writeUTF(cnobString);		//		cnobString����
				
				if(tempLoginCount == 1){
					macAddress = dis.readUTF();
					File macFile = new File("C:\\Server\\MacAddress", (this.id + ".madr"));
					FileWriter macFR = new FileWriter(macFile);
					macFR.write(macAddress);
					macFR.close();
				}
				
				// ���⼭ Ŭ���̾�Ʈ ��ǻ���� IP�� ���۹޽��ϴ�.
				//clientIP = dis.readUTF();
				clientIP = socket.getInetAddress().getHostAddress();
				File ip = new File("C:\\Server\\ClientIP", (this.id + ".ipadr"));
				FileWriter ipfw = new FileWriter(ip);
				ipfw.write(clientIP);
				ipfw.close();
	
				// ���⼭ User.cnob�� ����������մϴ�..
				sendCnob = new File("C:\\Server\\UserObject", (id + ".cnob"));
				FileInputStream fis = new FileInputStream(sendCnob);
				BufferedInputStream bis = new BufferedInputStream(fis);
				
				int readCount = 0;
				buffer = new byte[512];
				while((readCount = bis.read(buffer)) != -1){
					bos.write(buffer, 0, readCount);
				}
				bos.flush();
				bos.close();
				

				nextLogin = true;
			}
			
			dos.close();
			out.close();
			dis.close();
			in.close();
		}
		catch (IOException e) {e.printStackTrace();} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return this.id;
	}
	
	public String getID()
	{
		return id;
	}
	
	static String getTime()
	{
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	}
}