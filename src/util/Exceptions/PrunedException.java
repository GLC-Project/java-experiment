package util.Exceptions;

import util.crypto.Sha256Hash;

/**
 * PrunedException is thrown in cases where a fully verifying node has deleted
 * (pruned) old block data that turned out to be necessary for handling a
 * re-org. Normally this should never happen unless you're playing with the
 * testnet as the pruning parameters should be set very conservatively, such
 * that an absolutely enormous re-org would be required to trigger it.
 */
public class PrunedException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 3384457235110664199L;
    private final Sha256Hash hash;

    /**
     * @param hash1
     */
    public PrunedException(Sha256Hash hash1) {
	super(hash1.toString());
	this.hash = hash1;
    }

    /**
     * @return hash
     */
    public Sha256Hash getHash() {
	return this.hash;
    }
}
