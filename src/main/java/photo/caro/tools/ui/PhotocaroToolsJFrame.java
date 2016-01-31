/**
 * 
 */
package photo.caro.tools.ui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import photo.caro.tools.helper.AlbumNamesHelper;
import photo.caro.tools.helper.DirectoryHelper;
import photo.caro.tools.helper.TransformPicturesThreadHelper;

/**
 * Fenêtre principale de l'outil de transformation des photos.
 * 
 * @author nicolas
 */
public class PhotocaroToolsJFrame extends JFrame {

	/** Le manager de logs. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PhotocaroToolsJFrame.class);
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
				    		JOptionPane.showMessageDialog(null, "Ton album "+names.getHumanName()+" ("+directory.listOriginalPicture().size()+
				    				  " photos)  est envoyé sur Phot'Ô Caro, bitch !", "Traitement terminé", JOptionPane.INFORMATION_MESSAGE);
			        	} catch (Exception e) {
			        		LOGGER.error("Une erreur est survenue lors de la création de l'album "+panel.getAlbumTextfield().getText()+" depuis "
			        				+ "le dossier "+ panel.getSourceTextField().getText()+".", e);
			        		ErrorDialogBox dialog = new ErrorDialogBox(PhotocaroToolsJFrame.this, e);
			        		dialog.setVisible(true);
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
