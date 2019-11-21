/*
 * Writed by Sangwoon Park(Dept.Infomation Security of Suwon Univ.) (c) 2016.
 * Writed by Won Sung Yeon (Dept. Information Media at Suwon University)
 * Writed by Kim Hye Jue(Dept. Information Security at Suwon University)
 */


package Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;


public class Client_FileRestore_UI extends JFrame {
	BufferedImage img = null;
	JButton Restorebt;
	JTree tree = null;
	Client_FileRestore fres= null;

	public Client_FileRestore_UI(JTree tree, Client_FileRestore fres){

		this.fres = fres;
		this.tree = tree; //tree 셋팅
		setTitle("Cryptonite Restore");
		setBounds(1170,200,450,610);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		//레이어드팬
		JLayeredPane layeredpane = new JLayeredPane();
		layeredpane.setBounds(0,0,450,600);
		layeredpane.setLayout(null);
		
		//배경 layout
		Bg bgPanel = new Bg();
		bgPanel.setBounds(0,0,450,600);
		
		//Jtree구현한 패널
		tree.setRootVisible(true);
		tree.setOpaque(false);


		JScrollPane Scrolltree = new JScrollPane(tree);
		Scrolltree.setBounds(67,110,308,365);
		///배경 투명
		Scrolltree.setOpaque(false);
		Scrolltree.getViewport().setOpaque(false);
		Scrolltree.setBorder(BorderFactory.createEmptyBorder());
		///여기서 Viewport를 사용해서 opaque처리를 하면된다는데 그럼 트리전체가 사라져버림 ㅜㅜ...

		//배경img
		try{
			img = ImageIO.read(new File("c:/cryptonite/img/bgimg.png"));
		}catch(IOException e){
			System.out.println("실패");
			System.exit(0);
		}

		//Restore bt
		Restorebt = new JButton(new ImageIcon("c:/cryptonite/img/restorebt.png"));
        Restorebt.setBounds(150, 505, 140, 37);
        Restorebt.setBorderPainted(false);
        Restorebt.setFocusPainted(false);
        Restorebt.setContentAreaFilled(false);
        Restorebt.setRolloverIcon(new ImageIcon("c:/cryptonite/img/restorehbt.png"));
        //완료시   Restorebt.setPressedIcon(new ImageIcon("c:/cryptonite/img/Loginpr.png"));
        Restorebt.addActionListener(new ActionListener(){ 
            public void actionPerformed(ActionEvent e)
            {
          	 //restore액션메소드
				String fullText = null;
				Vector<String> selectFileList = new Vector<String>();
				boolean isInsert = false;

				DefaultTreeModel model = (DefaultTreeModel)tree.getModel(); //모델정보를 가져온다
				TreePath[] paths = tree.getSelectionPaths();
				for(TreePath path : paths) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
					if (node.isRoot()) {
						//선택한 노드가 루트노드일 때 그냥 종료 한다
						setVisible(false);
						return;
					}
					StringTokenizer stk = new StringTokenizer(path.toString(), "[,]");
					stk.nextToken();
					if(stk.hasMoreTokens()){
						fullText = stk.nextToken().trim();

						while(stk.hasMoreTokens()){
							fullText += "/" +stk.nextToken().trim();
						}
					}
					selectFileList.add(fullText);
					isInsert = true;
				}
				if(isInsert){
					for(String s: selectFileList){
						System.out.println("selectFileList:" +s);
					}
					fres.recieveSelectFile(selectFileList);
				}
				setVisible(false);
            }
        }
     );
		
        
		//레이어드패널에 추가시킬것들(순서중요함)

		DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer();
		dtcr.setBackgroundNonSelectionColor(Color.decode("#202020"));
		dtcr.setTextNonSelectionColor(Color.LIGHT_GRAY);
		tree.setCellRenderer(dtcr);

		layeredpane.add(Restorebt);		
		layeredpane.add(Scrolltree);
		layeredpane.add(bgPanel);
		///맨마지막
		getContentPane().add(layeredpane); 
		setVisible(true);
	}
	
	class Bg extends JPanel{
		public void paint(Graphics g){
			g.drawImage(img, 0, 0, null);
		}
	}
	
}

