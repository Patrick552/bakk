package at.jku.wall.xuggler.thread;

import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IAudioSamples.Format;

public class ThreadAudio extends Thread {

	public final LinkedBlockingQueue<AudioSample> audioQueue = new LinkedBlockingQueue<AudioSample>();
	public volatile boolean abort = false;

	public ThreadAudio() {

	}

	// Collects all AudioSamples from the Microphone and saves it in a Queue
	public void run() {
		int i = 1;
		while (!abort) {
			IAudioSamples temp = customAudioStream();
			AudioSample sample = new AudioSample(temp, System.nanoTime());
			audioQueue.add(sample);
			System.out.println("Audio Sample " + i + " saved");
			i++;
			// sleep (??);
		}
		System.err.println("AudioThread KILL: ");
	}

	public IAudioSamples customAudioStream(/*IMediaWriter writerCam*/) {
		int audioTime = 0;

		// audio parameters
		int channelCount = 2;
		int sampleRate = 44100;

//		IContainer container = writerCam.getContainer();
//		IStream stream = container.getStream(1);
//		int sampleCount = stream.getStreamCoder().getDefaultAudioFrameSize();

		// create a place for audio samples
		IAudioSamples samples = IAudioSamples.make(1024 * 5, channelCount,
				IAudioSamples.Format.FMT_S16);
		// setup code
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
		// end setup code
		
		byte[] data = new byte[4096 * 5];
		int sz = line.read(data, 0, data.length);

		samples.put(data, 0, 0, sz);
		// samples.setPts(aValue); hier noch starttime - system.nanotime einbauen
		audioTime += (sz);

		// double sAudioTime = (audioTime) / 44.1000;

		samples.setComplete(true, sz / 4, sampleRate, channelCount,
				Format.FMT_S16, audioTime / 4);

		return (samples);
	}

	public LinkedBlockingQueue<AudioSample> getAudioQueue() {
		return audioQueue;
	}

	public boolean getAbort() {
		return abort;
	}

	public void setAbort(boolean abort) {
		this.abort = abort;
	}
}
