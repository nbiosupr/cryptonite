package Client;

/*
 * Writed by Seung Yeon Won (Dept. Information Media at Suwon University)
 * Edited by Sangwoon Park (Dept. Information Security at Suwon University)
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static java.lang.System.exit;

public class Decrypt_UI extends JFrame implements ActionListener {
    private BufferedImage img = null;

    private JLabel ing;
    private JPasswordField pw;
    private JButton Decryptbt;
    private DecryptManagement dm;
    private JCheckBox doExecute;
    private boolean complete = false;

    public static void main(String[] args) {

        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        DecryptManagement dm = new DecryptManagement(args);

        if (!dm.getUserObject()) {
            JOptionPane.showMessageDialog((Component)null, "로그인해주세요.", "오프라인", 1);
            exit(1);
        }
        dm.getFilePath();
        new Decrypt_UI(dm);
    }

    public Decrypt_UI(DecryptManagement dm) {
        this.dm = dm;

        setTitle("Cryptonite");
        setBounds(710, 200, 460, 645);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 레이아웃 설정
        getContentPane().setLayout(null);
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 460, 645);
        layeredPane.setLayout(null);

        // 패널1
        // 이미지 받아오기
        try {
            img = ImageIO.read(new File("C:/cryptonite/img/DECRYPT.png"));
        } catch (IOException e) {
            System.out.println("이미지 불러오기 실패");
            exit(0);
        }

        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, 460, 645);

        Font f1 = new Font("나눔고딕", Font.BOLD, 20);

        ing = new JLabel("WE WILL DECRYPT");
        ing.setFont(f1);
        ing.setBounds(70, 340, 300, 40);
        //주석해제하면 텍스트필드 투명화됨
        ing.setForeground(Color.LIGHT_GRAY);
        ing.setOpaque(false);
        ing.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        ing.setHorizontalAlignment(ing.CENTER);
        layeredPane.add(ing);

        ing = new JLabel(dm.getFileName());
        ing.setHorizontalAlignment(pw.CENTER);
        ing.setFont(new Font("나눔고딕", Font.BOLD, 30));

        ing.setBounds(70, 367, 300, 55);
        //주석해제하면 텍스트필드 투명화됨
        ing.setForeground(Color.white);
        ing.setHorizontalAlignment(ing.CENTER);
        ing.setOpaque(false);
        ing.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(ing);

        pw = new JPasswordField();
        pw.setFont(f1);
        pw.setBounds(50, 425, 330, 50);
        pw.setForeground(Color.gray);
        pw.setHorizontalAlignment(pw.CENTER);
        pw.setEchoChar((char) 0);
        pw.setText("Please Insert Password!!");

        pw.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                pw.setEchoChar('●');
                pw.setText("");
            }

            public void focusLost(FocusEvent e) {
                if(pw.getPassword().length == 0) {
                    pw.setEchoChar((char) 0);
                    pw.setText("Please Insert Password!!");
                }
            }
        });
        pw.setOpaque(false);
        pw.setBorder(javax.swing.BorderFactory.createEmptyBorder());

        layeredPane.add(pw);

        doExecute = new JCheckBox("Execute File after Decrypting");
        doExecute.setBounds(41, 475, 300, 30);
        doExecute.setForeground(Color.LIGHT_GRAY);
        doExecute.setOpaque(false);
        doExecute.setFont(new Font("나눔고딕", Font.BOLD, 15));
        doExecute.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        if(dm.isHereProtected()){
            doExecute.setSelected(true);
            doExecute.setEnabled(false);
        }
        layeredPane.add(doExecute);

        Decryptbt = new JButton(new ImageIcon("C:/cryptonite/img/DECRYPTbt.png"));
        Decryptbt.setBounds(140, 510, 160, 40);
        Decryptbt.setOpaque(false);
        Decryptbt.setBorderPainted(false);
        Decryptbt.setContentAreaFilled(false);
        Decryptbt.setFocusPainted(false);
        Decryptbt.setRolloverIcon(new ImageIcon("C:/cryptonite/img/DECRYPTbtH.png"));
        Decryptbt.addActionListener(this);
        layeredPane.add(Decryptbt);

        // 마지막 추가들
        layeredPane.add(panel);

        getContentPane().add(layeredPane);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean result = false;
        // TODO Auto-generated method stub
        //버튼 Click 시
        if(complete){
            exit(0);
        }
        else {
            if (dm.checkPassword(new String(pw.getPassword()))) {
                Decryptbt.setDisabledIcon(new ImageIcon("C:/cryptonite/img/decryptingbt.png"));
                Decryptbt.setEnabled(false);
                if (!dm.isHereProtected()) {
                    if (doExecute.isSelected()) {
                        dm.decrypt(1);
                    } else {
                        dm.decrypt(2);
                    }
                } else {
                    dm.decrypt(1);
                }
                Decryptbt.setEnabled(true);
                Decryptbt.setIcon(new ImageIcon("c:/cryptonite/img/completebt.png"));
                Decryptbt.setRolloverEnabled(false);
                complete = true;
            } else {
                showMessage("비밀번호 불일치", "비밀번호가 일치하지 않습니다.\n다시입력해주세요");
            }
        }
    }

    class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(img, 0, 0, null);
        }
    }

    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog((Component)null, message, title, 1);
    }

}


