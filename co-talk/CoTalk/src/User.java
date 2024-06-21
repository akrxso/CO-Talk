import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;

public class User {

	private String serverAddress;
	private int serverPort;
	private String uid;
	private Socket socket;
	private ObjectOutputStream out;
	public BufferedOutputStream bos;
	private Thread receiveThread = null;

	public String grade; // 학년
	public String stuID; // 학번
	public String dept; // 학과
	public String name; // 이름
	public Font myCustomFont;
	public int role;

	// 화면 객체 생성
	public LoginGUI login;
	public MentoGUI mento;
	public MentiGUI menti;
	public CoTalkRoom room;

	public User myUser;

	// 멘토 데이터 리스트
	public List<String> vPoint = new ArrayList<String>();
	public List<String> vChatRequest = new ArrayList<String>();

	// 멘티 데이터 리스트
	public List<String> vMento = new ArrayList<String>();;
	public List<String> vSendReq = new ArrayList<String>();

	// 공통 데이터 리스트
	public List<String> vFile = new ArrayList<String>();

	public User(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		readFont(); // 폰트 읽음

		// 로그인 화면 build
		login = new LoginGUI(this);
	}

	public void connectToServer(User user) throws UnknownHostException, IOException { // 서버 접속 요청

		this.myUser = user;

		socket = new Socket();
		SocketAddress sa = new InetSocketAddress(serverAddress, serverPort);
		socket.connect(sa, 3000); // 3초안에 연결 한다.

		bos = new BufferedOutputStream(socket.getOutputStream());
		out = new ObjectOutputStream(bos);

		receiveThread = new Thread(new Runnable() {
			private BufferedInputStream bis;
			private ObjectInputStream in;

			protected void receiveMessage() {
				try {
					ChatMsg inMsg = (ChatMsg) in.readObject();
					if (inMsg == null) {
						disconnect();
						return;
					}

					switch (inMsg.mode) {
					case ChatMsg.MODE_TX_STRING: // 채팅방(1) - 메시지전송
						if (inMsg.stuID.equals(user.stuID)) {
							myUser.room.printDisplay(inMsg.message, 1);
						} else {
							myUser.room.printDisplay(inMsg.message, 2);
						}
						break;

					case ChatMsg.MODE_TX_IMAGE: // 채팅방(2) - 이미지전송
						myUser.room.printDisplay(inMsg.stuID + ": " + inMsg.message, 2);
						myUser.room.printDisplay(inMsg.image);

						break;

					case ChatMsg.MODE_TX_FILE: // 채팅방(3) - 파일전송
		                  long size = inMsg.size;
		                  String filename = inMsg.message;
		                  BufferedOutputStream bfos = new BufferedOutputStream(new FileOutputStream(filename));

		                  byte[] buffer = new byte[1024];
		                  int nRead;

		                  while (size > 0) {
		                     nRead = bis.read(buffer);
		                     size -= nRead;

		                     bfos.write(buffer, 0, nRead);
		                  }
		                  bfos.close();
		                  
		                  room.printDisplay(inMsg.stuID + ": " + inMsg.message,2);
		                  String str = filename;
		                  String[] index = str.split("/");
		                  String lastPart = index[index.length - 1];
		                  user.vFile.add(lastPart);
		                  break;

					case ChatMsg.MODE_TX_POINT: // 채팅방(4) - 포인트전송
						// 멘토 포인트 리스트에 추가해야함
						myUser.vPoint.add(inMsg.stuID+"님과의 멘토링 포인트 점수:"+inMsg.message);
						break;

					case ChatMsg.MODE_TX_CREATEROOM: // 채팅방생성
						// 방 생성과 함께 룸넘버세팅
						myUser.room = new CoTalkRoom(myUser);
						myUser.room.roomNumber = inMsg.roomNumber;

						// 채팅방 타이머 설정
						TimeManager time = new TimeManager(myUser, myUser.room);
						time.boom();
						
						// 멘티의 보낸 채팅요청패널 리스트 업데이트
						SwingUtilities.invokeLater(() -> {
						Iterator<String> iterator = myUser.vSendReq.iterator();
					    while (iterator.hasNext()) {
					        String s = iterator.next();
					        if (s.contains(inMsg.mentoID)) {
					            iterator.remove();
					        }
					    }
					    });

						break;

					case ChatMsg.MODE_Q: // 채팅 요청
						// 이미 채팅 요청을 한 상태면 리스트에 더하지 않음.
						if (myUser.vChatRequest.contains(inMsg.mentiID)) {
							break;
						} else {
							myUser.vChatRequest.add(inMsg.mentiID);
							break;
						}

					case ChatMsg.MODE_REMOVE: // 채팅 요청 삭제
						// 채팅 요청이 리스트에 실제로 있으면 삭제
						if (myUser.vChatRequest.contains(inMsg.mentiID)) {
							myUser.vChatRequest.remove(inMsg.mentiID);
						}

						break;

					case ChatMsg.MODE_DRAW: 
						if (myUser.room.drawGUI != null) { // 그림판 null 체크
					        SwingUtilities.invokeLater(() -> {
					            myUser.room.drawGUI.drawPanel.vStart.add(inMsg.p);
					            myUser.room.drawGUI.currentColor = inMsg.color;

					            if (inMsg.flag == 1) { 
					                myUser.room.drawGUI.drawPanel.repaint();
					            }
					        });
					    }

						break;
					}

				} catch (ClassNotFoundException e) {
					System.out.println("잘못된 객체가 전달되었습니다.");

				} catch (IOException e) {
					System.out.println("연결을 종료했습니다.");
					System.exit(-1);
				}
			}

			@Override
			public void run() {
				try {
					bis = new BufferedInputStream(socket.getInputStream());
					in = new ObjectInputStream(bis);
				} catch (IOException e) {
					System.out.println("입력 스트림이 열리지 않음");
				}
				while (receiveThread == Thread.currentThread()) {
					receiveMessage();
				}
			}
		});
		receiveThread.start();
	}

	public void disconnect() { // 접속 끊기 메소드
		send(new ChatMsg(uid, ChatMsg.MODE_LOGOUT));

		try {
			receiveThread = null;
			socket.close();
		} catch (IOException e) {
			System.err.println("클라이언트 접속 끊기 오류> " + e.getMessage());
			System.exit(-1);
		}
	}

	public void send(ChatMsg msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException e) {
			System.err.println("클라이언트 일반 전송 오류>" + e.getMessage());
		}
	}

	public void sendUserInfo(int num) {
		switch (num) {
		case 1: // 멘토로 로그인
			send(new ChatMsg(myUser.grade, myUser.stuID, myUser.dept, myUser.name, ChatMsg.MODE_LOGIN1));
			break;
		case 2: // 멘티로 로그인
			send(new ChatMsg(myUser.grade, myUser.stuID, myUser.dept, myUser.name, ChatMsg.MODE_LOGIN2));
			break;
		}
	}

	public void readFont() { // 폰트 읽는 메소드
		String fontFilePath = "/font/dahaeng.ttf";

		// InputStream을 사용하여 Font 객체 생성
		try {
			InputStream fontStream = User.class.getResourceAsStream(fontFilePath);
			myCustomFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
		} catch (FontFormatException e) {
			System.out.println("지원되지 않는 폰트형식 입니다.");
		} catch (IOException e) {
			System.out.println("폰트 파일이 존재하지 않습니다.");
		}
	}

	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort = 54321;

		new User(serverAddress, serverPort);
	}
}