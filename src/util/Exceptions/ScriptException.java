package util.Exceptions;

/**
 * @author Amir Eslampanah
 * 
 */
public class ScriptException extends VerificationException {

    /**
     * 
     */
    private static final long serialVersionUID = 3068712305740023220L;

    /**
     * @param msg
     */
    public ScriptException(String msg) {
	super(msg);
    }

    /**
     * @param msg
     * @param e
     */
    public ScriptException(String msg, Exception e) {
	super(msg, e);
    }
}
