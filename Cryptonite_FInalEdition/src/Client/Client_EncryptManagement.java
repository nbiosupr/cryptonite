package Client;

/*
 * Developed by Park Sang woon (Dept.Infomation Security of Suwon Univ.)
 * */

import Crypto.*;

import java.util.*;

import javax.crypto.SecretKey;

import java.io.*;

public class Client_EncryptManagement extends Thread{
	  
	   private FileED fileED;
	   private File target = null;
	   private boolean goEncryption = false;
	   private String testKey = null;
	   private String filter = null;
	   
	   StringTokenizer st = null;
	   
	   public Client_EncryptManagement (String address, SecretKey Key) 
	   {
	      fileED = new FileED(Key);
	      
	      this.target = new File(address);
	      this.start();
	   }
	   
	   @Override
	   public synchronized void run()
	   {
	   		st = new StringTokenizer(target.getName() ,".");
			while(st.hasMoreTokens())
			{
				filter = st.nextToken();
			}
			
        	if(filter.equals("cnec"))
        	{
        			goEncryption = false;
        	}
        	else
        	{
        		goEncryption = true;
        	}
        	
        	if(goEncryption == true)
        	{
        		try 
        	 	{
        			fileED.encrypt(target);
        			goEncryption = false;
        	 	} 
               catch (Exception e) { e.printStackTrace(); }
        	}
          	
	   }
}
