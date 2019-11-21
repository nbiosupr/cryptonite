/*
 * Writed by Sangwoon Park(Dept.Infomation Security of Suwon Univ.) (c) 2016.
 */

package Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by Sangwoon Park(Dept.Information Security of Suwon Univ.) on 2016-05-28.
 */
public class Client_FileRestore{
    //�÷��� �� ���
    private final int REQUIRE_FILE_LIST = 1;    //Ž���� UI�� ���� ���ϸ���Ʈ�� ������ ��û
    private final int REQUIRE_SELECT_LIST = 2;  //���õ� ������ �������� ���� ������ ��û
    private final int PROCESSING =1;    //�۾�ó������ �˸� - �۾���
    private final int COMPLETE =2;  //�۾�ó������ �˸� - �Ϸ�

    private Socket socket = null;

    private InputStream in = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private BufferedInputStream bis = null;

    private OutputStream out = null;
    private BufferedOutputStream bos = null;
    private ObjectInputStream ois = null;

    private String id = null;
    private String protectedAdr = null;
    private JTree jTree = null;
    private Vector<String> SelectedFileList = null;

    public boolean Connect(String id){
        try{
            socket= new Socket("127.0.0.1", 11001);

            in = socket.getInputStream();
            out = socket.getOutputStream();

           dis = new DataInputStream(in);
            dos = new DataOutputStream(out);
            bis = new BufferedInputStream(in);
            bos = new BufferedOutputStream(out);

            setProtectedAdr();
            this.id = id;
        }catch(Exception e) {
            e.printStackTrace();
            showMessage("���� ����", "���ͳ��� �������ּ���.");
            return false;
        }
        return true;
    }

    public JTree requireFileList(){
        try {
            send_Require(REQUIRE_FILE_LIST);   //������ ���ϸ���Ʈ ��û�� �˸�
            boolean result = receiveState(PROCESSING);
            if(result == true){
                dos.writeUTF(id);
                try {
                    File forTree = new File("C:\\cryptonite\\Tree", (this.id + ".ser"));
                    FileOutputStream fos = new FileOutputStream(forTree);
                    BufferedOutputStream fbos = new BufferedOutputStream(fos);

                    long totalSize = dis.readLong();

                    byte[] buffer = new byte[1024];
                    int readCount = 0;
                    while(totalSize > 0){
                        readCount = bis.read(buffer);
                        fbos.write(buffer, 0, readCount);
                        totalSize -= readCount;
                    }
                    fbos.close();
                    fos.close();
                    //fbos.flush();
                   // fbos.close();
                   // fos.close();

                    FileInputStream fis = new FileInputStream(forTree);
                    ois = new ObjectInputStream(fis);
                    jTree = (JTree)ois.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jTree;
    }

    public void recieveSelectFile(Vector<String> fileList){

        byte[] buffer = new byte[1024];

        try{
            send_Require(REQUIRE_SELECT_LIST);
            if(receiveState(PROCESSING)){
                dos.writeInt(fileList.size());  //��û�� ���ϰ��� ������
                System.out.println("���ϸ���Ʈ ���� : " + fileList.size());
                dos.flush();
                for(String path: fileList){ 
	                dos.writeUTF(path);
	                dos.flush();
                }
            }   //���� �ּ�

            boolean checkRestore = false;
            for(int i = 0; i < fileList.size(); i++) {
            	String fileName = fileList.get(i);
                //System.out.println("recieselfile ���� ���μ��� ����");

                //��ȣ������ �������
                String replaceFileName = protectedAdr +"/"+ fileName;
                File target = new File(replaceFileName);
                BufferedOutputStream fbos = new BufferedOutputStream(new FileOutputStream(target));
                System.out.println("target: "+ target.getPath());

                System.out.println("0����Ʈ �������!");
                send_State(PROCESSING);
                System.out.println("send_State �Լ� ����Ϸ�");
                long totalSize = dis.readLong();
                int readCount = 0;

                while(totalSize > 0){
                    readCount = bis.read(buffer);
                    fbos.write(buffer, 0, readCount);
                    totalSize -= readCount;
                }
                System.out.println("���ϼ���");
                
                fbos.flush(); fbos.close();
            }
        }catch(IOException e) {
        	e.printStackTrace();
        }
    }



    /*
    * �������� �Լ���.
    **/
    private boolean receiveState(int state) throws IOException {
        //������ ���� ó�� ���� �ޱ�
        int _state = 0;
        _state = dis.readInt();
        System.out.println("receivestate int:"+_state);
        if(state == _state) return true;
        else return false;
    }

    private void send_State(int state) throws IOException {
        //�������� ó�� ��Ȳ ������
        dos.writeInt(state);
        dos.flush();
    }
    private void send_Require(int require) throws IOException {
        //�������� �䱸���� ������
        dos.writeInt(require);
        dos.flush();
    }
    private void setProtectedAdr(){
        File adrText = new File("C:/cryptonite/protected.adr");
        try{
            BufferedReader br = new BufferedReader(new FileReader(adrText));
            protectedAdr = br.readLine();
            br.close();
        } catch(IOException e) {e.printStackTrace();}
    }
    
    static void showMessage(String title, String message) {
  		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
  	}
}
