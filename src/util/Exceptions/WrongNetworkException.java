package util.Exceptions;

import java.util.Arrays;

/**
 * This exception is thrown by the Address class when you try and decode an
 * address with a version code that isn't used by that network. You shouldn't
 * allow the user to proceed in this case as they are trying to send money
 * across different chains, an operation that is guaranteed to destroy the
 * money.
 * 
 * @author Amir Eslampanah
 * 
 */
public class WrongNetworkException extends AddressFormatException {
    /**
     * 
     */
    private static final long serialVersionUID = 93674411899996664L;

    /** The version code that was provided in the address. */
    public int verCode;
    /**
     * The list of acceptable versions that were expected given the addresses
     * network parameters.
     */
    public int[] acceptableVersions;

    /**
     * @param verCode1
     * @param acceptableVersions1
     */
    public WrongNetworkException(int verCode1, int[] acceptableVersions1) {
	super("Version code of address did not match acceptable versions for network: " //$NON-NLS-1$
		+ verCode1 + " not in " //$NON-NLS-1$
		+ Arrays.toString(acceptableVersions1));
	this.verCode = verCode1;
	this.acceptableVersions = acceptableVersions1;
    }
}
