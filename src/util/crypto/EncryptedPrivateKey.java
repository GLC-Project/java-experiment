package util.crypto;

import java.util.Arrays;

/**
 * <p>
 * An EncryptedPrivateKey contains the information produced after encrypting the
 * private key bytes of an ECKey.
 * </p>
 * 
 * <p>
 * It contains two member variables - initialisationVector and
 * encryptedPrivateBytes. The initialisationVector is a randomly chosen list of
 * bytes that were used to initialise the AES block cipher when the private key
 * bytes were encrypted. You need these for decryption. The
 * encryptedPrivateBytes are the result of AES encrypting the private keys using
 * an AES key that is drrived from a user entered password. You need the
 * password to recreate the AES key in order to decrypt these bytes.
 * </p>
 */
public class EncryptedPrivateKey {

    private byte[] initialisationVector = null;
    private byte[] encryptedPrivateBytes = null;

    /**
     * Cloning constructor.
     * 
     * @param encryptedPrivateKey
     *            EncryptedPrivateKey to clone.
     */
    public EncryptedPrivateKey(EncryptedPrivateKey encryptedPrivateKey) {
	Preconditions.checkNotNull(encryptedPrivateKey);
	setInitialisationVector(encryptedPrivateKey.getInitialisationVector());
	setEncryptedPrivateBytes(encryptedPrivateKey.getEncryptedBytes());
    }

    /**
     * @param iv
     * @param encryptedPrivateKeys
     */
    public EncryptedPrivateKey(byte[] initialisationVector,
	    byte[] encryptedPrivateKeys) {
	setInitialisationVector(initialisationVector);
	setEncryptedPrivateBytes(encryptedPrivateKeys);
    }

    public byte[] getInitialisationVector() {
	return this.initialisationVector;
    }

    /**
     * Set the initialisationVector, cloning the bytes.
     * 
     * @param initialisationVector
     */
    public void setInitialisationVector(byte[] initialisationVector) {
	if (initialisationVector == null) {
	    this.initialisationVector = null;
	    return;
	}

	byte[] cloneIV = new byte[initialisationVector.length];
	System.arraycopy(initialisationVector, 0, cloneIV, 0,
		initialisationVector.length);

	this.initialisationVector = cloneIV;
    }

    public byte[] getEncryptedBytes() {
	return this.encryptedPrivateBytes;
    }

    /**
     * Set the encrypted private key bytes, cloning them.
     * 
     * @param encryptedPrivateBytes
     */
    public void setEncryptedPrivateBytes(byte[] encryptedPrivateBytes) {
	if (encryptedPrivateBytes == null) {
	    this.encryptedPrivateBytes = null;
	    return;
	}

	this.encryptedPrivateBytes = Arrays.copyOf(encryptedPrivateBytes,
		encryptedPrivateBytes.length);
    }

    @Override
    public EncryptedPrivateKey clone() {
	return new EncryptedPrivateKey(getInitialisationVector(),
		getEncryptedBytes());
    }

    @Override
    public int hashCode() {
	return com.google.common.base.Objects.hashCode(
		this.encryptedPrivateBytes, this.initialisationVector);
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final EncryptedPrivateKey other = (EncryptedPrivateKey) obj;

	return com.google.common.base.Objects.equal(this.initialisationVector,
		other.initialisationVector)
		&& com.google.common.base.Objects
			.equal(this.encryptedPrivateBytes,
				other.encryptedPrivateBytes);
    }

    @Override
    public String toString() {
	return "EncryptedPrivateKey [initialisationVector=" //$NON-NLS-1$
		+ Arrays.toString(this.initialisationVector)
		+ ", encryptedPrivateKey=" //$NON-NLS-1$
		+ Arrays.toString(this.encryptedPrivateBytes) + "]"; //$NON-NLS-1$
    }

    /**
     * Clears all the EncryptedPrivateKey contents from memory (overwriting all
     * data including PRIVATE KEYS). WARNING - this method irreversibly deletes
     * the private key information.
     */
    public void clear() {
	if (this.encryptedPrivateBytes != null) {
	    Arrays.fill(this.encryptedPrivateBytes, (byte) 0);
	}
	if (this.initialisationVector != null) {
	    Arrays.fill(this.initialisationVector, (byte) 0);
	}
    }
}
