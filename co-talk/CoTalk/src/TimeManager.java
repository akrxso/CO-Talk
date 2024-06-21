import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class TimeManager {
	CoTalkRoom room;
	User user;
	Calendar now = Calendar.getInstance();
	int hour = now.get(Calendar.HOUR_OF_DAY);
	int min = now.get(Calendar.MINUTE);
	int sec = now.get(Calendar.SECOND);

	HashMap<String, List<Integer>> mentoTime = new HashMap<>();

	// 멘토 리스트 갱신 전용 생성자
	public TimeManager(User user) {
		this.user = user;
	}

	// 채팅방 타이머 사용 전용 생성자
	public TimeManager(User user, CoTalkRoom room) {
		this(user);
		this.room = room;
	}

	public void nowMento() { // 멘토 리스트 갱신 메소드

		mentoTime.put("박지윤_2071041", Arrays.asList(11, 18));
		mentoTime.put("김서현_2091052", Arrays.asList(11, 18));
		mentoTime.put("이찬수_1101**", Arrays.asList(11, 24));
		mentoTime.put("황기태_5113**", Arrays.asList(11, 24));
		mentoTime.put("김우현_1991143", Arrays.asList(14, 18));
		mentoTime.put("김현서_2071021", Arrays.asList(13, 14));

		for (String s : mentoTime.keySet()) { // 멘토 조회
			for (int j = mentoTime.get(s).get(0); j <= mentoTime.get(s).get(1); j++) { // 멘토링 시작 시간 ~ 끝나는시간 -1
				if (j == hour) {
					// 지금 시간이 멘토링 시간에 포함 + 벡터에 값이 없으면 => add
					user.vMento.add(s);
				}
			}
		}

		TimerThread th = new TimerThread(this, 1);
		th.start();
	}

	public void boom() { // 채팅방 일정시간 이후 삭제 메소드 (채팅방 타이머)

		TimerThread th = new TimerThread(this, 2);
		th.timerGUI = new TimerGUI(user);
		th.start();

	}
}

// 타이머 쓰레드
class TimerThread extends Thread {
	TimeManager time;
	TimerGUI timerGUI;
	int mode = 0;
	int x = 1;
	int y = 0;

	TimerThread(TimeManager timeManager, int mode) {
		this.time = timeManager;
		this.mode = mode;
	}

	@Override
	public void run() {
		int n = 0;

		// 멘토 리스트 갱신 메소드
		while (mode == 1) {
			time.now = Calendar.getInstance();
			time.hour = time.now.get(Calendar.HOUR_OF_DAY);
			time.min = time.now.get(Calendar.MINUTE);
			time.sec = time.now.get(Calendar.SECOND);

			for (String s : time.mentoTime.keySet()) { // 멘토 리스트 조회
				if (time.hour == time.mentoTime.get(s).get(1)) { // 지금 시간 == 멘토별 끝나는 시간-1
					for (n = 0; n < time.user.vMento.size(); n++) // 벡터에 넣어져있음
					{
						if (time.user.vMento.get(n) == s.toString()) { // 해당하는 멘토 index 집어넣어서 삭제
							time.user.vMento.remove(n);
						}
					}
				}
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
			n = 0;
		}

		// 타이머 모드
		while (mode == 2) {

			int a = 60 - (y + 1);

			if (y == 60) {
				time.room.setVisible(false);
				timerGUI.setVisible(false);
				return;
			}
			timerGUI.label.setText("남은 시간 " + a + "분");

			try {
				Thread.sleep(1000);
				x++;
				if (x == 60) {
					y++;
					x = 1;
				}
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
