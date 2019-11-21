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
                        System.out.println("������ ���Ͽ���");
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
            System.out.println("���Ϻ������ ���� ���� ����"); //�׽�Ʈ�� ����
        }
    }
}

class Management implements Runnable{
    private final int REQUIRE_FILE_LIST = 1;    //Ž���� UI�� ���� ���ϸ���Ʈ ��û����
    private final int REQUIRE_SELECT_LIST = 2;  //���õ� ������ �������� ���� ��û����
    private final int SEND_ID= 3;
    private final int PROCESSING =1;    //�۾�ó������ �˸� - �۾���
    private final int COMPLETE =2;  //�۾�ó������ �˸� - �Ϸ�

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
                    case REQUIRE_FILE_LIST: //���ϸ���Ʈ�� ��û�޾��� ���
                        System.out.println("REQUIRE_FILE_LIST");
                        send_State(PROCESSING);
                        System.out.println("PROCESSING ����");
                        send_FileList();
                        System.out.println("filelist ����");
                        break;
                    case REQUIRE_SELECT_LIST:   //���õ� ����Ʈ�� ���ϵ��� ��û���� ���
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
        //Ŭ���̾�Ʈ�� ó�����¸� ���޹޽��ϴ�.
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
        //Ŭ���̾�Ʈ�� ������ ó�����¸� �����մϴ�.
        try {
            dos.writeInt(state);
            dos.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void send_FileList(){  //JTree�� �������
        //��� �������ִ� ������ ����Ʈ�� �����մϴ�.
        try {
            user_id= dis.readUTF();
            System.out.println("���� id: " + user_id);
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
            oos.flush(); // ���ϻ���
            oos.close();
            fos.close();

            FileInputStream fis = new FileInputStream(forTree);
            BufferedInputStream fbis = new BufferedInputStream(fis);

            long totalSize = forTree.length();
            dos.writeLong(totalSize);    // ���� �뷮 ����
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
        //Ŭ���̾�Ʈ�� �䱸�ϴ� ������ ����Ʈ�� �޾� Ŭ���̾�Ʈ�� �����մϴ�.
        System.out.println("send_selfile ����");
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
                System.out.println("send_selfile ���� ���μ��� ��ٸ�");
                receiveState(PROCESSING);
                System.out.println("send_selfile ���� ���μ��� ����");
                
                long totalSize = target.length();
                dos.writeLong(totalSize);    // ���� �뷮 ����
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
        //Ŭ���̾�Ʈ�� �䱸�ϴ� ���ϵ��� ����Ʈ�� �޽��ϴ�.
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
        for(String str : selectedFileList) System.out.println("���� ���õ� ���: " + str);
        System.out.println("�ޱⳡ");
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

