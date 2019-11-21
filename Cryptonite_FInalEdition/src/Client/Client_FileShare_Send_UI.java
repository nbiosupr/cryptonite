package Client;

// Developed by Won Sung Yeon (Dept. Information Media at Suwon University)
// Developed by Youn Hee Seung (Dept. Information Security at Suwon University)

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client_FileShare_Send_UI extends JFrame {
	
    BufferedImage img = null;
    private boolean sendFlag = false;
    
    // JLabel 인스턴스
    private JTextField OTP;
    private JLabel nowYouWillSend;
    private JLabel fileCount;
    
    // JButton 인스턴스
    private JButton Sendbt;
    private JButton Sending;
    private JButton Complete;
    
    Font font1 = new Font("SansSerif", Font.BOLD, 32);
    Font font2 = new Font("SansSerif", Font.BOLD, 23);
    Font font3 = new Font("SansSerif", Font.BOLD, 30);

    public Client_FileShare_Send_UI() 
    {
        setTitle("Cryptonite");
        setBounds(710,200,460,645);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 
        // 레이아웃 설정
        getContentPane().setLayout(null);
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 440, 619);
        layeredPane.setLayout(null);
 
        // 패널1
        // 이미지 받아오기
        try {
            img = ImageIO.read(new File("C:\\cryptonite\\img\\FileShare_SEND.png"));
        } catch (IOException e) {
            System.out.println("이미지 불러오기 실패");
            System.exit(0);
        }
         
        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, 460, 645);
        
        OTP = new JTextField();
        OTP.setBounds(105, 347, 330, 50);
        OTP.setForeground(Color.white);
        OTP.setFont(font1);
        OTP.setEditable(false);
        OTP.setOpaque(false);
        OTP.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        OTP.setHorizontalAlignment(JTextField.CENTER);
        layeredPane.add(OTP);
        
        nowYouWillSend = new JLabel();
        nowYouWillSend.setBounds(59, 405, 330, 50);
        nowYouWillSend.setForeground(Color.white);
        nowYouWillSend.setFont(font2);
        nowYouWillSend.setOpaque(false);
        nowYouWillSend.setText("NOW YOU WILL SEND");
        nowYouWillSend.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        nowYouWillSend.setHorizontalAlignment(JTextField.CENTER);
        layeredPane.add(nowYouWillSend);
        
        fileCount = new JLabel();
        fileCount.setBounds(57, 450, 330, 50);
        fileCount.setForeground(Color.white);
        fileCount.setFont(font3);
        fileCount.setOpaque(false);
        fileCount.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        fileCount.setHorizontalAlignment(JTextField.CENTER);
        layeredPane.add(fileCount);
        
        Sendbt = new JButton(new ImageIcon("C:\\cryptonite\\img\\SEND.png"));
        Sendbt.setBounds(140, 510, 160, 40);
        Sendbt.setOpaque(false);
        Sendbt.setBorderPainted(false);
        Sendbt.setContentAreaFilled(false);
        Sendbt.setFocusPainted(false);
        Sendbt.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\SENDH.png"));
        Sendbt.addActionListener(new ActionListener() 
        {
         	public void actionPerformed(ActionEvent e) 
         	{
         		sendFlag = true;
         	}
         });
        layeredPane.add(Sendbt);
        
        Sending = new JButton(new ImageIcon("C:\\cryptonite\\img\\ShareSending.png"));
        Sending.setBounds(140, 510, 160, 40);
        Sending.setOpaque(false);
        Sending.setBorderPainted(false);
        Sending.setContentAreaFilled(false);
        Sending.setFocusPainted(false);
        layeredPane.add(Sending);
        
        Complete = new JButton(new ImageIcon("C:\\cryptonite\\img\\COMPLETEbt_1.png"));
        Complete.setBounds(140, 510, 160, 40);
        Complete.setOpaque(false);
        Complete.setBorderPainted(false);
        Complete.setContentAreaFilled(false);
        Complete.setFocusPainted(false);
        Complete.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\COMPLETEbt_2.png"));
        Complete.addActionListener(new ActionListener() 
        {
         	public void actionPerformed(ActionEvent e) 
         	{
         		OTP.setText("");
         		setVisible(false);
         	}
         });
        layeredPane.add(Complete);
        
        // 마지막 추가들
        layeredPane.add(panel);
        getContentPane().add(layeredPane);    
        //setVisible(true);
    }
    
    public JTextField getOTPLabel()
    {
    	return this.OTP;
    }
    
    public void sendToSending()
    {
    	this.Sendbt.setVisible(false);
    	this.Sending.setVisible(true);
    }
    
    public void sendingToComplete()
    {
    	this.Sending.setVisible(false);
    	this.Complete.setVisible(true);
    }
    
    public JLabel getFileCountLabel()
    {
    	return this.fileCount;
    }
    
    public boolean getSendFlag()
    {
    	return this.sendFlag;
    }
    
    public void fileShare_Send_UI_ON()
    {
    	setVisible(true);
    }
    
    public void fileShare_Send_UI_OFF()
    {
    	setVisible(false);
    }
 
    class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(img, 0, 0, null);
        }
    }
}
