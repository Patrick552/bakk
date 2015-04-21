package at.jku.wall.xuggler.impl;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import at.jku.wall.xuggler.thread.CamImage;


public class Helper {

	public static Robot robot;
	public static Toolkit toolkit;
	public static Rectangle skriptSize;

	public Helper() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		toolkit = Toolkit.getDefaultToolkit();

		skriptSize = new Rectangle(toolkit.getScreenSize());
	}

	public static BufferedImage convertToType(BufferedImage sourceImage,
			int targetType) {
		BufferedImage image;
		if (sourceImage.getType() == targetType) {
			image = sourceImage;
		} else {
			image = new BufferedImage(sourceImage.getWidth(),
					sourceImage.getHeight(), targetType);
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
		}
		return image;
	}

	public static BufferedImage prepareForEncoding(BufferedImage sourceImage) {
		return Helper.convertToType(sourceImage, BufferedImage.TYPE_3BYTE_BGR);
	}
	
	// Test for Thread
	public static BufferedImage prepareForEncoding(CamImage sourceImage) {
		return Helper.convertToType(sourceImage.getImage(), BufferedImage.TYPE_3BYTE_BGR);
	}

	/////////// FOR APPSHOT ///////////////
	public BufferedImage getAppShot() {

		return robot.createScreenCapture(skriptSize);
	}
	public Rectangle getAppSize() {
		return skriptSize;
	}
	///////////////////////////////////////

}
