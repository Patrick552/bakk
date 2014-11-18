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
	
	
	
	public static int audioTime = 0;

	public static IAudioSamples customAudioStream(IMediaWriter writerCam) {

		// audio parameters
		int channelCount = 2;
		int sampleRate = 44100;

		IContainer container = writerCam.getContainer();
		IStream stream = container.getStream(1);
		int sampleCount = stream.getStreamCoder().getDefaultAudioFrameSize();

		// create a place for audio samples
		IAudioSamples samples = IAudioSamples.make(1024 * 5, channelCount,
				IAudioSamples.Format.FMT_S16);

		TargetDataLine line = null;
		AudioFormat audioFormat = new AudioFormat(sampleRate, (int) 16,
				channelCount, true, false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class,
				audioFormat);
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(audioFormat);
		} catch (LineUnavailableException ex) {
			System.out.println("ERROR: " + ex.toString());
		}

		line.start();
		byte[] data = new byte[4096 * 5];
		int sz = line.read(data, 0, data.length);

		samples.put(data, 0, 0, sz);
		audioTime += (sz);

//		double sAudioTime = (audioTime) / 44.1000;

		samples.setComplete(true, sz / 4, sampleRate, channelCount,
				Format.FMT_S16, audioTime / 4);

		return (samples);
	}
}
