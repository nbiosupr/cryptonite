package Client;

// Developed by Won Sung Yeon (Dept. Information Media at Suwon University)
// Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
// Developed by Park Sang Woon (Dept. Information Security at Suwon University)
// Developed by Kim Hye Ju (Dept. Information Security at Suwon University)

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Client_MainFrame_UI extends JFrame{
	// 이미지와 버튼관련
		BufferedImage img = null;
		private JButton Up;
		private JButton Down;
		private JButton Recovery;
		private JButton Openfolder;
		private JButton btnHome;
		private JButton btnDevelopers;
		private JButton btnLogout;
		
		// JLabel들
		private JLabel Home;
		private JLabel Developers;
		private JLabel Name;
		private JLabel ThankYou;
		
		// 스레드종합
		private Client_Login_UI lf = null;
		private Client_FileShare_Send fss = null;
		private Client_FileShare_Receive fsr = null;
		private EXIT_1 logout = null;

        // ID
        private String id = null;

        private Client_FileRestore fres = null;
        private Client_FileRestore_UI cfrsUI = null;
		
	Font fontbt = new Font("SansSerif", Font.BOLD,20);
	Font fontH = new Font("SansSerif", Font.BOLD,22);
	
	Font font1 = new Font("SansSerif", Font.BOLD, 15);
    Font font2 = new Font("SansSerif", Font.BOLD, 23);
    Font font3 = new Font("SansSerif", Font.BOLD, 48);
	
	public Client_MainFrame_UI(Client_Login_UI lf, String id){
        this.id = id;
		this.lf = lf;
		getContentPane().setBackground(Color.WHITE);
		setTitle("Cryptonite");
		setBounds(500,300,900,517);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		getContentPane().setLayout(null);
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 900, 500);
        layeredPane.setLayout(null);
        
        try {
            img = ImageIO.read(new File("C:\\cryptonite\\img\\Main.png"));
        } catch (IOException e) {
            System.out.println("이미지 불러오기 실패");
            System.exit(0);
        }
        
        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, 900, 500);
                      
        Up = new JButton(new ImageIcon("C:\\cryptonite\\img\\Uploads.png"));		
        Up.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\UploadsH.png"));
		Up.setBounds(69, 364, 100, 100);
		Up.setBorderPainted(false);
        Up.setFocusPainted(false);
        Up.setContentAreaFilled(false);
        Up.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent arg0) {
         		// 여기서 파일공유 스레드 켜줍니다.
         		fss = new Client_FileShare_Send();
                fss.start();
         	}
         });
        
        Down = new JButton(new ImageIcon("C:\\cryptonite\\img\\DownLoads.png"));
        Down.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\DownloadsH.png"));
        Down.setBounds(275, 364, 100, 100);
        Down.setFocusPainted(false);
        Down.setBorderPainted(false);
        Down.setContentAreaFilled(false);
        Down.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent arg0) {
         		// 여기서 파일공유 스레드 켜줍니다.
         		fsr = new Client_FileShare_Receive();
                fsr.start();
         	}
         });
             
        Recovery = new JButton(new ImageIcon("C:\\cryptonite\\img\\Recovery.png"));
        Recovery.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\RecoveryH.png"));
        Recovery.setBounds(488, 364, 100, 100);
        Recovery.setFocusPainted(false);
        Recovery.setBorderPainted(false);
        Recovery.setContentAreaFilled(false);
        Recovery.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isNetworkable;

                fres = new Client_FileRestore();
                isNetworkable = fres.Connect(id);
                if(isNetworkable){
                    new Client_FileRestore_UI(fres.requireFileList(),fres);
                }

            }
        });
              
        Openfolder = new JButton(new ImageIcon("C:\\cryptonite\\img\\Encryption.png"));
        Openfolder.setRolloverIcon(new ImageIcon("C:\\cryptonite\\img\\EncryptionH.png"));
        Openfolder.setBounds(703, 364, 100, 100);
        Openfolder.setFocusPainted(false);
        Openfolder.setBorderPainted(false);
        Openfolder.setContentAreaFilled(false);
        Openfolder.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent arg0){
        		boolean loginCheck = false;
    			File userCheck = new File("C:\\cryptonite");
    			String[] filenames = userCheck.list();
    			
    			for(int i = 0; i < filenames.length; i++){
    				if(filenames[i].equals("User.cnob") == true){
    					try {
    						FileReader fr = new FileReader("C:\\cryptonite\\protected.adr");
    						BufferedReader br = new BufferedReader(fr);
    						StringTokenizer st = new StringTokenizer(br.readLine(),"\\");
    						String[] tokenTemp = new String[st.countTokens()];
    						for(int j = 0; j < tokenTemp.length; j++){
    							tokenTemp[j] = st.nextToken();
    						}
    						String address = tokenTemp[1];
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
        	}
        });
         
        btnHome = new JButton("Home");
        btnHome.setHorizontalAlignment(SwingConstants.LEFT);
        btnHome.setBounds(42, 175, 157, 37);
        btnHome.setContentAreaFilled(false);
        btnHome.setBorderPainted(false);
        btnHome.setFocusPainted(false);
        btnHome.setFont(fontbt);
        btnHome.setForeground(Color.white);
        btnHome.addMouseListener(new MouseAdapter(){
          	public void mouseClicked(MouseEvent e){       		
          		Developers.setVisible(false);
          		Name.setVisible(false);
          		ThankYou.setVisible(false);
          		Home.setVisible(true);
          	}
          	public void mouseEntered(MouseEvent e){
          		
          		btnHome.setFont(fontH);
               
                
          	}
          	public void mouseExited(MouseEvent e){
          		
          		btnHome.setFont(fontbt);
          		
          		
          	}
          });
        
        btnDevelopers = new JButton("Developer");
        btnDevelopers.setHorizontalAlignment(SwingConstants.LEFT);
        btnDevelopers.setBounds(42, 224, 157, 37);  
        btnDevelopers.setFont(fontbt);
        btnDevelopers.setForeground(Color.white);
        btnDevelopers.setContentAreaFilled(false);
        btnDevelopers.setBorderPainted(false);
        btnDevelopers.setFocusPainted(false);
        btnDevelopers.addMouseListener(new MouseAdapter(){
          	public void mouseClicked(MouseEvent e){    
          		Home.setVisible(false);
          		Developers.setVisible(true);
          		Name.setVisible(true);
          		ThankYou.setVisible(true);
          	}
          	public void mouseEntered(MouseEvent e){
          		
          		btnDevelopers.setFont(fontH);
               
                
          	}
          	public void mouseExited(MouseEvent e){
          		
          		btnDevelopers.setFont(fontbt);
          		
          		
          	}
          });
        
        Home = new JLabel(new ImageIcon("C:\\cryptonite\\img\\MainFrame_Home.png"));
        Home.setBounds(173, 0, 723, 317);
        Home.setForeground(Color.white);
        Home.setFont(font2);
        Home.setOpaque(false);
        Home.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        Home.setHorizontalAlignment(JTextField.CENTER);
        Home.setVisible(true);
        layeredPane.add(Home);
        
        Developers = new JLabel();
        Developers.setBounds(350, 12, 330, 50);
        Developers.setForeground(Color.white);
        Developers.setFont(font2);
        Developers.setOpaque(false);
        Developers.setText("Cryptonite Developers");
        Developers.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        Developers.setHorizontalAlignment(JTextField.CENTER);
        Developers.setVisible(false);
        layeredPane.add(Developers);
        
        Name = new JLabel();
        Name.setBounds(200, 100, 630, 50);
        Name.setForeground(Color.white);
        Name.setFont(font1);
        Name.setOpaque(false);
        Name.setText("Youn Hee Seung            Park Sang Woon                Won Sung Yeon              Kim Hye Ju");
        Name.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        Name.setHorizontalAlignment(JTextField.CENTER);
        Name.setVisible(false);
        layeredPane.add(Name);
        
        ThankYou = new JLabel();
        ThankYou.setBounds(205, 194, 630, 50);
        ThankYou.setForeground(Color.white);
        ThankYou.setFont(font3);
        ThankYou.setOpaque(false);
        ThankYou.setText("Thank you for your efforts!");
        ThankYou.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        ThankYou.setHorizontalAlignment(JTextField.CENTER);
        ThankYou.setVisible(false);
        layeredPane.add(ThankYou);
               
        btnLogout = new JButton("Logout");
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setBounds(42, 277, 157, 37);
        btnLogout.setFont(fontbt);
        btnLogout.setForeground(Color.white);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent arg0) {
         		logout = new EXIT_1(false, lf);
         	}
         });
        btnLogout.addMouseListener(new MouseAdapter(){
          	public void mouseClicked(MouseEvent e){       		
          	
          		
          	}
          	public void mouseEntered(MouseEvent e){
          		
          		btnLogout.setFont(fontH);
               
                
          	}
          	public void mouseExited(MouseEvent e){
          		
          		btnLogout.setFont(fontbt);
          		
          		
          	}
          });
        
        
        layeredPane.add(Up);
        layeredPane.add(Down);
        layeredPane.add(Recovery);
        layeredPane.add(Openfolder);
        layeredPane.add(btnHome);        
        layeredPane.add(btnDevelopers);
        layeredPane.add(btnLogout);
        layeredPane.add(panel);
        getContentPane().add(layeredPane);
        
        //setVisible(true);
	}
	
	public void mainFrameUI_ON()
	{
		setVisible(true);
	}
	
	class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(img, 0, 0, null);
       }
   }
}