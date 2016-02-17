/**
 * 
 */
package photo.caro.tools.business.ftp;

import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

/**
 * Filtre sur des images Jpeg pour des FTPFile.
 * 
 * @author nicolas
 */
public class JpegFtpFileFilter implements FTPFileFilter {

	@Override
	public boolean accept(FTPFile file) {
		
		return file != null && FilenameUtils.isExtension(file.getName(), Arrays.asList("jpg", "jpeg", "JPEG", "JPG"));
	}

}
