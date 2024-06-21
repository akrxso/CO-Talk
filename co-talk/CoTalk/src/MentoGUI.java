import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MentoGUI extends JFrame {

	private Color lightBlue = new Color(153, 204, 255);

	public JLabel l_name = new JLabel();
	public JLabel l_grade = new JLabel();
	public JLabel l_dept = new JLabel();
	public JLabel l_stuID = new JLabel();
	public JPanel currentPanel;

	ClassLoader classLoader = getClass().getClassLoader();

	DefaultListModel<String> requestList_mento = null;
	DefaultListModel<String> pointList = null;
	DefaultListModel<String> fileList_mento = null;

	StringTokenizer st;

	User user;

	public MentoGUI(User user) {
		super("Co-Talk 멘토메인");
		this.user = user;
		setLayout(new BorderLayout(0, 10)); // vGap

		currentPanel = messagePanel_inMento();

		add(createUpPanel(), BorderLayout.NORTH);
		add(createDownPanel(), BorderLayout.CENTER);

		setSize(400, 650);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	// 메인 상단 정보 패널
	public JPanel createUpPanel() {
		JPanel p = createRoundedPanel();
		p.setLayout(new GridLayout(2, 0));

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel p2 = new JPanel(new GridLayout(0, 3));

		// 이름, 아이콘
		l_name = new JLabel();
		l_name.setForeground(Color.white);
		l_name.setFont(user.myCustomFont.deriveFont(Font.BOLD, 35));
		ImageIcon icon = new ImageIcon(classLoader.getResource("hansung8.png"));
		JLabel userIcon;

		// 이미지 조절
		if (icon.getIconWidth() > 70) {
			Image img = icon.getImage();
			Image changeImg = img.getScaledInstance(70, -1, Image.SCALE_SMOOTH);
			icon = new ImageIcon(changeImg);
			userIcon = new JLabel(icon);
		} else
			userIcon = new JLabel(icon);

		// 학년,학과,학번 Label 스타일+부착
		l_grade = new JLabel();
		l_grade.setForeground(Color.white);
		l_grade.setHorizontalAlignment(SwingConstants.CENTER);
		l_grade.setFont(user.myCustomFont.deriveFont(Font.BOLD, 20));

		l_dept = new JLabel();
		l_dept.setForeground(Color.white);
		l_dept.setHorizontalAlignment(SwingConstants.CENTER);
		l_dept.setFont(user.myCustomFont.deriveFont(Font.BOLD, 15));

		l_stuID = new JLabel();
		l_stuID.setForeground(Color.white);
		l_stuID.setHorizontalAlignment(SwingConstants.CENTER);
		l_stuID.setFont(user.myCustomFont.deriveFont(Font.BOLD, 20));

		l_name.setText(user.name + "           [멘토]");
		l_grade.setText(user.grade);
		l_dept.setText(user.dept);
		l_stuID.setText(user.stuID);

		p1.add(userIcon);
		p1.add(l_name);
		p2.add(l_grade);
		p2.add(l_dept);
		p2.add(l_stuID);

		p1.setBackground(lightBlue);
		p2.setBackground(lightBlue);
		p.add(p1);
		p.add(p2);
		p.setOpaque(true);

		return p;
	}

	// 메인 하단 패널
	public JPanel createDownPanel() {
		JPanel p = createRoundedPanel();
		p.setLayout(new BorderLayout());

		p.add(createSelectBtPanel(), BorderLayout.WEST);
		p.add(createDisplayPanel(), BorderLayout.CENTER);

		return p;
	}

	// 하단 패널 WEST 선택 패널
	public JPanel createSelectBtPanel() {
		JPanel p = new JPanel(new FlowLayout());

		// (1) 멘토창 버튼
		ImageIcon img = new ImageIcon(classLoader.getResource("email.png"));
		ImageIcon img2 = new ImageIcon(img.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH));
		JButton b_req = new JButton(img2);
		b_req.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(1);
			}
		});
		b_req.setOpaque(false);
		b_req.setContentAreaFilled(false); // 내용 영역을 투명하게 설정
		b_req.setBorderPainted(false);

		// (2) 보낸 요청 버튼
		ImageIcon img3 = new ImageIcon(classLoader.getResource("heart.png"));
		ImageIcon img4 = new ImageIcon(img3.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH));
		JButton b_point = new JButton(img4);
		b_point.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(2);
			}
		});
		b_point.setOpaque(false);
		b_point.setContentAreaFilled(false); // 내용 영역을 투명하게 설정
		b_point.setBorderPainted(false);

		// (3) 파일 버튼
		ImageIcon img5 = new ImageIcon(classLoader.getResource("folder.png"));
		ImageIcon img6 = new ImageIcon(img5.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH));
		JButton b_file = new JButton(img6);
		b_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(3);
			}
		});
		b_file.setOpaque(false);
		b_file.setContentAreaFilled(false); // 내용 영역을 투명하게 설정
		b_file.setBorderPainted(false);

		// (4) 로그아웃 버튼
		ImageIcon img7 = new ImageIcon(classLoader.getResource("logout.png"));
		ImageIcon img8 = new ImageIcon(img7.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH));
		JButton b_logout = new JButton(img8);
		b_logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int answer = JOptionPane.showConfirmDialog(p, "로그아웃하시겠습니까?", "로그아웃창", JOptionPane.YES_NO_CANCEL_OPTION);
				if (answer == JOptionPane.YES_OPTION)
					System.exit(0);
			}
		});
		b_logout.setOpaque(false);
		b_logout.setContentAreaFilled(false); // 내용 영역을 투명하게 설정
		b_logout.setBorderPainted(false);

		p.add(b_req);
		p.add(b_point);
		p.add(b_file);
		p.add(b_logout);

		p.setBackground(lightBlue);
		p.setPreferredSize(new Dimension(80, 200));

		return p;
	}

	// 하단 패널 CENTER 화면패널
	public JPanel createDisplayPanel() {
		JPanel p = new JPanel(new BorderLayout());

		p.setBackground(Color.white);
		p.add(currentPanel, BorderLayout.CENTER);

		p.setPreferredSize(new Dimension(286, 391));

		return p;
	}

	// 화면패널-(1) 받은 채팅 요청 패널
	synchronized private JPanel messagePanel_inMento() {
		JPanel p = new JPanel(new BorderLayout());
		requestList_mento = new DefaultListModel<>();

		Iterator<String> it = user.vChatRequest.iterator();

		// 채팅 요청 받음
		while (it.hasNext()) {
			String str = it.next();
			requestList_mento.addElement(str);
		}

		JList<String> l_req = new JList<>(requestList_mento);
		l_req.setBorder(BorderFactory.createTitledBorder("채팅 요청"));

		// 리스트 요소 클릭 시 호출되는 메소드
		l_req.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedIndex = l_req.getSelectedIndex();
				if (selectedIndex != -1) {
					// 리스트 요소 클릭 시 나타나는 다이얼로그
					int answer = JOptionPane.showConfirmDialog(this, "채팅 요청을 수락하시겠습니까?", "채팅 수락창",
							JOptionPane.YES_NO_CANCEL_OPTION);
					if (answer == JOptionPane.YES_OPTION) {
						// 채팅방 개설
						System.out.println(user.stuID + " " + requestList_mento.get(selectedIndex));

						if (user.room != null) // 이미 채팅방이 생성되어있음.
						{
							user.room.printDisplay(requestList_mento.get(selectedIndex) + "가 참여하였습니다.", 2);
							user.send(new ChatMsg(user.stuID, user.vChatRequest.get(selectedIndex),
									ChatMsg.MODE_TX_CREATEROOM, user.room.roomNumber));
						}

						else { // 새 채팅방 생성
							// 방 생성, 타임매니저
							user.room = new CoTalkRoom(user);
							TimeManager time = new TimeManager(user, user.room);
							time.boom();

							// 방 번호 생성, 방 생성 알림 보냄
							user.room.roomNumber = Math.random() * 50 + 1;
							String result = String.format("%.2f", user.room.roomNumber);
							user.room.roomNumber = Double.parseDouble(result);

							user.send(new ChatMsg(user.stuID, user.vChatRequest.get(selectedIndex),
									ChatMsg.MODE_TX_CREATEROOM, user.room.roomNumber));
						}

						// 멘토 채팅요청리스트 삭제
						user.vChatRequest.remove(selectedIndex);

					}

					// 선택 해제
					l_req.clearSelection();
				}
			}
		});

		// 스크롤바 팬에 부착 후 패널에 add
		p.add(new JScrollPane(l_req), BorderLayout.CENTER);

		return p;
	}

	// 화면패널-(2) 받은 포인트(만족도) 패널
	synchronized private JPanel pointPanel_inMento() {
		JPanel p = new JPanel(new BorderLayout());
		pointList = new DefaultListModel<>();

		Iterator<String> it = user.vPoint.iterator();

		// 포인트 목록
		while (it.hasNext()) {
			String str = it.next();
			pointList.addElement(str);
		}

		// 리스트 생성
		JList<String> l_point = new JList<>(pointList);
		l_point.setBorder(BorderFactory.createTitledBorder("만족도(포인트) 목록"));

		// 스크롤바 팬에 부착 후 패널에 add
		p.add(new JScrollPane(l_point), BorderLayout.CENTER);

		return p;
	}

	// 화면패널-(3) 주고받은 파일/이미지 저장소 패널
	synchronized private JPanel filePanel_inMento() {
		JPanel p = new JPanel(new BorderLayout());

		fileList_mento = new DefaultListModel<>();

		Iterator<String> it = user.vFile.iterator();

		// 채팅 요청 받음
		while (it.hasNext()) {
			String str = it.next();
			fileList_mento.addElement(str);
		}

		JList<String> l_req = new JList<>(this.fileList_mento);
		l_req.setBorder(BorderFactory.createTitledBorder("파일 보관"));

		// 리스트 요소 클릭 시 호출되는 메소드
		l_req.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedIndex = l_req.getSelectedIndex();
				if (selectedIndex != -1) {
					// 리스트 요소 클릭 시 나타나는 다이얼로그
					int answer = JOptionPane.showConfirmDialog(this, "파일을 삭제하시겠습니까?");
					if (answer == JOptionPane.YES_OPTION) {
						user.vFile.remove(selectedIndex);

					}
					// 선택 해제
					l_req.clearSelection();
				}
			}
		});

		// 스크롤바 팬에 부착 후 패널에 add
		p.add(new JScrollPane(l_req), BorderLayout.CENTER);

		return p;
	}

	// 화면 패널 변경해주는 메소드 (버튼클릭시)
	private void switchPanel(int num) {
		Container parent = currentPanel.getParent();
		if (parent != null) {
			// 부모 패널에서 현재 패널 remove 하고 다시 그리기
			parent.remove(currentPanel);
			parent.revalidate();
			parent.repaint();
		}

		switch (num) { // num에 따라 현재 패널 바뀜
		case 1:
			currentPanel = messagePanel_inMento(); // 1-> 메시지
			break;
		case 2:
			currentPanel = pointPanel_inMento(); // 2-> 포인트
			break;
		case 3:
			currentPanel = filePanel_inMento(); // 3-> 파일
			break;
		}

		SwingUtilities.invokeLater(() -> {
			// 부모 패널에서 현재 패널 add 하고 다시 그리기
			parent.add(currentPanel);
			parent.revalidate();
			parent.repaint();
		});
	}

	protected JPanel createRoundedPanel() {
		JPanel panel = new JPanel();

		// 경계선을 둥글게 처리하는 Border를 생성하여 패널에 설정
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // 라인
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 내부공백
		return panel;
	}

}
