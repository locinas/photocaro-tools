/**
 * 
 */
package photo.caro.tools;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import photo.caro.tools.exceptions.PhotocaroException;
import photo.caro.tools.helper.AlbumNamesHelper;
import photo.caro.tools.helper.DirectoryHelper;
import photo.caro.tools.helper.TransformPicturesThreadHelper;

/**
 * Fenêtre principale de l'outil de transformation des photos.
 * 
 * @author nicolas
 */
public class PhotocaroToolsJFrame extends JFrame {

	/** Le panel qui contient tous les widgets. */
	private PhtotocaroToolsPanel panel;
	
	/**
	 * Constructeur.
	 */
	public PhotocaroToolsJFrame()  {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 265);
		setTitle("Phot'Ô Caro");
		getContentPane().setLayout(null);
		
		panel = new PhtotocaroToolsPanel(600, 265);
		getContentPane().add(panel);
		
		onBind();
	}

	private void onBind() {
		panel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.setTransformEnable(false);
			    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			    Thread t = new Thread(){
			        public void run(){
			        	try {
			        		AlbumNamesHelper names = new AlbumNamesHelper(panel.getAlbumTextfield().getText());
			        		DirectoryHelper directory = new DirectoryHelper(panel.getSourceTextField().getText());
			        		TransformPicturesThreadHelper transformHelper = new TransformPicturesThreadHelper(names, directory, panel.getProgressBar());
			        		transformHelper.run();
				    		JOptionPane.showMessageDialog(null, "Tes photos sont prêtes pour Phot'Ô Caro, bitch !", "Traitement terminé", JOptionPane.INFORMATION_MESSAGE);
			        	} catch (PhotocaroException e) {
			        		e.printStackTrace();
			        		JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			        	} finally {
			    			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			    			panel.setTransformEnable(true);
			    		}
			        }
			    };
			    t.start();
			}
		});
	}
}
