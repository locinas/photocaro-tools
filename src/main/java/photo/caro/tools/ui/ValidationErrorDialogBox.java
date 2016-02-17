/**
 * 
 */
package photo.caro.tools.ui;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import photo.caro.tools.business.globalvalidation.ValidationError;

/**
 * Fenêtre qui affiche les erreurs de validations suite à une validation globale du site.
 * 
 * @author nicolas
 */
public class ValidationErrorDialogBox extends JDialog {

	private List<ValidationError> errors;

	public ValidationErrorDialogBox(JFrame parent, List<ValidationError> errors) {
		super(parent, "Ton site est un peu pourri ...");
		this.errors = errors;
		
		getContentPane().setLayout(null);
		setSize(900, 280);
		setModal(true);
		initializeWidgets();
	}

	private void initializeWidgets() {
		JLabel repportLabel = new JLabel("Rapport de validation de Phot'O Caro : ");
		repportLabel.setBounds(10, 20, 586, 15);
		
		ValidationErrorTable table = new ValidationErrorTable(new ValidationErrorTableModel(errors));
		table.setDefaultRenderer(ValidationError.class, new ValidationErrorCellRenderer());
		table.setRowHeight(20);
		table.setTableHeader(null);
		
		JScrollPane scroller = new JScrollPane(table);
		scroller.setPreferredSize(new Dimension(850, 185));
		scroller.setBounds(20, 45, 850, 185);
		
		getContentPane().add(repportLabel);
		getContentPane().add(scroller);
	}
}
