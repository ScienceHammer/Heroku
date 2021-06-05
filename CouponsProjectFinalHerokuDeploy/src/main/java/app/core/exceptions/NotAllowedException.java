package app.core.exceptions;

public class NotAllowedException extends ServiceException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotAllowedException() {
    }

    public NotAllowedException(String message) {
        super(message);
    }

    public NotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAllowedException(Throwable cause) {
        super(cause);
    }
}
