package Client;

// Developed by Won Sung Yeon (Dept. Information Media at Suwon University)
// Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
// Developed by Sangwoon Park (Dept. Information Seccurity at Suwon Univ.)

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
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

public class Client_SignUp_UI extends JFrame {
	   
	   BufferedImage img = null;
	   SHA_256 sha = null;
	   
	   private boolean checkID = false;
	   public static boolean goSignUP = false;
	   public static boolean goFolderScan = false;
	   
	   public static boolean getGoSignUP()
	   {
		   return goSignUP;
	   }
	   
	   public static boolean getgoFoldercan()
	   {
		   return goFolderScan;
	   }
	   
	   private String serverIP = FINAL_CONSTANCE.serverIP;
	   private int serverPort = 10001;
	   private Socket socket = null;
	   
	   private boolean passwordCheck = false;
	   
	   JTextField nameField;
	   private String name = "init";
	   
	   JTextField idField;
	   private String id = "init";

	   JPasswordField passwdField;
	   private String password = "init";
	   
	   JPasswordField passwdField2;
	   private String password2 = "init2";
	   
	   JTextField emailField;
	   private String email = "init";
	   
	   JButton same;
	   JButton ok;
	   JButton no;
	   ButtonGroup group = new ButtonGroup();
	   
	   Font fontblank = new Font ("SansSerif", Font.BOLD,13);
	   
	   public Client_SignUp_UI()
	   {
		   
	      setTitle("CRYPTONITE");
	      setBounds(710,200,508, 660);
	      
	      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	      getContentPane().setLayout(null);
	      setBackground(Color.BLACK);
	      
	      //이미지 받아오기
	      try
	      {
	         img = ImageIO.read(new File("C:\\cryptonite\\img\\Join.png"));//바탕화면 집어넣기
	         
	      }catch(IOException e){
	         System.out.println("이미지를 불러오지 못했습니다.");
	         System.exit(0);
	      }
	          
	      JLayeredPane layeredpane =new JLayeredPane();
	      layeredpane.setBounds(0, 0, 490, 655);
	      layeredpane.setLayout(null);
	      
	      Mypanel panel = new Mypanel();
	      panel.setBounds(0,0,490,655);      
	      
	      nameField = new JTextField();
	      nameField.setBounds(193,290, 130, 20);
	      
	      nameField.setForeground(Color.WHITE);
	      nameField.setBorder(BorderFactory.createEmptyBorder());
	      nameField.setOpaque(false);
	      
	      nameField.addKeyListener(new KeyListener()
	      {
			@Override
			public void keyPressed(KeyEvent e) { }
			
			@Override
			public void keyReleased(KeyEvent e) 
			{
				name = nameField.getText();
			}
			
			@Override
			public void keyTyped(KeyEvent e)
			{
				
			}
			
	      });
	      layeredpane.add(nameField);
	   
	      idField = new JTextField();
	      idField.setBounds(193, 326, 130, 20);
	      
	      idField.setForeground(Color.WHITE);
	      idField.setBorder(BorderFactory.createEmptyBorder());
	      idField.setOpaque(false);
	      
	      idField.addKeyListener(new KeyListener()
	      {
			@Override
			public void keyPressed(KeyEvent e)  { }
			
			@Override
			public void keyReleased(KeyEvent e) 
			{
				id = idField.getText();
			}
			
			@Override
			public void keyTyped(KeyEvent e) { }
	      }); 
	      layeredpane.add(idField);
	      
	      passwdField = new JPasswordField();
	      passwdField.setBounds(194, 365, 130, 20);
	      
	      passwdField.setEchoChar('●');
	      passwdField.setBorder(BorderFactory.createEmptyBorder());
	      passwdField.setForeground(Color.WHITE);
	      passwdField.setOpaque(false);
	      passwdField.addKeyListener(new KeyListener()
	      {
			@Override
			public void keyPressed(KeyEvent e) { }
			
			@Override
			public void keyReleased(KeyEvent e) 
			{
				password = new String(passwdField.getPassword());
			}
			
			@Override
			public void keyTyped(KeyEvent e) { }
	      });
	      layeredpane.add(passwdField);
	      
	      passwdField2 = new JPasswordField();
	      passwdField2.setBounds(194, 408, 130, 20);
	      passwdField2.setEchoChar('●');
	      passwdField2.setBorder(BorderFactory.createEmptyBorder());
	      passwdField2.setForeground(Color.WHITE);
	      passwdField2.setOpaque(false);
	      passwdField2.addKeyListener(new KeyListener()
	      {
			@Override
			public void keyPressed(KeyEvent e) { }
			
			@Override
			public void keyReleased(KeyEvent e) 
			{
				password2 = new String(passwdField2.getPassword());
				
				if(password2.equals(password))
				{
					passwordCheck = true;
				}
				else if(password2.equals(password) == false && password2.equals(null) == false)
				{
					passwordCheck = false;
				}
				
			}
			
			@Override
			public void keyTyped(KeyEvent e) { }
			
	      });
	      layeredpane.add(passwdField2);
	   
	      emailField = new JTextField();
	      emailField.setBounds(193, 455, 160, 20);
	      emailField.setBorder(BorderFactory.createEmptyBorder());
	      emailField.setForeground(Color.WHITE);
	      emailField.setOpaque(false);
	      emailField.addKeyListener(new KeyListener()
	      {
			@Override
			public void keyPressed(KeyEvent e) { }
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				email = emailField.getText();
			}
			
			@Override
			public void keyTyped(KeyEvent e) { }
			
	      });
	      layeredpane.add(emailField);
	      
	      
	      same = new JButton(new ImageIcon("C:\\cryptonite\\img\\Same.png"));//안에 이미지 넣어주기
	      same.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\Samech.png"));
	      same.setBounds(356, 312, 80, 38);
	      same.setBorderPainted(false);
	      same.setFocusPainted(false);
	      same.setContentAreaFilled(false);
	      same.addActionListener(new ActionListener()
	      {
	      	public void actionPerformed(ActionEvent arg0)
	      	{
	      		try 
	      		{
	      			if(id.equals("init") == false)
	      			{
						socket = new Socket(serverIP, serverPort);
						
						java.io.OutputStream out = socket.getOutputStream();
						DataOutputStream dos = new DataOutputStream(out);
						
						dos.writeInt(2);	// 중복확인 플래그 전달
						dos.flush();
						System.out.println("중복확인 플래그 2 전달");
						
						dos.writeUTF(id);	// 1번째
						dos.flush();
						
						InputStream in = socket.getInputStream();
						DataInputStream dis = new DataInputStream(in);
						
						checkID = dis.readBoolean();	// 2번째
						if(checkID == true)
						{
							showMessage("아이디 중복 없음", "중복되지 않는 아이디입니다.");
							goSignUP = true;
						}
						else if(checkID == false)
						{
							showMessage("아이디 중복", "이미 존재하는 아이디 입니다.");
						}
							
						dos.close();
						out.close();
						
						dis.close();
						in.close();
	      			}
	      			else
	      			{
	      				showMessage("입력 오류", "아이디를 입력해주세요.");
	      			}
	      			
				} catch (UnknownHostException e) { e.printStackTrace();
				} catch (IOException e) { e.printStackTrace();
				}
	      		
	      	}
	      });   
	      
	      layeredpane.add(same);
	      
	      no = new JButton(new ImageIcon("C:\\cryptonite\\img\\Cancle.png"));//안에 이미지 넣어주기
	      no.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\Canclech.png"));
	      no.setPressedIcon(new ImageIcon("C:\\cryptonite\\img\\Canclepr.png"));
	      
	      no.setBounds(260, 544, 82, 38);
	      no.setBorder(BorderFactory.createEmptyBorder());
	      
	      no.setBorderPainted(false);
	      no.setFocusPainted(false);
	      no.setContentAreaFilled(false);
	      
	      no.addActionListener(new ActionListener() 
	      {
	      	public void actionPerformed(ActionEvent arg0) 
	      	{
	      		dispose();
	      	}
	      	
	      });
	      layeredpane.add(no);
	      
	      ok = new JButton(new ImageIcon("C:\\cryptonite\\img\\Joinbt.png"));//안에 이미지 넣어주기
	      ok.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\Joinch.png"));
	      ok.setPressedIcon(new ImageIcon("C:\\cryptonite\\img\\Joinpr.png"));
	      
	      ok.setBounds(156, 544, 82, 38);
	      ok.setBorderPainted(false);
	      ok.setFocusPainted(false);
	      ok.setContentAreaFilled(false);
	      
	      ok.addActionListener(new ActionListener() 
	      {
	        	public void actionPerformed(ActionEvent arg0)
	        	{
	        		if(name.equals("init") == false && id.equals("init") == false && password.equals("init") == false &&
	        				password2.equals("init2") == false && email.equals("init") == false && goSignUP == true)
	        		{
		        		if(passwordCheck == true)
		        		{
		        			showMessage("가입 완료", "회원가입이 완료되었습니다. 로그인해주세요.");
		        			
		        			userKeyGenerator ukg = new userKeyGenerator();
		        			sha = new SHA_256(name,id,password,email,ukg.genEncAesKey(password), ukg.getSalt(), ukg.getIterationCount());//
		        			
		        			dispose();
		        		}
		        		else if(passwordCheck == false)
		        		{
		        			showMessage("비밀번호 오류", "비밀번호가 일치하지 않습니다.");
		        		}
		        		
	        		}
	        		else
	        		{
	        			showMessage("가입 오류", "모든 항목을 다 입력하지않았거나, 아이디 중복입니다.");
	        		}
	        	}
	        });
	      
	      layeredpane.add(ok);
	      layeredpane.add(panel);//패널1을 레이아웃에 넣기
	      getContentPane().add(layeredpane);
	      
	      setVisible(true);
	   }
	   
	   class Mypanel extends JPanel
	   {
	      public void paint(Graphics g)
	      {
	         g.drawImage(img,0,0,null);
	      }
	   }

	   private void showMessage(String title, String message) 
	   {
			JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	   }
	   
	}

	class SHA_256
	{
		MessageDigest md = null;
		
		private String serverIP = FINAL_CONSTANCE.serverIP;
		private int serverPort = 10001;
		private Socket socket = null;
		
		private String name;
		private String id;
		private String password;
		private String email;
		
		private byte[] AES_Key;
		private byte[] salt;
		private int iteration;
		
		private byte[] temp_name;
		private byte[] temp_pwd;
		private byte[] temp_email;
		
		public SHA_256(String name, String id, String password, String email, byte[] AES_Key, byte[] salt, int iteration)
		{
			this.name = name;
			this.id = id;
			this.password = password;
			this.email = email;
			this.AES_Key = AES_Key;
			this.salt = salt;
			this.iteration = iteration;
			
			SHA_Encryption();
			sendPrivacy();
		}
		
		public void sendPrivacy()
		{
			try {
				socket = new Socket(serverIP, serverPort);
				System.out.println("socket : " + socket);
				
				java.io.OutputStream out = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(out);
				User user = new User(id, password, AES_Key, salt, iteration);			
				
				dos.writeInt(1); // 회원가입 플래그 전송
				dos.flush();
				
				dos.writeUTF(name);
				dos.flush();
				
				dos.writeUTF(id);
				dos.flush();
				
				dos.writeUTF(password);
				dos.flush();
				
				dos.writeUTF(email);
				dos.flush();			
				
				dos.writeUTF(AES_Key.toString());
				dos.flush();
				
				dos.writeUTF(salt.toString());
				dos.flush();
				
				dos.writeInt(iteration);
				dos.flush();
				
				ObjectOutputStream oos = new ObjectOutputStream(out);
				oos.writeObject(user);
				oos.flush();
				
				oos.close();
				dos.close();
				out.close();
			} 
			catch (UnknownHostException e) {
				e.printStackTrace();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void SHA_Encryption()
		{
			setName();
			setPWD();
			setEmail();
		}
		
		private void setName()
		{
			try {
				md = MessageDigest.getInstance("SHA-256");
				temp_name = name.getBytes();
				md.update(temp_name);
				//encrypted_name = md.digest();
				this.name = new String(Base64Coder.encode(md.digest()));
				/*this.name = Integer.toHexString(encrypted_name[0]);
				for(int i = 1; i < encrypted_name.length; i++){
					this.name += Integer.toHexString(encrypted_name[i]);
				}*/
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		
		private void setPWD()
		{
			try {
				md = MessageDigest.getInstance("SHA-256");
				temp_pwd = password.getBytes();
				md.update(temp_pwd);
				password = new String(Base64Coder.encode(md.digest()));
				/*encrypted_pwd = md.digest();
				this.password = Integer.toHexString(encrypted_pwd[0]);
				for(int i = 1; i < encrypted_pwd.length; i++){
					this.password += Integer.toHexString(encrypted_pwd[i]);
				}*/
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		
		private void setEmail()
		{
			try {
				md = MessageDigest.getInstance("SHA-256");
				temp_email = email.getBytes();
				md.update(temp_email);
				email = new String(Base64Coder.encode(md.digest()));
				/*encrypted_email = md.digest();
				this.email = Integer.toHexString(encrypted_email[0]);
				for(int i = 1; i < encrypted_email.length; i++){
					this.email += Integer.toHexString(encrypted_email[i]);
				}*/
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}

	class userKeyGenerator
	{
		private byte[] salt = null;
		private int iterationCount = 0;

		public userKeyGenerator()
		{
			SecureRandom  random = new SecureRandom();
			salt = new byte[8]; 
			random.nextBytes(salt);
			iterationCount = 1000;
		}
		
		public byte[] genEncAesKey(String password)
		{
			System.out.println(new String(salt));
			PBE aesPBE = new PBE(salt, iterationCount);
			
			SecureRandom  random = new SecureRandom();
			byte[] keyData = new byte[32];
			random.nextBytes(keyData);
			
			byte[] encAesKey = null;
			try {
				encAesKey = aesPBE.encrypt(password, keyData);
				
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return encAesKey;
		}
		
		public byte[] getSalt() { return salt; }
		
		public int getIterationCount() { return iterationCount; }
		
	}

	class PBE
	{
	//private Charset charset = Charset.forName("EUC-KR");//
		
		private byte[] salt;
		private int iterationCount;
		
		public PBE(byte[] salt, int iterationCount)
		{
			this.salt = salt;
			this.iterationCount = iterationCount;
		}
		
		private SecretKey generateKey(String strPassword) throws GeneralSecurityException 
		{
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec keySpec = new PBEKeySpec(strPassword.toCharArray(), salt, iterationCount, 256);
			SecretKey secretKey = new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), "AES");
			
			return secretKey;
		}
		
		public byte[] encrypt (String password, byte[] aesKey) throws GeneralSecurityException 
		{
			SecretKey secretKey = generateKey(password);
			
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			
			byte[] encryptData = cipher.doFinal(aesKey);
			return encryptData;
		}
		
		public SecretKey decrypt(String password, byte[] aesKey) throws GeneralSecurityException
		{
			SecretKey secretKey = generateKey(password);
			
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			
			byte[] plainData = cipher.doFinal(aesKey);
			SecretKey decryptAesKey = new SecretKeySpec(plainData, "AES");
			
			return decryptAesKey;
		}

	}

	class Base64Coder
	{

		//The line separator string of the operating system.
		private static final String systemLineSeparator = System.getProperty("line.separator");

		//Mapping table from 6-bit nibbles to Base64 characters.
		private static final char[] map1 = new char[64];
		static 
		{
		   int i=0;
		   
		   for (char c='A'; c<='Z'; c++) map1[i++] = c;
		   for (char c='a'; c<='z'; c++) map1[i++] = c;
		   for (char c='0'; c<='9'; c++) map1[i++] = c;
		   
		   map1[i++] = '+'; map1[i++] = '/'; 
		}

		//Mapping table from Base64 characters to 6-bit nibbles.
		private static final byte[] map2 = new byte[128];
		static 
		{
		   for (int i=0; i<map2.length; i++) map2[i] = -1;
		   for (int i=0; i<64; i++) map2[map1[i]] = (byte)i; 
		}

		/**
		* Encodes a string into Base64 format.
		* No blanks or line breaks are inserted.
		* @param s  A String to be encoded.
		* @return   A String containing the Base64 encoded data.
		*/
		public static String encodeString (String s) 
		{
			return new String(encode(s.getBytes())); 
		}

		/**
		* Encodes a byte array into Base 64 format and breaks the output into lines of 76 characters.
		* This method is compatible with <code>sun.misc.BASE64Encoder.encodeBuffer(byte[])</code>.
		* @param in  An array containing the data bytes to be encoded.
		* @return    A String containing the Base64 encoded data, broken into lines.
		*/
		public static String encodeLines (byte[] in)
		{
			return encodeLines(in, 0, in.length, 76, systemLineSeparator); 
		}

		/**
		* Encodes a byte array into Base 64 format and breaks the output into lines.
		* @param in            An array containing the data bytes to be encoded.
		* @param iOff          Offset of the first byte in <code>in</code> to be processed.
		* @param iLen          Number of bytes to be processed in <code>in</code>, starting at <code>iOff</code>.
		* @param lineLen       Line length for the output data. Should be a multiple of 4.
		* @param lineSeparator The line separator to be used to separate the output lines.
		* @return              A String containing the Base64 encoded data, broken into lines.
		*/
		public static String encodeLines (byte[] in, int iOff, int iLen, int lineLen, String lineSeparator)
		{
			int blockLen = (lineLen*3) / 4;
			if (blockLen <= 0) { throw new IllegalArgumentException(); }
			
			int lines = (iLen+blockLen-1) / blockLen;
			int bufLen = ((iLen+2)/3)*4 + lines*lineSeparator.length();
			
			StringBuilder buf = new StringBuilder(bufLen);
			int ip = 0;
			
			while (ip < iLen) 
			{
			   int l = Math.min(iLen-ip, blockLen);
			   
			   buf.append(encode(in, iOff+ip, l));
			   buf.append(lineSeparator);
			   
			   ip += l; 
			}
			
			return buf.toString(); 
		}

		/**
		* Encodes a byte array into Base64 format.
		* No blanks or line breaks are inserted in the output.
		* @param in  An array containing the data bytes to be encoded.
		* @return    A character array containing the Base64 encoded data.
		*/
		public static char[] encode (byte[] in) 
		{
			return encode(in, 0, in.length); 
		}

		/**
		* Encodes a byte array into Base64 format.
		* No blanks or line breaks are inserted in the output.
		* @param in    An array containing the data bytes to be encoded.
		* @param iLen  Number of bytes to process in <code>in</code>.
		* @return      A character array containing the Base64 encoded data.
		*/
		public static char[] encode (byte[] in, int iLen)
		{
			return encode(in, 0, iLen); 
		}

		/**
		* Encodes a byte array into Base64 format.
		* No blanks or line breaks are inserted in the output.
		* @param in    An array containing the data bytes to be encoded.
		* @param iOff  Offset of the first byte in <code>in</code> to be processed.
		* @param iLen  Number of bytes to process in <code>in</code>, starting at <code>iOff</code>.
		* @return      A character array containing the Base64 encoded data.
		*/
		public static char[] encode (byte[] in, int iOff, int iLen) 
		{
			int oDataLen = (iLen*4+2)/3;       // output length without padding
			int oLen = ((iLen+2)/3)*4;         // output length including padding
			
			char[] out = new char[oLen];
			
			int ip = iOff;
			int iEnd = iOff + iLen;
			
			int op = 0;
			
			while (ip < iEnd) 
			{
			   int i0 = in[ip++] & 0xff;
			   int i1 = ip < iEnd ? in[ip++] & 0xff : 0;
			   int i2 = ip < iEnd ? in[ip++] & 0xff : 0;
			   
			   int o0 = i0 >>> 2;
			   int o1 = ((i0 &   3) << 4) | (i1 >>> 4);
			   int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
			   int o3 = i2 & 0x3F;
			   
			   out[op++] = map1[o0];
			   out[op++] = map1[o1];
			   
			   out[op] = op < oDataLen ? map1[o2] : '='; op++;
			   out[op] = op < oDataLen ? map1[o3] : '='; op++; 
			   
			}
			
			return out; 
		}

		/**
		* Decodes a string from Base64 format.
		* No blanks or line breaks are allowed within the Base64 encoded input data.
		* @param s  A Base64 String to be decoded.
		* @return   A String containing the decoded data.
		* @throws   IllegalArgumentException If the input is not valid Base64 encoded data.
		*/
		public static String decodeString (String s) 
		{
			return new String(decode(s)); 
		}

		/**
		* Decodes a byte array from Base64 format and ignores line separators, tabs and blanks.
		* CR, LF, Tab and Space characters are ignored in the input data.
		* This method is compatible with <code>sun.misc.BASE64Decoder.decodeBuffer(String)</code>.
		* @param s  A Base64 String to be decoded.
		* @return   An array containing the decoded data bytes.
		* @throws   IllegalArgumentException If the input is not valid Base64 encoded data.
		*/
		public static byte[] decodeLines (String s) 
		{
			char[] buf = new char[s.length()];
			int p = 0;
			
			for (int ip = 0; ip < s.length(); ip++) 
			{
			   char c = s.charAt(ip);
			   if (c != ' ' && c != '\r' && c != '\n' && c != '\t')
			   {
				   buf[p++] = c;
			   }
			}
			
			return decode(buf, 0, p); 
		}

		/**
		* Decodes a byte array from Base64 format.
		* No blanks or line breaks are allowed within the Base64 encoded input data.
		* @param s  A Base64 String to be decoded.
		* @return   An array containing the decoded data bytes.
		* @throws   IllegalArgumentException If the input is not valid Base64 encoded data.
		*/
		public static byte[] decode (String s) 
		{
			return decode(s.toCharArray());
		}

		/**
		* Decodes a byte array from Base64 format.
		* No blanks or line breaks are allowed within the Base64 encoded input data.
		* @param in  A character array containing the Base64 encoded data.
		* @return    An array containing the decoded data bytes.
		* @throws    IllegalArgumentException If the input is not valid Base64 encoded data.
		*/
		public static byte[] decode (char[] in) 
		{
			return decode(in, 0, in.length); 
		}
		
		/**
		* Decodes a byte array from Base64 format.
		* No blanks or line breaks are allowed within the Base64 encoded input data.
		* @param in    A character array containing the Base64 encoded data.
		* @param iOff  Offset of the first character in <code>in</code> to be processed.
		* @param iLen  Number of characters to process in <code>in</code>, starting at <code>iOff</code>.
		* @return      An array containing the decoded data bytes.
		* @throws      IllegalArgumentException If the input is not valid Base64 encoded data.
		*/
		public static byte[] decode (char[] in, int iOff, int iLen)
		{
			if (iLen%4 != 0) 
			{
				throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4."); 
			}
			
			while (iLen > 0 && in[iOff+iLen-1] == '=') { iLen--; }
			
			int oLen = (iLen*3) / 4;
			byte[] out = new byte[oLen];
			
			int ip = iOff;
			int iEnd = iOff + iLen;
			int op = 0;
			
			while (ip < iEnd) 
			{
			   int i0 = in[ip++];
			   int i1 = in[ip++];
			   int i2 = ip < iEnd ? in[ip++] : 'A';
			   int i3 = ip < iEnd ? in[ip++] : 'A';
			   
			   if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
			   {
			      throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
			   }
			   
			   int b0 = map2[i0];
			   int b1 = map2[i1];
			   int b2 = map2[i2];
			   int b3 = map2[i3];
			   
			   if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
			   {
			      throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
			   }
			   
			   int o0 = ( b0       <<2) | (b1>>>4);
			   int o1 = ((b1 & 0xf)<<4) | (b2>>>2);
			   int o2 = ((b2 &   3)<<6) |  b3;
			   
			   out[op++] = (byte)o0;
			   if (op<oLen) { out[op++] = (byte)o1; }
			   if (op<oLen) { out[op++] = (byte)o2; }
			   
			}
			
			return out; 
		}

		//Dummy constructor.
		private Base64Coder() {}

	}