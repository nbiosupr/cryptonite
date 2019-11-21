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
		this.tree = tree; //tree ����
		setTitle("Cryptonite Restore");
		setBounds(1170,200,450,610);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		//���̾����
		JLayeredPane layeredpane = new JLayeredPane();
		layeredpane.setBounds(0,0,450,600);
		layeredpane.setLayout(null);
		
		//��� layout
		Bg bgPanel = new Bg();
		bgPanel.setBounds(0,0,450,600);
		
		//Jtree������ �г�
		tree.setRootVisible(true);
		tree.setOpaque(false);


		JScrollPane Scrolltree = new JScrollPane(tree);
		Scrolltree.setBounds(67,110,308,365);
		///��� ����
		Scrolltree.setOpaque(false);
		Scrolltree.getViewport().setOpaque(false);
		Scrolltree.setBorder(BorderFactory.createEmptyBorder());
		///���⼭ Viewport�� ����ؼ� opaqueó���� �ϸ�ȴٴµ� �׷� Ʈ����ü�� ��������� �̤�...

		//���img
		try{
			img = ImageIO.read(new File("c:/cryptonite/img/bgimg.png"));
		}catch(IOException e){
			System.out.println("����");
			System.exit(0);
		}

		//Restore bt
		Restorebt = new JButton(new ImageIcon("c:/cryptonite/img/restorebt.png"));
        Restorebt.setBounds(150, 505, 140, 37);
        Restorebt.setBorderPainted(false);
        Restorebt.setFocusPainted(false);
        Restorebt.setContentAreaFilled(false);
        Restorebt.setRolloverIcon(new ImageIcon("c:/cryptonite/img/restorehbt.png"));
        //�Ϸ��   Restorebt.setPressedIcon(new ImageIcon("c:/cryptonite/img/Loginpr.png"));
        Restorebt.addActionListener(new ActionListener(){ 
            public void actionPerformed(ActionEvent e)
            {
          	 //restore�׼Ǹ޼ҵ�
				String fullText = null;
				Vector<String> selectFileList = new Vector<String>();
				boolean isInsert = false;

				DefaultTreeModel model = (DefaultTreeModel)tree.getModel(); //�������� �����´�
				TreePath[] paths = tree.getSelectionPaths();
				for(TreePath path : paths) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
					if (node.isRoot()) {
						//������ ��尡 ��Ʈ����� �� �׳� ���� �Ѵ�
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
		
        
		//���̾���гο� �߰���ų�͵�(�����߿���)

		DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer();
		dtcr.setBackgroundNonSelectionColor(Color.decode("#202020"));
		dtcr.setTextNonSelectionColor(Color.LIGHT_GRAY);
		tree.setCellRenderer(dtcr);

		layeredpane.add(Restorebt);		
		layeredpane.add(Scrolltree);
		layeredpane.add(bgPanel);
		///�Ǹ�����
		getContentPane().add(layeredpane); 
		setVisible(true);
	}
	
	class Bg extends JPanel{
		public void paint(Graphics g){
			g.drawImage(img, 0, 0, null);
		}
	}
	
}

