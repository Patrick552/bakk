package at.jku.wall.xuggler;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class SelectionRect {

	public Point p1 = null;
	public Point p2 = null;

	public SelectionRect() {

		EventQueue.invokeLater(new Runnable() {
			public void run() {

				JFrame frame = new JFrame("select area");
				frame.setUndecorated(true);
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new BorderLayout());
				frame.add(new BackgroundPane());
				frame.setAlwaysOnTop(true);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public class BackgroundPane extends JPanel {
		
		private static final long serialVersionUID = 1L;
		private BufferedImage background;
		private Point pos1;
		private Point pos2;

		private SelectionPane selectionPane;

		public BackgroundPane() {
			try {
				Robot robot = new Robot();
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Rectangle bounds = new Rectangle(toolkit.getScreenSize());
				background = robot.createScreenCapture(bounds);
				// Robot bot = new Robot();
				// background =
				// bot.createScreenCapture(getScreenViewableBounds());
			} catch (AWTException ex) {
				// Unhandled
			}

			selectionPane = new SelectionPane();
			setLayout(null);
			this.add(selectionPane);

			MouseAdapter adapter = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					pos1 = e.getPoint();
					pos2 = null;
					selectionPane.setLocation(pos1);
					selectionPane.setSize(0, 0);
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					pos2 = e.getPoint();
					int width = pos2.x - pos1.x;
					int height = pos2.y - pos1.y;

					int x = pos1.x;
					int y = pos1.y;

					if (width < 0) {
						x = pos2.x;
						width *= -1;
					}
					if (height < 0) {
						y = pos2.y;
						height *= -1;
					}
					selectionPane.setBounds(x, y, width, height);
					selectionPane.revalidate();
					selectionPane.repaint();
				}

			};
			addMouseListener(adapter);
			addMouseMotionListener(adapter);

		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D graphic = (Graphics2D) g.create();
			graphic.drawImage(background, 0, 0, this);
			graphic.dispose();
		}

	}

	public class SelectionPane extends JPanel {

		private static final long serialVersionUID = 1L;
		private JButton button;
		private JLabel label;

		public SelectionPane() {
			button = new JButton("ACCEPT");
			setOpaque(false);

			label = new JLabel("Rectangle");
			label.setOpaque(true);
			label.setBorder(new EmptyBorder(4, 4, 4, 4));
			label.setBackground(Color.LIGHT_GRAY);
			label.setForeground(Color.WHITE);
			setLayout(new GridBagLayout());

			GridBagConstraints temp = new GridBagConstraints();
			temp.gridx = 0;
			temp.gridy = 0;
			add(label, temp);
			temp.gridy = 1;
			add(button, temp);

			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SwingUtilities.getWindowAncestor(SelectionPane.this)
							.dispose();
					Point temp1 = new Point(getX(), getY());
					Point temp2 = new Point(getWidth(), getHeight());

					// Koordinaten müssen gerade sein für Rectangle beim
					// ScreenImage
					if ((temp1.x % 2) == 1) {
						temp1.x = temp1.x + 1;
					}
					if ((temp1.y % 2) == 1) {
						temp1.y = temp1.y + 1;
					}
					if ((temp2.x % 2) == 1) {
						temp2.x = temp2.x + 1;
					}
					if ((temp2.y % 2) == 1) {
						temp2.y = temp2.y + 1;
					}

					finalGui.p1 = temp1;
					finalGui.p2 = temp2;

					System.out.println("Point1: " + finalGui.p1.toString()
							+ "Point2: " + finalGui.p2.toString());

					finalGui.frame.setState(Frame.NORMAL);
				}
			});

			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					label.setText("Rectangle: " + getX() + " : " + getY()
							+ " | " + getWidth() + " : " + getHeight());
				}
			});

		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D graphic = (Graphics2D) g.create();
			graphic.setColor(new Color(128, 128, 128, 64));
			graphic.fillRect(0, 0, getWidth(), getHeight());
			graphic.drawRect(0, 0, getWidth(), getHeight());
			graphic.dispose();
		}

	}

	public Point getP1() {
		return p1;
	}

	public Point getP2() {
		return p2;
	}

}
