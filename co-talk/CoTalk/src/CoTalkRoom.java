import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class CoTalkRoom extends JFrame {
   private JTextPane t_display;
   private DefaultStyledDocument document;
   private JTextField t_input;
   private JButton b_send, b_image, b_file, b_draw;
   public double roomNumber;
   public String point;
   public int score;

   
   ClassLoader classLoader = getClass().getClassLoader();
   private ImageIcon imgBtn = new ImageIcon(classLoader.getResource("imageButton.png"));
   private ImageIcon fileBtn = new ImageIcon(classLoader.getResource("fileButton.png"));
   private ImageIcon drawBtn = new ImageIcon(classLoader.getResource("drawButton.png"));

   User user;
   DrawGUI drawGUI;

   public CoTalkRoom(User user) {
	      super("Co-Talk : " + user.stuID);

	      this.user = user;
	      setSize(400, 650);
	      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	      add(createDisplayPanel(), BorderLayout.CENTER);
	      JPanel p_pink = new JPanel(new GridLayout(2, 0)); // 2행
	      p_pink.add(createInputPanel());
	      p_pink.add(createInfoPanel());
	      add(p_pink, BorderLayout.SOUTH);
	      setVisible(true); // 가시화 처리

	      if(user.role == 1) { // 멘토일경우
	         this.addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent e) {
	               user.room=null;
	            }
	         });
	      }
	      
	      else if (user.role == 2) { // 멘티일경우
	         this.addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent e) {
	               
	               while(true) {
	                  point = JOptionPane.showInputDialog("만족도 점수를 입력해주세요.(0~10 반드시 숫자로 입력바람)");
	                  if(point==null)continue;
	                  
	                  try {
	                       score=Integer.parseInt(point);
	                   } catch (NumberFormatException e1) { // 입력 점수가 int 형태가 아님.
	                      JOptionPane.showMessageDialog(p_pink.getParent(), "정수 형태의 숫자만 입력할 수 있습니다.");
	                       continue;
	                   }
	                  
	                  for (int i = 0; i <= 10; i++) {
	                     if (score == i) { // 입력 점수가 0~10에 포함
	                        user.send(new ChatMsg(user.stuID, ChatMsg.MODE_TX_POINT, point, score, user.room.roomNumber));
	                        return ;
	                     }
	                     else if (i == 10) { // 입력 점수가 0~10에 포함되지 않음
	                        JOptionPane.showMessageDialog(p_pink.getParent(), "0부터 10만 입력할 수 있습니다.");
	                        break;
	                     }
	                  }
	               }
	            }
	         });
	      }
	   }
   

   private JPanel createDisplayPanel() {
      JPanel p = new JPanel(new BorderLayout());
      document = new DefaultStyledDocument(); // 기본스타일 문서객체
      t_display = new JTextPane(document); // 기존의 tetxarea대체
      t_display.setEditable(false); // 편집할 수 없음. 편집모드 비활성화 (글씨 검정)

      p.add(new JScrollPane(t_display), BorderLayout.CENTER);
      // 현재 패널에 부착해야함
      // JScrollPane JTextArea 영역이 길어지면 자동으로 스크롤바가 나타나 스크롤 처리가 이뤄짐

      return p;
   }

   private JPanel createInputPanel() {
      JPanel p = new JPanel(new BorderLayout());

      t_input = new JTextField();

      t_input.addFocusListener(new FocusAdapter() { // 포커스 리스너 사용
         @Override
         public void focusGained(FocusEvent e) {
            b_send.setEnabled(true); // JTextField가 클릭되면 활성화 됨
         }
      });

      t_input.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            sendMessage();
         }

      });
      p.add(t_input);
      t_input.setEnabled(true); // 내용입력
      return p;
   }

   private JPanel createInfoPanel() {
      JPanel p = new JPanel(new GridLayout());
      JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JPanel p2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

      b_image = new JButton(imgBtn);
      b_image.setPreferredSize(new Dimension(20, 20)); // 버튼 사이즈 조정메소드
      b_image.addActionListener(new ActionListener() {

         JFileChooser chooser = new JFileChooser();

         @Override
         public void actionPerformed(ActionEvent e) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF & PNG Images", "jpg", "gif",
                  "png");

            chooser.setFileFilter(filter);

            int ret = chooser.showSaveDialog(user.room);
            if (ret != JFileChooser.APPROVE_OPTION) {
               JOptionPane.showMessageDialog(user.room, "파일을 선택하지 않음 ");
               return;
            }

            t_input.setText(chooser.getSelectedFile().getAbsolutePath()); // 절대 경로명
            sendImage();
         }

      });
      b_file = new JButton(fileBtn);
      b_file.setPreferredSize(new Dimension(20, 20));
      b_file.addActionListener(new ActionListener() {
         JFileChooser chooser = new JFileChooser();

         @Override
         public void actionPerformed(ActionEvent e) {

            int ret = chooser.showOpenDialog(user.room);
            if (ret != JFileChooser.APPROVE_OPTION) {
               JOptionPane.showMessageDialog(user.room, "파일을 선택하지 않음 ");
               return;
            }

            t_input.setText(chooser.getSelectedFile().getAbsolutePath()); // 절대 경로명
            sendFile();
         }

      });
      b_draw = new JButton(drawBtn);
      b_draw.setPreferredSize(new Dimension(20, 20));
      b_draw.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            drawGUI = new DrawGUI(user);
            drawGUI.buildImageGUI();
         }
      });

      b_send = new JButton("보내기");
      b_send.setEnabled(false);

      b_send.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            sendMessage();
         }

      });
      p1.add(b_image);
      p1.add(b_file);
      p1.add(b_draw);
      p2.add(b_send);
      p.add(p1);
      p.add(p2);
      return p;
   }

   protected void printDisplay(String msg, int mode) {
      int len = t_display.getDocument().getLength();

      if (mode == 1) // 자기자신일때 => 오른쪽에 출력
      {
         try {
            document.insertString(len, "\t\t\t" + msg + "\n", null);
         } catch (BadLocationException e) {
            printDisplay("텍스트 삽입 오류", 1);
         }
      }

      else if (mode == 2) {
         try {
            document.insertString(len, msg + "\n", null);
         } catch (BadLocationException e) {
            printDisplay("텍스트 삽입 오류", 1);
         }
      }

      t_display.setCaretPosition(len);
   }

   protected void printDisplay(ImageIcon icon) {
      t_display.setCaretPosition(t_display.getDocument().getLength());

      // 이미지 크기 조절
      if (icon.getIconWidth() > 400) {
         Image img = icon.getImage();
         Image changeImg = img.getScaledInstance(400, -1, Image.SCALE_SMOOTH);
         icon = new ImageIcon(changeImg);
      }

      t_display.insertIcon(icon);

      printDisplay("", 1);
      t_input.setText("");
   }

   private void sendMessage() {
      String message = t_input.getText(); // 문자열로 얻어옴
      if (message.isEmpty()) // 아무것도 입력하지 않은 상태
         return;

      user.send(new ChatMsg(user.stuID, ChatMsg.MODE_TX_STRING, user.name + ":" + message, roomNumber));
      t_input.setText("");
   }

   private void sendImage() {

      String filename = t_input.getText().strip();

      if (filename.isEmpty())
         return;

      File file = new File(filename);
      if (!file.exists()) {
         printDisplay(">>파일이 존재하지 않습니다 :" + filename, 1);
         return;
      }

      ImageIcon icon = new ImageIcon(filename); // 파일을 열수 있으면 이미지객체 생성
      user.send(new ChatMsg(user.stuID, ChatMsg.MODE_TX_IMAGE, file.getName(), icon, roomNumber));

      t_input.setText("");
   }

   public void sendFile() {

      String filename = t_input.getText().strip();

      if (filename.isEmpty())
         return;

      File file = new File(filename);
      if (!file.exists()) {
         printDisplay(">> 파일이 존재하지 않습니다 :" + filename, 1);
         return;
      }

      user.send(new ChatMsg(user.stuID, ChatMsg.MODE_TX_FILE, file.getName(), file.length(), roomNumber));

      BufferedInputStream bis = null;

      try {
         bis = new BufferedInputStream(new FileInputStream(file));

         byte[] buffer = new byte[1024];
         int nRead;

         while ((nRead = bis.read(buffer)) != -1) {
            user.bos.write(buffer, 0, nRead);
         }
         user.bos.flush(); // 한번에 하나의 파일만 보낼 수 있다

         printDisplay("전송완료" + filename, 1);

         t_input.setText("");

      } catch (FileNotFoundException e) {
         printDisplay(">> 파일이 존재하지 않습니다 : " + e.getMessage(), 1);
         return;
      } catch (IOException e) {
         printDisplay(">>파일을 읽을 수 없습니다 : " + e.getMessage(), 1);
         return;
      } finally {
         try {
            if (bis != null)
               bis.close();
         } catch (IOException e) {
            printDisplay(">>파일을 닫을 수 없습니다 : " + e.getMessage(), 1);
            return;
         }
      }
   }
}
