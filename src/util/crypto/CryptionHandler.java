package util.crypto;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.mightyfish.crypto.generators.SCrypt;
import org.mightyfish.openssl.EncryptionException;

/**
 * This class allows for abstraction of encryption sequences.
 * 
 * Some things in this class may seem excessive or obtuse, but there are
 * cryptographically sound reasons for implementing them in that way.
 * 
 * Same goes for the round-about way of accessing/setting certain fields.
 * 
 * The Java Security Manager can play in heavily here. (at least when this class
 * is used as a part of GoldCoin (GLD))
 * 
 * 
 * All input into this class NEEDS to be a copy!
 * 
 * Do not half-hazardly change anything in this class!
 * 
 * Note not all combinations are possible. Please check your modes beforehand.
 * 
 * @author Amir Eslampanah
 * 
 */
public class CryptionHandler {

    /**
     * 
     * Note: GOST28147 and XSALSA20 and true HC256 cannot function without the
     * installation of
     * http://www.oracle.com/technetwork/java/javase/downloads/jce
     * -7-download-432124.html
     * 
     * This is due to USA export laws on encryption (Oracle is a US based
     * company).
     */
    @SuppressWarnings("nls")
    public static final String[] encryptionModes = { "AES", "BLOWFISH",
	    "CAMELLIA", "CAST5", "CAST6", "DES", "DESEDE", "GCM", "GOST28147",
	    "IDEA", "NOEKEON", "RC2", "RC5-32", "RC5-64", "RC6", "RIJNDAEL",
	    "SEED", "SERPENT", "SKIPJACK", "TEA", "THREEFISH-256",
	    "THREEFISH-512", "THREEFISH-1024", "TWOFISH", "XTEA", "RC4",
	    "HC128", "HC256", "CHACHA", "SALSA20", "XSALSA20", "VMPC",
	    "GRAINV1", "GRAIN128", "RSA", "DSA", "CDSA", "MGF1", "CNR",
	    "GOST3410", "ELGAMAL", "ECDSA", "ECGOST3410", "ISO9796d2", "PSS",
	    "NONE" };

    /*
     * "AES", "BLOWFISH", "CAMEILLIA", "CAST5", "CAST6", "DES", "DESede", "GCM",
     * "GOST28147", "CHACHA", "DESEDE3", "IDEA", "NOEKEON", "RC2", "RC5",
     * "RC5-64", "RC6", "RIJNDAEL", "SEED", "POLY1305-SEED", "SERPENT",
     * "POLY1305-SERPENT", "SHACAL2", "SIPHASH", "SKIPJACK", "TEA",
     * "THREEFISH-256", "THREEFISH-512", "THREEFISH-1024", "TWOFISH",
     * "POLY1305-TWOFISH", "XTEA", "RC4", "HC128", "HC256", "PBKDF2", "CHACHA",
     * "SALSA20", "XSALSA20", "ISAAC", "VMPC", "VMPC-KSA3", "GRAIN128",
     * "GRAINV1", "RSA", "ELGAMAL", "NTRU", "SCRYPT"
     */

    /**	
     * 
     */
    @SuppressWarnings("nls")
    public static final String[] paddingModes = { "PKCS5PADDING",
	    "PKCS7PADDING", "ISO10126PADDING", "ISO10126-2PADDING",
	    "X9.23PADDING", "ISO7816-4PADDING", "ISO9797-1PADDING",
	    "X923PADDING", "TBCPADDING", "ZEROBYTEPADDING", "WITHCTS",
	    "NOPADDING" };
    /**
     * 
     */
    @SuppressWarnings("nls")
    public static final String[] symmetricCipherModes = { "ECB", "CBC", "SIC",
	    "CTR", "OpenPGPCFB", "CTS", "GOFB", "GCFB", "CCM", "EAX", "GCM",
	    "OCB", "OFB8", "CFB8", "NONE" };

    /**
     * 
     */
    @SuppressWarnings("nls")
    public static final String[] asymmetricCipherModes = { "OAEP", "PKCS1",
	    "ISO9796d1", "NONE" };

    /**
     * 
     */
    @SuppressWarnings("nls")
    public static final String[] digestModes = { "MD2", "MD4", "MD5",
	    "RIPEMD128", "RIPEMD160", "RIPEMD256", "RIPEMD320", "SHA1",
	    "SHA224", "SHA256", "SHA384", "SHA512", "SHA3-224", "SHA3-256",
	    "SHA3-384", "SHA3-512", "Skein-256-128", "Skein-256-160",
	    "Skein-256-224", "Skein-256-256", "Skein-512-128", "Skein-512-160",
	    "Skein-512-224", "Skein-512-256", "Skein-512-384", "Skein-512-512",
	    "Skein-1024-384", "Skein-1024-512", "Skein-1024-1024", "SM3",
	    "TIGER", "GOST3411", "WHIRLPOOL", "NONE" };

    /**
     * 
     */
    @SuppressWarnings("nls")
    public static final String[] MACModes = { "CBC", "CFB", "CMAC", "128-GMAC",
	    "GOST28147", "ISO9797ALG3", "HMAC", "POLY1305-AES",
	    "POLY1305-SERPENT", "SKEINMAC", "SIPHASH", "VMPC-MAC", "NONE",
	    "HMAC-MD2", "HMAC-MD4", "HMAC-MD5", "HMAC-RIPEMD128",
	    "HMAC-RIPEMD160", "HMAC-RIPEMD256", "HMAC-RIPEMD320", "HMAC-SHA1",
	    "HMAC-SHA224", "HMAC-SHA256", "HMAC-SHA384", "HMAC-SHA512",
	    "HMAC-SHA3-224", "HMAC-SHA3-256", "HMAC-SHA3-384", "HMAC-SHA3-512",
	    "HMAC-Skein-256-128", "HMAC-Skein-256-160", "HMAC-Skein-256-224",
	    "HMAC-Skein-256-256", "HMAC-Skein-512-128", "HMAC-Skein-512-160",
	    "HMAC-Skein-512-224", "HMAC-Skein-512-256", "HMAC-Skein-512-384",
	    "HMAC-Skein-512-512", "HMAC-Skein-1024-384", "HMAC-Skein-1024-512",
	    "HMAC-Skein-1024-1024", "HMAC-TIGER", "HMAC-GOST3411",
	    "HMAC-WHIRLPOOL", "NONE" };

    /**
     * 
     * "PKCS5S1", "PKCS5S2", "PKCS12PBE", "OPENSSL"
     */
    @SuppressWarnings("nls")
    public static final String[] PBEModes = { "PBEWITHMD2ANDDES",
	    "PBEWITHMD2ANDRC2", "PBEWITHMD5ANDDES", "PBEWITHMD5ANDRC2",
	    "PBEWITHSHA1ANDDES", "PBEWITHSHA1ANDRC2", "PBKDF2WITHHMACSHA1",
	    "PBKDF2WITHHMACSHA1ANDUTF8", "PBKDF2WITHHMACSHA1AND8BIT",
	    "PBEWITHSHAAND2-KEYTRIPLEDES-CBC",
	    "PBEWITHSHAAND3-KEYTRIPLEDES-CBC", "PBEWITHSHAAND128BITRC2-CBC",
	    "PBEWITHSHAAND40BITRC2-CBC", "PBEWITHSHAAND128BITRC4",
	    "PBEWITHSHAAND40BITRC4", "PBEWITHSHAANDTWOFISH-CBC",
	    "PBEWITHSHAANDIDEA-CBC", "NONE" };

    /**
     * 
     */
    @SuppressWarnings("nls")
    public static final String[] signatureModes = { "DSTU4145",
	    "GOST3411WITHGOST3410", "GOST3411WITHECGOST3410", "MD2WITHRSA",
	    "MD5WITHRSA", "SHA1WITHRSA", "RIPEMD128WITHRSA",
	    "RIPEMD160WITHRSA", "RIPEMD160WITHECDSA", "RIPEMD256WITHRSA",
	    "SHA1WITHDSA", "SHA224WITHDSA", "SHA256WITHDSA", "SHA384WITHDSA",
	    "SHA512WITHDSA", "SHA1WITHDETDSA", "SHA224WITHDETDSA",
	    "SHA256WITHDETDSA", "SHA384WITHDETDSA", "SHA512WITHDETDSA",
	    "NONEWITHDSA", "SHA1WITHDETECDSA", "SHA224WITHDETECDSA",
	    "SHA256WITHDETECDSA", "SHA384WITHDETECDSA", "SHA512WITHDETECDSA",
	    "SHA1WITHECDSA", "NONEWITHECDSA", "SHA224WITHECDSA",
	    "SHA256WITHECDSA", "SHA384WITHECDSA", "SHA512WITHECDSA",
	    "SHA1WITHECNR", "SHA224WITHECNR", "SHA256WITHECNR",
	    "SHA384WITHECNR", "SHA512WITHECNR", "SHA224WITHRSA",
	    "SHA256WITHRSA", "SHA384WITHRSA", "SHA512WITHRSA",
	    "SHA1WITHRSAANDMGF1", "SHA256WITHRSAANDMGF1",
	    "SHA384WITHRSAANDMGF1", "SHA512WITHRSAANDMGF1" };

    @SuppressWarnings("nls")
    private String curEncryptionMode = "NONE";
    @SuppressWarnings("nls")
    private String curPaddingMode = "NoPadding";
    @SuppressWarnings("nls")
    private String curSymmetricCipherMode = "NONE";
    @SuppressWarnings("nls")
    private String curAsymmetricCipherMode = "NONE";
    @SuppressWarnings("nls")
    private String curDigestMode = "NONE";
    @SuppressWarnings("nls")
    private String curMACMode = "NONE";
    @SuppressWarnings("nls")
    private String curSignatureMode = "NONE";
    @SuppressWarnings("nls")
    private String curPBEMode = "NONE";

    /**
     * 
     */
    public CryptionHandler() {
    }

    /**
     * @param encryptionMode
     * @param paddingMode
     * @param symmetricCipherMode
     * @param asymmetricCipherMode
     * @param digestMode
     * @param MACMode
     * @param PBEMode
     * 
     */
    public CryptionHandler(String encryptionMode, String paddingMode,
	    String symmetricCipherMode, String asymmetricCipherMode,
	    String digestMode, String MACMode, String PBEMode) {
	if (encryptionMode != null) {
	    boolean validEncryptMode = false;

	    for (int x = 0; x < CryptionHandler.encryptionModes.length; x++) {
		if (encryptionMode == CryptionHandler.encryptionModes[x]) {
		    validEncryptMode = true;
		}
	    }

	    if (validEncryptMode) {
		this.curEncryptionMode = new String(encryptionMode);
	    }

	}

	if (paddingMode != null) {
	    boolean validPaddingMode = false;

	    for (int x = 0; x < CryptionHandler.paddingModes.length; x++) {
		if (paddingMode == CryptionHandler.paddingModes[x]) {
		    validPaddingMode = true;
		}
	    }

	    if (validPaddingMode) {
		this.curPaddingMode = new String(paddingMode);
	    }

	}

	if (symmetricCipherMode != null) {
	    boolean validSymCipherMode = false;

	    for (int x = 0; x < CryptionHandler.symmetricCipherModes.length; x++) {
		if (symmetricCipherMode == CryptionHandler.symmetricCipherModes[x]) {
		    validSymCipherMode = true;
		}
	    }

	    if (validSymCipherMode) {
		this.curSymmetricCipherMode = new String(symmetricCipherMode);
	    }

	}

	if (asymmetricCipherMode != null) {
	    boolean validAsymCipherMode = false;

	    for (int x = 0; x < CryptionHandler.asymmetricCipherModes.length; x++) {
		if (asymmetricCipherMode == CryptionHandler.asymmetricCipherModes[x]) {
		    validAsymCipherMode = true;
		}
	    }

	    if (validAsymCipherMode) {
		this.curAsymmetricCipherMode = new String(asymmetricCipherMode);
	    }

	}

	if (digestMode != null) {
	    boolean validDigestMode = false;

	    for (int x = 0; x < CryptionHandler.digestModes.length; x++) {
		if (digestMode == CryptionHandler.digestModes[x]) {
		    validDigestMode = true;
		}
	    }

	    if (validDigestMode) {
		this.curDigestMode = new String(digestMode);
	    }

	}

	if (MACMode != null) {
	    boolean validMACMode = false;

	    for (int x = 0; x < CryptionHandler.MACModes.length; x++) {
		if (MACMode == CryptionHandler.MACModes[x]) {
		    validMACMode = true;
		}
	    }

	    if (validMACMode) {
		this.curMACMode = new String(MACMode);
	    }

	}

	if (PBEMode != null) {
	    boolean validPBEMode = false;

	    for (int x = 0; x < CryptionHandler.PBEModes.length; x++) {
		if (PBEMode == CryptionHandler.PBEModes[x]) {
		    validPBEMode = true;
		}
	    }

	    if (validPBEMode) {
		this.curPBEMode = new String(PBEMode);
	    }

	}

    }

    /**
     * @param data
     * @param keyBytes
     *            Data that needs to be encrypted
     * @param ivBytes
     *            Randomizer Bytes/Nonce value (must be shared publicly for
     *            decryption!)
     * @param iterations
     *            Number of times to run the encryption algorithm
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws ShortBufferException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    @SuppressWarnings("nls")
    public byte[] encrypt(byte[] data, byte[] keyBytes, byte[] ivBytes,
	    int iterations) throws NoSuchAlgorithmException,
	    NoSuchProviderException, NoSuchPaddingException,
	    InvalidKeyException, InvalidAlgorithmParameterException,
	    ShortBufferException, IllegalBlockSizeException,
	    BadPaddingException {

	ByteBuffer data2 = ByteBuffer.wrap(Arrays.copyOf(data, data.length));
	byte[] keyBytes2 = Arrays.copyOf(keyBytes, keyBytes.length);
	byte[] ivBytes2 = Arrays.copyOf(ivBytes, ivBytes.length);

	// Initialize our Key and IV
	SecretKeySpec key = new SecretKeySpec(keyBytes2,
		this.getCurEncryptionMode());
	IvParameterSpec ivSpec = new IvParameterSpec(ivBytes2);

	String cipherMode = (this.getCurSymmetricCipherMode().compareTo("NONE")) == 0 ? this
		.getCurAsymmetricCipherMode() : this
		.getCurSymmetricCipherMode();

	// Create a new cipher based on our encryption mode
	Cipher cipher = Cipher.getInstance(this.getCurEncryptionMode() + "/"
		+ cipherMode + "/" + this.getCurPaddingMode(), "BC");

	if (this.getCurEncryptionMode().compareTo("RC5-64") == 0) {
	    RC5ParameterSpec a = new RC5ParameterSpec(1, 18, 64, ivBytes2);

	    try {
		cipher.init(Cipher.ENCRYPT_MODE, key, a);
	    } catch (Exception e) {
		a = new RC5ParameterSpec(1, 18, 64);
		cipher.init(Cipher.ENCRYPT_MODE, key, a);
	    }
	} else if (this.getCurEncryptionMode().compareTo("RC2") == 0) {
	    RC2ParameterSpec a = new RC2ParameterSpec(keyBytes2.length,
		    ivBytes2);
	    cipher.init(Cipher.ENCRYPT_MODE, key, a);

	} else if (this.getCurEncryptionMode().compareTo("RC4") == 0
		|| this.getCurEncryptionMode().compareTo("RSA") == 0
		|| this.getCurEncryptionMode().compareTo("ELGAMAL") == 0) {

	    KeyFactory keyFactory = KeyFactory.getInstance(this
		    .getCurEncryptionMode());
	    EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyBytes2);
	    PublicKey publicKey2;
	    try {
		publicKey2 = keyFactory.generatePublic(publicKeySpec);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey2);
	    } catch (InvalidKeySpecException e) {
		cipher.init(Cipher.ENCRYPT_MODE, key);
	    }

	} else {
	    try {
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
	    } catch (Exception e) {
		cipher.init(Cipher.ENCRYPT_MODE, key);
	    }
	}

	int ctLength = 0;
	ByteBuffer cipherText = null;

	for (int x = 0; x < iterations; x++) {
	    cipherText = ByteBuffer.wrap(new byte[cipher.getOutputSize(data2
		    .limit())]);

	    ctLength += cipher.update(data2.array(), 0, data2.limit(),
		    cipherText.array(), 0);

	    data2 = ByteBuffer
		    .wrap(Arrays.copyOf(data2.array(), data2.limit()));
	}

	ctLength += cipher.doFinal(cipherText.array(), ctLength);

	return cipherText.array();
    }

    /**
     * @param data
     * @param keyBytes
     *            Data that needs to be encrypted
     * @param ivBytes
     *            Randomizer Bytes/Nonce value (must be shared publicly for
     *            decryption!)
     * @param iterations
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws ShortBufferException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    @SuppressWarnings("nls")
    public byte[] decrypt(byte[] data, byte[] keyBytes, byte[] ivBytes,
	    int iterations) throws NoSuchAlgorithmException,
	    NoSuchProviderException, NoSuchPaddingException,
	    InvalidKeyException, InvalidAlgorithmParameterException,
	    ShortBufferException, IllegalBlockSizeException,
	    BadPaddingException {

	ByteBuffer data2 = ByteBuffer.wrap(Arrays.copyOf(data, data.length));
	byte[] keyBytes2 = Arrays.copyOf(keyBytes, keyBytes.length);
	byte[] ivBytes2 = Arrays.copyOf(ivBytes, ivBytes.length);

	// Initialize our Key and IV
	SecretKeySpec key = new SecretKeySpec(keyBytes2,
		this.getCurEncryptionMode());
	IvParameterSpec ivSpec = new IvParameterSpec(ivBytes2);

	String cipherMode = (this.getCurSymmetricCipherMode().compareTo("NONE")) == 0 ? this
		.getCurAsymmetricCipherMode() : this
		.getCurSymmetricCipherMode();

	// Create a new cipher based on our encryption mode
	Cipher cipher = Cipher.getInstance(this.getCurEncryptionMode() + "/"
		+ cipherMode + "/" + this.getCurPaddingMode(), "BC");

	// Set to De-crypt
	if (this.getCurEncryptionMode().compareTo("RC5-64") == 0) {
	    RC5ParameterSpec a = new RC5ParameterSpec(1, 18, 64, ivBytes2);

	    try {
		cipher.init(Cipher.DECRYPT_MODE, key, a);
	    } catch (Exception e) {
		a = new RC5ParameterSpec(1, 18, 64);
		cipher.init(Cipher.DECRYPT_MODE, key, a);
	    }
	} else if (this.getCurEncryptionMode().compareTo("RC2") == 0) {
	    RC2ParameterSpec a = new RC2ParameterSpec(keyBytes2.length,
		    ivBytes2);
	    cipher.init(Cipher.DECRYPT_MODE, key, a);

	} else if (this.getCurEncryptionMode().compareTo("RC4") == 0
		|| this.getCurEncryptionMode().compareTo("RSA") == 0
		|| this.getCurEncryptionMode().compareTo("ElGamal") == 0) {
	    KeyFactory keyFactory = KeyFactory.getInstance(this
		    .getCurEncryptionMode());
	    EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(keyBytes2);
	    PrivateKey privateKey2;
	    try {
		privateKey2 = keyFactory.generatePrivate(privateKeySpec);
		cipher.init(Cipher.DECRYPT_MODE, privateKey2);
	    } catch (InvalidKeySpecException e) {
		cipher.init(Cipher.DECRYPT_MODE, key);
	    }

	} else {
	    try {
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
	    } catch (Exception e) {
		cipher.init(Cipher.DECRYPT_MODE, key);
	    }
	}

	int ctLength = 0;
	ByteBuffer plainText = null;

	for (int x = 0; x < iterations; x++) {
	    plainText = ByteBuffer.wrap(new byte[cipher.getOutputSize(data2
		    .limit())]);

	    ctLength += cipher.update(data2.array(), 0, data2.limit(),
		    plainText.array(), 0);

	    data2 = ByteBuffer
		    .wrap(Arrays.copyOf(data2.array(), data2.limit()));
	}

	ctLength += cipher.doFinal(plainText.array(), ctLength);

	plainText = ByteBuffer.wrap(Arrays.copyOf(plainText.array(), ctLength));
	return plainText.array();
    }

    /**
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    @SuppressWarnings("nls")
    public byte[] calculateDigest(byte[] data) throws NoSuchAlgorithmException,
	    NoSuchProviderException {
	MessageDigest digest = MessageDigest.getInstance(
		this.getCurDigestMode(), "BC");

	digest.update(data);

	return digest.digest();

    }

    /**
     * @param data
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @author Snippet from org.bouncycastle.jce.provider.test
     * @author Modified by Amir Eslampanah
     */
    @SuppressWarnings("nls")
    public byte[] calculateMAC(byte[] data, byte[] keyBytes)
	    throws NoSuchAlgorithmException, NoSuchProviderException,
	    InvalidKeyException {

	byte[] data2 = Arrays.copyOf(data, data.length);
	byte[] keyBytes2 = Arrays.copyOf(keyBytes, keyBytes.length);

	SecretKey key = new SecretKeySpec(keyBytes2, this.getCurMACMode());
	byte[] out;
	Mac mac;

	mac = Mac.getInstance(
		this.getCurMACMode() + "/" + this.getCurDigestMode(), "BC");

	mac.init(key);

	mac.reset(); // Ensure MAC wasn't messed with in memory.

	mac.update(data2, 0, data2.length);

	out = mac.doFinal();
	/*
	 * // no key generator for the old algorithms if
	 * (this.getCurMACMode().startsWith("Old")) { return out; }
	 * 
	 * KeyGenerator kGen = KeyGenerator .getInstance(this.getCurMACMode(),
	 * "BC");
	 * 
	 * mac.init(kGen.generateKey());
	 * 
	 * mac.update(data2);
	 * 
	 * out = mac.doFinal();
	 */

	return out;
    }

    /**
     * @param data
     * @param keyBytes
     * @param macBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @author Snippet from org.bouncycastle.jce.provider.test
     * @author Modified by Amir Eslampanah
     */
    @SuppressWarnings("nls")
    public boolean authenticateMAC(byte[] data, byte[] keyBytes, byte[] macBytes)
	    throws NoSuchAlgorithmException, NoSuchProviderException,
	    InvalidKeyException {

	byte[] data2 = Arrays.copyOf(data, data.length);
	byte[] keyBytes2 = Arrays.copyOf(keyBytes, keyBytes.length);
	byte[] macBytes2 = Arrays.copyOf(macBytes, macBytes.length);

	SecretKey key = new SecretKeySpec(keyBytes2, this.getCurMACMode());
	byte[] out;
	Mac mac;

	mac = Mac.getInstance(
		this.getCurMACMode() + "/" + this.getCurDigestMode(), "BC");

	mac.init(key);

	mac.reset(); // Ensure MAC wasn't messed with in memory.

	mac.update(data2, 0, data2.length);

	out = mac.doFinal();

	// no key generator for the old algorithms
	if (this.getCurMACMode().startsWith("Old")) {
	    if (Arrays.equals(out, macBytes2)) {
		return true;
	    }
	    return false;
	}

	KeyGenerator kGen = KeyGenerator
		.getInstance(this.getCurMACMode(), "BC");

	mac.init(kGen.generateKey());

	mac.update(data2);

	out = mac.doFinal();

	if (Arrays.equals(out, macBytes2)) {
	    return true;
	}
	return false;
    }

    /**
     * Scrypt takes three tuning parameters: N, r and p. They affect running
     * time and memory usage:
     * 
     * Memory usage is approximately 128*r*N bytes. Note that the function takes
     * log_2(N) as a parameter. As an
     * 
     * example, the defaultParams
     * 
     * N = 14, r = 8 and p = 1 lead to scrypt using 128 * 8 * 2^14 = 16M bytes
     * of memory.
     * 
     * Running time is proportional to all of N, r and p. Since it's influence
     * on memory usage is small, p can be used to
     * 
     * independently tune the running time.
     * 
     * 
     * @param message
     *            Passphrase or message you want to sign
     * @param salt
     *            Salt to use
     * @param N
     *            General work factor, iteration count
     * @param r
     *            blocksize in use for underlying hash; fine-tunes the relative
     *            memory-cost.
     * @param p
     *            parallelization factor; fine-tunes the relative cpu-cost.
     * @param dkLen
     *            Intended output length in octets of the derived key
     * @return
     * 
     *         TODO: Check if parameter N is raised over 2.
     * @throws Exception
     */
    @SuppressWarnings("nls")
    public byte[] Scrypt(byte[] message, byte[] salt, int N, int r, int p,
	    int dkLen) throws Exception {

	if (r <= 0) {
	    throw new Exception(
		    "Scrypt: Value of parameter: r must be greater than zero.");
	}

	if (p <= 0) {
	    throw new Exception(
		    "Scrypt: Value of parameter: p must be greater than zero.");
	}

	if (Math.multiplyExact(r, p) >= Math.pow(2, 30)) {

	    throw new Exception(
		    "Scrypt: Value of parameters r and p must satisfy r*p < 2^30.");
	}

	if (dkLen > (Math.pow(2, 32) - 1) * 32) {
	    throw new Exception(
		    "Scrypt: Requested Key Length (dkLen) is too long");
	}

	return SCrypt.generate(message, salt, N, r, p, dkLen);

    }

    /**
     * Encrypts the given byte data with the given password using the AES
     * algorithm.
     * 
     * @param unencryptedByteData
     *            the byte data to encrypt
     * @param password
     *            the password used for encryption
     * @return the encrypted data as array of bytes
     * @throws EncryptionException
     *             when a problem occured during the encryption process
     */
    @SuppressWarnings("nls")
    public byte[] encryptPBE(byte[] unencryptedByteData, char[] password,
	    byte[] salt, int iterations) throws EncryptionException {

	byte[] encryptedData;

	PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
	PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt,
		iterations);

	try {
	    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(
		    this.getCurPBEMode(), "BC");
	    SecretKey pbeKey = secretKeyFactory.generateSecret(pbeKeySpec);

	    // Generate and initialize a PBE cipher
	    Cipher cipher = Cipher.getInstance(this.getCurPBEMode(), "BC");
	    cipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParameterSpec);

	    // encrypt
	    encryptedData = cipher.doFinal(unencryptedByteData);

	} catch (GeneralSecurityException e) {
	    throw new EncryptionException(
		    "There was a problem during the encryption process. See the stacktrace for details.",
		    e);
	}
	encryptedData = (encryptedData == null) ? new byte[] {} : encryptedData;
	pbeKeySpec.clearPassword();
	return encryptedData;
    }

    /**
     * Decrypts the given byte data with the given password using the AES
     * algorithm.
     * 
     * @param encryptedByteData
     *            the byte data to decrypt
     * @param password
     *            the password used for decryption
     * @return the decrypted data as array of bytes
     * @throws Exception
     */
    @SuppressWarnings("nls")
    public byte[] decryptPBE(byte[] encryptedByteData, char[] password,
	    byte[] salt, int iterations) throws Exception {

	byte[] decryptedData;

	PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
	PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt,
		iterations);

	try {
	    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(
		    this.getCurPBEMode(), "BC");
	    SecretKey pbeKey = secretKeyFactory.generateSecret(pbeKeySpec);

	    // Generate and initialize a PBE cipher
	    Cipher cipher = Cipher.getInstance(this.getCurPBEMode(), "BC");
	    cipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParameterSpec);

	    // decrypt
	    decryptedData = cipher.doFinal(encryptedByteData);

	} catch (InvalidKeyException e) {
	    throw new Exception("Check your password.", e);
	} catch (BadPaddingException e) {
	    throw new Exception("Check your password.", e);
	} catch (GeneralSecurityException e) {
	    throw new EncryptionException(
		    "There was a problem during the decryption process. See the stacktrace for details.",
		    e);
	}
	decryptedData = (decryptedData == null) ? new byte[] {} : decryptedData;
	return decryptedData;
    }

    /**
     * @param message
     * @param privKeyBytes
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws InvalidKeySpecException
     */
    @SuppressWarnings("nls")
    public byte[] signMessage(byte[] message, byte[] privKeyBytes)
	    throws InvalidKeyException, NoSuchAlgorithmException,
	    NoSuchProviderException, SignatureException,
	    InvalidKeySpecException {

	KeyFactory factory = KeyFactory.getInstance(
		this.getCurEncryptionMode(), "BC");

	PrivateKey privKey = factory.generatePrivate(new PKCS8EncodedKeySpec(
		privKeyBytes));

	Signature signature = Signature.getInstance(this.getCurSignatureMode(),
		"BC");

	signature.initSign(privKey);

	signature.update(message);

	return signature.sign();
    }

    /**
     * @param data
     * @param pubKeyBytes
     * @param sigBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    @SuppressWarnings("nls")
    public boolean verifyMessage(byte[] data, byte[] pubKeyBytes,
	    byte[] sigBytes) throws NoSuchAlgorithmException,
	    NoSuchProviderException, InvalidKeySpecException,
	    InvalidKeyException, SignatureException {
	KeyFactory factory = KeyFactory.getInstance(
		this.getCurEncryptionMode(), "BC");

	PublicKey pubKey = factory.generatePublic(new X509EncodedKeySpec(
		pubKeyBytes));

	Signature signature = Signature.getInstance(this.getCurSignatureMode(),
		"BC");
	signature.initVerify(pubKey);
	signature.update(data);
	return signature.verify(sigBytes);
    }

    /**
     * @return the curEncryptionMode
     */
    public String getCurEncryptionMode() {
	return this.curEncryptionMode;
    }

    /**
     * @param curEncryptionMode1
     *            the curEncryptionMode to set
     */
    public void setCurEncryptionMode(String curEncryptionMode1) {
	this.curEncryptionMode = curEncryptionMode1;
    }

    /**
     * @return the curPaddingMode
     */
    public String getCurPaddingMode() {
	return this.curPaddingMode;
    }

    /**
     * @param curPaddingMode1
     *            the curPaddingMode to set
     */
    public void setCurPaddingMode(String curPaddingMode1) {
	this.curPaddingMode = curPaddingMode1;
    }

    /**
     * @return the curSymmetricCipherMode
     */
    public String getCurSymmetricCipherMode() {
	return this.curSymmetricCipherMode;
    }

    /**
     * @param curSymmetricCipherMode1
     *            the curSymmetricCipherMode to set
     */
    public void setCurSymmetricCipherMode(String curSymmetricCipherMode1) {
	this.curSymmetricCipherMode = curSymmetricCipherMode1;
    }

    /**
     * @return the curAsymmetricCipherMode
     */
    public String getCurAsymmetricCipherMode() {
	return this.curAsymmetricCipherMode;
    }

    /**
     * @param curAsymmetricCipherMode1
     *            the curAsymmetricCipherMode to set
     */
    public void setCurAsymmetricCipherMode(String curAsymmetricCipherMode1) {
	this.curAsymmetricCipherMode = curAsymmetricCipherMode1;
    }

    /**
     * @return the curdigestMode
     */
    public String getCurDigestMode() {
	return this.curDigestMode;
    }

    /**
     * @param curdigestMode
     *            the curdigestMode to set
     */
    public void setCurDigestMode(String curDigestMode1) {
	this.curDigestMode = curDigestMode1;
    }

    /**
     * @return the curMACMode
     */
    public String getCurMACMode() {
	return this.curMACMode;
    }

    /**
     * @param curMACMode1
     *            the curMACMode to set
     */
    public void setCurMACMode(String curMACMode1) {
	this.curMACMode = curMACMode1;
    }

    /**
     * @return the curPBEMode
     */
    public String getCurPBEMode() {
	return this.curPBEMode;
    }

    /**
     * @param curPBEMode1
     *            the curPBEMode to set
     */
    public void setCurPBEMode(String curPBEMode1) {
	this.curPBEMode = curPBEMode1;
    }

    /**
     * @return the curSignatureMode
     */
    public String getCurSignatureMode() {
	return curSignatureMode;
    }

    /**
     * @param curSignatureMode
     *            the curSignatureMode to set
     */
    public void setCurSignatureMode(String curSignatureMode) {
	this.curSignatureMode = curSignatureMode;
    }

}
