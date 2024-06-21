import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;

class TimerGUI extends JFrame
{
	public JLabel label;
	User user;
	
	public TimerGUI(User user) {
		super("Timer : "+user.stuID);
		this.user=user;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		label = new JLabel();
		label.setText(getName());
		label.setFont(user.myCustomFont.deriveFont(Font.PLAIN, 25));
		label.setHorizontalAlignment(JLabel.CENTER);
		add(label, BorderLayout.CENTER);
		setSize(300,100);
		setLocation(1240,0);
		setVisible(true);
	}
}