package util.Exceptions;

/**
 * @author Amir Eslampanah
 * 
 */
public class VerificationException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 6733831738203918587L;

    /**
     * @param msg
     */
    public VerificationException(String msg) {
	super(msg);
    }

    /**
     * @param msg
     * @param t
     */
    public VerificationException(String msg, Throwable t) {
	super(msg, t);
    }
}
