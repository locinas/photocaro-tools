package photo.caro.tools;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class Main {
	
	public static void main(String[] args) throws IOException {
		GalleryImageReziser galleryImageReziser = new GalleryImageReziser("inectes", "/home/nicolas/test", true);
		galleryImageReziser.build();
		System.out.println("Traitement terminé !");
	}
}
