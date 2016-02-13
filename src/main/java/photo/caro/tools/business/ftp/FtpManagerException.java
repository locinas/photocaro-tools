package photo.caro.tools.business.ftp;

public class FtpManagerException extends Exception {

	private static final long serialVersionUID = 1L;

	public FtpManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public FtpManagerException(String message) {
		super(message);
	}
}
