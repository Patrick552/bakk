package at.jku.wall.xuggler.thread;

import java.awt.image.BufferedImage;

public class CamImage {
	public BufferedImage image;
	public long timeStamp;
	
	public CamImage (BufferedImage image, long timeStamp) {
		this.image = image;
		this.timeStamp = timeStamp;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
}
