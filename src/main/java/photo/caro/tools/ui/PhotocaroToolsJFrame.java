package photo.caro.tools.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import photo.caro.tools.business.helper.AlbumNamesHelper;
import photo.caro.tools.business.helper.DirectoryHelper;
import photo.caro.tools.business.helper.TransformPicturesThreadHelper;

/**
 * Fenêtre principale de l'outil de transformation des photos.
 * 
 * @author nicolas
 */
public class PhotocaroToolsJFrame extends JFrame {

	/** Le manager de logs. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PhotocaroToolsJFrame.class);
	/** Le panel qui contient tous les widgets pour créer un album. */
	private CreationAlbumPanel panelCreationAlbum = new CreationAlbumPanel();
	/** Le panel qui permet de valider le site. */
	private GlobalValidationPanel panelValidation = new GlobalValidationPanel();
	
	/**
	 * Constructeur.
	 */
	public PhotocaroToolsJFrame()  {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 280);
		setTitle("Phot'Ô Caro 1.2.0");

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Création d'album", panelCreationAlbum);
		tabbedPane.addTab("Validation du site", panelValidation);
		topPanel.add(tabbedPane, BorderLayout.CENTER);
		
		onBindCreationAlbum();
		onBindValidation();
	}

	private void onBindValidation() {
		panelValidation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelValidation.setValidationEnable(false);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				Thread t = new Thread(){
			        public void run(){
			        	try {
			        		// TODO compléter avec l'action de valider.
				    		JOptionPane.showMessageDialog(null, "Hey Face de pine, ton site est niquel."+System.getProperty("line.separator")+" Je ne vois pas de problème donc tu te calmes !",
				    				"Validation terminé", JOptionPane.INFORMATION_MESSAGE);
			        	} catch (Exception e) {
			        		LOGGER.error("Une erreur est survenue lors de la validation du site Phot'Ô Caro.", e);
			        		ErrorDialogBox dialog = new ErrorDialogBox(PhotocaroToolsJFrame.this, e, "Impossible de créer ton album, Pine. Envoies moi la trace d'erreur par mail stp.");
			        		dialog.setVisible(true);
			        	} finally {
			    			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			    			panelValidation.setValidationEnable(true);
			    		}
			        }
			    };
			    t.start();
			}
		});
		
	}

	private void onBindCreationAlbum() {
		panelCreationAlbum.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelCreationAlbum.setTransformEnable(false);
			    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			    Thread t = new Thread(){
			        public void run(){
			        	try {
			        		AlbumNamesHelper names = new AlbumNamesHelper(panelCreationAlbum.getAlbumTextfield().getText());
			        		DirectoryHelper directory = new DirectoryHelper(panelCreationAlbum.getSourceTextField().getText());
			        		TransformPicturesThreadHelper transformHelper = new TransformPicturesThreadHelper(names, directory, panelCreationAlbum.getProgressBar());
			        		transformHelper.run();
				    		JOptionPane.showMessageDialog(null, "Ton album "+names.getHumanName()+" ("+directory.listOriginalPicture().size()+
				    				  " photos)  est envoyé sur Phot'Ô Caro, bitch !", "Traitement terminé", JOptionPane.INFORMATION_MESSAGE);
			        	} catch (Exception e) {
			        		LOGGER.error("Une erreur est survenue lors de la création de l'album "+panelCreationAlbum.getAlbumTextfield().getText()+" depuis "
			        				+ "le dossier "+ panelCreationAlbum.getSourceTextField().getText()+".", e);
			        		ErrorDialogBox dialog = new ErrorDialogBox(PhotocaroToolsJFrame.this, e, "Impossible de valider ton site, Pine. Envoies moi la trace d'erreur par mail stp.");
			        		dialog.setVisible(true);
			        	} finally {
			    			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			    			panelCreationAlbum.setTransformEnable(true);
			    		}
			        }
			    };
			    t.start();
			}
		});
	}
}
