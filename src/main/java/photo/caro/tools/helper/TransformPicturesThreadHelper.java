package photo.caro.tools.helper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import photo.caro.tools.AlbumManager;
import photo.caro.tools.GalleryImageReziser;
import photo.caro.tools.StepTransform;
import photo.caro.tools.exceptions.PhotocaroException;
import photo.caro.tools.ftp.FtpManagerException;

/**
 * 
 */

/**
 * @author nicolas
 *
 */
public class TransformPicturesThreadHelper {

	/** Le regroupement de noms à utiliser. */
	private AlbumNamesHelper namesHelper;
	/** Les outils pour manipuler les dossiers. */
	private DirectoryHelper directoryHelper;
	/** La barre de progression à mettre à jour. */
	private JProgressBar progressBar;
	
	/**
	 * Constructeur.
	 * 
	 * @param namesHelper L'objet qui contient l'ensemble des nom à manipuler.
	 * @param directoryHelper L'objet qui contient l'ensemble des dossiers à manipuler.
	 * @param progressBar La barre de progression à mettre à jour en fonction de l'avancement du thread.
	 */
	public TransformPicturesThreadHelper(AlbumNamesHelper namesHelper, DirectoryHelper directoryHelper, JProgressBar progressBar) {
		this.namesHelper = namesHelper;
		this.directoryHelper = directoryHelper;
		this.progressBar = progressBar;
	}
	
	public void run() throws PhotocaroException {
		
		try {
			// Initialisation
			initProgressBar();
			FileUtils.deleteDirectory(directoryHelper.getTempFolder());
			directoryHelper.getTempFolder().mkdir();
			
			// Transformation des images.
			GalleryImageReziser galleryImageReziser = new GalleryImageReziser(namesHelper, directoryHelper);
			List<File> listOriginalPicture = directoryHelper.listOriginalPicture();
			for(int i=1; i<= listOriginalPicture.size(); i++) {
				galleryImageReziser.transfornImage(i, listOriginalPicture.get(i-1));
				int percent = (int) (i * listOriginalPicture.size() / 25);
				actualisePercentProgressBar(percent, StepTransform.STEP_1);
			}
			
			// Envoie les images et les fichiers nécessaires à l'affichage sur le serveur FTP.
			AlbumManager albumManager = new AlbumManager(namesHelper, directoryHelper);
			actualisePercentProgressBar(50, StepTransform.STEP_2);
			albumManager.createAlbum();
			actualisePercentProgressBar(75, StepTransform.STEP_3);
			File galleryPicture = galleryImageReziser.buildGalleryPicture();
			albumManager.updateGalleryHtml(galleryPicture);
			actualisePercentProgressBar(90, StepTransform.STEP_4);
			albumManager.creatAlbumPhpFile();
			actualisePercentProgressBar(100, StepTransform.FINISH);

		} catch (IOException e) {
			throw new PhotocaroException("Une erreur s'est produite lors de la création du dossier temp.", e);
		} catch (FtpManagerException | URISyntaxException e) {
			throw new PhotocaroException("Une erreur s'est produite lors de la communication avec le serveur FTP.", e);
		}
	}

	private void initProgressBar() {
		progressBar.setMaximum(100);
		progressBar.setValue(0);
		progressBar.setString("0%");
		progressBar.setStringPainted(true);
		progressBar.setVisible(true);
	}
	
	private void actualisePercentProgressBar(final Integer percent, StepTransform step) {
		final StringBuilder message = new StringBuilder();
		switch (step) {
		case STEP_1:
			message.append("Etape 1/4 : transformation des photos ("+percent+"%)");
			break;
		case STEP_2:
			message.append("Etape 2/4 : envoie des photos en cours ...");
			break;
		case STEP_3:
			message.append("Etape 3/4 : envoie de gallery.html en cours ...");
			break;
		case STEP_4:
			message.append("Etape 4/4 : envoie du fichier php en cours ...");
			break;
		default:
			message.append("Conversion et envoie terminés");
		}
		
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
				progressBar.setString(message.toString());
				progressBar.setValue(percent);
//			}
//		});
	}
}
