import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class Server extends JFrame {
   private int port;
   private ServerSocket serverSocket = null;
   private Thread acceptThread = null;
   private JTextPane t_display;
   private JButton b_connect;
   private JButton b_disconnect;
   private JButton b_exit;
   private DefaultStyledDocument document;

   private volatile Vector<ClientHandler> users = new Vector<ClientHandler>();

   public Server(int port) {
      super("Co-Talk 서버");

      buildGUI();
      setSize(400, 500);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      setVisible(true);
      this.port = port;
   }

   private void buildGUI() {
      add(createDisplayPanel(), BorderLayout.CENTER);
      add(createControlPanel(), BorderLayout.SOUTH);
   }

   private JPanel createDisplayPanel() {
      JPanel p = new JPanel(new BorderLayout());

      document = new DefaultStyledDocument();
      t_display = new JTextPane(document);
      t_display.setEditable(false);

      JScrollPane scrollPane = new JScrollPane(t_display); // 스크롤
      scrollPane.setSize(400, 350);
      p.add(scrollPane);

      return p;
   }

   private JPanel createControlPanel() {
      JPanel p = new JPanel(new GridLayout());

      b_connect = new JButton("서버 시작");
      b_connect.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            acceptThread = new Thread(new Runnable() {
               public void run() {
                  startServer();
               }
            });

            acceptThread.start();
            b_disconnect.setEnabled(true);
            b_connect.setEnabled(false);
            b_exit.setEnabled(false);
         }
      });

      b_disconnect = new JButton("서버 종료");
      b_disconnect.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            disconnect();
            b_disconnect.setEnabled(false);
            b_connect.setEnabled(true);
            b_exit.setEnabled(true);
         }
      });

      b_exit = new JButton("종료");
      b_exit.addActionListener(new ActionListener() { // 종료시켜야함
         public void actionPerformed(ActionEvent e) {
            System.exit(-1);
         }
      });

      p.add(b_connect);
      p.add(b_disconnect);
      p.add(b_exit);
      return p;
   }

   private void printDisplay(String msg) { // 출력
      int len = t_display.getDocument().getLength();

      try {
         document.insertString(len, msg + "\n", null);
      } catch (BadLocationException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      t_display.setCaretPosition(len);
   }

   private void startServer() {
      Socket socket = null;
      try {
         serverSocket = new ServerSocket(port);
         printDisplay("서버가 시작되었습니다.(" + getLocalAddr() + ")");

         while (acceptThread == Thread.currentThread()) {
            socket = serverSocket.accept();
            printDisplay("클라이언트가 연결되었습니다.(" + socket.getLocalSocketAddress() + ")");

            ClientHandler client = new ClientHandler(socket);

            users.add(client);
            client.start();
         }

      } catch (SocketException e) {
         System.err.println("서버 소켓 종료: " + e.getMessage());
         printDisplay("서버 소켓 종료");
         System.exit(-1);

      } catch (IOException e) {
         System.err.println("port가 이미 사용 중 입니다 : " + e.getMessage());
         printDisplay("port가 이미 사용 중 입니다.");
         System.exit(-1);

      } finally {
         try {
            if (socket != null)
               socket.close();
            if (serverSocket != null)
               serverSocket.close();
         } catch (IOException e) {
            System.err.println("서버 닫기 오류> " + e.getMessage());
            printDisplay("서버 닫기 오류> " + e.getMessage());
            System.exit(-1);
         }
      }
   }

   private void disconnect() { // 서버 연결 종료
      try {
         acceptThread = null;
         serverSocket.close();
      } catch (IOException e) {
         System.err.println("서버 소켓 닫기 오류>" + e.getMessage());
         System.exit(-1);
      }
   }

   private class ClientHandler extends Thread { // 클라이언트 소켓 핸들러
      private Socket socket;
      private BufferedOutputStream bos;
      private ObjectOutputStream out;
      private BufferedInputStream bis;

      // 정보
      private String grade; // 학년
      private String stuID; // 학번
      private String dept; // 학과
      private String name; // 이름
      public double roomNumber; // 채팅방번호

      private int role; // 1-> 멘토 2-> 멘티

      public ClientHandler(Socket clientSocket) {
         this.socket = clientSocket;
      }

      private void receiveMessages(Socket socket) { // 문자 수신
         try {
            bis = new BufferedInputStream(socket.getInputStream());
            ObjectInputStream in = new ObjectInputStream(bis);

            bos = new BufferedOutputStream(socket.getOutputStream());
            out = new ObjectOutputStream(bos);

            ChatMsg inMsg;

            while ((inMsg = (ChatMsg) in.readObject()) != null) {

               if (inMsg.mode == ChatMsg.MODE_LOGIN1) { // 로그인(멘토)
                  this.grade = inMsg.grade;
                  this.dept = inMsg.dept;
                  this.name = inMsg.name;
                  this.stuID = inMsg.stuID;
                  this.role = 1;

                  printDisplay(name + " [멘토]" + "님이 로그인하였습니다.");
                  printDisplay("현재 참가자 수: " + users.size());
               }

               else if (inMsg.mode == ChatMsg.MODE_LOGIN2) { // 로그인(멘티)
                  this.grade = inMsg.grade;
                  this.dept = inMsg.dept;
                  this.name = inMsg.name;
                  this.stuID = inMsg.stuID;
                  this.role = 2;

                  printDisplay(name + " [멘티]" + "님이 로그인하였습니다.");
                  printDisplay("현재 참가자 수: " + users.size());
               }

               else if (inMsg.mode == ChatMsg.MODE_LOGOUT) { // 로그아웃
                  if (inMsg.role == 1) { // 멘토 로그아웃
                     printDisplay(name + " [멘토]" + "님이 로그아웃하였습니다.");
                  } else { // 멘티 로그아웃
                     printDisplay(name + " [멘티]" + "님이 로그아웃하였습니다.");
                  }
                  printDisplay("현재 참가자 수: " + users.size());

                  break;
               }

               else if (inMsg.mode == ChatMsg.MODE_TX_STRING) { // 채팅방(1)-메시지전송
                  printDisplay(inMsg.roomNumber + " 채팅방 모두에게 >> \n" + stuID + ": " + inMsg.message);
                  broadcastingRoom(inMsg);
               }

               else if (inMsg.mode == ChatMsg.MODE_TX_IMAGE) { // 채팅방(2)-이미지전송
                  printDisplay(inMsg.roomNumber + " 채팅방 모두에게 >> \n" + stuID + ": " + inMsg.message);
                  broadcastingRoom(inMsg);
               }

               else if (inMsg.mode == ChatMsg.MODE_TX_FILE) { // 채팅방(3)-파일 전송
                  printDisplay(inMsg.roomNumber + " 채팅방 자신을 제외한 모두에게 >> \n" + stuID + ": " + "(" + inMsg.message + ")");
                  broadcastingFile(inMsg);
               }

               else if (inMsg.mode == ChatMsg.MODE_TX_POINT) { // 채팅방(4)-포인트 전송
                  broadcastingRoom(inMsg); // 포인트 정보 전송 (멘토만)
               }

               else if (inMsg.mode == ChatMsg.MODE_TX_CREATEROOM) { 
                  printDisplay("[채팅방 " + Double.toString(inMsg.roomNumber) + "가 생성되었습니다]");

                  // 멘토의 룸넘버 저장
                  roomNumber = inMsg.roomNumber;

                  // 멘티의 룸넘버 저장
                  for (ClientHandler c : users) {
                     if (c.stuID.equals(inMsg.mentiID)) {
                        c.roomNumber = inMsg.roomNumber;
                     }
                  }

                  // 멘티에게 채팅방 생성 요청
                  broadcastingP2P(inMsg);
               }

               else if (inMsg.mode == ChatMsg.MODE_Q) {
                  printDisplay(inMsg.mentiID + "님이 " + inMsg.mentoID + "님에게 채팅 요청을 보냈습니다.");
                  broadcastingP2P(inMsg);
               }

               else if (inMsg.mode == ChatMsg.MODE_REMOVE) {
                  printDisplay(inMsg.mentiID + "님이 "+ inMsg.mentoID+ "님에게 보낸 채팅 요청을 삭제하였습니다.");
                  broadcastingP2P(inMsg);
               }

               else if (inMsg.mode == ChatMsg.MODE_DRAW) {
                  broadcastingRoom(inMsg);
               }
            }

            users.removeElement(this);

         } catch (IOException e) {
            users.removeElement(this);
            printDisplay(stuID + " 퇴장. 현재 참가자 수: " + users.size());

         } catch (ClassNotFoundException e) {
            System.err.println("메시지(클래스) 형식을 찾을 수 없음>" + e.getMessage());

         } finally {
            try {
               socket.close();
            } catch (IOException e) {
               System.err.println("서버 닫기 오류> " + e.getMessage());
               System.exit(-1);
            }
         }
      }

      // 객체 전송 메서드
      private void send(ChatMsg msg) {
         try {
            out.writeObject(msg);
            out.flush();
         } catch (IOException e) {
            System.err.println("서버 메시지 전송 오류> " + e.getMessage());
            System.exit(-1);
         }
      }


      // 멘토-멘티 OR 개인간 객체 전송 메서드
      private void broadcastingP2P(ChatMsg cMsg) {
         if (cMsg.mode == ChatMsg.MODE_TX_CREATEROOM) { // 채팅요청수락 (멘토 -> 멘티)
            for (ClientHandler c : users) {
               if (c.stuID.equals(cMsg.mentiID)) {
                  c.send(cMsg);
               }
            }
         }

         else if (cMsg.mode == ChatMsg.MODE_Q) { // 채팅요청 (멘티 -> 멘토)
            for (ClientHandler c : users) {
               if (c.stuID.equals(cMsg.mentoID)) {
                  c.send(cMsg);
               }
            }
         }

         else if (cMsg.mode == ChatMsg.MODE_REMOVE) { // 채팅요청삭제알림 (멘티 -> 멘토)
            for (ClientHandler c : users) {
               if (c.stuID.equals(cMsg.mentoID)) {
                  c.send(cMsg);
               }
            }
         }
      }

      // 채팅방 객체 전송 메서드
      public void broadcastingRoom(ChatMsg cMsg) {
         if (cMsg.mode==ChatMsg.MODE_TX_POINT) { // 포인트 모드
            for (ClientHandler c : users) {
               if (c.role == 1) { // 멘토일때
                  if (c.roomNumber == cMsg.roomNumber) { // 룸넘버체크
                     printDisplay(cMsg.roomNumber + " 채팅방 >> " + "멘티 " + cMsg.stuID + "의 멘토 " + c.stuID
                           + "과의 멘토링 만족도 포인트 : " + cMsg.message);
                     c.send(cMsg);
                  }
               }
            }
         }

         else {
            for (ClientHandler c : users) {
               if (cMsg.roomNumber == c.roomNumber) { // 룸넘버체크
                  c.send(cMsg);
               }
            }
         }
      }

      // 파일 전송 메서드
      private void broadcastingFile(ChatMsg cMsg) { // 벡터 내 모든 요소 sendMessage 호출
         for (ClientHandler c : users) {
            if (c.stuID.equals(cMsg.stuID) == false) { // 본인 x
               if (c.roomNumber == cMsg.roomNumber) { // 룸넘버체크
                  c.send(cMsg);
                  c.redirectStream(bis, cMsg.size);
               }
            }
         }
      }
      
      private void redirectStream(BufferedInputStream bis, long size) {

         byte[] buffer = new byte[1024];
         int nRead = 0;

         try {
            while (size > 0) {
               nRead = bis.read(buffer);
               size -= nRead;

               bos.write(buffer, 0, nRead);
            }
            bos.flush();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      @Override
      public void run() {
         receiveMessages(socket);
      }
   }

   private String getLocalAddr() {
      try {
         return InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException e) {
         return "Unknown Host";
      }
   }

   public static void main(String args[]) {
      int port = 54321;

      Server server = new Server(port);
   }
}
