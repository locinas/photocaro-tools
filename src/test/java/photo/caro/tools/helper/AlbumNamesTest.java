package photo.caro.tools.helper;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import photo.caro.tools.helper.AlbumNamesHelper;

public class AlbumNamesTest {
	
	@Test
	public void testWithWhiteSpaceAndAccent() {
		AlbumNamesHelper names = new AlbumNamesHelper("A l'orée du bois");
		assertThat(names.getHumanName()).isEqualTo("A l'orée du bois");
		assertThat(names.getLanguagesName()).isEqualTo("aloreedubois");
		assertThat(names.getSmallNamePattern()).isEqualTo("smallAloreedubois");
	}
	
	@Test
	public void testCasNominal() {
		AlbumNamesHelper names = new AlbumNamesHelper("Insectes");
		assertThat(names.getHumanName()).isEqualTo("Insectes");
		assertThat(names.getLanguagesName()).isEqualTo("insectes");
		assertThat(names.getSmallNamePattern()).isEqualTo("smallInsectes");
	}
	
	@Test
	public void testCasNullParameter() {
		AlbumNamesHelper names = new AlbumNamesHelper(null);
		assertThat(names.getHumanName()).isNull();
		assertThat(names.getLanguagesName()).isNull();
		assertThat(names.getSmallNamePattern()).isNull();
	}
	
	@Test
	public void testCasEmptyStringParameter() {
		AlbumNamesHelper names = new AlbumNamesHelper("");
		assertThat(names.getHumanName()).isNull();
		assertThat(names.getLanguagesName()).isNull();
		assertThat(names.getSmallNamePattern()).isNull();
	}
}
