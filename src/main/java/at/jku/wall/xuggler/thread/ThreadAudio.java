package at.jku.wall.xuggler.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import at.jku.wall.xuggler.Settings;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IAudioSamples.Format;

public class ThreadAudio extends Thread {

	public final LinkedBlockingQueue<AudioSample> audioQueue = new LinkedBlockingQueue<AudioSample>();
	public volatile boolean abort = false;
	// public boolean setUpDone = false;
	public long startTime = 0;
	public CountDownLatch countdown;

	public ThreadAudio(CountDownLatch countdown) {
		this.countdown = countdown;
	}

	// Collects all AudioSamples from the Microphone and saves it in a Queue
	public void run() {
		int i = 1;

		// Done before because the setupCode needs to much time for live
		// recording
		IAudioSamples temp = customAudioStream();

		try {
			countdown.countDown();
			countdown.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Started recording audio at " + System.nanoTime());
		startTime = System.nanoTime();
		while (!abort) {
			temp = customAudioStream();
			AudioSample sample = new AudioSample(temp, System.nanoTime()
					- startTime);
			audioQueue.add(sample);
			if (Settings.DEBUG_AUDIO)
				System.out.println("Audio Sample " + i + " saved by: "
						+ sample.getTimpStamp());
			i++;
		}
		System.err.println("AudioThread KILL: ");
	}

	public IAudioSamples customAudioStream() {
		long audioTime = 0;

		// audio parameters
		int channelCount = 2;
		int sampleRate = 44100;

		// create new audio samples buffer
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

		audioTime = audioTime + sz;
		// audioTime = System.nanoTime() - audioTime;

		// hier noch starttime - system.nanotime einbauen
		samples.setPts(System.nanoTime() - startTime);

		samples.setComplete(true, sz / 4, sampleRate, channelCount,
				Format.FMT_S16, audioTime / 4);

		return (samples);
	}

	// public short[] custiomAudioStreamArray () throws LineUnavailableException
	// {
	// // Pick a format. Need 16 bits, the rest can be set to anything
	// // It is better to enumerate the formats that the system supports,
	// because getLine() can error out with any particular format
	// AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);
	//
	// // Get default TargetDataLine with that format
	// DataLine.Info dataLineInfo = new DataLine.Info( TargetDataLine.class,
	// audioFormat );
	// TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
	//
	// // Open and start capturing audio
	// line.open(audioFormat, line.getBufferSize());
	// line.start();
	//
	// while (true) {
	// // read as raw bytes
	// byte[] audioBytes = new byte[ line.getBufferSize() / 2 ]; // best size?
	// int numBytesRead = 0;
	// numBytesRead = line.read(audioBytes, 0, audioBytes.length);
	//
	// // convert to signed shorts representing samples
	// int numSamplesRead = numBytesRead / 2;
	// short[] audioSamples = new short[ numSamplesRead ];
	// if (audioFormat.isBigEndian()) {
	// for (int i = 0; i < numSamplesRead; i++) {
	// audioSamples[i] = (short)((audioBytes[2*i] << 8) | audioBytes[2*i + 1]);
	// }
	// }
	// else {
	// for (int i = 0; i < numSamplesRead; i++) {
	// audioSamples[i] = (short)((audioBytes[2*i + 1] << 8) | audioBytes[2*i]);
	// }
	// }
	//
	// // use audioSamples in Xuggler etc
	// }
	// }

	/*
	 * public short[] customAudioStreamArray() { int audioTime = 0;
	 * 
	 * // audio parameters int channelCount = 2; int sampleRate = 44100;
	 * 
	 * // create a place for audio samples IAudioSamples samples =
	 * IAudioSamples.make(1024 * 5, channelCount, IAudioSamples.Format.FMT_S16);
	 * // setup code TargetDataLine line = null; AudioFormat audioFormat = new
	 * AudioFormat(sampleRate, (int) 16, channelCount, true, false);
	 * DataLine.Info info = new DataLine.Info(TargetDataLine.class,
	 * audioFormat); try { line = (TargetDataLine) AudioSystem.getLine(info);
	 * line.open(audioFormat); } catch (LineUnavailableException ex) {
	 * System.out.println("ERROR: " + ex.toString()); }
	 * 
	 * line.start(); // end setup code
	 * 
	 * byte[] data = new byte[4096 * 5]; int sz = line.read(data, 0,
	 * data.length);
	 * 
	 * samples.put(data, 0, 0, sz); // samples.setPts(aValue); hier noch
	 * starttime - system.nanotime einbauen audioTime += (sz);
	 * 
	 * // double sAudioTime = (audioTime) / 44.1000;
	 * 
	 * samples.setComplete(true, sz / 4, sampleRate, channelCount,
	 * Format.FMT_S16, audioTime / 4);
	 * 
	 * return (samples); }
	 */
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
