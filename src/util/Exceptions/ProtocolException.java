package util.Exceptions;

/**
 * @author Amir Eslampanah
 * 
 */
public class ProtocolException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -7311859229428037183L;

    /**
     * @param msg
     */
    public ProtocolException(String msg) {
	super(msg);
    }

    /**
     * @param e
     */
    public ProtocolException(Exception e) {
	super(e);
    }

    /**
     * @param msg
     * @param e
     */
    public ProtocolException(String msg, Exception e) {
	super(msg, e);
    }
}
