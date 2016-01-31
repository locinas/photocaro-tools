package photo.caro.tools.helper;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;

public class JpegExtensionFileFilter implements FileFilter {

	@Override
	public boolean accept(File arg0) {
		
		return arg0 != null && FilenameUtils.isExtension(arg0.getName(), Arrays.asList("jpg", "jpeg"));
	}
}
