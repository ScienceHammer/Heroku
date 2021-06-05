package app.core.exceptions;

public class NotNullOrBlankException extends ServiceException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotNullOrBlankException() {
    }

    public NotNullOrBlankException(String message) {
        super(message);
    }

    public NotNullOrBlankException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotNullOrBlankException(Throwable cause) {
        super(cause);
    }
}
