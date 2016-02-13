/**
 * 
 */
package photo.caro.tools.business.albumcreation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import photo.caro.tools.business.ftp.FtpManager;
import photo.caro.tools.business.ftp.FtpManagerException;
import photo.caro.tools.business.helper.AlbumNamesHelper;
import photo.caro.tools.business.helper.DirectoryHelper;

/**
 * Classe utilisé pour la création d'un album sur le ftp de Caroline. Elle permet de modifier gallery.html, de créer 
 * l'album avec ses photos ainsi que le fichier php.
 * 
 * @author nicolas
 */
public class AlbumManager {

	/** Le manager de logs. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AlbumManager.class);
	/** L'ensemble des noms de l'album utilisables par l'application. */
	private AlbumNamesHelper names;
	/** Les chemins des différents dossiers utilisables par l'application. */
	private DirectoryHelper folderHelper;
	/** Utilisé pour envoyer ou recevoir des fichier du serveur free. */
	private FtpManager ftpManager;
	
	/**
	 * Constructeur.
	 * 
	 * @param namesHelper Contient tous les noms de l'album utilisables par l'application.. 
	 * @param folderHelper Contient tous les chemins des dossiers utilisés par l'application.
	 */
	public AlbumManager(AlbumNamesHelper namesHelper, DirectoryHelper folderHelper) {
		this.names = namesHelper;
		this.folderHelper = folderHelper;
		this.ftpManager = new FtpManager(folderHelper.getTempFolder());
	}
	
	/**
	 * Récupère du serveur FTP le fichier gallery.html, y ajoute l'album puis le renvoie sur le serveure.
	 * @param galleryPicture La photo à envoyer qui servira de couverture d'album.
	 * 
	 * @throws FtpManagerException Si une exception survient lors de la récupération du fichier ou lors de son envoie.
	 * @throws IOException Si une exception survient lors de la modification du contenu du fichier.
	 */
	public void updateGalleryHtml(File galleryPicture) throws FtpManagerException, IOException {
		// Récupération du gallery.html et création du nouveau
		File galleryHtmlCopyFile = ftpManager.getCopyGalleryHtmlFile();
		File galleryHtmlFile = new File(folderHelper.getTempFolder().getAbsolutePath()+File.separator+"gallery.html");
		galleryHtmlFile.createNewFile();
		
		// Initialisation des buffers de lecture et d'écriture.
		FileReader galleryReader = new FileReader(galleryHtmlCopyFile.getAbsolutePath());
		BufferedReader galleryBuffer = new BufferedReader(galleryReader);
		FileOutputStream galleryOutputStream = new FileOutputStream(galleryHtmlFile);
		BufferedWriter galleryBufferWriter = new BufferedWriter(new OutputStreamWriter(galleryOutputStream));
		
		// Ecriture dans le fichier et supprime la copie téléchargée.
		for(String line = galleryBuffer.readLine(); line != null; line = galleryBuffer.readLine()) {
			galleryBufferWriter.write(line);
			galleryBufferWriter.newLine();

			if(line.contains("<!-- [PHOTOSCARO-TOOLS] -->")) {
				String bloc = buildBlocDiv();
				galleryBufferWriter.write(bloc);
				galleryBufferWriter.newLine();
			}
		}

		galleryBuffer.close();
		galleryBufferWriter.close();
		galleryOutputStream.close();
		
		// Envoie de la photo de couverture sur le FTP.
		ftpManager.sendFile(galleryPicture, "/photos/gallerie");
		ftpManager.sendFile(galleryHtmlFile, null);
		LOGGER.info("Le fichier "+galleryHtmlFile.getName() +" a bien été envoyé sur le serveur ftp. Il en est de même pour la photo de "
				+ "l'album "+galleryPicture.getName()+ " qui est dans /photos/gallerie.");
	}

	
	/**
	 * Transfère les photos dans le bon dossier sur le FTP.
	 * 
	 * @throws FtpManagerException Si une exception survient lors de la création du dossier ou lors de l'envoie des photos.
	 */
	public void createAlbum() throws FtpManagerException {
		ftpManager.createAlbumFolder(names.getLanguagesName());
		String remoteAlbumPath = "/photos/"+names.getLanguagesName();
		List<File> picturesToSend = folderHelper.listTempPicture();
		ftpManager.sendListFile(picturesToSend, remoteAlbumPath);
		LOGGER.info("L'album "+names.getLanguagesName()+" a bien été créé et contient "+picturesToSend.size()+" photos.");
	}
	
	/**
	 * Créé le fichier php correspondant à l'album et l'envoie sur le serveur FTP.
	 * 
	 * @throws FtpManagerException Si une erreur survient lors de l'envoie du fichier php.
	 * @throws IOException Si une exception survient lors de la modification du contenu du fichier php.
	 * @throws URISyntaxException Si une exception survient lors de la transformation du template en URI.
	 */
	public void creatAlbumPhpFile() throws FtpManagerException, IOException, URISyntaxException {
		// Créé le fichier php.
		ClassLoader classLoad = getClass().getClassLoader();
		File templatePhp = new File(new URI(classLoad.getResource("template.php").toString()));
		File albumPhp = new File(folderHelper.getTempFolder().getAbsolutePath()+File.separator+names.getLanguagesName()+".php");
		albumPhp.createNewFile();
		
		// Initialisation des buffers de lecture et d'écriture.
		FileReader templateReader = new FileReader(templatePhp);
		BufferedReader templateBufferR = new BufferedReader(templateReader);
		FileOutputStream phpOutputStream = new FileOutputStream(albumPhp);
		BufferedWriter phpBufferW = new BufferedWriter(new OutputStreamWriter(phpOutputStream));
		
		// Ecriture dans le fichier php.
		for(String line = templateBufferR.readLine(); line != null; line = templateBufferR.readLine()) {
			if(line.contains("[NOM_ALBUM_HUMAIN]")) {
				line = StringUtils.replace(line, "[NOM_ALBUM_HUMAIN]", names.getHumanName());
			} else if(line.contains("[NOM_ALBUM_INFO]")) {
				line = StringUtils.replace(line, "[NOM_ALBUM_INFO]", names.getLanguagesName());
				
			}
			
			phpBufferW.write(line);
			phpBufferW.newLine();
		}
		
		templateBufferR.close();
		phpBufferW.close();
		phpOutputStream.close();
		
		ftpManager.sendFile(albumPhp, null);
		LOGGER.info("Le fichier "+albumPhp.getName()+" a bien été envoyé sur le serveur.");
	}
	
	/**
	 * Remet le serveur ftp tel qu'il était avant la création de l'album.
	 * 
	 * @throws FtpManagerException Si une exception survient pendant le remplacement du gallery.html ou pendant la suppression des images ou 
	 * du fichier php. 
	 */
	public void cancelAlbumCreation() throws FtpManagerException {
		// Remettre gallery.html
		String pathGalleryHtml = folderHelper.getTempFolder().getAbsolutePath()+File.separator+"gallery.html";
		File galleryHtml = new File(pathGalleryHtml);
		File galleryHtmlToDelete = galleryHtml;
		galleryHtmlToDelete.delete();
		File galleryHtmlSave = new File(folderHelper.getTempFolder().getAbsolutePath()+File.separator+"gallerySave.html");
		galleryHtmlSave.renameTo(galleryHtml);
		ftpManager.sendFile(galleryHtml, null);
		LOGGER.info("Annulation : le fichier gallery.html est reverté.");
		
		// Supprimer les photos et le fichier php.
		List<String> filesToDelete = Arrays.asList(names.getLanguagesName()+".php", "/photos/gallerie/"+names.getLanguagesName()+".jpg");
		ftpManager.deleteFile(filesToDelete);
		LOGGER.info("Annulation : le fichier "+names.getLanguagesName()+".php et /photos/gallerie/"+names.getLanguagesName()+".jpg ne sont pas envoyés");
		ftpManager.deleteFolder("/photos/"+names.getLanguagesName());
		LOGGER.info("Annulation : l'album /photos/"+names.getLanguagesName()+" n'est pas envoyé.");
		
	}
	
	private String buildBlocDiv() {
		StringBuilder blocBuilder = new StringBuilder(System.getProperty("line.separator"));
		blocBuilder.append("        <div class=\"grid_4\">");
		blocBuilder.append(System.getProperty("line.separator"));
		blocBuilder.append("          <div class=\"box\">");
		blocBuilder.append(System.getProperty("line.separator"));
		blocBuilder.append("            <a href=\""+names.getLanguagesName()+".php\"><img src=\"photos/gallerie/"+names.getLanguagesName()+".jpg\" alt=\"\"><span></span></a>");
		blocBuilder.append(System.getProperty("line.separator"));
		blocBuilder.append("            <div class=\"box_bot\">");
		blocBuilder.append(System.getProperty("line.separator"));
		blocBuilder.append("              <div class=\"box_bot_title\">"+names.getHumanName()+"</div>");
		blocBuilder.append(System.getProperty("line.separator"));
		blocBuilder.append("              <p><!-- Citacion ou phrase d'accroche &agrave; mettre ici. --></p>");
		blocBuilder.append(System.getProperty("line.separator"));
		blocBuilder.append("              <a href=\""+names.getLanguagesName()+".php\" class=\"btn\">album</a>");
		blocBuilder.append(System.getProperty("line.separator"));
		blocBuilder.append("            </div>");
		blocBuilder.append(System.getProperty("line.separator"));
		blocBuilder.append("          </div>");
		blocBuilder.append(System.getProperty("line.separator"));
		blocBuilder.append("        </div>");
		blocBuilder.append(System.getProperty("line.separator"));
		
		return blocBuilder.toString();
	}
}
