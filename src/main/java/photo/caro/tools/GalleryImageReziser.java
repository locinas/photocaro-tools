package photo.caro.tools;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.imageio.ImageIO;

import photo.caro.tools.helper.AlbumNamesHelper;
import photo.caro.tools.helper.DirectoryHelper;

/**
 * Classe offrant des outils pour retailler les images à envoyer sur le site.
 * 
 * @author nicolas
 */
public class GalleryImageReziser {
	/** Coefficient de rognage horizontal. */
	private static int refWidthSmallPicture = 370;
	/** Coefficient de rognage vertical. */
	private static int refHeightSmallPicture = 207;

	/** L'ensemble des noms de l'album utilisables par l'application. */
	private AlbumNamesHelper names;
	/** Les chemins des différents dossiers utilisables par l'application. */
	private DirectoryHelper folderHelper;
	
	/**
	 * Constructeur.
	 * 
	 * @param namesHelper Contient tous les noms de l'album utilisables par l'application.. 
	 * @param folderHelper Contient tous les chemins des dossiers utilisés par l'application.
	 */
	public GalleryImageReziser(AlbumNamesHelper namesHelper, DirectoryHelper folderHelper) {
		this.names = namesHelper;
		this.folderHelper = folderHelper;
	}

	/**
	 * Prend une image, la rogne, l'ajuste à la bonne taille, diminue sa qualité et la place dans un dossier.
	 * 
	 * @param num Le numéro qui complètera le nom de l'image.
	 * @param image L'image à travailler.
	 * @throws IOException
	 */
	public void transfornImage(int num, File image) throws IOException {
		if (image.isFile() && isImage(image)) {
			String extentions = image.getName()
					.substring(image.getName().lastIndexOf(".")).toLowerCase();

			String smallName = names.getSmallNamePattern() + num + extentions;
			BufferedImage originalBufferedImage = ImageIO.read(image);
			this.saveRognedImage(folderHelper.getTempFolder(), smallName, originalBufferedImage);

			String bigName = names.getLanguagesName() + num + extentions;
			File bigFile = new File(folderHelper.getTempFolder().getAbsolutePath() + File.separator + bigName);
			Files.copy(image.toPath(), bigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	

	/**
	 * Prend une photo parmis les images originales, la retaille et en fait une photo de couverture pour l'album.
	 * 
	 * @return Une photo de couverture pour l'album photo.
	 * @throws IOException 
	 */
	public File buildGalleryPicture() throws IOException {
		File albumPicture = null;
		List<File> originalPicturesList = folderHelper.listOriginalPicture();
		if(!originalPicturesList.isEmpty()) {
			File workingPicture = originalPicturesList.get(0);
			BufferedImage originalBufferedImage = ImageIO.read(workingPicture);
			int type = originalBufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalBufferedImage.getType();
			int commonCoef = calculateCoefCoverAlbumPicture(originalBufferedImage);
			int width = 369 * commonCoef;
			int heightPropor = originalBufferedImage.getHeight() * width / originalBufferedImage.getWidth();

			BufferedImage resizedImage = new BufferedImage(width, heightPropor,	type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(originalBufferedImage, 0, 0, width, heightPropor, null);
			g.dispose();

			int height = 216 * commonCoef;
			int startY = (heightPropor - height) / 2;
			resizedImage = resizedImage.getSubimage(0, startY, resizedImage.getWidth(), height);
			
			albumPicture = new File(folderHelper.getSourceFolder() + File.separator + "temp"+ File.separator + names.getLanguagesName()+".jpg");
			ImageIO.write(resizedImage, "jpg", albumPicture);
			
		}
		return albumPicture;
	}

	private void saveRognedImage(File destinationFolder, String name, BufferedImage originalBufferedImage)
			throws IOException {
		int type = originalBufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalBufferedImage.getType();
		int commonCoef = calculateCoef(originalBufferedImage);
		int width = refWidthSmallPicture * commonCoef;
		int heightPropor = originalBufferedImage.getHeight() * width / originalBufferedImage.getWidth();

		BufferedImage resizedImage = new BufferedImage(width, heightPropor,	type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalBufferedImage, 0, 0, width, heightPropor, null);
		g.dispose();

		int height = refHeightSmallPicture * commonCoef;
		int startY = (heightPropor - height) / 2;
		resizedImage = resizedImage.getSubimage(0, startY, resizedImage.getWidth(), height);

		ImageIO.write(resizedImage, "jpg", new File(destinationFolder.getAbsolutePath() + File.separator + name));
	}
	
	private int calculateCoef(BufferedImage originalBufferedImage) {
		int coefW = originalBufferedImage.getWidth() / refWidthSmallPicture;
		int coefH = originalBufferedImage.getHeight() / refHeightSmallPicture;
		
		if (coefH < coefW) {
			return coefH;
		} else {
			return coefW;
		}
	}
	
	private int calculateCoefCoverAlbumPicture(BufferedImage originalBufferedImage) {
		int coefW = originalBufferedImage.getWidth() / 369;
		int coefH = originalBufferedImage.getHeight() / 216;

		if (coefH < coefW) {
			return coefH;
		} else {
			return coefW;
		}
	}

	private static boolean isImage(File f) throws IOException {
		String mimetype = java.nio.file.Files.probeContentType(f.toPath());
		String type = mimetype.split("/")[0];
		return type.equals("image");
	}
}
