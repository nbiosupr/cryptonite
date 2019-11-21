package Client;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.awt.Image;
import java.awt.Toolkit;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JWindow;

import Crypto.*;

public class Client_EncryptionEndAnimation extends JWindow
{
	 Dimension dimScreen;
	 Rectangle winSize;
	 Image img;
	 
	 public Client_EncryptionEndAnimation()
	 {
		  img = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Youn\\Desktop\\Team_Cryptonite\\img\\encryption_complete.png");
		  this.setSize(400, 0);
		  this.setAlwaysOnTop(true);
		  this.setEnabled(false);
		  this.setVisible(true);
		  
		  winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		  dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
		  
		  this.setLocation(dimScreen.width-this.getSize().width, dimScreen.height-this.getSize().height-(dimScreen.height-winSize.height));
		  Thread thread = new Thread(new Runnable() 
		  {
			   @Override
			   public void run()
			   {
				    try 
				    {
					     for(int i = 0 ; i<150 ; i++)
					     {
					    	 Client_EncryptionEndAnimation.this.setSize(400, i);
					    	 Client_EncryptionEndAnimation.this.setLocation(dimScreen.width-Client_EncryptionEndAnimation.this.getSize().width, dimScreen.height-Client_EncryptionEndAnimation.this.getSize().height-(dimScreen.height-winSize.height));
					    	
					    	 Thread.sleep(1);
					     }
					     Thread.sleep(2000);
					     
					     for(int i =0 ; i<150 ; i++)
					     {
					    	 Client_EncryptionEndAnimation.this.setSize(400, 150-i);
					    	 Client_EncryptionEndAnimation.this.setLocation(dimScreen.width-Client_EncryptionEndAnimation.this.getSize().width, dimScreen.height-Client_EncryptionEndAnimation.this.getSize().height-(dimScreen.height-winSize.height));
					    	 
					    	 Thread.sleep(3);
					     }
					    } catch (InterruptedException e) { e.printStackTrace(); }
					    //System.exit(0); // 이건 나중에 제거
			   }
		   
		  });
		  thread.start();
 	}
	 
	 public void paint(Graphics g)
	 {
		 g.drawImage(img, 0, 0, this);
	 }
	 
}

class Client_checkEncryptionAnime extends Thread
{
	private boolean stopFlag = false;
	
	public synchronized void run()
	{
		try
		{
			while(true)
			{
				if(stopFlag == true)
				{
					this.interrupt();
					wait();
				}
				
				if(FileED.encryptedVector.isEmpty() == false)
				{
					while(true)
					{
						if(FileED.encryptedVector.isEmpty() == true)
						{
							try
							{
								sleep(1000);
								
								if(FileED.encryptedVector.isEmpty() == true)
								{
									new Client_EncryptionEndAnimation();
									break;
								}
								
							} catch (InterruptedException e) { /*여기서 오류한번나고}*/ }
						}
					}
				}
				
			}
		}
		catch(InterruptedException e) { }
		
	}
	
	public void stopThread()
	{
		stopFlag = true;
	}
	
}