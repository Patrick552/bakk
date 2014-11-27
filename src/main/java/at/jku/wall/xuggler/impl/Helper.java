package at.jku.wall.xuggler.impl;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import at.jku.wall.xuggler.thread.CamImage;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;

// Test Kopie von UE6 @matthiasSteinbauer

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

		// Aktuell noch ganzer Screen, wie bekommt an die abmessung der
		// aktuellen APP ?
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
