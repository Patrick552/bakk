package at.jku.wall.xuggler.thread;

import static at.jku.wall.xuggler.impl.Helper.prepareForEncoding;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.awt.AWTException;
import java.awt.Point;

import at.jku.wall.xuggler.Settings;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class ScreenThread extends Thread {

	// public final LinkedBlockingQueue<ScreenImage> screenQueue = new
	// LinkedBlockingQueue<ScreenImage>();
	public volatile boolean abort = false;
	public long FPS;
	private final String dir;
	private final ScreenImage appshot;

	public static IMediaWriter writer;
	

	public ScreenThread(String dir, long FPS) throws AWTException {
		this.dir = dir;
		this.FPS = FPS;
		appshot = new ScreenImage(/*p1, p2*/);
	}

	public void run(){
		System.out.println("started recording screenshots");
		if(Settings.DEBUG_SCREEN) System.out.println("Writing to " + dir);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		writer = ToolFactory.makeWriter(dir);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		writer.addVideoStream(0, 1, ICodec.ID.CODEC_ID_H264, appshot.getBounds().width, appshot.getBounds().height);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		if(Settings.DEBUG_SCREEN) System.out.println("Screenshot stream open");
		
		long startTime = System.nanoTime();
		while (!abort) {
			for (int i=0; i<FPS; i++) {
				long ts = (System.nanoTime() - startTime);
				writer.encodeVideo(0, prepareForEncoding(appshot.getScreenShot()), ts, NANOSECONDS);
				try {
					Thread.sleep(1000 / FPS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(Settings.DEBUG_SCREEN) System.out.println("1 second Screenshots record");
		}
		writer.flush();
		writer.close();
		System.err.println("ScreenThread KILL:");
	}

	public boolean isAbort() {
		return abort;
	}

	public void setAbort(boolean abort) {
		this.abort = abort;
	}


	
	
}
