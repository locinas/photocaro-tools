package photo.caro.tools;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

public class GalleryImageReziser {

	private static int refWidth = 370;
	private static int refHeight = 207;
	
	private String albumName;
	private String pathSource;
	private boolean rogne;
	
	public GalleryImageReziser(String album, String source, boolean rogne) {
		this.albumName=album;
		this.pathSource = source;
		this.rogne=rogne;
	}
	
	public void build() throws IOException{
		File refDir = new File(this.pathSource);
		
		File tempDir = new File(this.pathSource+File.separator+"temp");
		FileUtils.deleteDirectory(tempDir);
		tempDir.mkdir();
		
		// Copie des image + Renomage + création liste smallFile.
		List<File> listOfFiles = Arrays.asList(refDir.listFiles());
		Collections.sort(listOfFiles, new FileNameComparator());
		int num = 1;
		for (File image : listOfFiles) {
			transfornImage(tempDir, num, image);
			num++;
		}
	}

	public void transfornImage(File tempDir, int num, File image)
			throws IOException {
		if (image.isFile() && isImage(image)) {
			String extentions = image.getName().substring(image.getName().lastIndexOf(".")).toLowerCase();
			
			String smallName = "small"+Character.toUpperCase(albumName.charAt(0))+albumName.substring(1)+num+extentions;
			BufferedImage originalBufferedImage = ImageIO.read(image);
			this.saveRognedImage(tempDir, smallName, originalBufferedImage, rogne);
			
			String bigName = albumName.toLowerCase()+num+extentions;
			File bigFile = new File(tempDir.getAbsolutePath()+File.separator+bigName);
			Files.copy(image.toPath(), bigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public void saveRognedImage(File destinationFolder, String name, BufferedImage originalBufferedImage, boolean rogne) throws IOException {
		int type = originalBufferedImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalBufferedImage.getType();
		int commonCoef = calculateCoef(originalBufferedImage);
		int width = refWidth * commonCoef;
		int heightPropor = originalBufferedImage.getHeight() * width / originalBufferedImage.getWidth();
		
		BufferedImage resizedImage = new BufferedImage(width, heightPropor, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalBufferedImage, 0, 0, width, heightPropor, null);
		g.dispose();
		
		if(rogne){
			int height = refHeight * commonCoef;
			int startY = (heightPropor-height) / 2 ;
			resizedImage = resizedImage.getSubimage(0, startY, resizedImage.getWidth(), height);
		}
		
		ImageIO.write(resizedImage, "jpg", new File(destinationFolder.getAbsolutePath()+File.separator+name));
	}
	
	public void saveImage(File destinationFolder, String name, BufferedImage originalBufferedImage) throws IOException {
		int type = originalBufferedImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalBufferedImage.getType();
		int commonCoef = calculateCoef(originalBufferedImage);
		int width = refWidth * commonCoef;
		int heightPropor = originalBufferedImage.getHeight() * width / originalBufferedImage.getWidth();
		
		BufferedImage resizedImage = new BufferedImage(width, heightPropor, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalBufferedImage, 0, 0, width, heightPropor, null);
		g.dispose();
		
		int height = refHeight * commonCoef;
		int startY = (heightPropor-height) / 2 ;
		resizedImage = resizedImage.getSubimage(0, startY, resizedImage.getWidth(), height);
		
		ImageIO.write(resizedImage, "jpg", new File(destinationFolder.getAbsolutePath()+File.separator+name));
	}
	
	private int calculateCoef(BufferedImage originalBufferedImage){
		int coefW = originalBufferedImage.getWidth() / refWidth;
		int coefH = originalBufferedImage.getHeight() / refHeight;
		
		if(coefH < coefW) {
			return coefH;
		} else { 
			return coefW; 
		}
	}
	
	private static boolean isImage(File f) throws IOException {
		String mimetype =  java.nio.file.Files.probeContentType(f.toPath()); 
		String type = mimetype.split("/")[0];
		return type.equals("image");
	}
}
