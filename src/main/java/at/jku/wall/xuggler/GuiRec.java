package at.jku.wall.xuggler;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import at.jku.wall.xuggler.impl.RecImpl;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class GuiRec extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField skript;
	private JTextField fileCamName;
	private JTextField fileSkriptName;

	private JButton recordButton = null;

	public Webcam webcam = Webcam.getDefault();
	
	public static JFrame recFrame = new JFrame();

	public GuiRec() {
		initUi();
		initWebCam();
	}

	private void initUi() {
		// final JFrame recFrame = this;
		recFrame.setTitle("LVA Tool");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.LINE_AXIS));

		skript = new JTextField();
		skript.setColumns(50);

		Box inputBox = Box.createVerticalBox();
		Box fileBox = Box.createHorizontalBox();
		Box saveSkriptBox = Box.createHorizontalBox();
		Box saveCamBox = Box.createHorizontalBox();

		JButton SkriptDirButton = new JButton("search");
		JButton FileCamDirButon = new JButton("search");
		JButton FileSkriptDirButton = new JButton("search");

		// Prepare fileBox
		fileBox.add(Box.createHorizontalStrut(10));
		fileBox.add(new JLabel("Skriptum"));
		fileBox.add(Box.createHorizontalStrut(10));
		fileBox.add(skript);
		fileBox.add(Box.createHorizontalStrut(10));
		fileBox.add(SkriptDirButton);
		SkriptDirButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "PDF Files";
					}

					@Override
					public boolean accept(File f) {
						String str = f.getName();
						return str.endsWith(".pdf");
					}
				});
				if (fc.showOpenDialog(recFrame) == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					skript.setText(f.getAbsolutePath());
				}
			}
		});

		// Prepare saveCamBox
		fileCamName = new JTextField();
		fileCamName.setColumns(50);
		saveCamBox.add(Box.createHorizontalStrut(10));
		saveCamBox.add(new JLabel("Save Cam cast:"));
		saveCamBox.add(Box.createHorizontalStrut(10));
		saveCamBox.add(fileCamName);
		saveCamBox.add(Box.createHorizontalStrut(10));
		saveCamBox.add(FileCamDirButon);
		saveCamBox.add(Box.createHorizontalStrut(10));
		FileCamDirButon.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "H.264 MP4 Files";
					}

					@Override
					public boolean accept(File f) {
						String n = f.getName();
						return n.endsWith(".mp4");
					}
				});
				if (fc.showSaveDialog(recFrame) == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					fileCamName.setText(f.getAbsolutePath());
				}
			}
		});

		// saveSkriptBox

		fileSkriptName = new JTextField();
		fileSkriptName.setColumns(50);
		saveSkriptBox.add(Box.createHorizontalStrut(10));
		saveSkriptBox.add(new JLabel("Save Skript cast:"));
		saveSkriptBox.add(Box.createHorizontalStrut(10));
		saveSkriptBox.add(fileSkriptName);
		saveSkriptBox.add(Box.createHorizontalStrut(10));
		saveSkriptBox.add(FileSkriptDirButton);
		saveSkriptBox.add(Box.createHorizontalStrut(10));
		FileSkriptDirButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "H.264 MP4 Files";
					}

					@Override
					public boolean accept(File f) {
						String n = f.getName();
						return n.endsWith(".mp4");
					}
				});
				if (fc.showSaveDialog(recFrame) == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					fileSkriptName.setText(f.getAbsolutePath());
				}
			}
		});

		inputBox.add(fileBox);
		saveCamBox.add(Box.createVerticalStrut(10));
		inputBox.add(saveCamBox);
		saveSkriptBox.add(Box.createVerticalStrut(10));
		inputBox.add(saveSkriptBox);

		// Start Record Button
		recordButton = new JButton();
		// recordButton.addActionListener(new ActionListener() {
		//
		// public void actionPerformed(ActionEvent e) {
		//
		//
		// }
		// });

		try {
			recordButton.addActionListener(new RecImpl(recordButton,
					fileCamName, skript, webcam));
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		getContentPane().add(inputBox);
		getContentPane().add(recordButton);

		pack();

	}

	public void initWebCam() {

		Dimension size = WebcamResolution.QVGA.getSize();

		webcam.setViewSize(size);
		// Liste aller angeschlossenen WebCams UNGETESTET
		// List<Webcam> camList = Webcam.getWebcams();
		//
		// for (int i = 0; i < camList.size(); i++) {
		// try {
		// Webcam temp = camList.get(i);
		// System.out.println("(" + i + ") Webcam: " + temp.getName());
		// } catch (Exception e) {
		// System.out.println("Error CAM: " + i);
		// }
		// }

		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);

		JFrame window = new JFrame("Test webcam panel");
		window.add(panel);
		window.setResizable(false);
		// window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Test to set the window in the right bottom edge ?? better solution ?
		window.setLocation(2048, 1800);

		window.pack();
		window.setVisible(true);

	}

	// public void startRecording() {
	// RecImpl rec = new RecImpl(recordButton, fileCamName.getText(),
	// skript.getText(), webcam);
	// }

	public static void main(String[] args) {
		GuiRec app = new GuiRec();
		app.setVisible(true);

	}

}