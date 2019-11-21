package Client;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.net.*;

public class Client_GetMacAddress 
{
	private NetworkInterface ni = null;
	byte[] mac = null;
	String macAddress = "";
	
	public Client_GetMacAddress(InetAddress localIP)
	{
		try 
		{
			ni = NetworkInterface.getByInetAddress(localIP);
			mac = ni.getHardwareAddress();
			for(int i  = 0; i < mac.length; i++){
				macAddress += String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "");
			}
		} 
		catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public String getMacAddress()
	{
		return this.macAddress;
	}
}
