package at.jku.wall.xuggler.thread;

import static at.jku.wall.xuggler.impl.Helper.prepareForEncoding;

import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import at.jku.wall.xuggler.Settings;

import com.github.sarxos.webcam.Webcam;

public class ThreadCam extends Thread {

	public final LinkedBlockingQueue<CamImage> camQueue = new LinkedBlockingQueue<CamImage>();
	public final Webcam webcam;
	public volatile boolean abort = false;
	public CountDownLatch countdown;

	public ThreadCam(String name, Webcam webcam, CountDownLatch countdown) {
		super(name);
		this.webcam = webcam;
		this.countdown = countdown;
	}

	// Collects all Images from the Cam and saves it an a Queue
	public void run() {
		
		//webcam.open(true);
		try {
			countdown.countDown();
			countdown.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("started recording video from cam at " + System.nanoTime());
		long startTime = System.nanoTime();
		while (!abort) {
			long ts = (System.nanoTime() - startTime);
			BufferedImage bImg = webcam.getImage(); 
			// prepare... gleich hier damit man gleich auf Img zugreift damit das Image verwendet wird
			// wenn man erst später beim encoden auf das object zugreift wird nicht das gespeicherte sondern das
			// aktuelle Bild genommen
			CamImage image = new CamImage(prepareForEncoding(bImg), ts);
//			System.out.println(bImg.getClass().getName());

			try {
				camQueue.put(image);
				if(Settings.DEBUG_CAM) System.out.println("frame: " + camQueue.size() + " to queue und Zeit: " + image.timeStamp);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		System.err.println("CamThread Kill: ");
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			Thread.currentThread().interrupt();
//		}
	}

	public LinkedBlockingQueue<CamImage> getCamQueue() {
		return camQueue;
	}

	public boolean getAbort() {
		return abort;
	}

	public void setAbort(boolean abort) {
		this.abort = abort;
	}

}
