/**
 * 
 */
package photo.caro.tools.business.globalvalidation;

/**
 * Représente une erreur de validation du site.
 * 
 * @author nicolas
 */
public class ValidationError {
	/** Le message d'erreur de validation. */
	private String message;
	/** Le niveau de sévérité de l'erreur. */
	private SeverityErrorEnum severity;

	/**
	 * Constructeur.
	 * 
	 * @param severity Le niveau de sévérité de l'erreur.
	 * @param message Le message d'erreur de validation.
	 */
	public ValidationError(SeverityErrorEnum severity, String message) {
		this.message = message;
		this.severity = severity;
	}

	public String getMessage() {
		return message;
	}

	public SeverityErrorEnum getSeverity() {
		return severity;
	}
}
