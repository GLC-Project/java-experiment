package util.Exceptions;

/**
 * @author Amir Eslampanah
 * 
 */
public class AddressFormatException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -2548980955149084729L;

    /**
     * 
     */
    public AddressFormatException() {
	super();
    }

    /**
     * @param message
     */
    public AddressFormatException(String message) {
	super(message);
    }
}
