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
    //플래그 용 상수
    private final int REQUIRE_FILE_LIST = 1;    //탐색기 UI를 위한 파일리스트를 서버에 요청
    private final int REQUIRE_SELECT_LIST = 2;  //선택된 파일을 전송해줄 것을 서버에 요청
    private final int PROCESSING =1;    //작업처리상태 알림 - 작업중
    private final int COMPLETE =2;  //작업처리상태 알림 - 완료

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
            showMessage("연결 오류", "인터넷을 연결해주세요.");
            return false;
        }
        return true;
    }

    public JTree requireFileList(){
        try {
            send_Require(REQUIRE_FILE_LIST);   //서버에 파일리스트 요청을 알림
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
                dos.writeInt(fileList.size());  //요청한 파일갯수 보내기
                System.out.println("파일리스트 개수 : " + fileList.size());
                dos.flush();
                for(String path: fileList){ 
	                dos.writeUTF(path);
	                dos.flush();
                }
            }   //파일 주소

            boolean checkRestore = false;
            for(int i = 0; i < fileList.size(); i++) {
            	String fileName = fileList.get(i);
                //System.out.println("recieselfile 에서 프로세싱 보냄");

                //보호폴더로 경로지정
                String replaceFileName = protectedAdr +"/"+ fileName;
                File target = new File(replaceFileName);
                BufferedOutputStream fbos = new BufferedOutputStream(new FileOutputStream(target));
                System.out.println("target: "+ target.getPath());

                System.out.println("0바이트 만들었따!");
                send_State(PROCESSING);
                System.out.println("send_State 함수 실행완료");
                long totalSize = dis.readLong();
                int readCount = 0;

                while(totalSize > 0){
                    readCount = bis.read(buffer);
                    fbos.write(buffer, 0, readCount);
                    totalSize -= readCount;
                }
                System.out.println("파일수신");
                
                fbos.flush(); fbos.close();
            }
        }catch(IOException e) {
        	e.printStackTrace();
        }
    }



    /*
    * 가독성용 함수들.
    **/
    private boolean receiveState(int state) throws IOException {
        //서버로 부터 처리 상태 받기
        int _state = 0;
        _state = dis.readInt();
        System.out.println("receivestate int:"+_state);
        if(state == _state) return true;
        else return false;
    }

    private void send_State(int state) throws IOException {
        //서버에게 처리 상황 보내기
        dos.writeInt(state);
        dos.flush();
    }
    private void send_Require(int require) throws IOException {
        //서버에게 요구사항 보내기
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
