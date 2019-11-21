package Client;

// Developed by Won Sung Yeon (Dept. Information Media at Suwon University)
// Developed by Youn Hee Seung (Dept. Information Security at Suwon University)

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client_FileShare_Receive_UI extends JFrame {
	
    BufferedImage img = null;
    
    // 	JLabel 인스턴스
    private JLabel youWillDownload;
    private JLabel fileCount;

    // JTextField 인스턴스
    private JTextField OTP;
    private JLabel route;
    
    // JButton 인스턴스
    private JButton pathSelector;
    private JButton Checkbt;
    private JButton Downloading;
    private JButton Complete;
    
    // OTP 인스턴스
    private String OTPString = "init";
    private boolean receiveFlag = false;
    private boolean folderSelectionEnd = false;
    
    //FolderSelector 관련
    private FolderSelector folderSelector = null;
    
    Font font1 = new Font("SansSerif", Font.BOLD, 20);
    Font fontOTP = new Font("SansSerif", Font.BOLD, 25);
    Font font2 = new Font("SansSerif", Font.BOLD, 28);
    Font font3 = new Font("SansSerif", Font.BOLD, 30);
    
    public Client_FileShare_Receive_UI() {
    	folderSelector = new FolderSelector();
    	
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
            img = ImageIO.read(new File("C:\\cryptonite\\img\\FileShare_Receive.png"));
        } catch (IOException e) {
            System.out.println("이미지 불러오기 실패");
            System.exit(0);
        }
         
        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, 460, 645);
        
        OTP = new JTextField();
        OTP.setBounds(165, 315, 208, 50);
        OTP.setForeground(Color.white);
        OTP.setOpaque(false);
        OTP.setFont(fontOTP);
        OTP.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        OTP.setHorizontalAlignment(JTextField.CENTER);
        OTP.addKeyListener(new KeyListener()
	      {
			@Override
			public void keyPressed(KeyEvent e) { }
			
			@Override
			public void keyReleased(KeyEvent e) 
			{
				OTPString = OTP.getText();
			}
			
			@Override
			public void keyTyped(KeyEvent e)
			{
				
			}

	       });
        layeredPane.add(OTP);
        
        route = new JLabel();
        route.setBounds(55, 412, 283, 30);
        route.setForeground(Color.black);    
        route.setOpaque(true);
        route.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(route);
        
        youWillDownload = new JLabel();
        youWillDownload.setBounds(58, 445, 330, 50);
        youWillDownload.setForeground(Color.white);
        youWillDownload.setFont(font1);
        youWillDownload.setOpaque(false);
        youWillDownload.setText("NOW YOU WILL DOWNLOAD");
        youWillDownload.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        youWillDownload.setHorizontalAlignment(JTextField.CENTER);
        layeredPane.add(youWillDownload);
        
        fileCount = new JLabel();
        fileCount.setBounds(56, 483, 330, 50);
        fileCount.setForeground(Color.white);
        fileCount.setFont(font2);
        fileCount.setOpaque(false);
        fileCount.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        fileCount.setHorizontalAlignment(JTextField.CENTER);
        layeredPane.add(fileCount);
        
        pathSelector = new JButton(new ImageIcon("C:\\cryptonite\\img\\b.png"));
        pathSelector.setBounds(350, 402, 40, 40);
        pathSelector.setOpaque(false);
        pathSelector.setBorderPainted(false);
        pathSelector.setContentAreaFilled(false);
        pathSelector.setFocusPainted(false);       
        pathSelector.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\bh.png"));
        pathSelector.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
         		try{
         			folderSelector.folderSelectorON();
         			while(folderSelector.getSelectionEnd() == false)
             		{
             			try 
             			{
    						Thread.sleep(1);
    					} 
             			catch (InterruptedException e1) 
             			{
    						e1.printStackTrace();
    					}
             		}
             		route.setText(folderSelector.getSelectedPath());
             		folderSelectionEnd = true;
         		}
         		catch(NullPointerException e1){
         			System.out.println("취소 버튼 눌렀습니다.");
         		}
         	}
         });
        layeredPane.add(pathSelector);
        
        Checkbt = new JButton(new ImageIcon("C:\\cryptonite\\img\\CHECKbt.png"));
        Checkbt.setBounds(140, 539, 160, 40);
        Checkbt.setOpaque(false);
        Checkbt.setBorderPainted(false);
        Checkbt.setContentAreaFilled(false);
        Checkbt.setFocusPainted(false);
        Checkbt.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\CHECKbtH.png"));
        Checkbt.addActionListener(new ActionListener() 
        {
         	public void actionPerformed(ActionEvent e)
         	{
         		if(OTPString.equals("init") == false && folderSelector.getSelectionEnd() == true){
         			receiveFlag = true;
         		}
         		else
         			showMessage("오류", "OTP를 적거나, 경로를 먼저 지정해주세요.");
         	}
        });
        layeredPane.add(Checkbt);
        
        Downloading = new JButton(new ImageIcon("C:\\cryptonite\\img\\DOWNLOADING.png"));
        Downloading.setBounds(140, 539, 160, 40);
        Downloading.setOpaque(false);
        Downloading.setBorderPainted(false);
        Downloading.setContentAreaFilled(false);
        Downloading.setFocusPainted(false);
        layeredPane.add(Downloading);
        
        Complete = new JButton(new ImageIcon("C:\\cryptonite\\img\\COMPLETEbt_1.png"));
        Complete.setBounds(140, 539, 160, 40);
        Complete.setOpaque(false);
        Complete.setBorderPainted(false);
        Complete.setContentAreaFilled(false);
        Complete.setFocusPainted(false);
        Complete.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\COMPLETEbt_2.png"));
        Complete.addActionListener(new ActionListener() 
        {
         	public void actionPerformed(ActionEvent e) 
         	{
         		setVisible(false);
         	}
         });
        layeredPane.add(Complete);
        
        // 마지막 추가들
        layeredPane.add(panel);
        
        getContentPane().add(layeredPane);
    } 
 
    class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(img, 0, 0, null);
        }
    }
    
    public void completeToCheck()
    {
    	this.Complete.setVisible(false);
    	this.Checkbt.setVisible(true);
    }
    
    public void checkToDownloading()
    {
    	this.Checkbt.setVisible(false);
    	this.Downloading.setVisible(true);
    }
    
    public void downloadingToComplete()
    {
    	this.Downloading.setVisible(false);
    	this.Complete.setVisible(true);
    }
    
    public boolean getFolderSelectionEnd()
    {
    	return folderSelectionEnd;
    }
    
    public void fileShare_Receive_UI_ON()
    {
    	setVisible(true);
    }
    
    public void fileShare_Receive_UI_OFF()
    {
    	setVisible(false);
    }
    
    public JLabel getRoute()
    {
    	return route;
    }
    
    public JLabel getFileCount()
    {
    	return fileCount;
    }
    
    public boolean getReceiveFlag()
    {
    	return receiveFlag;
    }
    
    public void setReceiveFlag()
    {
    	receiveFlag = false;
    }
    
    public String getOTP()
    {
    	return OTPString;
    }
    
    private static void showMessage(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}

class FolderSelector
{
	private JFileChooser chooser = new JFileChooser();
	private String selectedPath = null;
	private boolean selectionEnd = false;
	
	public FolderSelector()
	{
		
	}
	
	public void folderSelectorON()
	{
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.showOpenDialog(chooser);
	  	File dir = chooser.getSelectedFile();
	  	selectedPath = dir.getPath();
	  	selectionEnd = true;
	}
	
	public String getSelectedPath()
	{
		return this.selectedPath;
	}
	
	public boolean getSelectionEnd()
	{
		return selectionEnd;
	}
}