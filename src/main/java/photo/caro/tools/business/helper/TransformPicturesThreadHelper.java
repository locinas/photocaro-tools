package photo.caro.tools.business.helper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.JProgressBar;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import photo.caro.tools.business.albumcreation.AlbumManager;
import photo.caro.tools.business.albumcreation.GalleryImageReziser;
import photo.caro.tools.business.albumcreation.StepTransform;
import photo.caro.tools.business.ftp.FtpManager;
import photo.caro.tools.business.ftp.FtpManagerException;
import photo.caro.tools.exceptions.PhotocaroException;

/**
 * Contient la logique de transformation et d'envoie du thread lancé lors du clic sur Transformer.
 * 
 * @author nicolas
 */
public class TransformPicturesThreadHelper {

	/** Le manager de logs. */
	private static final Logger LOGGER = LoggerFactory.getLogger(TransformPicturesThreadHelper.class);
	/** Le regroupement de noms à utiliser. */
	private AlbumNamesHelper namesHelper;
	/** Les outils pour manipuler les dossiers. */
	private DirectoryHelper directoryHelper;
	/** La barre de progression à mettre à jour. */
	private JProgressBar progressBar;
	/** Temps moyen en seconde de l'envoie d'une photo originale (small + big). */
	private int averageSendTimeInSeconds = 20;
	
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
		
		AlbumManager albumManager = new AlbumManager(namesHelper, directoryHelper);
		try {
			// Initialisation
			initProgressBar();
			FileUtils.deleteDirectory(directoryHelper.getTempFolder());
			directoryHelper.getTempFolder().mkdir();
			LOGGER.info("Dossier temp créé.");
			
			// Transformation des images.
			GalleryImageReziser galleryImageReziser = new GalleryImageReziser(namesHelper, directoryHelper);
			List<File> listOriginalPicture = directoryHelper.listOriginalPicture();
			for(int i=1; i<= listOriginalPicture.size(); i++) {
				galleryImageReziser.transfornImage(i, listOriginalPicture.get(i-1));
				int percent = (int) (i * 100 / listOriginalPicture.size());
				actualisePercentProgressBar(percent, StepTransform.STEP_1);
			}
			actualisePercentProgressBar(100, StepTransform.STEP_1);
			LOGGER.info("Etape 1  : les photos sont retravaillées et leur tailles sont adaptées.");
			
			// Envoie les images et les fichiers nécessaires à l'affichage sur le serveur FTP.
			actualisePercentProgressBar(null, StepTransform.STEP_2);
			albumManager.createAlbum();
			LOGGER.info("Etape 2  : le dossier de l'album est créé avec ses photos.");
			actualisePercentProgressBar(null, StepTransform.STEP_3);
			File galleryPicture = galleryImageReziser.buildGalleryPicture();
			albumManager.updateGalleryHtml(galleryPicture);
			LOGGER.info("Etape 3  : le fichier gallery.html et la photo de couverture sont envoyés aussi.");
			actualisePercentProgressBar(null, StepTransform.STEP_4);
			albumManager.creatAlbumPhpFile();
			actualisePercentProgressBar(null, StepTransform.FINISH);
			LOGGER.info("Etape 4  : le fichier php est créé et envoyé.");

		} catch (IOException e) {
			try {
				albumManager.cancelAlbumCreation();
			} catch (FtpManagerException e1) {
				throw new PhotocaroException("Une erreur s'est produite lors du traitement des photos et il est impossible d'annuler "
						+ "la création de l'album. Appèles moi, bith !.", e);
			}
			throw new PhotocaroException("Une erreur s'est produite lors du traitement des photos.", e);
		} catch (FtpManagerException | URISyntaxException e) {
			try {
				albumManager.cancelAlbumCreation();
			} catch (FtpManagerException e1) {
				throw new PhotocaroException("Une erreur s'est produite lors de la communication avec le serveur FTP. Il est impossible "
						+ "d'annuler la création de l'album. Appèles moi, bith !.", e);
			}
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
			progressBar.setValue(25);
			break;
		case STEP_2:
			message.append("Etape 2/4 : envoie des photos en cours "+editSendPictureTimeMessage()+" ...");
			progressBar.setValue(50);
			break;
		case STEP_3:
			message.append("Etape 3/4 : envoie de gallery.html en cours ...");
			progressBar.setValue(75);
			break;
		case STEP_4:
			message.append("Etape 4/4 : envoie du fichier php en cours ...");
			progressBar.setValue(100);
			break;
		default:
			message.append("Conversion et envoie terminés");
		}
		
		progressBar.setString(message.toString());
	}
	
	private String editSendPictureTimeMessage() {
		int waitTime = directoryHelper.listOriginalPicture().size() * averageSendTimeInSeconds;
		
		if(waitTime < 60) {
			return "("+waitTime+"s)";
		} else {
			return "("+(waitTime/60+1)+"mn)";
		}
	}
}
