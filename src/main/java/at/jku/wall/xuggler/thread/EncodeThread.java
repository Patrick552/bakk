package at.jku.wall.xuggler.thread;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.sarxos.webcam.WebcamResolution;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class EncodeThread extends Thread {

	public final LinkedBlockingQueue<CamImage> camQueue;
	public final LinkedBlockingQueue<AudioSample> audioQueue;
	public final String CamFileDir;

	public static IMediaWriter writerCam;
	

	public EncodeThread(LinkedBlockingQueue<CamImage> camQueue,
			LinkedBlockingQueue<AudioSample> audioQueue, String CamFileDir) {
		this.camQueue = camQueue;
		this.audioQueue = audioQueue;
		this.CamFileDir = CamFileDir;
	}

	public void run() {
		boolean abort = false;

		// XUGGLER preperation
		writerCam = ToolFactory.makeWriter(CamFileDir);

		Dimension size = WebcamResolution.QVGA.getSize();

		writerCam.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width,
				size.height);
		writerCam.addAudioStream(1, 0, ICodec.ID.CODEC_ID_AAC, 2, 44100);
		
		while (!abort) {
			System.out.println("CamQueue has " + camQueue.size() + " Elements");
			try {
				CamImage image = camQueue.take();
				long ts = image.getTimeStamp();
				BufferedImage img = image.getImage();
				System.err.println("" + (ts) + ": "/* + img.hashCode()*/);
				writerCam.encodeVideo(0, img, ts, NANOSECONDS);

				if(!audioQueue.isEmpty()) {
					AudioSample sample = audioQueue.take();
					long audioTs = sample.getTimpStamp();
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
