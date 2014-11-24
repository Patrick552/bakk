package at.jku.wall.xuggler.thread;

import static at.jku.wall.xuggler.impl.Helper.prepareForEncoding;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.awt.Dimension;
import java.util.concurrent.LinkedBlockingQueue;

import at.jku.wall.xuggler.thread.AudioSample;
import at.jku.wall.xuggler.thread.CamImage;

import com.github.sarxos.webcam.WebcamResolution;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class EncodeThread extends Thread {

	public LinkedBlockingQueue<CamImage> camQueue = new LinkedBlockingQueue<CamImage>();
	public LinkedBlockingQueue<AudioSample> audioQueue = new LinkedBlockingQueue<AudioSample>();
	public String CamFileDir;

	public static IMediaWriter writerCam;
	
	public boolean abort = false;

	public EncodeThread(LinkedBlockingQueue<CamImage> camQueue,
			LinkedBlockingQueue<AudioSample> audioQueue, String CamFileDir) {
		this.camQueue = camQueue;
		this.audioQueue = audioQueue;
		this.CamFileDir = CamFileDir;
	}

	public void run() {

		// XUGGLER preperation
		writerCam = ToolFactory.makeWriter(CamFileDir);

		Dimension size = WebcamResolution.QVGA.getSize();

		writerCam.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width,
				size.height);
		writerCam.addAudioStream(1, 0, ICodec.ID.CODEC_ID_AAC, 2, 44100);
		// andere Abbruchbedingung
		
		while (!abort) {
			System.out.println("CamQueue has " + camQueue.size() + " Elements");
			try {
				CamImage image = camQueue.take();
				long ts = image.getTimeStamp();
				System.err.println("" + (ts));
				writerCam.encodeVideo(0, prepareForEncoding(image.getImage()),
						ts, NANOSECONDS);

				if(!audioQueue.isEmpty()) {
					AudioSample sample = audioQueue.take();
					writerCam.encodeAudio(1, sample.getSamples());
				}
				if (camQueue.isEmpty() && audioQueue.isEmpty()) {
					abort=true;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}

		writerCam.flush();
		writerCam.close();
		
		System.out.println("File Encoded !");
	}

}
