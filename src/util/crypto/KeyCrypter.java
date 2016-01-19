package util.crypto;

import java.io.Serializable;

import org.mightyfish.crypto.params.KeyParameter;

import util.Exceptions.KeyCrypterException;

/**
 * <p>
 * A KeyCrypter can be used to encrypt and decrypt a message. The sequence of
 * events to encrypt and then decrypt a message are as follows:
 * </p>
 * 
 * <p>
 * (1) Ask the user for a password. deriveKey() is then called to create an
 * KeyParameter. This contains the AES key that will be used for encryption.
 * </p>
 * <p>
 * (2) Encrypt the message using encrypt(), providing the message bytes and the
 * KeyParameter from (1). This returns an EncryptedPrivateKey which contains the
 * encryptedPrivateKey bytes and an initialisation vector.
 * </p>
 * <p>
 * (3) To decrypt an EncryptedPrivateKey, repeat step (1) to get a KeyParameter,
 * then call decrypt().
 * </p>
 * 
 * <p>
 * There can be different algorithms used for encryption/ decryption so the
 * getUnderstoodEncryptionType is used to determine whether any given KeyCrypter
 * can understand the type of encrypted data you have.
 * </p>
 */
public interface KeyCrypter extends Serializable {

    /**
     * Return the EncryptionType enum value which denotes the type of
     * encryption/ decryption that this KeyCrypter can understand.
     */
    public EncryptionType getUnderstoodEncryptionType();

    /**
     * Create a KeyParameter (which typically contains an AES key)
     * 
     * @param password
     * @return KeyParameter The KeyParameter which typically contains the AES
     *         key to use for encrypting and decrypting
     * @throws KeyCrypterException
     */
    public KeyParameter deriveKey(CharSequence password)
	    throws KeyCrypterException;

    /**
     * Decrypt the provided encrypted bytes, converting them into unencrypted
     * bytes.
     * 
     * @param encryptedBytesToDecode
     * @param aesKey
     * @return
     * 
     * @throws KeyCrypterException
     *             if decryption was unsuccessful.
     */
    public byte[] decrypt(EncryptedPrivateKey encryptedBytesToDecode,
	    KeyParameter aesKey) throws KeyCrypterException;

    /**
     * Encrypt the supplied bytes, converting them into ciphertext.
     * 
     * @return encryptedPrivateKey An encryptedPrivateKey containing the
     *         encrypted bytes and an initialisation vector.
     * @throws KeyCrypterException
     *             if encryption was unsuccessful
     */
    public EncryptedPrivateKey encrypt(byte[] plainBytes, KeyParameter aesKey)
	    throws KeyCrypterException;
}
