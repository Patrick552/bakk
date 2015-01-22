package at.jku.wall.xuggler.thread;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import at.jku.wall.xuggler.finalGui;

public class ScreenImage {

	private Robot robot;
	private Toolkit toolkit;
	private Rectangle bounds;

	public ScreenImage(/* Point p1, Point p2 */) throws AWTException {

		this.robot = new Robot();
		this.toolkit = Toolkit.getDefaultToolkit();

		// Select Area wurde nicht durchgeführt
		if (finalGui.p1 == null || finalGui.p2 == null) {
			this.bounds = new Rectangle(toolkit.getScreenSize());
		} else {
			this.bounds = new Rectangle(finalGui.p1.x, finalGui.p1.y,
					finalGui.p2.x, finalGui.p2.y);
		}
	}

	public void setBounds(int x, int y, int width, int height) {
		bounds.setBounds(x, y, width, height);
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public BufferedImage getScreenShot() {
		return robot.createScreenCapture(bounds);
	}

}
