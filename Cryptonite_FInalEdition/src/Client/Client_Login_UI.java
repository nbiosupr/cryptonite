package Client;

// Developed by Won Sung Yeon (Dept. Information Media at Suwon University)
// Develpoed by Kim Hye Ju (Dept. Information Security at Suwon University)
// Developed by Youn Hee Seung (Dept. Information Security at Suwon University)

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import User.User;
import FINAL_CONSTANCE.FINAL_CONSTANCE;

public class Client_Login_UI extends JFrame{
	
	// 버퍼이미지
    BufferedImage img = null;
    
    // 로그인 관련
    SendLogin sl = null;
    boolean firstTime = true;
    
    // 아이디 필드와 패스워드 필드
    JTextField loginTextField;
    private String id = "init";
    JPasswordField passwordField;
    private String password = "init";
    private String tempPassword = "init";
    
    // 보호폴더 선택 객체 선언
    private Client_FolderChooser_UI fc = null;
    
    // 로그인 카운터 읽어주기 위한것
    private FileReader fr = null;
    private String loginCount = null;
    private StringTokenizer st = null;
    
    // 로그인 횟수가 2번이상일 경우 여기서 스레드 실행
    private Client_FolderScan cfs = null;
    private Client_SendFiles csf = null;
    private Client_checkEncryptionAnime cea = null;
    private Client_FileShare_Send cfss = null;
    
    // 메인프레임 UI
    private boolean mainFrameFlag = false;
    private Client_MainFrame_UI cmfu = null;
    
    // AES_Key 추출관련
    private User loginedUser = null;
    private byte[] AES_Key = null;
    
    // 맥 어드레스 추출
    
    Font font1 = new Font("SansSerif", Font.BOLD, 25);
    Font fontjoin = new Font("SansSerif", Font.BOLD,13);
    Font fontid = new Font ("SansSerif", Font.BOLD,15);
    JButton Loginbt;      
    JButton Joinbt;
    JButton Joinupbt;
    
     public Client_Login_UI() {
    	 setTitle("Cryptonite");
         setBounds(710,200,470,645);
         setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  
         // 레이아웃 설정
         getContentPane().setLayout(null);
         JLayeredPane layeredPane = new JLayeredPane();
         layeredPane.setBounds(0, 0, 470, 645);
         layeredPane.setLayout(null);
  
         // 패널1
         // 이미지 받아오기
         try {
             img = ImageIO.read(new File("C:\\cryptonite\\img\\Login2.png"));
         } catch (IOException e) {
             System.out.println("이미지 불러오기 실패");
             System.exit(0);
         }
          
         MyPanel panel = new MyPanel();
         panel.setBounds(0, 0, 470, 645);
         
  
         // 로그인 필드
         
         loginTextField = new JTextField(15);
         loginTextField.setBounds(134, 327, 200, 30);
         layeredPane.add(loginTextField);
         loginTextField.setOpaque(false);
         loginTextField.setForeground(Color.white);
         loginTextField.setFont(fontid);
         loginTextField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
         loginTextField.setHorizontalAlignment(JTextField.CENTER);
         loginTextField.setText("ID를 입력하시오.");
         loginTextField.addKeyListener(new KeyListener(){
     		@Override
     		public void keyPressed(KeyEvent e) {}
     		@Override
     		public void keyReleased(KeyEvent e) {
     			id = loginTextField.getText();
     		}
     		@Override
     		public void keyTyped(KeyEvent e) {
     			if(firstTime == true){
     				loginTextField.setText("");
     				firstTime = false;
     			}
     		}
           });
         loginTextField.addMouseListener(new MouseAdapter(){
         	public void mouseClicked(MouseEvent e){
         		loginTextField.setText("");
         	}
         });
         
         // 패스워드

         passwordField = new JPasswordField(15);
         passwordField.setBounds(136, 395, 200, 30);
         passwordField.setOpaque(false);
         passwordField.setForeground(Color.white);
         passwordField.setFont(fontid);
         passwordField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
         passwordField.setHorizontalAlignment(JTextField.CENTER);
         passwordField.setEchoChar((char)0);
         passwordField.setText("PASSWORD를 입력하시오.");
         passwordField.addFocusListener(new FocusAdapter(){
        	 public void focusGained(FocusEvent fe){
        		 passwordField.setText("");
        		 passwordField.setEchoChar('●');
        	 }
         });
         passwordField.addKeyListener(new KeyListener(){
     		@Override
     		public void keyPressed(KeyEvent e) {}
     		@Override
     		public void keyReleased(KeyEvent e) {
     			password = new String(passwordField.getPassword());
     		}
     		@Override
     		public void keyTyped(KeyEvent e) {}
           });
         layeredPane.add(passwordField);
         
         passwordField.addMouseListener(new MouseAdapter(){
         	public void mouseClicked(MouseEvent e){
         		passwordField.setText("");
         		passwordField.setEchoChar('●');
         	}
         });

         Loginbt = new JButton(new ImageIcon("C:\\cryptonite\\img\\Loginbt.png"));
         Loginbt.setBounds(193, 449, 80, 37);
         Loginbt.setBorderPainted(false);
         Loginbt.setFocusPainted(false);
         Loginbt.setContentAreaFilled(false);
         Loginbt.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\LoginbtChange.png"));
         Loginbt.addMouseListener(new MouseAdapter(){
         	public void mouseClicked(MouseEvent e){
         		if(id.equals("init") == false && password.equals("init") == false){
         			try 
         			{
         				tempPassword = password;		// 임시
						MessageDigest md = MessageDigest.getInstance("SHA-256");
						byte[] temp = password.getBytes();
						md.update(temp);
						password = new String(Base64Coder.encode(md.digest()));
						System.out.println("password : " + password);
						sl = new SendLogin(id,password);
					} 
         			catch (NoSuchAlgorithmException e2) 
         			{
						e2.printStackTrace();
					}
         			
             		if(sl.getNextLogin() == true){
             			notifyLogin nl = new notifyLogin();
             			showMessage("로그인 완료", "로그인이 완료되었습니다.");
             			nl.login();
             			cmfu = new Client_MainFrame_UI(getLoginFrame(), id);
             			
             			if(sl.getLoginCount() == 1){
             				showMessage("최초 로그인", "최초로그인 이므로 보호폴더 지정을 하셔야 합니다.");
             				fc = new Client_FolderChooser_UI(tempPassword,nl,cmfu);
             				cmfu.mainFrameUI_ON();
                 			cmfu.setEnabled(false);
             			}
             			
             			if(sl.getLoginCount() >= 2){
             				cfs = new Client_FolderScan();
             				loginedUser = nl.getUserObejct();
             				AES_Key = loginedUser.getAesKey();
             				PBE pbe = new PBE(loginedUser.getSalt(), 1000);
             				try 
             				{
								cfs.setAES_Key(pbe.decrypt(tempPassword, AES_Key));
							} 
             				catch (GeneralSecurityException e1) 
             				{
								e1.printStackTrace();
							}
             				
             				cmfu.mainFrameUI_ON();
                 			cmfu.setEnabled(true);
             				
             				csf = new Client_SendFiles();
             				cea = new Client_checkEncryptionAnime();
             				
             				cfs.start();
             				csf.start();
             				cea.start();		// 로그인 횟수가 2회 이상일경우 여기서 스레드 시작
             			}
             			dispose();
             			mainFrameFlag = true;
             		}
             		else{
             			if(sl.getPathMac() != 0)	// 수정
             				showMessage("로그인 실패", "존재하지 않는 아이디거나, 비밀번호가 틀렸습니다.");
             		}
         		}
         		else{
         			showMessage("로그인 오류", "모든 항목을 입력해주세요.");
         		}
         	}
         });
         
         Joinbt = new JButton(new ImageIcon("C:\\cryptonite\\img\\SignUp1.png"));
         Joinbt.setFont(fontjoin);
         Joinbt.setForeground(Color.white);
         Joinbt.setBounds(153,516,155,49);
         Joinbt.setBorderPainted(false);
         Joinbt.setFocusPainted(false);
         Joinbt.setContentAreaFilled(false);
         Joinbt.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\SignUp2.png"));
         Joinbt.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent arg0) {
         		new Client_SignUp_UI();
         	}
         });
        
         layeredPane.add(Loginbt);
         layeredPane.add(Joinbt);
         // 마지막 추가들
         layeredPane.add(panel);
              
         getContentPane().add(layeredPane);          
         setVisible(true);
    } 
   
    class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(img, 0, 0, null);
        }
    }
    
    public Client_Login_UI getLoginFrame()
    {
    	return this;
    }
    
    public boolean getMainFrameFlag()
    {
    	return mainFrameFlag;
    }
    
    public Client_FolderChooser_UI getFolderChooser()
    {
    	return fc;
    }
    
    public Client_FolderScan getCFS()
    {
    	return cfs;
    }
    
    public Client_SendFiles getCSF()
    {
    	return csf;
    }
    
    public Client_checkEncryptionAnime getCEA()
    {
    	return cea;
    }
    
    public Client_MainFrame_UI getMainFrame_UI()
    {
    	return cmfu;
    }
    
    public SendLogin getSendLogin()
    {
    	return sl;
    }
    
    private void showMessage(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}

class SendLogin
{
	// SHA - 256
	MessageDigest md = null;
	
	// boolean관련
	public static boolean checkLogin;
	private boolean nextLogin = false;
	
	// 서버 초기화 및 파일관련
	private String serverIP = FINAL_CONSTANCE.serverIP;
	private int serverPort = 10001;
	private Socket socket = null;
	private File cnobFile = null;
	private FileWriter fw = null;
	
	// cnobString 및 아이디 패스워드
	private String cnobString = "init";
	private String id;
	private String password;
	
	// 기타 바이트 배열
	private byte[] buffer;
	
	// 맥 어드레스 관련 인스턴스
	private Client_GetMacAddress cgma = null;
	private int count = 0;
	private String macAddress = null;
	private int pathMac = 0;	// 0이면 false, 1이면 true, 2이면 FileNotFoundException
	
	// 아이피 전송 및 위치추적 관련 인스턴스
	private String clientIP = null;
	private String userAddress = null;
	
	public SendLogin(String id, String password)
	{
		try {
			cgma = new Client_GetMacAddress(InetAddress.getLocalHost());
			this.macAddress = cgma.getMacAddress();
			
			socket = new Socket(serverIP, serverPort);

			this.id = id;
			this.password = password;
			
			OutputStream out = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(out);		
			
			InputStream in = socket.getInputStream();
			DataInputStream dis = new DataInputStream(in);
			BufferedInputStream bis = new BufferedInputStream(in);
			
			md = MessageDigest.getInstance("SHA-256");
		
			dos.writeInt(0);	// 로그인 플래그 전달
			dos.flush();
			
			dos.writeUTF(this.id); // 첫번째
			dos.flush();
			
			dos.writeUTF(this.password); // 두번째
			dos.flush();
			
			checkLogin = dis.readBoolean(); // 세번째

			
			if(checkLogin == true){
				// 여기서 맥어드레스를 보내줍니다.
				dos.writeUTF(macAddress);			//	
				dos.flush();
				
				pathMac = dis.readInt();
				if(pathMac == 0){
					showMessage("컴퓨터 인증 실패", "현재 로그인 하신 컴퓨터는 등록된 컴퓨터가 아닙니다.");
					showMessage("위치 추적 안내", "고객님의 계정으로 로그인했던 위치를 안내 하겠습니다.");
					userAddress = dis.readUTF();
					showMessage("위치 안내", "마지막으로 로그인했던 위치는 " + userAddress + " 입니다. (Whois OPEN API)");
					nextLogin = false;
				}
				
				else if(pathMac == 1 || pathMac == 2){
					if(pathMac == 1){
						showMessage("컴퓨터 인증 성공", "컴퓨터 인증에 성공하셨습니다.");
					}
					
					dos.writeInt(1);		//	로그인 횟수 추가
					dos.flush();
					
					cnobFile = new File("C:\\cryptonite\\Privacy",(id+".txt"));
					fw = new FileWriter(cnobFile);
					cnobString = dis.readUTF();
					fw.write(cnobString);
					fw.close();
					
					count = getLoginCount();
					if(count == 1){
						showMessage("컴퓨터 등록 안내", "최초 로그인이므로 현재 로그인하신 컴퓨터로 컴퓨터 등록이 진행됩니다.");
						dos.writeUTF(macAddress);
						dos.flush();
					}
					
					//User.cnob를 가져와줘야합니다..
					
					File receiveCnob = new File("C:\\cryptonite\\Privacy\\User.cnob");
					FileOutputStream fos = new FileOutputStream(receiveCnob);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					buffer = new byte[512];
					int readCount = 0;
					while((readCount = bis.read(buffer)) != -1){
						bos.write(buffer, 0, readCount);
					}
					bos.flush();
					bos.close();
					bis.close();
					fos.close();
					
					nextLogin = true;
				}
			}
			
			dos.close();
			out.close();
			dis.close();
			in.close();
		}
		catch(ConnectException e){
			showMessage("서버 접속 오류", "네트워크가 연결이 안됐거나, 서버가 꺼진상태입니다.");
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getPathMac()
	{
		return pathMac;
	}
	
	public String getID()
    {
    	return this.id;
    }
	
	public int getLoginCount()
	{
		StringTokenizer st = new StringTokenizer(this.cnobString, "★");
		String tempCount = "";
		while(st.hasMoreTokens()){
			tempCount = st.nextToken();
		}
		
		return Integer.parseInt(tempCount); 
	}
	
	public static boolean getCheckLogin()
	{
		return checkLogin;
	}
	
	public boolean getNextLogin()
	{
		return nextLogin;
	}
	
	 private void showMessage(String title, String message) {
			JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
		}
}

class notifyLogin {
	   private String savedPath = "C:\\cryptonite\\User.cnob";
	   private String[] filename = null;
	   private String temp = null;
	   private String[] splitArray = null;
	   private File loginFile = null;
	   private User user = null;

	   public notifyLogin()
	   {
		  loginFile = new File("C:\\cryptonite\\Privacy\\User.cnob");
	   }
	   
	   public void login()
	   {
	      try {
	    	 byte[] buffer = new byte[512];
	         FileInputStream fis = new FileInputStream(loginFile);
	         BufferedInputStream bis = new BufferedInputStream(fis);
	         
	         FileOutputStream fos = new FileOutputStream(savedPath);
	         BufferedOutputStream bos = new BufferedOutputStream(fos);
	         
	         int readCount = 0;
	         while((readCount = bis.read(buffer)) != -1){
	        	 bos.write(buffer, 0, readCount);
	         }
	         bos.close();
	         fos.close();
	         bis.close();
	         fis.close();
	         
	         
	         FileInputStream readObject = new FileInputStream(savedPath);
	         ObjectInputStream ois = new ObjectInputStream(readObject);
	         user = (User)ois.readObject();
	         
	         this.loginFile.delete();
	      } 
	      catch (IOException e) 
	      {	  
	    	  
	      } 
	     catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	   }
	   
	   public void logout()
	   {
	      new File(savedPath).delete();
	   }
	   
	   public User getUserObejct()
	   {
		   return user;
	   }
	   
	}