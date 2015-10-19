package photo.caro.tools;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

public class MainJFrame extends JFrame{
	
	private JTextField albumTextfield;
	private JTextField sourceTextField;
	private JButton btnTransformer;
	private JProgressBar progressBar;
	private JCheckBox rognerCheckBox;
	
	public MainJFrame() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 300);
		getContentPane().setLayout(null);
		setTitle("Phot'Ô Caro");
		
		JLabel lblPhotCaro = new JLabel("Préparation photos");
		lblPhotCaro.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPhotCaro.setBounds(79, 12, 238, 24);
		getContentPane().add(lblPhotCaro);
		
		initAlbumFields();
		initSourceFields();
		initRognerFields();
		
		
		progressBar = new JProgressBar();
		progressBar.setBounds(12, 239, 428, 21);
		progressBar.setVisible(false);
//		progressBar.setBorder(null);
		getContentPane().add(progressBar);
		
		btnTransformer = new JButton("Transformer");
		btnTransformer.setBounds(452, 237, 128, 25);
		btnTransformer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doTransform();
			}
		});
		getContentPane().add(btnTransformer);
	}

	private void initRognerFields() {
		JLabel lblRogner = new JLabel("Rogner :");
		lblRogner.setBounds(12, 174, 70, 15);
		getContentPane().add(lblRogner);
		
		rognerCheckBox = new JCheckBox("");
		rognerCheckBox.setBounds(75, 170, 129, 23);
		rognerCheckBox.setSelected(true);
		getContentPane().add(rognerCheckBox);
	}
	
	private void doTransform() {
			btnTransformer.setEnabled(false);
		    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		    Thread t = new Thread(){
		        public void run(){
		    		
		        	try {
		        		File refDir = new File(sourceTextField.getText());
		        		progressBar.setMaximum(refDir.listFiles().length);
		        		progressBar.setValue(0);
		        		progressBar.setString("0%");
		        		progressBar.setStringPainted(true);
			    		progressBar.setVisible(true);
			    		
		        		File tempDir = new File(refDir.getAbsolutePath()+File.separator+"temp");
						FileUtils.deleteDirectory(tempDir);
			    		tempDir.mkdir();
			    		
			    		// Copie des image + Renomage + création liste smallFile.
			    		List<File> listOfFiles = Arrays.asList(refDir.listFiles());
			    		Collections.sort(listOfFiles, new FileNameComparator());
			    		GalleryImageReziser galleryImageReziser = new GalleryImageReziser(albumTextfield.getText(), sourceTextField.getText(), rognerCheckBox.isSelected());
			    		final int nbPictures = listOfFiles.size();
						for(int i = 0 ; i < nbPictures ; i++){
			    			galleryImageReziser.transfornImage(tempDir, i+1, listOfFiles.get(i));
			                final int percent = i+1;
			                SwingUtilities.invokeLater(new Runnable() {
			                    public void run() {
			                    	progressBar.setString(String.valueOf(percent*100/nbPictures)+"%");
			                        progressBar.setValue(percent);
			                    }
			                  });
			            }
			    		JOptionPane.showMessageDialog(null, "Tes photos sont prêtes pour Phot'Ô Caro, bitch !", "Traitement terminé", JOptionPane.INFORMATION_MESSAGE);
		        	} catch (IOException e) {
		        		e.printStackTrace();
		        		JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		        	} finally {
		    			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    			btnTransformer.setEnabled(true);
		    			progressBar.setVisible(false);
		    		}
		        }
		    };
		    t.start();
	}

	private void initSourceFields() {
		JLabel lblSource = new JLabel("Source :");
		lblSource.setBounds(12, 130, 70, 15);
		getContentPane().add(lblSource);
		
		sourceTextField = new JTextField();
		sourceTextField.setBounds(79, 128, 465, 19);
		sourceTextField.setColumns(10);
		getContentPane().add(sourceTextField);
		
		JButton chooseSourceButton = new JButton();
		chooseSourceButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("Status-folder-open-icon.png")));
		chooseSourceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooseFolder(sourceTextField);
			}
		});
		chooseSourceButton.setBounds(556, 125, 24, 24);
		getContentPane().add(chooseSourceButton);
	}

	private void initAlbumFields() {
		JLabel lblNewLabel = new JLabel("Album :");
		lblNewLabel.setBounds(12, 86, 70, 15);
		getContentPane().add(lblNewLabel);

		albumTextfield = new JTextField();
		albumTextfield.setBounds(79, 84, 248, 19);
		albumTextfield.setColumns(10);
		getContentPane().add(albumTextfield);
	}
	
	private void chooseFolder(JTextField champ) {
		JFileChooser chooser = new JFileChooser();
		 chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		 chooser.setAcceptAllFileFilterUsed(false);
		 if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			 champ.setText(chooser.getSelectedFile().getAbsolutePath());
		 }
	}

	public static void main(String[] args) {
		MainJFrame mainJFrame = new MainJFrame();
		mainJFrame.setVisible(true);
	}
}
