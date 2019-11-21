/*
 * Writed by Sangwoon Park(Dept.Infomation Security of Suwon Univ.) (c) 2016.
 */

package Server;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by Sangwoon Park(Suwon Univ.) on 2016-05-28.
 */

public class Server_FileRestore {
    private ServerSocket FileBackupSocket = null;
    private Vector<Thread> runningList = null;

    public Server_FileRestore(ServerSocket FileBackupSocket){
        this.FileBackupSocket = FileBackupSocket;
    }

    public void Start(){
        runningList = new Vector<Thread>();
        Thread ConnectionThd = new Thread(){
            @Override
            public void run(){
                while(true){
                    try {
                        Socket socket = FileBackupSocket.accept();
                        System.out.println("복원에 소켓연결");
                        Thread managementThd = new Thread(new Management(socket));
                        runningList.add(managementThd);
                        managementThd.start();
                    } catch  (IOException e) {e.printStackTrace();}
                }
            }
        };

        ConnectionThd.start();
    }
    public void StopService(){
        for(Thread thd : runningList){
            thd.interrupt();
            System.out.println("파일복원기능 제공 서비스 종료"); //테스트용 문구
        }
    }
}

class Management implements Runnable{
    private final int REQUIRE_FILE_LIST = 1;    //탐색기 UI를 위한 파일리스트 요청받음
    private final int REQUIRE_SELECT_LIST = 2;  //선택된 파일을 전송해줄 것을 요청받음
    private final int SEND_ID= 3;
    private final int PROCESSING =1;    //작업처리상태 알림 - 작업중
    private final int COMPLETE =2;  //작업처리상태 알림 - 완료

    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private DataInputStream dis;
    private DataOutputStream dos;

    private String user_id;
    private Socket socket;

    public Management(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);
            bis =  new BufferedInputStream(is);
            bos = new BufferedOutputStream(os);



            while(true){
                switch(dis.readInt()) {
                    case REQUIRE_FILE_LIST: //파일리스트를 요청받았을 경우
                        System.out.println("REQUIRE_FILE_LIST");
                        send_State(PROCESSING);
                        System.out.println("PROCESSING 보냄");
                        send_FileList();
                        System.out.println("filelist 보냄");
                        break;
                    case REQUIRE_SELECT_LIST:   //선택된 리스트의 파일들을 요청받을 경우
                        send_State(PROCESSING);
                        System.out.println("REQUIRE_SELECT_LIST");
                        Vector<String> selectedFileList = recieve_SelectedFileList();
                        send_SelectedFile(selectedFileList);
                        break;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();}
    }

    private boolean receiveState(int state){
        //클라이언트의 처리상태를 전달받습니다.
        int _state = 0;
        try {
            _state = dis.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(state == _state) return true;
        else return false;
    }

    private void send_State(int state){
        //클라이언트에 서버의 처리상태를 전달합니다.
        try {
            dos.writeInt(state);
            dos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void send_FileList(){  //JTree로 수정요망
        //백업 폴더에있는 파일의 리스트를 전송합니다.
        try {
            user_id= dis.readUTF();
            System.out.println("받은 id: " + user_id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JTreeMaker jTreeMaker = new JTreeMaker(user_id);
        JTree fileList = jTreeMaker.getJTree();
        System.out.print("fileList : " + fileList);
        try {
            File forTree = new File("C:\\Server\\Tree", (user_id + ".ser"));
            FileOutputStream fos = new FileOutputStream(forTree);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(fileList);
            oos.flush(); // 파일생성
            oos.close();
            fos.close();

            FileInputStream fis = new FileInputStream(forTree);
            BufferedInputStream fbis = new BufferedInputStream(fis);

            long totalSize = forTree.length();
            dos.writeLong(totalSize);    // 파일 용량 전송
            dos.flush();

            byte[] buffer = new byte[1024];
            int readCount = 0;
            while(totalSize > 0){
                readCount = fbis.read(buffer);
                bos.write(buffer, 0, readCount);
                totalSize -= readCount;
            }
            bos.flush();
            fbis.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send_SelectedFile(Vector<String> selectedFileList){
        //클라이언트가 요구하는 파일의 리스트를 받아 클라이언트로 전송합니다.
        System.out.println("send_selfile 들어옴");
        FileInputStream fis = null;
        BufferedInputStream fbis = null;

        byte[] buffer = new byte[1024];
        int readCount = 0;

        for(int i = 0 ; i < selectedFileList.size(); i++){
        	String filePath = selectedFileList.get(i);
            File target = new File(filePath);
            try 
            {
                fis = new FileInputStream(target);
                fbis = new BufferedInputStream(fis);
            } 
            catch (FileNotFoundException e) 
            {
                e.printStackTrace();
            }

            try {
                System.out.println("send_selfile 에서 프로세싱 기다림");
                receiveState(PROCESSING);
                System.out.println("send_selfile 에서 프로세싱 받음");
                
                long totalSize = target.length();
                dos.writeLong(totalSize);    // 파일 용량 전송
                dos.flush();

                while(totalSize > 0){
                    readCount = fbis.read(buffer);
                    bos.write(buffer, 0, readCount);
                    totalSize -= readCount;
                }
                bos.flush();
                fbis.close();
                fis.close();
            }catch(IOException e) { e.printStackTrace();}
        }
    }

    private Vector<String> recieve_SelectedFileList(){
        //클라이언트가 요구하는 파일들의 리스트를 받습니다.
        Vector<String> selectedFileList = new Vector<String>();
        String filePath = null;
        try {
            int arrSize = dis.readInt();
            System.out.println("arrSize: " + arrSize);
            for(int i=0; i< arrSize; i++) {
                filePath = dis.readUTF();
                selectedFileList.add("C:/Server/Backup/"+ user_id +"/"+ filePath);
            }
        } catch(IOException e) {}
        for(String str : selectedFileList) System.out.println("받은 선택된 목록: " + str);
        System.out.println("받기끝");
        return selectedFileList;
    }
}


class JTreeMaker{
    private String id;
    private JTree fileList = null;
    private DefaultMutableTreeNode root;
    private File[] rootDir;

    public JTreeMaker(String id){
        this.id = id;
        makeAList();
    }

    public JTree getJTree(){
        return fileList;
    }

    private void makeAList(){
        System.out.println("JTreeMaker ID: "+ id);
        rootDir = new File("C:/Server/Backup/" + id).listFiles();
        root = new DefaultMutableTreeNode("BackUp");
        addChildFile(rootDir, root);
        //String[] list = {"abc","ddc","eed"};

        fileList = new JTree(root);
    }

    private void addChildFile(File[] rootDir, DefaultMutableTreeNode root){
        DefaultMutableTreeNode child = null;
        for(File target : rootDir) {
            System.out.println(target.getPath());
            child = new DefaultMutableTreeNode(target.getName());
            root.add(child);
            if (target.isDirectory()) {
                File[] childDir = target.listFiles();
                addChildFile(childDir, child);
            }
        }
    }
}

