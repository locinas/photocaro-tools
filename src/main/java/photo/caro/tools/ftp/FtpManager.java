package photo.caro.tools.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilitaire pour envoyer ou récupérer des fichiers du ftp de Caroline.
 * 
 * @author nicolas
 */
public class FtpManager {
	/** Le manager de logs. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FtpManager.class);
	/** Le serveur ftp sur lequel est le site de Caroline. */
	private static String server = "ftpperso.free.fr";
	/** Le n° de port utilisé par FTP. */
	private static int port = 21;
	/** Le login utitilsé pour se connecter au ftp du site de Caroline. */
	private static String user = "photoscaro";
	/** Le mote de passe utitilsé pour se connecter au ftp du site de Caroline. */
	private static String pass = "54525452";
	/** Le fichier gallery.html. */
	private File galleryHtml;
	/** Le répertoire temp de travail utilisé par photoscaro. */
	private File tempFolder;
	

	/**
	 * Constructeur.
	 * @param tempFolder Le dossier temp dans lequel travaille le logiciel pgotoscaro-tools.
	 * @throws IllegalArgumentException Si tempFolder est null.
	 */
	public FtpManager(File tempFolder) throws IllegalArgumentException {
		if(tempFolder == null) {
			throw new IllegalArgumentException("Le répertoire temp doit être renseigner pour que le module FtpManager puisse fonctionner !");
		}
		this.tempFolder = tempFolder;
	}

	/**
	 * Evoie un fichier dans le répertoire sur le ftp de Caroline. Si remotePath est vide alors le fichier sera envoyé à la racine du FTP.
	 * 
	 * Si fileToSend est null alors rien ne se passe.
	 * @param fileToSend Le fichier à envoyer sur le FTP de Carolie.
	 * @param remotePath Le dossier sur le ftp qui recevra le fichier fileToSend.
	 * @throws FtpManagerException Si une exception survient lors de l'envoie du fichier ou lors de la connexion/déconnextion.
	 */
	public void sendFile(File fileToSend, String remotePath) throws FtpManagerException {
		if(fileToSend != null) {
			FTPClient ftpClient = new FTPClient();
	        try {
	        	// Connection au FTP de Caroline.
	            ftpClient.connect(server, port);
	            ftpClient.login(user, pass);
	            ftpClient.enterLocalPassiveMode();
	            if(StringUtils.isNotBlank(remotePath)) {
	            	ftpClient.changeWorkingDirectory(remotePath);
	            }

	            // Envoie du fichier.
	            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	            InputStream inputStream = new FileInputStream(fileToSend);
	            boolean done = ftpClient.storeFile(fileToSend.getName(), inputStream);
	            inputStream.close();
	            
	            if(!done){
	            	throw new FtpManagerException("Le fichier "+fileToSend.getName()+" n'a pu être envoyé sur le serveur FTP à "
	            			+ "l'emplacement : ["+remotePath+"]. La méthode storeFile a répondu false.");
	            }
	            LOGGER.info("Le fichier "+fileToSend.getName()+" a été envoyé sur le ftp à l'emplacement : ["+remotePath+"]. "
	            		+ "Si l'emplacement est vide le fichier est à la racine");
	        } catch (IOException ex) {
	            throw new FtpManagerException("Erreur lors de l'envoie du fichier "+fileToSend.getName()+".", ex);
	        } finally {
	        	deconnexion(ftpClient, " Impossible d'envoyer le fichier "+fileToSend.getName()+".");
	        }
		}
	}
	
	/**
	 * Evoie un ensemble de fichiers dans le répertoire sur le ftp de Caroline. Si remotePath est vide alors les fichiers seront envoyés à 
	 * la racine du FTP. Si files est null alors rien ne se passe.
	 * 
	 * @param files La liste des fichiers à envoyer sur le FTP de Caroline.
	 * @param remotePath Le dossier sur le ftp qui recevra le fichier fileToSend.
	 * @throws FtpManagerException Si une exception survient lors de l'envoie du fichier ou lors de la connexion/déconnextion.
	 */
	public void sendListFile(List<File> files, String remotePath) throws FtpManagerException {
		if(CollectionUtils.isNotEmpty(files)) {
			FTPClient ftpClient = new FTPClient();
	        try {
	        	// Connection au FTP de Caroline.
	            ftpClient.connect(server, port);
	            ftpClient.login(user, pass);
	            ftpClient.enterLocalPassiveMode();
	            if(StringUtils.isNotBlank(remotePath)) {
	            	ftpClient.changeWorkingDirectory(remotePath);
	            }

	            // Envoie des fichiers sur le serveur ftp.
	            List<String> filesNotSent = new ArrayList<String>();
	            for (File fileToSend : files) {
	            	ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	            	InputStream inputStream = new FileInputStream(fileToSend);
	            	boolean done = ftpClient.storeFile(fileToSend.getName(), inputStream);
	            	inputStream.close();
	            	if (!done) {
	            		filesNotSent.add(fileToSend.getName());
	            	}
				}
	            
	            if(!filesNotSent.isEmpty()) {
	            	throw new FtpManagerException("Les fichiers suivants n'ont pas pu être envoyés sur le serveur ftp à "
	            			+ "l'emplacement "+remotePath+" : "+filesNotSent);
	            }
	            
	            LOGGER.info(files.size()+" fichiers ont été envoyés sur le serveur ftp à l'emplacement suivant : "+remotePath);
	        } catch (IOException ex) {
	            throw new FtpManagerException("Erreur lors de l'envoie d'un fichier parmis les "+files.size()+" fichiers à envoyer.", ex);
	        } finally {
	        	deconnexion(ftpClient, " Vérifier que les fichiers suivants sont dans le dossier "+remotePath+" : "+listFileName(files));
	        }
		}
	}

	/**
	 * Récupère sur le serveur FTP le fichier gallery.html et le place dans le dossier temp dans lequel travaille photoscaro-tools.
	 * 
	 * @return Le fichier gallerySave.html qui correspond exactement à gallery.html.
	 * @throws FtpManagerException Si une erreur survient lors de la connexion ou déconnexion ou lors de la récupération du fichier.
	 */
	public File getCopyGalleryHtmlFile() throws FtpManagerException {
		FTPClient ftpClient = new FTPClient();
		OutputStream outputStream = null;
		try {
			if(galleryHtml != null && galleryHtml.exists()) {
				galleryHtml.delete();
			}
			galleryHtml = new File(tempFolder.getAbsolutePath()+File.separator+"gallerySave.html");
			outputStream = new BufferedOutputStream(new FileOutputStream(galleryHtml));

	    	ftpClient.connect(server, port);
            ftpClient.login(user, pass);
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        if(ftpClient.retrieveFile("gallery.html", outputStream)) {
	        	LOGGER.info("Le fichier gallery.html a été mis dans le dossier "+tempFolder.getName()+" sous le nom de "+galleryHtml.getName());
	        } else {
	        	throw new FtpManagerException("Impossible de récupérer le fichier gallery.html sur le serveur ftp.");
	        }
	    } catch (IOException ex) {
	        throw new FtpManagerException("Erreur lors de la récupération du fichier gallery.html.", ex);
	    } finally {
	        if (outputStream != null) {
	        	// Fermeture du flux du fichier gallery.html
	        	try {
					outputStream.close();
				} catch (IOException e) {
					throw new FtpManagerException("Erreur lors de la récupération du fichier gallery.html.", e);
				}
	        }
	        deconnexion(ftpClient, "Erreur lors de la récupération du fichier gallery.html.");
	    }
		
		return galleryHtml;
	}
	
	/**
	 * Créé un dossier vide dans le dossier photos du FTP pour l'album albumName.
	 * @param albumName Le nom du dossier à créer
	 * @throws FtpManagerException Si une erreur survient lors de la connexion ou déconnexion ou lors de la crétion du dossier.
	 */
	public void createAlbumFolder(String albumName) throws FtpManagerException{
		if(StringUtils.isNotBlank(albumName)){
			FTPClient ftpClient = new FTPClient();
	        try {
	        	// Connection au FTP de Caroline.
	            ftpClient.connect(server, port);
	            ftpClient.login(user, pass);
	            ftpClient.enterLocalPassiveMode();
	            ftpClient.changeWorkingDirectory("/photos/");
	            
	            boolean folderCreated = ftpClient.makeDirectory(albumName);
	            if(!folderCreated) {
	            	throw new FtpManagerException("L'album "+albumName+" n'a pas été créé sur le ftp dans /photos/. La méthode makeDirectory a répondu false.");
	            }
	            LOGGER.info("L'album "+albumName+" a été créé sur le ftp dans /photos/. Il est vierge.");
	        } catch (IOException ex) {
	            throw new FtpManagerException("Erreur lors de la création de l'album "+albumName+".", ex);
	        } finally {
	        	deconnexion(ftpClient, " Impossible de créer l'album "+albumName+".");
	        }
		}
	}
	
	/**
	 * Supprime un dossier sur le serveur ftp. Les fichiers que contient ce dossier seront supprimés.
	 * 
	 * @param folderPath Le chemin distant du dossier sur le serveur ftp.
	 * @throws FtpManagerException Si une erreur survient lors de la connection/déconnection ou lors de la suppression.
	 */
	public void deleteFolder(String folderPath) throws FtpManagerException {
		if(StringUtils.isNotBlank(folderPath)){
			FTPClient ftpClient = new FTPClient();
			
			try {
				// Connection au FTP de Caroline.
				ftpClient.connect(server, port);
				ftpClient.login(user, pass);
				ftpClient.enterLocalPassiveMode();
				
				// Suppressions des photos et de l'album.
				ftpClient.changeWorkingDirectory(folderPath);
				List<String> listErrorPicture = new ArrayList<String>();
				FTPFile[] pictureList = ftpClient.listFiles(folderPath);
				for (FTPFile pictureToDelete : pictureList) {
					boolean deleteOk = ftpClient.deleteFile(pictureToDelete.getName());
					if(!deleteOk) {
						listErrorPicture.add(pictureToDelete.getName());
					}
				}
				boolean deleteDirectoryOk = ftpClient.removeDirectory(folderPath);
				
				// Affichage des fichiers qui n'ont pas pu être supprimés.
				String errorMessage = null;
				if(!deleteDirectoryOk) {
					errorMessage = "Impossible de supprimer le dossier "+folderPath+". "+System.getProperty("line.separator");
				}
				if(!listErrorPicture.isEmpty()){
					errorMessage += "Impossible de supprimer les photos suivantes du dossier "+folderPath+ " : "+listErrorPicture;
				}
				if(errorMessage != null) {
					throw new FtpManagerException(errorMessage);
				}
				LOGGER.info("Le dossier "+folderPath+" a été supprimé du serveur ftp.");
				
			} catch (IOException ex) {
				throw new FtpManagerException("Erreur lors de la suppression du dossier "+folderPath+" sur le ftp.", ex);
			} finally {
				deconnexion(ftpClient, " Impossible de supprimer le fichier "+folderPath+" sur le ftp.");
			}
		}
	}
	
	/**
	 * Supprime un ensemble de fichiers sur le serveur ftp.
	 * 
	 * @param filePathList La liste des fichiers et leur chemin à supprimer sur le serveur.
	 * @throws FtpManagerException Si une erreur survient lors de la connection/déconnection ou lors de la suppression.
	 */
	public void deleteFile(List<String> filePathList) throws FtpManagerException {
		if(CollectionUtils.isNotEmpty(filePathList)){
			FTPClient ftpClient = new FTPClient();
	        try {
	        	// Connection au FTP de Caroline.
	            ftpClient.connect(server, port);
	            ftpClient.login(user, pass);
	            ftpClient.enterLocalPassiveMode();

	            // Suppression des fichiers.
	            List<String> listErrorPicture = new ArrayList<String>();
	            for (String fileToDelete : filePathList) {
	            	boolean removeOk = ftpClient.deleteFile(fileToDelete);
	            	if(!removeOk) {
	            		listErrorPicture.add(fileToDelete);
	            	}
				}
	            
	            // Afichage d'un message d'erreur si des fichiers n'ont pas pu être supprimés.
	            if(!listErrorPicture.isEmpty()) {
	            	throw new FtpManagerException("Impossible de supprimer les fichiers suivants sur le serveur ftp : "+listErrorPicture);
	            }
	            
	            LOGGER.info("Les fichiers suivants ont bien été supprimés du serveur ftp : "+filePathList);
	        } catch (IOException ex) {
	            throw new FtpManagerException("Erreur lors de la suppression des fichiers "+filePathList+" sur le ftp.", ex);
	        } finally {
	        	deconnexion(ftpClient, " Impossible de supprimer les fichiers "+filePathList+" sur le ftp.");
	        }
		}
		
	}
	
	private void deconnexion(FTPClient ftpClient, String errorMessage) throws FtpManagerException {
		try {
		    if (ftpClient.isConnected()) {
		        ftpClient.logout();
		        ftpClient.disconnect();
		    }
		} catch (IOException ex) {
			throw new FtpManagerException("Erreur lors de la déconnection à "+server+". "+errorMessage, ex);
		}
	}
	
	private static String listFileName(List<File> files) {
		StringBuilder names = new StringBuilder();
		
		if(CollectionUtils.isNotEmpty(files)){
			for (File file : files) {
				names.append(file.getName()).append(", ");
			}
			names.delete(names.length()-2, names.length());
		}
		
		return names.toString();
	}
}
