package app.core.exceptions;

public class NotFoundExceptions extends ServiceException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotFoundExceptions() {
    }

    public NotFoundExceptions(String message) {
        super(message);
    }

    public NotFoundExceptions(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundExceptions(Throwable cause) {
        super(cause);
    }
}
