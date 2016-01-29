package photo.caro.tools.helper;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import org.fest.assertions.Assertions;
import org.junit.Test;

public class DirectoryHelperTest {
	
	@Test
	public void testSourceFolder() {
		DirectoryHelper helper = new DirectoryHelper("./src/test/resources/photo/caro/tools/helper");
		File sourceFolder = helper.getSourceFolder();
		
		assertThat(sourceFolder.isDirectory()).isTrue();
		assertThat(sourceFolder.getAbsolutePath()).endsWith("/src/test/resources/photo/caro/tools/helper");
	}
	
	@Test
	public void testTempFolder() {
		DirectoryHelper helper = new DirectoryHelper("./src/test/resources/photo/caro/tools/helper");
		File tempFolder = helper.getTempFolder();
		
		assertThat(tempFolder.isDirectory()).isTrue();
		assertThat(tempFolder.getAbsolutePath()).endsWith("/src/test/resources/photo/caro/tools/helper/temp");
	}
	
	@Test
	public void testListOriginalPicture() {
		DirectoryHelper helper = new DirectoryHelper("./src/test/resources/photo/caro/tools/helper");
		
		assertThat(helper.listOriginalPicture()).hasSize(2);
		for (File picture : helper.listOriginalPicture()) {
			assertThat(picture.getName()).endsWith(".jpg");
		}
	}
	
	@Test
	public void testEmptyPath() {
		DirectoryHelper helper = new DirectoryHelper("");
		
		assertThat(helper.getTempFolder()).isNull();
		assertThat(helper.getSourceFolder()).isNull();
		assertThat(helper.listOriginalPicture()).isEmpty();
	}
	
	@Test
	public void testNullPath() {
		DirectoryHelper helper = new DirectoryHelper(null);
		
		assertThat(helper.getTempFolder()).isNull();
		assertThat(helper.getSourceFolder()).isNull();
		assertThat(helper.listOriginalPicture()).isEmpty();
	}
}
