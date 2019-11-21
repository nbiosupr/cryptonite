package Client;

// Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
// Developed by Kim Hye Ju (Dept. Information Security at Suwon University)

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import FINAL_CONSTANCE.FINAL_CONSTANCE;

public class Client_TrayIcon implements ActionListener
{
	private SystemTray systemTray;
	private PopupMenu mPopup;
	private MenuItem menuOC, menuOPF, menuLogout, menuExit;
	private boolean logoutORexit = false;
	private String protectedFolder = null;
	private String serverIP = FINAL_CONSTANCE.serverIP;
	
	private String[] tokenTemp = null;
	private String address = null;
	private StringTokenizer st = null;
	EXIT_1 forExit = null;
	
	private Client_Login_UI lf = null;
	//
	
	// 메인프레임 UI받아오기
	private Client_MainFrame_UI cmfu = null;
	
	public Client_TrayIcon(Client_Login_UI lf)
	{
		try {
			this.lf = lf;
			initSystemTrayIcon();
		} catch (AWTException awte) {
			System.out.println("##### Error occurred during create UI!!!");
			System.out.println(awte.toString());
			System.exit(0);
		}
	}

	private void initSystemTrayIcon() throws AWTException {
		if (SystemTray.isSupported()) {
			
			mPopup = new PopupMenu();
			menuOC = new MenuItem("Cryptonite 실행");
			menuOPF = new MenuItem("보호폴더 열기");
			menuLogout = new MenuItem("로그아웃");
			menuExit = new MenuItem("종료");

			menuOC.addActionListener(this);
			menuOPF.addActionListener(this);
			menuLogout.addActionListener(this);
			menuExit.addActionListener(this);

			mPopup.add(menuOC);
			mPopup.addSeparator();
			mPopup.add(menuOPF);
			mPopup.addSeparator();
			mPopup.add(menuLogout);
			mPopup.add(menuExit);

			Image image = Toolkit.getDefaultToolkit().getImage("C:\\cryptonite\\img\\trayicon.png");
			TrayIcon trayIcon = new TrayIcon(image, "Cryptonite", mPopup);
			trayIcon.setImageAutoSize(true);

			systemTray = SystemTray.getSystemTray();
			systemTray.add(trayIcon);
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == menuOC) {
			boolean loginCheck = false;
			File userCheck = new File("C:\\cryptonite");
			String[] filenames = userCheck.list();
			
			for(int i = 0; i < filenames.length; i++){
				if(filenames[i].equals("User.cnob") == true){
					lf.getMainFrame_UI().setVisible(true);
					loginCheck = true;
					break;
				}
			}
			
			if(loginCheck == false){
				showMessage("오류", "로그인을 먼저 진행해주세요.");
			}
		} 
		else if (ae.getSource() == menuOPF) {
			boolean loginCheck = false;
			File userCheck = new File("C:\\cryptonite");
			String[] filenames = userCheck.list();
			
			for(int i = 0; i < filenames.length; i++){
				if(filenames[i].equals("User.cnob") == true){
					try {
						FileReader fr = new FileReader("C:\\cryptonite\\protected.adr");
						BufferedReader br = new BufferedReader(fr);
						st = new StringTokenizer(br.readLine(),"\\");
						tokenTemp = new String[st.countTokens()];
						for(int j = 0; j < tokenTemp.length; j++){
							tokenTemp[j] = st.nextToken();
						}
						address = tokenTemp[1];
						for(int a = 2; a < tokenTemp.length; a++){
							address += "\\";
							address += tokenTemp[a];
						}
						Runtime rt = Runtime.getRuntime();
						try {
							rt.exec("explorer.exe c:\\" + address); // 향후 수정 필요
						} catch (IOException e) {e.printStackTrace();}
					} 
					catch (FileNotFoundException e1) {
						System.out.println("폴더열기 오류, 프로그램 재시작 바랍니다.");	// 향후 수정 필요
						break;
					} 
					catch (IOException e1) {
						e1.printStackTrace();
					}
					loginCheck = true;
					break;
				}
			}
			
			if(loginCheck == false){
				showMessage("오류", "로그인을 먼저 진행해주세요.");
			}
		} 
		else if (ae.getSource() == menuLogout) {
			logoutORexit = false;	// false가 로그아웃
			forExit = new EXIT_1(logoutORexit, lf);
		} 
		else if (ae.getSource() == menuExit) {
			logoutORexit = true;	// true가 Exit
			forExit = new EXIT_1(logoutORexit, lf);
		}
	}

	private void showMessage(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}

class EXIT_1 extends JFrame{
	   
	   BufferedImage img = null;
	   
	   JButton YES;
	   JButton NO;
	   ButtonGroup group = new ButtonGroup();
	   private StringTokenizer st = null;
	   private String[] temp = null;
	   private File installFolder = null;
	   private String filter = null;
	   private boolean isCnob = false;
	   private String serverIP = FINAL_CONSTANCE.serverIP;
	   
	   private Client_Login_UI lf = null;
	   private Client_FolderChooser_UI fc = null;
	   
	   private Socket socket = null;
	   
	   private Client_FolderScan fs = null;
	   private Client_SendFiles sf = null;
	   private Client_checkEncryptionAnime cea = null;
	   
	   public EXIT_1(boolean choice, Client_Login_UI lf){ 
		   if(choice == true)
		      setTitle("Exit?");
		   else if(choice == false)
			   setTitle("Logout?");
		   
		      setLocation(750, 350);
		      setSize(315, 190);
		      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		      setLayout(null);
		      setBackground(Color.BLACK);
		      
		      //이미지 받아오기
		      try{
		    	  if(choice == true)
		    		  img = ImageIO.read(new File("C:\\cryptonite\\img\\exit.png"));//바탕화면 집어넣기
		    	  else
		    		  img = ImageIO.read(new File("C:\\cryptonite\\img\\login_exit.png"));
		      }catch(IOException e){
		         System.out.println("이미지를 불러오지 못했습니다.");
		         System.exit(0);
		      }
		            
		      JLayeredPane layeredpane =new JLayeredPane();
		      layeredpane.setBounds(0, 0, 315, 190);
		      layeredpane.setLayout(null);
		   
		      Mypanel panel = new Mypanel();
		      panel.setBounds(0,0,315,190); 
		      
		      NO = new JButton(new ImageIcon("C:\\cryptonite\\img\\NO.png"));//안에 이미지 넣어주기
		      NO.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\NO2.png"));
		      NO.setBounds(170, 100, 60, 30);
		      NO.setBorder(BorderFactory.createEmptyBorder());
		      layeredpane.add(NO);
		      
		      NO.addActionListener(new ActionListener()
		      {
		          public void actionPerformed(ActionEvent e)
		          {
		           dispose();
		          }
		      }
		      ) ;
		     
		      YES = new JButton(new ImageIcon("C:\\cryptonite\\img\\YES.png"));//안에 이미지 넣어주기
		      YES.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\YES2.png"));
		      YES.setBounds(70, 100, 60, 30);
		      YES.setBorder(BorderFactory.createEmptyBorder());
		      layeredpane.add(YES);
		      
		      if(choice == true){
			      YES.addActionListener(new ActionListener()
			      {
			          public void actionPerformed(ActionEvent e)
			          {
			             System.exit(0);
			          }
			      }
			      ) ;
		      }
		      else if(choice == false){
		    	  YES.addActionListener(new ActionListener()
			      {
			          public void actionPerformed(ActionEvent e)
			          {
			        	 String cnobName = null;
			        	 File cnobFile = null;
			             installFolder = new File("C:\\cryptonite");
			             temp = installFolder.list();
			             for(int i = 0; i < temp.length; i++){
			            	 st = new StringTokenizer(temp[i], ".");
			            	 while(st.hasMoreTokens()){
			            		 filter = st.nextToken();
			            		 if(filter.equals("cnob") == true){
			            			 cnobName = temp[i];
			            			 isCnob = true;
			            			 break;
			            		 }
			            	 }
			            	 if(isCnob == true)
			            		 break;
			             }
			             if(isCnob == true){
			            	 try {
								socket = new Socket(serverIP, 9000);	// serverIP는 나중에 수정하기
								if(lf.getSendLogin().getLoginCount() == 1){
									fc = lf.getFolderChooser();
					            	fs = fc.getFolderScan();
					            	sf = fc.getSendFiles();
					            	cea = fc.getCEA();
					            	 
					            	fs.stopThread();
					            	sf.stopThread();
					            	cea.stopThread();
								}
								else if(lf.getSendLogin().getLoginCount() >= 2){
									lf.getCFS().stopThread();
									lf.getCSF().stopThread();
									lf.getCEA().stopThread();
								}
								
				            	 cnobFile = new File("C:\\cryptonite",cnobName);
				            	 
				            	 java.io.OutputStream out = socket.getOutputStream();
				            	 DataOutputStream dos = new DataOutputStream(out);
				            	 
				            	 dos.writeBoolean(true);
				            	 dos.flush();
				            	 
				            	 cnobFile.delete();
				            	 dos.close();
				            	 
				            	 dispose();
				            	 lf.getMainFrame_UI().dispose();
				            	 
				            	 lf.setVisible(true);
							} catch (UnknownHostException e1) {
								e1.printStackTrace();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
			            	
			            	 lf.getMainFrame_UI().dispose();
			             }
			             else if(isCnob == false){
			            	 showMessage("로그아웃 오류", "로그인후 로그아웃을 진행해주세요.");
			             }
			             
			             dispose();
			          }
			      }
			      ) ;
		      }
		      
		      layeredpane.add(panel);//패널1을 레이아웃에 넣기
		      
		      add(layeredpane);
		      setVisible(true);
	   }
	   
	   class Mypanel extends JPanel{
	      public void paint(Graphics g){
	         g.drawImage(img,0,0,null);
	      }
	   }
	   
	   public EXIT_1 getEXIT()
	   {
		   return this;
	   }
	   
	   private void showMessage(String title, String message) {
			JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
		}
	}