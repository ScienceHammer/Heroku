package app.core.exceptions;

public class LoginManagerException extends CouponsSystemException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginManagerException() {
    }

    public LoginManagerException(String message) {
        super(message);
    }

    public LoginManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginManagerException(Throwable cause) {
        super(cause);
    }
}
