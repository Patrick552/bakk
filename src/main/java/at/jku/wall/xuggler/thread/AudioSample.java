package at.jku.wall.xuggler.thread;

import com.xuggle.xuggler.IAudioSamples;

public class AudioSample {
	
	public long timeStamp;
	public IAudioSamples samples;
	
	public AudioSample (IAudioSamples samples, long timeStamp) {
		this.samples = samples;
		this.timeStamp = timeStamp;
	}
	
	public long getTimpStamp() {
		return timeStamp;
	}
	public void setTimpStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public IAudioSamples getSamples() {
		return samples;
	}
	public void setSamples(IAudioSamples samples) {
		this.samples = samples;
	}

}
