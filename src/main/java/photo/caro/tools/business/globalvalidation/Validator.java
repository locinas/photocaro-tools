package photo.caro.tools.business.globalvalidation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import photo.caro.tools.business.ftp.FtpManager;
import photo.caro.tools.business.ftp.FtpManagerException;
import photo.caro.tools.business.helper.AlbumNamesHelper;

import com.google.common.collect.Sets;

/**
 * Valide le site web dans sa globalité : vérifie que <br>
 * - gallery.html est bien formé
 * - les albums existent et qu'il n'y ai pas d'albums orphelins
 * - les .php existes et qu'il n'y ai pas de fichiers orphelins
 * @author nicolas
 *
 */
public class Validator {
	/** Le manager de logs. */
	private static Logger LOGGER = LoggerFactory.getLogger(Validator.class);
	/** Le gestionnaire d'accès au serveur FTP de Caropine. */
	private FtpManager ftpManager;
	/** Le dossier de travail de la validation. */
	private File workingFolder;

	/**
	 * Constructeur.
	 */
	public Validator() {
		String workingFolderPath = System.getProperty("java.io.tmpdir")+File.separator+"photocarotoolstemp";
		this.workingFolder = new File(workingFolderPath);
		this.ftpManager = new FtpManager(this.workingFolder);
	}

	/**
	 * Valide tout les site web de caroline et liste les erreurs s'il y en a.
	 * @return La liste des erreur qui rendent le site instable. Retourne une liste vide si le site est correct.
	 * @throws IOException
	 * @throws FtpManagerException
	 */
	public List<ValidationError> validate() throws IOException, FtpManagerException {
		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		// Construit le dossier qui servira à récupérer les fichiers pour la validation.
		LOGGER.info(workingFolder.getAbsolutePath());
		FileUtils.deleteDirectory(workingFolder);
		workingFolder.mkdir();
		
		// Lecture du fichier gallery.hyml
		File galleryHtmlFile = ftpManager.getCopyGalleryHtmlFile();
		String blocs = extractAlbumsBloc(galleryHtmlFile);
		
		// Validation du fichier gallery.html et récupération des noms de fichiers.
		Pattern albumBlocPattern = Pattern.compile("grid_4(.*?)</div></div></div>");
		Matcher albumMBlocMatcher = albumBlocPattern.matcher(blocs.replace(" ", ""));
		Set<String> phpFileNames = new TreeSet<>();
		Set<String> jpgFileNames = new TreeSet<>();
		while(albumMBlocMatcher.find())
		{
			validateBlocAndExtract(albumMBlocMatcher.group(), phpFileNames, jpgFileNames, errors);
		}
		
		checkGalleryPictures(errors, jpgFileNames);
		checkPhpFiles(errors, phpFileNames);		
		
		// Valide les album photos.
		Set<String> albumToTest = checkAlbumExistance(errors, phpFileNames, jpgFileNames);
		for (String album : albumToTest) {
			checkAlbumContent(errors, album);
		}
		
		return errors;
	}

	private void checkAlbumContent(List<ValidationError> errors, String album)
			throws FtpManagerException {
		List<FTPFile> pictures = ftpManager.listFilesFromAlbum(album);

		Set<String> smallNames = new TreeSet<>();
		Set<String> normalNames = new TreeSet<>();
		for (FTPFile ftpFile : pictures) {
			String ftpFileName = FilenameUtils.getBaseName(ftpFile.getName());
			if(ftpFileName.contains(AlbumNamesHelper.PREFIX_SMALL_IMAGE)) {
				smallNames.add(StringUtils.remove(ftpFileName, AlbumNamesHelper.PREFIX_SMALL_IMAGE).toLowerCase());
			} else {
				normalNames.add(ftpFileName.toLowerCase());
			}
		}
		
		// Teste si tuous les normal names sont présents.
		if(smallNames.isEmpty() && normalNames.isEmpty()) {
			errors.add(new ValidationError(SeverityErrorEnum.WARNNING, "L'album "+album+" est vide."));
		} else {
			Set<String> differenceNormalNames = Sets.difference(smallNames, normalNames);
			for (String pictureName : differenceNormalNames) {
				errors.add(new ValidationError(SeverityErrorEnum.ERREUR, "La photo "+pictureName+" de l'album "+album+" n'a pas sa mignature."));
			}
			Set<String> differenceSmallNames = Sets.difference(normalNames, smallNames);
			for (String pictureName : differenceSmallNames) {
				errors.add(new ValidationError(SeverityErrorEnum.ERREUR, "La photo small"+pictureName+" de l'album "+album+" n'a pas sa photo taille réelle."));
			}
		}
	}

	private Set<String> checkAlbumExistance(List<ValidationError> errors,
			Set<String> phpFileNames, Set<String> jpgFileNames)
			throws FtpManagerException {
		Set<String> albumNames = new TreeSet<>();
		for (String jpgFile : jpgFileNames) {
			albumNames.add(FilenameUtils.getBaseName(jpgFile));
		}
		for (String phpFile : phpFileNames) {
			albumNames.add(FilenameUtils.getBaseName(phpFile));
		}
		Set<String> directoriesNotFounds = ftpManager.checkDirectoriesExists(albumNames);
		for (String directory : directoriesNotFounds) {
			errors.add(new ValidationError(SeverityErrorEnum.ERREUR,"Le dossier "+directory+" n'existe pas."));
		}
		
		// Valide le contenu photo des albums.
		Set<String> albumToTest = Sets.difference(albumNames, directoriesNotFounds);
		return albumToTest;
	}

	private void checkPhpFiles(List<ValidationError> errors, Set<String> phpFileNames) throws FtpManagerException {
		Set<String> filesNotFound = ftpManager.checkFilesExists(phpFileNames);
		for (String phpFile : filesNotFound) {
			errors.add(new ValidationError(SeverityErrorEnum.ERREUR,"Le fichier php "+phpFile+" n'existe pas."));
		}
	}

	private void checkGalleryPictures(List<ValidationError> errors, Set<String> jpgFileNames) throws FtpManagerException {
		Set<String> filesNotFound = ftpManager.checkFilesExists(jpgFileNames);
		for (String picture : filesNotFound) {
				errors.add(new ValidationError(SeverityErrorEnum.ERREUR,"La photo de couverture "+picture+" n'existe pas."));
		}
	}

	private String extractAlbumsBloc(File galleryHtmlFile) throws FileNotFoundException, IOException {
		FileReader galleryReader = new FileReader(galleryHtmlFile.getAbsolutePath());
		BufferedReader galleryBuffer = new BufferedReader(galleryReader);
		StringBuilder blocs = new StringBuilder();
		boolean makingBlocs = false;
		for(String line = galleryBuffer.readLine(); line != null; line = galleryBuffer.readLine()) {
			if(line.contains("<!-- [PHOTOSCARO-TOOLS] -->")) {
				makingBlocs = true;
			} else if(line.contains("[FIN-VALIDATION-PHOTOSCARO-TOOLS]")) {
				break;
			} else if(makingBlocs) {
				blocs.append(line);
			}
		}
		galleryBuffer.close();
		
		return blocs.toString();
	}
	
	private void validateBlocAndExtract(String bloc, Set<String> phpFileNames, Set<String> jpgFileNames, List<ValidationError> errors) {
		if(StringUtils.isNotBlank(bloc)) {
			String [] tabBloc = bloc.split("\"");
			// Première référence au fichier php.
			if(StringUtils.isNotBlank(tabBloc[4])) {
				phpFileNames.add(tabBloc[4]);
			} else {
				errors.add(new ValidationError(SeverityErrorEnum.ERREUR,"Fichier gallery.html : il manque la première référence vers le fichier php."));
			}
			
			// Photo de couverture de l'album.
			if(StringUtils.isNotBlank(tabBloc[6])) {
				jpgFileNames.add(tabBloc[6]);
			} else {
				errors.add(new ValidationError(SeverityErrorEnum.ERREUR,"Fichier gallery.html : il manque la référence vers la photo de couverture."));
			}
			
			// Nom humain de l'album.
			if(StringUtils.isNotBlank(tabBloc[13])) {
				String humanName = tabBloc[13].substring(1, tabBloc[13].indexOf("<"));
				if(StringUtils.isBlank(humanName)) {
					errors.add(new ValidationError(SeverityErrorEnum.ERREUR,"Fichier gallery.html : il manque le nom humain de l'album."));
				}
			}
			
			// Seconde référence au fichier php.
			if(StringUtils.isNotBlank(tabBloc[14])) {
				phpFileNames.add(tabBloc[14]);
			} else {
				errors.add(new ValidationError(SeverityErrorEnum.ERREUR,"Fichier gallery.html : il manque la seconde référence vers le fichier php."));
			}
		}
	}
}
