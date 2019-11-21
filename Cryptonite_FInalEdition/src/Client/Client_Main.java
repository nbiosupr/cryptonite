package Client;

public class Client_Main
{
	public static void main(String[] args)
	{
		String protectedFolder = null;
		Client_Login_UI lf = new Client_Login_UI();
		Client_TrayIcon trayIcon = new Client_TrayIcon(lf);
	}
}