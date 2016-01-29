package photo.caro.tools.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 * Classe utilitaire pour envoyer ou récupérer des fichiers du ftp de Caroline.
 * 
 * @author nicolas
 */
public class FtpManager {
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
	            System.out.println("Start uploading first file");
	            boolean done = ftpClient.storeFile(fileToSend.getName(), inputStream);
	            inputStream.close();
	            if (done) {
	                System.out.println("The first file is uploaded successfully.");
	            }
	        } catch (IOException ex) {
	            System.out.println("Error: " + ex.getMessage());
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

	            for (File fileToSend : files) {
	            	// Envoie du fichier.
	            	ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	            	InputStream inputStream = new FileInputStream(fileToSend);
	            	boolean done = ftpClient.storeFile(fileToSend.getName(), inputStream);
	            	inputStream.close();
	            	if (!done) {
	            		System.err.println(fileToSend.getName()+" n'a pas été envoyé dans "+remotePath+".");
	            	}
				}
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
	 * @return Le fichier galleryCopy.html qui correspond exactement à gallery.html.
	 * @throws FtpManagerException Si une erreur survient lors de la connexion ou déconnexion ou lors de la récupération du fichier.
	 */
	public File getCopyGalleryHtmlFile() throws FtpManagerException {
		if(galleryHtml == null) {
			FTPClient ftpClient = new FTPClient();
			OutputStream outputStream = null;
			try {
				galleryHtml = new File(tempFolder.getAbsolutePath()+File.separator+"galleryCopy.html");
				outputStream = new BufferedOutputStream(new FileOutputStream(galleryHtml));

		    	ftpClient.connect(server, port);
	            ftpClient.login(user, pass);
		        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		        if(ftpClient.retrieveFile("gallery.html", outputStream)) {
		        	// TODO : tracer un log debug.
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
	            
	            ftpClient.makeDirectory(albumName);
	        } catch (IOException ex) {
	            throw new FtpManagerException("Erreur lors de la création de l'album "+albumName+".", ex);
	        } finally {
	        	deconnexion(ftpClient, " Impossible de créer l'album "+albumName+".");
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
