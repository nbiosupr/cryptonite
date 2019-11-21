package Client;

// Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
// Developed by Kim Hye Ju (Dept. Information Security at Suwon University)

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client_FileBackup_UI extends JFrame{   
   // 버퍼드 이미지
   BufferedImage img = null;
   // 기타 인스턴스
   private JTextField filename = null;
   private JLabel file;
   private JButton sending;
   private JButton complete;
   private ButtonGroup group = new ButtonGroup();
   
   // 폰트
   Font font = new Font("Dialog", Font.PLAIN, 20);
   
   //
   
   public Client_FileBackup_UI()
   {
	   setTitle("CRYPTONITE");
       setSize(415, 330);
       setLocation(600, 300);
       setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       setLayout(null);
       setBackground(Color.BLACK);
       
       //이미지 받아오기
       try{
          img = ImageIO.read(new File("C:\\cryptonite\\img\\BackupBackground.png"));//바탕화면 집어넣기
       }catch(IOException e){
          System.out.println("이미지를 불러오지 못했습니다.");
          System.exit(0);
       }
         
       JLayeredPane layeredpane =new JLayeredPane();
       layeredpane.setBounds(0, 0, 415, 330);
       layeredpane.setLayout(null);
       
       Mypanel panel = new Mypanel();
       panel.setBounds(0,0,415,330);      
      
       file = new JLabel();
       file.setBounds(50,150,300,80);
       file.setOpaque(false);
       file.setForeground(Color.WHITE);
       file.setBorder(BorderFactory.createEmptyBorder());     
       layeredpane.add(file);
       
       filename = new JTextField();
	   filename.setBounds(65,165, 276, 31);
	   filename.setForeground(Color.WHITE);
	   filename.setFont(font);
	   filename.setText("Nothing");
	   filename.setHorizontalAlignment(JTextField.CENTER);
	   filename.setBorder(BorderFactory.createEmptyBorder());
	   filename.setOpaque(false);
	   layeredpane.add(filename);
  
       sending = new JButton(new ImageIcon("C:\\cryptonite\\img\\Sending.png"));//안에 이미지 넣어주기
       sending.setBounds(125,230,150,50);
       sending.setBorder(BorderFactory.createEmptyBorder());
       layeredpane.add(sending);  
       
       
       complete = new JButton(new ImageIcon("C:\\cryptonite\\img\\Complete.png"));//안에 이미지 넣어주기
       complete.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\Complete2.png"));
       complete.setBounds(125,230,150,50);
       complete.setBorder(BorderFactory.createEmptyBorder());
       complete.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		setVisible(false);
        	}
        });
       layeredpane.add(complete);
       
       layeredpane.add(panel);//패널1을 레이아웃에 넣기
       add(layeredpane);
       setVisible(false);
   }
   
   public JButton getSendingButton()
   {
	   return sending;
   }
   
   public JButton getCompleteButton()
   {
	   return complete;
   }
   
   public JTextField getFilenameField()
   {
	   return filename;
   }
   
   public void fileBackupUI_ON()
   {
      this.setVisible(true);
   }
   
   public void fileBackUI_OFF()
   {
	   this.setVisible(false);
   }
   
   class Mypanel extends JPanel{
      public void paint(Graphics g){
         g.drawImage(img,0,0,null);
      }
   }
}
