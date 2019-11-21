package Client;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import User.User;

public class Client_FolderChooser_UI extends JFrame implements ActionListener{
    
	// BufferedImage �ν��Ͻ�
    BufferedImage img = null;
    
    // FileChooser�� ��ư����
    public static String protectedFolder = null;
    
    private JFileChooser jfc = new JFileChooser();
    private JButton find = new JButton("����ã��");
    private JButton save = new JButton("��ȣ�ϱ�");
    private JLabel jlb = new JLabel(" ");
    private boolean endChoose = false;
    
    // ������ĵ, ��������, �ִϸ��̼ǰ��� ��ü�� �����弱��
    private Client_FolderScan cfs = null;
    private Client_SendFiles  csf = null;
    private Client_checkEncryptionAnime cea = null;
    private Client_FileShare_Send cfss = null;
    
    // ��ȣ���� ������ C:\cryptonite�� protected.adr�� ������� �ν��Ͻ�
    private File protectedAddress = null;
    private FileWriter fw = null;
    
    // ����� ��й�ȣ
    private notifyLogin nl = null;
    private User loginedUser = null;
    private String password = null;
    private byte[] AES_Key = null;
      
  	public Client_FolderChooser_UI(String password, notifyLogin nl, Client_MainFrame_UI cmfu)
  	{
  		this.password = password;
  		this.nl = nl;
  		cmfu.setEnabled(false);
  		
  		setTitle("���� ����");
        setLocation(700,400);
        setSize(415, 235);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setAlwaysOnTop(true);	// �׻� �� ����
        
        try{
            img = ImageIO.read(new File("C:\\cryptonite\\img\\find1.png"));//����ȭ�� ����ֱ�
         }catch(IOException e){
            System.out.println("�̹����� �ҷ����� ���߽��ϴ�.");
            System.exit(0);
         }           
               
         JLayeredPane layeredpane =new JLayeredPane();
         layeredpane.setBounds(0, 0, 415, 235);
         layeredpane.setLayout(null);
          
         Mypanel panel = new Mypanel();
         panel.setBounds(0,0,415,235);      

         find = new JButton(new ImageIcon("C:\\cryptonite\\img\\find2.png"));//�ȿ� �̹��� �־��ֱ�
         find.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\find2_clicked.png"));
         find.setBounds(290, 120, 40, 25);
         find.setBorder(BorderFactory.createEmptyBorder());//
         layeredpane.add(find);//
         
         jlb = new JLabel();
         jlb.setBounds(30, 120, 250, 25);
         jlb.setOpaque(true);
         jlb.setForeground(Color.BLACK);
         jlb.setBorder(BorderFactory.createEmptyBorder());
         
         layeredpane.add(jlb);
         
         save = new JButton(new ImageIcon("C:\\cryptonite\\img\\find3.png"));//�ȿ� �̹��� �־��ֱ�
         save.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\find3_clicked.png"));
         save.setBounds(340, 120, 40, 25);
         save.setBorder(BorderFactory.createEmptyBorder());
         layeredpane.add(save);
         save.addActionListener(new ActionListener()
         { 
             public void actionPerformed(ActionEvent e)
             {
                protectedFolder = jlb.getText(); // ������θ� protectedFolder ��Ʈ���� ����
                protectedAddress = new File("C:\\cryptonite","protected.adr");
                System.out.println("protectedFolder : " + protectedFolder);
                try {
					fw = new FileWriter(protectedAddress);
					fw.write(protectedFolder);
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
                dispose();
                endChoose = true;
                System.out.println("endChoose : " + endChoose);
                showMessage("���� ��ȣ ����", "���� ��ȣ�� �����մϴ�."); // �� �޽��������
                cfs = new Client_FolderScan();
                loginedUser = nl.getUserObejct();
                System.out.println("loginedUser : " + loginedUser);
                System.out.println("getAesKey() : " + loginedUser.getAesKey());
 				AES_Key = loginedUser.getAesKey();
 				PBE pbe = new PBE(loginedUser.getSalt(), 1000);
 				try {
					cfs.setAES_Key(pbe.decrypt(password, AES_Key));
				} 
 				catch (GeneralSecurityException e1) {
					e1.printStackTrace();
				}
 				cmfu.setEnabled(true);
                
                csf = new Client_SendFiles();
                cea = new Client_checkEncryptionAnime();
                
                cfs.start();
                csf.start();
                cea.start();
             }
         }
      );
               
      layeredpane.add(panel);//�г�1�� ���̾ƿ��� �ֱ�
         
      add(layeredpane);
      setVisible(true);  
         
      this.start();
      
  	}
      	
      	public boolean getEndChoose()
      	{
      		return endChoose;
      	}
      
      	public String getProtectedFolder()
      	{
      		return protectedFolder;
      	}
         
        
        class Mypanel extends JPanel
        {
            public void paint(Graphics g)
            {
               g.drawImage(img,0,0,null);
            }
        }
         
      public Client_FolderScan getFolderScan()
      {
    	  return cfs;
      }
      
      public Client_SendFiles getSendFiles()
      {
    	  return csf;
      }
      
      public Client_checkEncryptionAnime getCEA()
      {
    	  return cea;
      }
               
      public void start()
      {
            find.addActionListener(this);
      }

      public void actionPerformed(ActionEvent arg0) 
      {
          if(arg0.getSource() == find)
          {
        	  jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        	  File dir = jfc.getSelectedFile();
        	  if(jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        	  {                        
        		  jlb.setText(jfc.getSelectedFile().toString());
        	  }
          }
      }
      
      private void showMessage(String title, String message) {
  		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
  	}
}
