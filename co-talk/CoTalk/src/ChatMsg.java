import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import javax.swing.ImageIcon;

public class ChatMsg implements Serializable{
	String name=""; // 이름
	String grade=""; // 학년
	String stuID=""; // 학번
	String dept=""; // 학과
	String mentoID=""; // 멘토 아이디
	String mentiID=""; // 멘티 아이디
	int mode; // 전송 모드
	
	Point p;
	String message;
	ImageIcon image;
	int point;
	int role; //role=1 멘토 role=2 멘티
	long size;
	Color color;
	double roomNumber;
	int flag=0;
	
	public final static int MODE_LOGIN1=0x01; // 멘토 로그인
	public final static int MODE_LOGIN2=0x02; // 멘티 로그인
	public final static int MODE_LOGOUT=0x03; // 로그아웃
	public final static int MODE_TX_STRING=0x10;  // 메시지
	public final static int MODE_TX_IMAGE=0x40; // 이미지
	public final static int MODE_TX_FILE=0x50; // 파일
	public final static int MODE_TX_POINT=0x60; // 포인트
	public final static int MODE_TX_CREATEROOM = 0x70; // 채팅방생성 (멘토 -> 멘티)
	public final static int MODE_Q = 0x80; // 채팅요청 (멘티 -> 멘토)
	public final static int MODE_REMOVE = 0x81; // 채팅요청삭제 (멘티 -> 멘토)
	public final static int MODE_DRAW = 0x91;//실시간 그림 전송
	
	// 로그인
	public ChatMsg(String grade,String stuID,String dept,String name,int mode) {
		this.grade=grade; // 학년
		this.stuID=stuID; // 학번
		this.dept=dept; // 학과
		this.name=name; // 이름
		this.mode=mode; //모드 ( 로그인 모드 )
	}
	
	// 기본 객체 (로그아웃)
	public ChatMsg(String stuID, int mode) { 
		this.stuID=stuID;
		this.mode=mode;
	}
	
	// 채팅방생성 
	public ChatMsg(String mentoID,String mentiID,int mode,double roomNumber) {
		this.mentoID=mentoID;
		this.mentiID=mentiID;
		this.mode=mode;
		this.roomNumber=roomNumber;
	}
	
	// 채팅요청 OR 채팅요청삭제
	public ChatMsg(String mentoID, String mentiID, int mode) {
		this.mentoID=mentoID;
		this.mentiID=mentiID;
		this.mode=mode;
	}
	
	// 메시지
	public ChatMsg(String stuID, int mode, String message,double roomNumber) {
		this.stuID=stuID;
		this.mode=mode;
		this.message=message;
		this.roomNumber = roomNumber;
	}
	
	// 이미지
	public ChatMsg(String stuID,int mode, String message,ImageIcon image,double roomNumber) {
		this(stuID,mode,message,roomNumber);
		this.image=image;
	}
	
	// 포인트
	public ChatMsg(String stuID,int mode, String message, int point,double roomNumber) {
		this.stuID=stuID;
		this.mode=mode;
		this.message=message;
		this.point=point;
		this.roomNumber=roomNumber;
	}

	// 파일
	public ChatMsg(String stuID, int mode,String filename, long size, double roomNumber){
		this.stuID=stuID;
		this.mode=mode;
		this.message=filename;
		this.size=size;
		this.roomNumber=roomNumber;
	}
	
	// 그림판
	public ChatMsg(String stuID,int mode, Point p, Color color,double roomNumber,int flag){
		this.stuID=stuID;
		this.mode=mode;
		this.p=p;
		this.color=color;
		this.roomNumber=roomNumber;
		this.flag=flag;
	}
}
		

