package at.jku.wall.xuggler;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import at.jku.wall.xuggler.impl.RecImpl;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class finalGui extends JFrame {

	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private JTextField txtScript;
	private JTextField txtCamDir;
	private JTextField txtScreenDir;
	private JButton btnSelectArea;
	private JButton btnStartRecord;
	private JPanel CamPanel;

	public Webcam webcam = Webcam.getDefault();

	private Properties userSettings;

	public static Point p1;
	public static Point p2;
	
	public SelectionRect rect = null;

	public finalGui() {
		loadProperties();
		initUi();
	}

	private void loadProperties() {
		this.userSettings = new Properties();
		try {
			this.userSettings.load(new FileInputStream(System
					.getProperty("user.home")
					+ "/.screenrecorder/recorder.properties"));
		} catch (Throwable t) {
			// Ignore just in case the file does not exist yet
		}
	}

	private void saveProperties() throws FileNotFoundException, IOException {
		File settingsFolder = new File(System.getProperty("user.home")
				+ "/.screenrecorder");
		settingsFolder.mkdirs();
		this.userSettings.store(
				new FileOutputStream(System.getProperty("user.home")
						+ "/.screenrecorder/recorder.properties"), "");
	}

	private void initUi() {
		frame = new JFrame("Recording Tool");
		frame.setBounds(100, 100, 500, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// Add a menue bar ///////////////////////////////////
		JMenuBar menuBar = new JMenuBar();
		// Add menu entry tools
		JMenu tools = new JMenu("Tools");
		// Add entries to tools
		JMenuItem miRec = new JMenuItem("Record Tool");
		// Not implemented yet
		JMenuItem miPlay = new JMenuItem("Player Tool");
		JMenuItem miQuit = new JMenuItem("Quit");
		miQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		tools.add(miRec);
		tools.add(miPlay);
		tools.add(miQuit);
		// add tools to menubar
		menuBar.add(tools);

		JMenu about = new JMenu("About");
		JMenuItem miInfo = new JMenuItem("Info");
		miInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"Recording Tool \nV1.0 \nAuthor: Patrick Wall");
			}
		});
		about.add(miInfo);

		menuBar.add(about);
		frame.setJMenuBar(menuBar);

		// End Menu Bar ///////////////////////////////////////

		JLabel lblScript1 = new JLabel("Choose the script: ");
		lblScript1.setBounds(20, 20, 120, 20);
		frame.getContentPane().add(lblScript1);

		JLabel lblScript2 = new JLabel("Script: ");
		lblScript2.setBounds(20, 50, 45, 20);
		frame.getContentPane().add(lblScript2);

		txtScript = new JTextField(userSettings.getProperty("scriptFile"));
		txtScript.setBounds(65, 45, 300, 25);
		frame.getContentPane().add(txtScript);
		txtScript.setColumns(50);

		JButton btnScript = new JButton(". . .");
		btnScript.setBounds(400, 45, 75, 30);
		btnScript.addActionListener(new ActionListener() {

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
				if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					txtScript.setText(f.getAbsolutePath());
					userSettings.setProperty("scriptFile", f.getAbsolutePath());
					try {
						saveProperties();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		frame.getContentPane().add(btnScript);

		JLabel lblChooseThePath = new JLabel(
				"Choose the path to save the cam record: ");
		lblChooseThePath.setBounds(20, 85, 270, 25);
		frame.getContentPane().add(lblChooseThePath);

		JLabel lblPath = new JLabel("Path:");
		lblPath.setBounds(20, 120, 60, 16);
		frame.getContentPane().add(lblPath);

		txtCamDir = new JTextField(userSettings.getProperty("camFile"));
		txtCamDir.setBounds(65, 115, 300, 25);
		frame.getContentPane().add(txtCamDir);
		txtCamDir.setColumns(50);

		JButton btnCamRec = new JButton(". . .");
		btnCamRec.setBounds(400, 115, 75, 30);
		btnCamRec.addActionListener(new ActionListener() {

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
				if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					txtCamDir.setText(f.getAbsolutePath());
					userSettings.setProperty("camFile", f.getAbsolutePath());
					try {
						saveProperties();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		frame.getContentPane().add(btnCamRec);

		JLabel lblChooseThePath_1 = new JLabel(
				"Choose the path to save the screen record: ");
		lblChooseThePath_1.setBounds(20, 155, 285, 25);
		frame.getContentPane().add(lblChooseThePath_1);

		JLabel lblPath_1 = new JLabel("Path:");
		lblPath_1.setBounds(20, 190, 60, 16);
		frame.getContentPane().add(lblPath_1);

		txtScreenDir = new JTextField(userSettings.getProperty("screenFile"));
		txtScreenDir.setBounds(65, 185, 300, 25);
		frame.getContentPane().add(txtScreenDir);
		txtScreenDir.setColumns(50);

		JButton btnScreenRec = new JButton(". . .");
		btnScreenRec.setBounds(400, 185, 75, 30);
		btnScreenRec.addActionListener(new ActionListener() {

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
				if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					txtScreenDir.setText(f.getAbsolutePath());
					userSettings.setProperty("screenFile", f.getAbsolutePath());
					try {
						saveProperties();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		frame.getContentPane().add(btnScreenRec);

		JPanel panel = new JPanel();
		panel.setBounds(160, 250, 320, 240);
		panel.add(initCamPanel());
		frame.getContentPane().add(panel);

		btnSelectArea = new JButton("Select Area");
		btnSelectArea.setBounds(20, 300, 120, 30);
		btnSelectArea.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// minimize app
				frame.setState(Frame.ICONIFIED);

				// Open script PDF File
				if (Desktop.isDesktopSupported()) {
					try {
						if (!(txtScript.getText().equals(""))) {
							File pdfFile = new File(txtScript.getText());
							Desktop.getDesktop().open(pdfFile);
						} else {
							System.out.println("PDF ERROR");
						}
					} catch (IOException ex) {
						// unhandled exception
					}
				}

				try {
					// Wait to open the pdf file 
					Thread.sleep(1000L);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				// Start measure Bounds
				rect = new SelectionRect();
				
			}
		});

		frame.getContentPane().add(btnSelectArea);

		btnStartRecord = new JButton("Start record");
		btnStartRecord.setBounds(20, 400, 120, 30);
		try {
			btnStartRecord.addActionListener(new RecImpl(btnStartRecord,
					txtScript, txtCamDir, txtScreenDir, webcam));
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		
		frame.getContentPane().add(btnStartRecord);

		frame.setResizable(false);
		frame.setVisible(true);

	}

	public WebcamPanel initCamPanel() {
		Dimension size = WebcamResolution.QVGA.getSize();
		webcam.setViewSize(size);
		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		return panel;
	}


	public static void main(String[] args) {
		finalGui app = new finalGui();
	}

}
