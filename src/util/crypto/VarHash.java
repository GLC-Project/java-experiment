package util.crypto;

import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;

import org.mightyfish.jce.provider.BouncyCastleProvider;

public class VarHash {
    /**
     * This class allows the construction of any kind of hash of any length and
     * any cipher
     * 
     * Methods for manipulating, constructing and dealing with the particular
     * hash type are also provided.
     * 
     * Both encrypted and non-encrypted hashes can be constructed here.
     * 
     */
    private final String salt = "7ad944"; //$NON-NLS-1$
    private final int iterations = 2000;
    private final int keyLength = 256;
    private final SecureRandom random = new SecureRandom();

    private String keyFactoryCipher;
    private String instanceCipher;
    private String passPhrase;
    private byte[] rawData;
    private byte[] ciphertext;

    /**
     * @param rawData1
     * @param passPhrase1
     * @param instanceCipher1
     * @param keyFactoryCipher1
     * @throws Exception
     */
    public VarHash(byte[] rawData1, String passPhrase1, String instanceCipher1,
	    String keyFactoryCipher1) throws Exception {
	Security.insertProviderAt(new BouncyCastleProvider(), 1);

	this.setPassPhrase(passPhrase1);
	this.setRawData(rawData1);
	this.setInstanceCipher(instanceCipher1);
	this.setKeyFactoryCipher(keyFactoryCipher1);

	this.setCiphertext(encrypt());
	String recoveredPlaintext = decrypt();

	System.out.println(recoveredPlaintext);
    }

    private byte[] encrypt() throws Exception {
	SecretKey key = generateKey();

	Cipher cipher = Cipher.getInstance(this.getInstanceCipher());
	cipher.init(Cipher.ENCRYPT_MODE, key, generateIV(cipher), this.random);
	return cipher.doFinal(this.getRawData());
    }

    private String decrypt() throws Exception {
	SecretKey key = generateKey();

	Cipher cipher = Cipher.getInstance(this.getInstanceCipher());
	cipher.init(Cipher.DECRYPT_MODE, key, generateIV(cipher), this.random);
	return new String(cipher.doFinal(this.getCiphertext()));
    }

    private SecretKey generateKey() throws Exception {
	PBEKeySpec keySpec = new PBEKeySpec(this.getPassPhrase().toCharArray(),
		this.getSalt().getBytes(), this.getIterations(),
		this.getKeyLength());
	SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(this
		.getKeyFactoryCipher());
	return keyFactory.generateSecret(keySpec);
    }

    private IvParameterSpec generateIV(Cipher cipher) throws Exception {
	byte[] ivBytes = new byte[cipher.getBlockSize()];
	this.random.nextBytes(ivBytes);
	return new IvParameterSpec(ivBytes);
    }

    /**
     * @return the keyFactoryCipher
     */
    public String getKeyFactoryCipher() {
	return this.keyFactoryCipher;
    }

    /**
     * @param keyFactoryCipher1
     *            the keyFactoryCipher to set
     */
    public void setKeyFactoryCipher(String keyFactoryCipher1) {
	this.keyFactoryCipher = keyFactoryCipher1;
    }

    /**
     * @return the instanceCipher
     */
    public String getInstanceCipher() {
	return this.instanceCipher;
    }

    /**
     * @param instanceCipher1
     *            the instanceCipher to set
     */
    public void setInstanceCipher(String instanceCipher1) {
	this.instanceCipher = instanceCipher1;
    }

    /**
     * @return the passPhrase
     */
    public String getPassPhrase() {
	return this.passPhrase;
    }

    /**
     * @param passPhrase1
     *            the passPhrase to set
     */
    public void setPassPhrase(String passPhrase1) {
	this.passPhrase = passPhrase1;
    }

    /**
     * @return the rawData
     */
    public byte[] getRawData() {
	return this.rawData;
    }

    /**
     * @param rawData1
     *            the rawData to set
     */
    public void setRawData(byte[] rawData1) {
	this.rawData = rawData1;
    }

    /**
     * @return the ciphertext
     */
    public byte[] getCiphertext() {
	return this.ciphertext;
    }

    /**
     * @param ciphertext1
     *            the ciphertext to set
     */
    public void setCiphertext(byte[] ciphertext1) {
	this.ciphertext = ciphertext1;
    }

    /**
     * @return the salt
     */
    public String getSalt() {
	return this.salt;
    }

    /**
     * @return the iterations
     */
    public int getIterations() {
	return this.iterations;
    }

    /**
     * @return the keyLength
     */
    public int getKeyLength() {
	return this.keyLength;
    }

}
