import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DrawGUI {

   private int x = 0, y = 0;
   private JFrame draw;
   public DrawPanel drawPanel;
   private JButton p_pen, p_circle, p_rect, line;
   private JButton c_red, c_orange, c_yello, c_green, c_cyan, c_blue, c_magenta, c_pink, c_black, c_white;
   private JLabel colorLabel;
   private JSlider[] sl = new JSlider[3];

   public Point startP, endP;
   public Color currentColor = Color.BLACK;

   User user;

   DrawGUI(User user) {
      this.user = user;
   }

   public void buildImageGUI() {
      draw = new JFrame("Co-Draw");
      draw.setSize(550, 650);
      draw.setLocation(400, 0);
      draw.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      draw.add(createDrawPanel(), BorderLayout.CENTER);
      draw.add(createPenPanel(), BorderLayout.EAST);
      draw.add(createColorPanel(), BorderLayout.SOUTH);
      draw.setVisible(true); // 가시화 처리
   }

   private JPanel createDrawPanel() {

      JPanel p = new JPanel();
      p.setLayout(new BorderLayout());
      drawPanel = new DrawPanel();
      p.add(drawPanel, BorderLayout.CENTER);
      p.setFocusable(true);
      p.requestFocus();

      return p;
   }

   class DrawPanel extends JPanel {
      public Vector<Point> vStart = new Vector<Point>(); // Point만 저장하는 제네릭 벡터
      public Vector<Point> vEnd = new Vector<Point>();
      int mode=0; // 0은 자기 자신, 1은 다른 유저

      public DrawPanel() {
         addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) { // 드래그 할 때
               Point startP = e.getPoint();
               vStart.add(startP);
               user.send(new ChatMsg(user.stuID, ChatMsg.MODE_DRAW, startP, currentColor, user.room.roomNumber,1));
               repaint();
            }

         });
         addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { // 마우스 버튼 눌렀을 때
               Point startP = e.getPoint();
               vStart.add(startP);
               user.send(new ChatMsg(user.stuID, ChatMsg.MODE_DRAW, startP, currentColor, user.room.roomNumber,0));
            }

            @Override
            public void mouseReleased(MouseEvent e) { // 마우스 버튼 뗐을 때
               Point endP = e.getPoint();
               vEnd.add(endP);
               user.send(new ChatMsg(user.stuID, ChatMsg.MODE_DRAW, endP, currentColor, user.room.roomNumber,1));
               repaint();
            }
         });
      }

      public void paintComponent(Graphics g) {
         g.setColor(currentColor);

         if(vStart.size()>=2) {
         g.drawLine((int) vStart.get(vStart.size() - 2).getX(), (int) vStart.get(vStart.size() - 2).getY(),
               (int) vStart.get(vStart.size() - 1).getX(), (int) vStart.get(vStart.size() - 1).getY());

         }
      }
   }

   private JPanel createPenPanel() {
      JPanel p = new JPanel();
      p_pen = new JButton("펜");
      p.add(p_pen);

      return p;
   }

   private JPanel createColorPanel() {
      JPanel p = new JPanel(new GridLayout(3, 5));
      c_red = new JButton();
      c_red.setBackground(Color.RED);
      c_red.setOpaque(true);
      c_red.setBorderPainted(false);
      c_red.addMouseListener(new ColorMouseListener(Color.RED));

      c_orange = new JButton();
      c_orange.setBackground(Color.ORANGE);
      c_orange.setOpaque(true);
      c_orange.setBorderPainted(false);
      c_orange.addMouseListener(new ColorMouseListener(Color.ORANGE));

      c_yello = new JButton();
      c_yello.setBackground(Color.YELLOW);
      c_yello.setOpaque(true);
      c_yello.setBorderPainted(false);
      c_yello.addMouseListener(new ColorMouseListener(Color.YELLOW));

      c_green = new JButton();
      c_green.setBackground(Color.GREEN);
      c_green.setOpaque(true);
      c_green.setBorderPainted(false);
      c_green.addMouseListener(new ColorMouseListener(Color.GREEN));

      c_cyan = new JButton();
      c_cyan.setBackground(Color.CYAN);
      c_cyan.setOpaque(true);
      c_cyan.setBorderPainted(false);
      c_cyan.addMouseListener(new ColorMouseListener(Color.CYAN));

      c_blue = new JButton();
      c_blue.setBackground(Color.BLUE);
      c_blue.setOpaque(true);
      c_blue.setBorderPainted(false);
      c_blue.addMouseListener(new ColorMouseListener(Color.BLUE));

      c_magenta = new JButton();
      c_magenta.setBackground(Color.MAGENTA);
      c_magenta.setOpaque(true);
      c_magenta.setBorderPainted(false);
      c_magenta.addMouseListener(new ColorMouseListener(Color.MAGENTA));

      c_pink = new JButton();
      c_pink.setBackground(Color.PINK);
      c_pink.setOpaque(true);
      c_pink.setBorderPainted(false);
      c_pink.addMouseListener(new ColorMouseListener(Color.PINK));

      c_black = new JButton();
      c_black.setBackground(Color.BLACK);
      c_black.setOpaque(true);
      c_black.setBorderPainted(false);
      c_black.addMouseListener(new ColorMouseListener(Color.BLACK));

      c_white = new JButton();
      c_white.setBackground(Color.WHITE);
      c_white.setOpaque(true);
      c_white.setBorderPainted(false);
      c_white.addMouseListener(new ColorMouseListener(Color.WHITE));

      p.add(c_red);
      p.add(c_orange);
      p.add(c_yello);
      p.add(c_green);
      p.add(c_cyan);
      p.add(c_blue);
      p.add(c_magenta);
      p.add(c_pink);
      p.add(c_black);
      p.add(c_white);

      colorLabel = new JLabel("SLIDER");
      for (int i = 0; i < sl.length; i++) {
         sl[i] = new JSlider(JSlider.HORIZONTAL, 0, 255, 128);
         sl[i].setPaintLabels(true);
         sl[i].setPaintTicks(true);
         sl[i].setPaintTrack(true);
         sl[i].setMajorTickSpacing(100);
         sl[i].setMinorTickSpacing(50);
         sl[i].addChangeListener(new MyChangeListener());
         p.add(sl[i]);
      }
      sl[0].setForeground(Color.red);
      sl[1].setForeground(Color.green);
      sl[2].setForeground(Color.blue);

      int r = sl[0].getValue();
      int g = sl[1].getValue();
      int b = sl[2].getValue();

      return p;
   }

   private class ColorMouseListener extends MouseAdapter {
      private Color color;

      public ColorMouseListener(Color color) {
         this.color = color;
      }

      @Override
      public void mouseClicked(MouseEvent e) {
         currentColor = color;
      }
   }

   private class MyChangeListener implements ChangeListener {
      public void stateChanged(ChangeEvent e) {
         int r = sl[0].getValue();
         int g = sl[1].getValue();
         int b = sl[2].getValue();
         currentColor = new Color(r, g, b);
      }
   }

}