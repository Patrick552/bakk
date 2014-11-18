package at.jku.wall.xuggler.thread;

import java.awt.image.BufferedImage;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.sarxos.webcam.Webcam;

public class ThreadCam extends Thread {

	public LinkedBlockingQueue<CamImage> camQueue = new LinkedBlockingQueue<CamImage>();
	public Webcam webcam;
	public boolean abort = false;

	public ThreadCam(String name, Webcam webcam) {
		super(name);
		this.webcam = webcam;
	}

	// Collects all Images from the Cam and saves it an a Queue
	public void run() {
		int i = 1;
		
		//webcam.open(true);
		long startTime = System.nanoTime();
		while (!abort) {
			long ts = System.nanoTime() - startTime;
			BufferedImage bImg = webcam.getImage();
			CamImage image = new CamImage(bImg, ts);

			try {
				camQueue.put(image);
				
				System.out.println("frame: " + i + " to queue und Zeit: " + image.timeStamp);
				i++;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		System.out.println("CamThread Kill");
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
