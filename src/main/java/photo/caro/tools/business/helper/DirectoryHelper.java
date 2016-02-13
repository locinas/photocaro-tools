/**
 * 
 */
package photo.caro.tools.business.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Utilitaire qui met à disposition les différents dossiers utilisés par
 * l'application.
 * 
 * @author nicolas
 */
public class DirectoryHelper {

	/** Le dossier dans lequel sont les photos originales. */
	private File sourceFolder;
	/** Le dossier temp quiaccueillera les photos et les diférents fichiers (html, php). */
	private File tempFolder;

	public DirectoryHelper(String sourcePath) {
		if (StringUtils.isNotBlank(sourcePath)) {
			File file = new File(sourcePath);
			if (file.isDirectory()) {
				sourceFolder = file;
				tempFolder = new File(sourcePath + File.separator + "temp");
			}
		}
	}
	
	/**
	 * Liste toutes les photos JPEG du dossier source.
	 * 
	 * @return Toutes les photos originales du dossier source.
	 */
	public List<File> listOriginalPicture() {
		return listPictureFromRemoteFolder(this.sourceFolder);
	}

	/**
	 * Liste toutes les photos JPEG du dossier temp.
	 * 
	 * @return Toutes les photos originales du dossier temp.
	 */
	public List<File> listTempPicture() {
		return listPictureFromRemoteFolder(this.tempFolder);
	}
	
	/**
	 * Getteur sur le dossier qui contient les photos originales.
	 * 
	 * @return Le dossier qui contient les photos originales.
	 */
	public File getSourceFolder() {
		return sourceFolder;
	}

	/**
	 * Getteur sur le dossier qui contiendra les différents fichiers, photos lors d ela transformation.
	 * 
	 * @return Le dossier qui contiendra les différents fichiers, photos lors d ela transformation.
	 */
	public File getTempFolder() {
		return tempFolder;
	}

	private List<File> listPictureFromRemoteFolder(File remoteFolder) {
		List<File> originalPictures = new ArrayList<>();
		
		if(remoteFolder != null) {
			File[] listFiles = remoteFolder.listFiles(new JpegExtensionFileFilter());
			originalPictures.addAll(Arrays.asList(listFiles));
		}
		
		return originalPictures;
	}
}
