package app.core.exceptions;

public class CouponsSystemException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CouponsSystemException() {
    }

    public CouponsSystemException(String message) {
        super(message);
    }

    public CouponsSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouponsSystemException(Throwable cause) {
        super(cause);
    }
}
