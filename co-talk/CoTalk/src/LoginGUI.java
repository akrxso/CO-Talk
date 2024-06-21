import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginGUI extends JFrame {

	protected Font customFont;
	protected JTextField t_grade, t_stuID, t_dept, t_name;
	protected JLabel l_grade, l_stuID, l_dept, l_name;
	protected JComboBox<String> cb_grade;
	protected JComboBox<String> cb_dept;
	protected JButton b_login;
	protected ButtonGroup bGroup;
	protected JRadioButton rb_mento;
	protected JRadioButton rb_menti;
	private Color lightBlue = new Color(153, 204, 255);

	private String grade; // 학년
	private String stuID; // 학번
	private String dept; // 학과
	private String name; // 이름

	public MentoGUI mentoGUI;
	public MentiGUI mentiGUI;
	User user;

	public LoginGUI(User user) {
		// 로그인 frame 생성
		super("Co-Talk 로그인");
		this.user = user;

		JPanel p0 = new JPanel(new FlowLayout());

		JLabel l_mini = new JLabel("나의 작은 코딩라운지");
		l_mini.setForeground(Color.white);
		l_mini.setFont(user.myCustomFont.deriveFont(Font.PLAIN, 25));
		l_mini.setHorizontalAlignment(SwingConstants.CENTER);
		l_mini.setPreferredSize(new Dimension(400, 30));
		p0.add(l_mini);

		JLabel l_title = new JLabel("한성 코톡!");
		l_title.setForeground(Color.white);
		l_title.setFont(user.myCustomFont.deriveFont(Font.PLAIN, 90));
		l_title.setHorizontalAlignment(SwingConstants.CENTER);
		l_title.setPreferredSize(new Dimension(400, 110));
		p0.add(l_title);
		p0.setPreferredSize(new Dimension(400, 160));

		p0.setBackground(lightBlue);

		add(p0, BorderLayout.NORTH);
		add(createInfoPanel(), BorderLayout.CENTER);
		add(createLoginPanel(), BorderLayout.SOUTH);

		setSize(400, 650);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);
	}

	// 정보 입력 패널
	private JPanel createInfoPanel() {
		JPanel p = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5); //

		JPanel p1 = new JPanel(new FlowLayout());
		JPanel p2 = new JPanel(new FlowLayout());
		JPanel p3 = new JPanel(new FlowLayout());
		JPanel p4 = new JPanel(new FlowLayout());
		JPanel p5 = new JPanel(new FlowLayout());

		// 로그인 정보 Label
		l_name = new JLabel("이름 :");
		l_grade = new JLabel("학년 :");
		l_stuID = new JLabel("학번 :");
		l_dept = new JLabel("학과 :");

		l_name.setForeground(Color.white);
		l_grade.setForeground(Color.white);
		l_stuID.setForeground(Color.white);
		l_dept.setForeground(Color.white);

		// 폰트, 크기 설정
		l_grade.setFont(user.myCustomFont.deriveFont(Font.PLAIN, 20));
		l_stuID.setFont(user.myCustomFont.deriveFont(Font.PLAIN, 20));
		l_dept.setFont(user.myCustomFont.deriveFont(Font.PLAIN, 20));
		l_name.setFont(user.myCustomFont.deriveFont(Font.PLAIN, 20));

		// 로그인 입력 TextField
		t_name = new JTextField(10);
		t_grade = new JTextField(10);
		t_stuID = new JTextField(10);
		t_dept = new JTextField(10);

		String[] arr_grade = { "선택안함", " 1학년 ", " 2학년 ", " 3학년 ", " 4학년 " };
		cb_grade = new JComboBox<String>(arr_grade);

		String[] arr_dept = { "선택안함", "모바일소프트웨어공학", "웹공학", "빅데이터", "AI" };
		cb_dept = new JComboBox<String>(arr_dept);

		// 폰트 크기 설정
		t_grade.setFont(user.myCustomFont.deriveFont(Font.PLAIN, 15));
		t_stuID.setFont(user.myCustomFont.deriveFont(Font.PLAIN, 15));
		t_dept.setFont(user.myCustomFont.deriveFont(Font.PLAIN, 15));
		t_name.setFont(user.myCustomFont.deriveFont(Font.PLAIN, 15));

		// 라디오 버튼
		bGroup = new ButtonGroup();
		rb_mento = new JRadioButton("멘토");
		rb_menti = new JRadioButton("멘티");
		rb_mento.setBackground(lightBlue);
		rb_menti.setBackground(lightBlue);
		bGroup.add(rb_mento);
		bGroup.add(rb_menti);

		// 컴포넌트 부착
		p1.add(l_name);
		p1.add(t_name);
		p2.add(l_grade);
		p2.add(cb_grade);
		p3.add(l_dept);
		p3.add(cb_dept);
		p4.add(l_stuID);
		p4.add(t_stuID);
		p5.add(rb_mento);
		p5.add(rb_menti);

		// 패널 색상 설정
		p1.setBackground(lightBlue);
		p2.setBackground(lightBlue);
		p3.setBackground(lightBlue);
		p4.setBackground(lightBlue);
		p5.setBackground(lightBlue);

		p.add(p1, gbc);
		gbc.gridy++;
		p.add(p2, gbc);
		gbc.gridy++;
		p.add(p3, gbc);
		gbc.gridy++;
		p.add(p4, gbc);
		gbc.gridy++;
		p.add(p5, gbc);

		// 배경 색깔 및 기타 정보
		p.setBackground(new Color(153, 204, 255));

		return p;
	}

	// 멘토 or 멘티 설정
	private JPanel createLoginPanel() {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());

		b_login = new JButton("로그인");
		b_login.setSize(getPreferredSize());
		b_login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 멘토면
				if (rb_mento.isSelected()) {
					String gradeScore = JOptionPane.showInputDialog("비밀번호를 입력하세요.");

					// 비밀번호 입력 완료
					if (gradeScore.equals("cotalk")) {
						// 사용자를 멘토로 인식 -> ConnectToServer() -> buildMain()
						try {
							user.connectToServer(user);

							// 로그인 입력 정보 가져오기
							user.name = t_name.getText();
							user.grade = ((String) cb_grade.getSelectedItem()).trim();
							user.stuID = t_stuID.getText();
							user.dept = ((String) cb_dept.getSelectedItem()).trim();

							// user 정보 전송
							user.sendUserInfo(1);
						} catch (UnknownHostException e1) {
							System.out.println("호스트의 IP 주소를 찾을 수 없습니다.");
						} catch (IOException e1) {
							System.out.println("서버에 접속 할 수 없습니다.");
						}

						user.role=1;
						mentoGUI = new MentoGUI(user);
						user.login.setVisible(false);
					}

					// 성적이 기준을 충족하지 않을 때
					else {
						JOptionPane.showMessageDialog(p, "비밀번호가 틀렸습니다.");
					}

				}
				// 멘티면
				else if (rb_menti.isSelected()) {
					try {
						// 서버 접속
						user.connectToServer(user);

						// 로그인 입력 정보 가져오기
						user.name = t_name.getText();
						user.grade = ((String) cb_grade.getSelectedItem()).trim();
						user.stuID = t_stuID.getText();
						user.dept = ((String) cb_dept.getSelectedItem()).trim();

						// user 정보 전송
						user.sendUserInfo(2);
					} catch (UnknownHostException e1) {
						System.out.println("호스트의 IP 주소를 찾을 수 없습니다.");
					} catch (IOException e1) {
						System.out.println("서버에 접속 할 수 없습니다.");
					}
					
					user.role=2;
					mentiGUI = new MentiGUI(user);
					user.login.setVisible(false);
				}
				// 선택안함
				else {
					// 멘토, 멘티 체크 안함 -> 체크 필수 다이얼로그 띄움
					JOptionPane.showMessageDialog(p, "멘토/멘티 체크는 필수입니다.");
				}
			}
		});

		p.setBackground(lightBlue);
		p.add(b_login);

		return p;
	}
}
