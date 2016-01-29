package photo.caro.tools.exceptions;

public class PhotocaroException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public PhotocaroException(String message, Throwable cause) {
		super(message, cause);
	}

	
}
