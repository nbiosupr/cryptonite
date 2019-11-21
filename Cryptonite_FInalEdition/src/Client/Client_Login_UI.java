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
	
	// �����̹���
    BufferedImage img = null;
    
    // �α��� ����
    SendLogin sl = null;
    boolean firstTime = true;
    
    // ���̵� �ʵ�� �н����� �ʵ�
    JTextField loginTextField;
    private String id = "init";
    JPasswordField passwordField;
    private String password = "init";
    private String tempPassword = "init";
    
    // ��ȣ���� ���� ��ü ����
    private Client_FolderChooser_UI fc = null;
    
    // �α��� ī���� �о��ֱ� ���Ѱ�
    private FileReader fr = null;
    private String loginCount = null;
    private StringTokenizer st = null;
    
    // �α��� Ƚ���� 2���̻��� ��� ���⼭ ������ ����
    private Client_FolderScan cfs = null;
    private Client_SendFiles csf = null;
    private Client_checkEncryptionAnime cea = null;
    private Client_FileShare_Send cfss = null;
    
    // ���������� UI
    private boolean mainFrameFlag = false;
    private Client_MainFrame_UI cmfu = null;
    
    // AES_Key �������
    private User loginedUser = null;
    private byte[] AES_Key = null;
    
    // �� ��巹�� ����
    
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
  
         // ���̾ƿ� ����
         getContentPane().setLayout(null);
         JLayeredPane layeredPane = new JLayeredPane();
         layeredPane.setBounds(0, 0, 470, 645);
         layeredPane.setLayout(null);
  
         // �г�1
         // �̹��� �޾ƿ���
         try {
             img = ImageIO.read(new File("C:\\cryptonite\\img\\Login2.png"));
         } catch (IOException e) {
             System.out.println("�̹��� �ҷ����� ����");
             System.exit(0);
         }
          
         MyPanel panel = new MyPanel();
         panel.setBounds(0, 0, 470, 645);
         
  
         // �α��� �ʵ�
         
         loginTextField = new JTextField(15);
         loginTextField.setBounds(134, 327, 200, 30);
         layeredPane.add(loginTextField);
         loginTextField.setOpaque(false);
         loginTextField.setForeground(Color.white);
         loginTextField.setFont(fontid);
         loginTextField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
         loginTextField.setHorizontalAlignment(JTextField.CENTER);
         loginTextField.setText("ID�� �Է��Ͻÿ�.");
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
         
         // �н�����

         passwordField = new JPasswordField(15);
         passwordField.setBounds(136, 395, 200, 30);
         passwordField.setOpaque(false);
         passwordField.setForeground(Color.white);
         passwordField.setFont(fontid);
         passwordField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
         passwordField.setHorizontalAlignment(JTextField.CENTER);
         passwordField.setEchoChar((char)0);
         passwordField.setText("PASSWORD�� �Է��Ͻÿ�.");
         passwordField.addFocusListener(new FocusAdapter(){
        	 public void focusGained(FocusEvent fe){
        		 passwordField.setText("");
        		 passwordField.setEchoChar('��');
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
         		passwordField.setEchoChar('��');
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
         				tempPassword = password;		// �ӽ�
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
             			showMessage("�α��� �Ϸ�", "�α����� �Ϸ�Ǿ����ϴ�.");
             			nl.login();
             			cmfu = new Client_MainFrame_UI(getLoginFrame(), id);
             			
             			if(sl.getLoginCount() == 1){
             				showMessage("���� �α���", "���ʷα��� �̹Ƿ� ��ȣ���� ������ �ϼž� �մϴ�.");
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
             				cea.start();		// �α��� Ƚ���� 2ȸ �̻��ϰ�� ���⼭ ������ ����
             			}
             			dispose();
             			mainFrameFlag = true;
             		}
             		else{
             			if(sl.getPathMac() != 0)	// ����
             				showMessage("�α��� ����", "�������� �ʴ� ���̵�ų�, ��й�ȣ�� Ʋ�Ƚ��ϴ�.");
             		}
         		}
         		else{
         			showMessage("�α��� ����", "��� �׸��� �Է����ּ���.");
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
         // ������ �߰���
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
	
	// boolean����
	public static boolean checkLogin;
	private boolean nextLogin = false;
	
	// ���� �ʱ�ȭ �� ���ϰ���
	private String serverIP = FINAL_CONSTANCE.serverIP;
	private int serverPort = 10001;
	private Socket socket = null;
	private File cnobFile = null;
	private FileWriter fw = null;
	
	// cnobString �� ���̵� �н�����
	private String cnobString = "init";
	private String id;
	private String password;
	
	// ��Ÿ ����Ʈ �迭
	private byte[] buffer;
	
	// �� ��巹�� ���� �ν��Ͻ�
	private Client_GetMacAddress cgma = null;
	private int count = 0;
	private String macAddress = null;
	private int pathMac = 0;	// 0�̸� false, 1�̸� true, 2�̸� FileNotFoundException
	
	// ������ ���� �� ��ġ���� ���� �ν��Ͻ�
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
		
			dos.writeInt(0);	// �α��� �÷��� ����
			dos.flush();
			
			dos.writeUTF(this.id); // ù��°
			dos.flush();
			
			dos.writeUTF(this.password); // �ι�°
			dos.flush();
			
			checkLogin = dis.readBoolean(); // ����°

			
			if(checkLogin == true){
				// ���⼭ �ƾ�巹���� �����ݴϴ�.
				dos.writeUTF(macAddress);			//	
				dos.flush();
				
				pathMac = dis.readInt();
				if(pathMac == 0){
					showMessage("��ǻ�� ���� ����", "���� �α��� �Ͻ� ��ǻ�ʹ� ��ϵ� ��ǻ�Ͱ� �ƴմϴ�.");
					showMessage("��ġ ���� �ȳ�", "������ �������� �α����ߴ� ��ġ�� �ȳ� �ϰڽ��ϴ�.");
					userAddress = dis.readUTF();
					showMessage("��ġ �ȳ�", "���������� �α����ߴ� ��ġ�� " + userAddress + " �Դϴ�. (Whois OPEN API)");
					nextLogin = false;
				}
				
				else if(pathMac == 1 || pathMac == 2){
					if(pathMac == 1){
						showMessage("��ǻ�� ���� ����", "��ǻ�� ������ �����ϼ̽��ϴ�.");
					}
					
					dos.writeInt(1);		//	�α��� Ƚ�� �߰�
					dos.flush();
					
					cnobFile = new File("C:\\cryptonite\\Privacy",(id+".txt"));
					fw = new FileWriter(cnobFile);
					cnobString = dis.readUTF();
					fw.write(cnobString);
					fw.close();
					
					count = getLoginCount();
					if(count == 1){
						showMessage("��ǻ�� ��� �ȳ�", "���� �α����̹Ƿ� ���� �α����Ͻ� ��ǻ�ͷ� ��ǻ�� ����� ����˴ϴ�.");
						dos.writeUTF(macAddress);
						dos.flush();
					}
					
					//User.cnob�� ����������մϴ�..
					
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
			showMessage("���� ���� ����", "��Ʈ��ũ�� ������ �ȵưų�, ������ ���������Դϴ�.");
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
		StringTokenizer st = new StringTokenizer(this.cnobString, "��");
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