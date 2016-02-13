/**
 * 
 */
package photo.caro.tools.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Fenêtre qui permet d'afficher l'errreur et sa trace d'exception.
 * 
 * @author nicolas
 */
public class ErrorDialogBox extends JDialog {

	/** L'exception à afficher dans la fenêtre. */
	private Throwable exception;
	/** Le message d'erreur qui résume le problème. */
	private String errorMessage;

	/**
	 * Constructeur.
	 * 
	 * @param parent Le parent de cette dialog.
	 * @param exception L'exception à afficher.
	 */
	public ErrorDialogBox(JFrame parent, Throwable exception, String errorMesage) {
		super(parent, "Putain, une erreur !");
		this.exception = exception;
		this.errorMessage = errorMesage;
		
		getContentPane().setLayout(null);
		setSize(590, 280);
		setModal(true);
		initializeWidgets();
	}

	private void initializeWidgets() {
		JLabel errorMessageLabel = new JLabel(this.errorMessage);
		errorMessageLabel.setBounds(10, 20, 586, 15);
		
		JLabel exceptionLabel = new JLabel("Trace de l'erreur :");
		exceptionLabel.setBounds(10, 60, 290, 15);
		Font font = exceptionLabel.getFont();
		exceptionLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		
		JTextArea exceptionTraceTextArea = new JTextArea();
		exceptionTraceTextArea.setText(formatStackStrace());
		JScrollPane scroller = new JScrollPane(exceptionTraceTextArea);
		scroller.setPreferredSize(new Dimension(540, 145));
		scroller.setBounds(20, 85, 540, 145);
		
		getContentPane().add(errorMessageLabel);
		getContentPane().add(exceptionLabel);
		getContentPane().add(scroller);
	}

	private String formatStackStrace() {
		StringWriter sw = new StringWriter();
	    exception.printStackTrace(new PrintWriter(sw));
	    return sw.toString();
	}
}
