package Server;

/*
 * Developed by Youn Hee Seung (Dept. Information Security at Suwon University)
 * */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Server_LocationTrack 
{
	private String userAddress = null;
	private String trimAddress = null;
	private String[] tempAddress = null;
	private String userIP = null;
	private String address = null;
	
	public Server_LocationTrack(String userIP)
	{
		this.userIP = userIP;
		this.address = "http://whois.kisa.or.kr/openapi/whois.jsp?query=" + this.userIP + "&key=2016042114064152123073&answer=xml";
	}
	
	public void trackingLocation()
	{
		try {
			URL url = new URL(address);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			
			String line;
			int a = 1;
			while((line = br.readLine()) != null){
				trimAddress = line.trim();
				if(a == 32){
				tempAddress = trimAddress.split("<addr>");
				tempAddress = tempAddress[1].split("</addr>");
				userAddress = tempAddress[0];
				break;
				}
				a++;
			}
			br.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public String getUserAddress()
	{
		return this.userAddress;
	}
}
