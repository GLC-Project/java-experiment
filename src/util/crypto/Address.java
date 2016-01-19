package util.crypto;

import net.NetworkParameters;
import util.Exceptions.WrongNetworkException;

/**
 * <p>
 * A Litecoin address looks like 1MsScoe2fTJoq4ZPdQgqyhgWeoNamYPevy and is
 * derived from an elliptic curve public key plus a set of network parameters.
 * Not to be confused with a {@link PeerAddress} or {@link AddressMessage} which
 * are about network (TCP) addresses.
 * </p>
 * 
 * <p>
 * A standard address is built by taking the RIPE-MD160 hash of the public key
 * bytes, with a version prefix and a checksum suffix, then encoding it
 * textually as base58. The version prefix is used to both denote the network
 * for which the address is valid (see {@link NetworkParameters}, and also to
 * indicate how the bytes inside the address should be interpreted. Whilst
 * almost all addresses today are hashes of public keys, another (currently
 * unsupported type) can contain a hash of a script instead.
 * </p>
 */
public class Address extends VersionedChecksummedBytes {
    /**
     * An address is a RIPEMD160 hash of a public key, therefore is always 160
     * bits or 20 bytes.
     */
    public static final int LENGTH = 20;

    /**
     * Construct an address from parameters and the hash160 form. Example:
     * <p>
     * 
     * <pre>
     * new Address(NetworkParameters.prodNet(),
     * 	Hex.decode(&quot;4a22c3c4cbb31e4d03b15550636762bda0baf85a&quot;));
     * </pre>
     * 
     * @throws Exception
     */
    public Address(NetworkParameters params, byte[] hash160) throws Exception {
	super(params.addressHeader, hash160);
	if (hash160.length != 20) // 160 = 8 * 20
	    throw new RuntimeException(
		    "Addresses are 160-bit hashes, so you must provide 20 bytes"); //$NON-NLS-1$
    }

    /**
     * Construct an address from parameters and the standard "human readable"
     * form. Example:
     * <p>
     * 
     * <pre>
     * new Address(NetworkParameters.prodNet(), &quot;17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL&quot;);
     * </pre>
     * <p>
     * 
     * @param params
     *            The expected NetworkParameters or null if you don't want
     *            validation.
     * @param address
     *            The textual form of the address, such as
     *            "17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL"
     * @throws Exception
     */
    public Address(NetworkParameters params, String address) throws Exception {
	super(address);
	if (params != null) {
	    boolean found = false;
	    for (int v : params.acceptableAddressCodes) {
		if (this.version == v) {
		    found = true;
		    break;
		}
	    }
	    if (!found) {
		throw new WrongNetworkException(this.version,
			params.acceptableAddressCodes);
	    }
	}
    }

    /**
     * The (big endian) 20 byte hash that is the core of a Litecoin address.
     * 
     * @return
     */
    public byte[] getHash160() {
	return this.bytes;
    }

    /**
     * Examines the version byte of the address and attempts to find a matching
     * NetworkParameters. If you aren't sure which network the address is
     * intended for (eg, it was provided by a user), you can use this to decide
     * if it is compatible with the current wallet. You should be able to handle
     * a null response from this method. Note that the parameters returned is
     * not necessarily the same as the one the Address was created with.
     * 
     * @return a NetworkParameters representing the network the address is
     *         intended for, or null if unknown.
     */
    public NetworkParameters getParameters() {
	// TODO: There should be a more generic way to get all supported
	// networks.
	NetworkParameters[] networks = new NetworkParameters[] {
		NetworkParameters.testNet(), NetworkParameters.prodNet() };

	for (NetworkParameters params : networks) {
	    for (int code : params.acceptableAddressCodes) {
		if (code == this.version) {
		    return params;
		}
	    }
	}
	return null;
    }

    /**
     * Given an address, examines the version byte and attempts to find a
     * matching NetworkParameters. If you aren't sure which network the address
     * is intended for (eg, it was provided by a user), you can use this to
     * decide if it is compatible with the current wallet. You should be able to
     * handle a null response from this method.
     * 
     * @param address
     * @return a NetworkParameters representing the network the address is
     *         intended for, or null if unknown.
     * @throws Exception
     */
    public static NetworkParameters getParametersFromAddress(String address)
	    throws Exception {
	try {
	    return new Address(null, address).getParameters();
	} catch (WrongNetworkException e) {
	    // Cannot happen.
	    throw new RuntimeException(e);
	}
    }
}
