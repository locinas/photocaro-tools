package photo.caro.tools.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

/**
 * Le panel qui contient tous les widgets de la fenêtre.
 * 
 * @author nicolas
 */
public class CreationAlbumPanel extends JPanel {

	/** Le widget du nom humain de l'album. */
	private JTextField albumTextfield = new JTextField();
	/** Le widget du path où sont les photos originales. */
	private JTextField sourceTextField = new JTextField();
	/** Le bouton pour lancer la transformation et l'envoie sur le FTP des photos. */
	private JButton btnTransformer = new JButton();
	/** La barre de progression de transformation et d'envoie. */
	private JProgressBar progressBar = new JProgressBar();
	
	/**
	 * Constructeur.
	 */
	public CreationAlbumPanel() {
		super();
		setLayout(null);
		initializeAlbumWidget();
		initializeSourceWidget();
		initializeButtonTransformWidget();
		initializeProgressBarWidget();
	}
	
	/**
	 * Ajoute un listener sur l'action de Transformation.
	 * 
	 * @param listener Le listener sur l'action pour transformer les photos.
	 */
	public void addActionListener(ActionListener listener) {
		if(listener != null) {
			btnTransformer.addActionListener(listener);
		}
	}
	
	/**
	 * Met à jour les widgets pour que l'utilisateur pouisse ou non lancer une transformation.
	 * 
	 * @param enable true pour autoriser la transformation. False pour ne pas recommencer une transformation et voir 
	 * l'avancement de celle en cours.
	 */
	public void setTransformEnable(boolean enable) {
		btnTransformer.setEnabled(enable);
		progressBar.setVisible(!enable);
	}
	
	/**
	 * Getteur sur le widget du nom de l'album.
	 * 
	 * @return Le widget du nom de l'album.
	 */
	public JTextField getAlbumTextfield() {
		return albumTextfield;
	}

	/**
	 * Getteur sur le widget du chemin du dossier qui contient les ĥotos originales.
	 * 
	 * @return Le widget du chemin du dossier qui contient les ĥotos originales.
	 */
	public JTextField getSourceTextField() {
		return sourceTextField;
	}
	
	/**
	 * Getteur sur la barre de progression.
	 * 
	 * @return La barre de progression.
	 */
	public JProgressBar getProgressBar() {
		return progressBar;
	}

	private void initializeProgressBarWidget() {
		progressBar.setBounds(12, 198, 428, 21);
		progressBar.setVisible(false);
		add(progressBar);
	}

	private void initializeButtonTransformWidget() {
		btnTransformer.setText("Transformer");
		btnTransformer.setBounds(452, 196, 128, 25);
		add(btnTransformer);
	}

	private void initializeSourceWidget() {
		JLabel lblSource = new JLabel("Source :");
		lblSource.setBounds(12, 70, 70, 15);
		add(lblSource);
		
		sourceTextField.setBounds(79, 68, 465, 19);
		sourceTextField.setColumns(10);
		add(sourceTextField);
		
		JButton chooseSourceButton = new JButton();
		chooseSourceButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("Status-folder-open-icon.png")));
		chooseSourceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				 chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				 chooser.setAcceptAllFileFilterUsed(false);
				 if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					 sourceTextField.setText(chooser.getSelectedFile().getAbsolutePath());
				 }
			}
		});
		chooseSourceButton.setBounds(556, 65, 24, 24);
		add(chooseSourceButton);
	}

	private void initializeAlbumWidget() {
		JLabel lblNewLabel = new JLabel("Album :");
		lblNewLabel.setBounds(12, 30, 70, 15);
		add(lblNewLabel);

		albumTextfield.setBounds(79, 28, 248, 19);
		albumTextfield.setColumns(10);
		add(albumTextfield);
		
		JLabel lblAlbumexemple = new JLabel("Exemple : A l'aurée du bois");
		lblAlbumexemple.setBounds(345, 30, 199, 15);
		add(lblAlbumexemple);
	}
}
