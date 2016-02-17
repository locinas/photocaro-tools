/**
 * 
 */
package photo.caro.tools.business.helper;

import org.apache.commons.lang3.StringUtils;

/**
 * Contient tous les noms d'un album dérivés pour les différents fichiers ou
 * photos. Ces nom seront utilisés aucours de la transformations d'images ou de
 * création/modification des fichiers html et php.
 * 
 * @author nicolas
 */
public class AlbumNamesHelper {

	/** Le prefixe utilisé pour le nom des fichiers miniatures. */
	public static final String PREFIX_SMALL_IMAGE = "small";
	/** Le nom humain de l'album. */
	private String humanName;
	/** Le nom de l'abulm utilisé dans les fichiers php ou html. */
	private String languagesName;
	/** Le pattern utilisé pour les petites images listées dans la gallery. */
	private String smallNamePattern;

	/**
	 * Consructeur.
	 * 
	 * @param humanName
	 *            Le nom humain de l'album. En théorie c'est le nom de lalbum
	 *            que verra l'internaute.
	 */
	public AlbumNamesHelper(String humanName) {
		if (StringUtils.isNotBlank(humanName)) {
			this.humanName = humanName;
			this.languagesName = transformToLanguagesName(humanName);
			this.smallNamePattern = PREFIX_SMALL_IMAGE + languagesName.substring(0,1).toUpperCase() + languagesName.substring(1); 
		}
	}

	/**
	 * Getteur sur le nom humain.
	 * 
	 * @return Le nom humain de l'album.
	 */
	public String getHumanName() {
		return humanName;
	}

	/**
	 * Getteur sur le nom de l'abulm utilisé dans les fichiers php ou html.
	 * 
	 * @return Le nom de l'abulm utilisé dans les fichiers php ou html.
	 */
	public String getLanguagesName() {
		return languagesName;
	}

	/**
	 * Getteur sur le pattern utilisé pour les petites images listées dans la gallery.
	 * @return Le pattern utilisé pour les petites images listées dans la gallery.
	 */
	public String getSmallNamePattern() {
		return smallNamePattern;
	}

	private String transformToLanguagesName(String humanName) {
		String albumName = new String(humanName);

		albumName = StringUtils.replace(albumName, " ", "");
		albumName = StringUtils.replace(albumName, "'", "");
		albumName = StringUtils.replace(albumName, "#", "");
		albumName = StringUtils.stripAccents(albumName);

		return albumName.toLowerCase();
	}
}
