package at.jku.wall.xuggler.impl;

import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JTextField;

import at.jku.wall.xuggler.thread.EncodeThread;
import at.jku.wall.xuggler.thread.ScreenThread;
import at.jku.wall.xuggler.thread.ThreadAudio;
import at.jku.wall.xuggler.thread.ThreadCam;

import com.github.sarxos.webcam.Webcam;

public class RecImpl implements ActionListener {

	private static final long FPS = 15;

	private final JTextField CamFileDir;
	private final JTextField SkriptFileDir;
	private final JTextField ScreenRecordDir;
	private Webcam webcam;

	private JButton button;

	public Helper appShot = new Helper();

	// public static IMediaWriter writerCam;
	// public static IMediaWriter writerSkript;

	private volatile boolean abortFlag = false;
	private boolean initFlag = false;

	public RecImpl(JButton button, 
			JTextField SkriptFileDir,JTextField CamFileDir, JTextField ScreenRecordDir, Webcam webcam) throws AWTException {

		this.button = button;

		if (CamFileDir == null || SkriptFileDir == null) {
			System.out.println("Min. ein Verzeichniss ist null");
			System.exit(0);
		}
		if (webcam == null) {
			System.out.println("Webcam = NULL !?!?!");
			System.exit(0);
		}
		this.CamFileDir = CamFileDir;
		this.SkriptFileDir = SkriptFileDir;
		this.ScreenRecordDir = ScreenRecordDir;
		this.webcam = webcam;

		// eventuell pruefen ob *.mp4 file
	}

	public void startRecording() throws InterruptedException, AWTException {

		// System.out.println(CamFileDir.getText());

		// Open PDF File
		// if (Desktop.isDesktopSupported()) {
		// try {
		// if (!(SkriptFileDir.getText().equals(""))) {
		// File pdfFile = new File(SkriptFileDir.getText());
		// Desktop.getDesktop().open(pdfFile);
		// } else {
		// System.out.println("PDF ERROR");
		// }
		// } catch (IOException ex) {
		//
		// }
		// }

		// //////// Cam Record
		// ////////////////////////////////////////////////////
		
		CountDownLatch countdown = new CountDownLatch(2);
		

		// Cam Thread
		ThreadCam threadCam = new ThreadCam(webcam.getName(), webcam, countdown);

		// Audio Thread
		ThreadAudio threadAudio = new ThreadAudio(countdown);

		// Appshot thread
		ScreenThread threadScreen = new ScreenThread(ScreenRecordDir.getText(),
				20);

		// start recording all Images and write it to the Queue
		threadCam.start();
		// start audio record
		threadAudio.start();
		// starts appshots
		threadScreen.start();
		
//		Thread.sleep(1000L);
		
		


		// // XUGGLER preperation
		// writerCam = ToolFactory.makeWriter(CamFileDir.getText());
		//
		// Dimension size = WebcamResolution.QVGA.getSize();
		//
		// writerCam.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width,
		// size.height);
		// writerCam.addAudioStream(1, 0, ICodec.ID.CODEC_ID_AAC, 2, 44100);

		// Skript Record

		// writerSkript = ToolFactory.makeWriter(SkriptFileDir.getText() +
		// ".mp4");
		// writerSkript.addVideoStream(1, 0, ID.CODEC_ID_H264,
		// appShot.getAppSize().height, appShot.getAppSize().width);

		// /// OLD ////////////////////////////////////////////////
		// long startTime = System.nanoTime();
		//
		// // Record
		// while (!abortFlag) {
		// // FPS loop to get all pictures
		// for (int i = 0; i < FPS; i++) {
		//
		// // Video Record
		// writerCam.encodeVideo(0,
		// prepareForEncoding(threadCam.getCamQueue().take()),
		// System.nanoTime() - startTime, NANOSECONDS);
		//
		// //Audio Record
		// // IAudioSamples audiostream =
		// // Helper.customAudioStream(writerCam);
		// writerCam.encodeAudio(1,
		// threadAudio.getAudioQueue().peek().getSamples());
		//
		// // PDF Record
		// // writerSkript.encodeVideo(1,
		// // prepareForEncoding(appShot.getAppShot()),
		// // System.nanoTime() - startTime, NANOSECONDS);
		//
		// // Thread.sleep(1000L / FPS);
		// // System.out.println("Frame: " + i);
		//
		// }
		//
		// System.out.println("Next record");
		// // Thread.sleep(1000L / FPS);
		// }
		// //////////////////////////////////////////////////////////////

		// TRY //
		while (true) {
			if (abortFlag) {

				// Stop writing to Queue and stops Thread hard
				threadCam.setAbort(true);
				System.out.println("CamThread Stopped");
				threadAudio.setAbort(true);
				System.out.println("AudioThread Stopped");
				threadScreen.setAbort(true);
				System.out.println("ScreenThread Stopped");

				// waits for both Threads until they die
				System.out.println("Waiting for cam thread");
				threadCam.join();
				System.out.println("Waiting for audio thread");
				threadAudio.join();
				System.out.println("Waiting for Screen thread");
				threadScreen.join();
				
				
				System.out.println("Starting encode thread");
				EncodeThread encode = new EncodeThread(threadCam.getCamQueue(),
						threadAudio.getAudioQueue(), CamFileDir.getText());
				encode.run();
//				System.out.println("TEST");
//
//				encode.setAbort(true);
//				System.out.println("Set abort in encode true");
				break;
			}
			// Thread.yield();
		}

		// writerCam.flush();
		// writerCam.close();
		// webcam.close();
		// writerSkript.flush();
		// writerSkript.close();

	}

	public void actionPerformed(ActionEvent e) {
		if (!initFlag) {
			System.out.println("Start recording");
			abortFlag = false;

			Runnable r = new Runnable() {

				public void run() {
					try {
						startRecording();
						System.out.println("Recording has stopped!");
						System.exit(1);
						// close Webcam
						// webcam.close();
						// System.out.println("Webcam closed");

					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (AWTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			(new Thread(r)).start();

		} else {
			System.out.println("Stop recording");
			abortFlag = true;
		}
		initFlag = !initFlag;
	}
}
