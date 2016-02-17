package photo.caro.tools.ui;

import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Panel qui contient les widgets nécessaires pour effectuer une validation globale du site Photocaro
 * 
 * @author nicolas
 */
public class GlobalValidationPanel extends JPanel {

	/** Le bouton pour lancer la validation. */
	private JButton validateButton = new JButton();

	/**
	 * Constructeur.
	 */
	public GlobalValidationPanel() {
		super(null);
		
		initializeExplications();
		initializeValidationButton();
	}
	
	/**
	 * Ajoute l'action de valider le site.
	 * 
	 * @param listener Le listener qui contient l'action de validation du site.
	 */
	public void addActionListener(ActionListener listener) {
		validateButton.addActionListener(listener);
	}
	
	/**
	 * Met à jour les widgets pour que l'utilisateur puisse ou non lancer une validation.
	 * 
	 * @param enable true pour autoriser la validation. False pour ne pas recommencer une validation et voir 
	 * l'avancement de celle en cours.
	 */
	public void setValidationEnable(boolean enable) {
		validateButton.setEnabled(enable);
	}

	private void initializeExplications() {
		JLabel label = new JLabel("Explications : ");
		label.setBounds(10, 10, 260, 15);
		Font font = label.getFont();
		label.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		
		JTextArea explications = new JTextArea();
		explications.setBounds(20, 35, 550, 60);
		explications.setEditable(false);
		explications.setLineWrap(true);
		explications.setWrapStyleWord(true);
		explications.setText("Tu peux voir si ton site est correctement formé. Ca te dit si les fichiers sont construits correctement et s'ils "
				+ "sont à la bonne place. S'il y a des erreurs dans ton site, cette validation te dira lesquelles. Il faudra m'appeller pour que "
				+ "je les corrige bien sur Face of Pine !");
		
		add(label);
		add(explications);
		
	}

	private void initializeValidationButton() {
		validateButton.setText("Valider");
		validateButton.setBounds(452, 196, 128, 25);
		add(validateButton);
		
	}
}
