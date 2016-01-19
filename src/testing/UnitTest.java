package testing;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import org.mightyfish.jce.provider.BouncyCastleProvider;
import org.mightyfish.util.Arrays;
import org.mightyfish.util.encoders.Hex;

import util.crypto.CryptionHandler;

/**
 * @author Amir Eslampanah
 * 
 */
public class UnitTest {

    /**
     * This class tests all client methods to see if they match expected outputs
     * 
     */
    public static void main(String[] args) {
	// ReplaceCertFactoryProvider.install();
	Security.addProvider(new BouncyCastleProvider());
	// testCryption();
    }

    /**
     * 
     */
    @SuppressWarnings("nls")
    public static void testCryption() {
	CryptionHandler a = new CryptionHandler();

	// Try out every encryption combination to find valid ones.
	System.out.println("Encryption Test:");
	try {
	    for (String encryptionMode : CryptionHandler.encryptionModes) {
		a.setCurEncryptionMode(encryptionMode);
		for (String paddingMode : CryptionHandler.paddingModes) {
		    a.setCurPaddingMode(paddingMode);
		    for (String symmetricCipherMode : CryptionHandler.symmetricCipherModes) {
			a.setCurSymmetricCipherMode(symmetricCipherMode);

			StringBuffer output = new StringBuffer();
			if (a.getCurEncryptionMode().compareTo("NONE") != 0) {
			    try {
				output.append(a.getCurEncryptionMode() + "/"
					+ a.getCurSymmetricCipherMode() + "/"
					+ a.getCurPaddingMode());

				// Now we generate suitable test data
				String data = generateRandom(16);
				String iv = generateRandom(8);

				// 128 Bit BlockSize algorithms
				// Data must be a multiple of 128 bits or
				// padded!
				if (a.getCurEncryptionMode()
					.compareTo("AES") == 0
					|| a.getCurEncryptionMode()
						.compareTo("CAMELLIA") == 0
					|| a.getCurEncryptionMode()
						.compareTo("CAST6") == 0
					|| a.getCurEncryptionMode()
						.compareTo("NOEKEON") == 0
					|| a.getCurEncryptionMode()
						.compareTo("RC5-64") == 0
					|| a.getCurEncryptionMode()
						.compareTo("RC6") == 0
					|| a.getCurEncryptionMode()
						.compareTo("RIJNDAEL") == 0
					|| a.getCurEncryptionMode()
						.compareTo("SEED") == 0
					|| a.getCurEncryptionMode()
						.compareTo("SERPENT") == 0
					|| a.getCurEncryptionMode()
						.compareTo("TWOFISH") == 0) {
				    // If we don't have padding enabled
				    if (a.getCurPaddingMode()
					    .compareTo("NOPADDING") == 0) {
					data = generateRandom(16);
				    } else {
					data = generateRandom(
						(new SecureRandom()).nextInt(24)
							+ 5);
				    }

				    iv = generateRandom(16);

				} else if (a.getCurEncryptionMode()
					.compareTo("BLOWFISH") == 0
					|| a.getCurEncryptionMode()
						.compareTo("CAST5") == 0
					|| a.getCurEncryptionMode()
						.compareTo("DES") == 0
					|| a.getCurEncryptionMode()
						.compareTo("DESEDE") == 0
					|| a.getCurEncryptionMode()
						.compareTo("GOST28147") == 0
					|| a.getCurEncryptionMode()
						.compareTo("IDEA") == 0
					|| a.getCurEncryptionMode()
						.compareTo("RC2") == 0
					|| a.getCurEncryptionMode()
						.compareTo("SKIPJACK") == 0
					|| a.getCurEncryptionMode()
						.compareTo("TEA") == 0
					|| a.getCurEncryptionMode()
						.compareTo("XTEA") == 0
					|| a.getCurEncryptionMode()
						.compareTo("RC5") == 0) {// 64
									 // bit

				    // If we don't
				    // have padding
				    // enabled
				    if (a.getCurPaddingMode()
					    .compareTo("NOPADDING") == 0) {
					data = generateRandom(8);
				    } else {
					data = generateRandom(
						(new SecureRandom()).nextInt(24)
							+ 5);
				    }

				    iv = generateRandom(8);

				} else if (a.getCurEncryptionMode()
					.contains("256")) {
				    // If we don't have padding enabled
				    if (a.getCurPaddingMode()
					    .compareTo("NOPADDING") == 0) {
					data = generateRandom(32);
				    } else {
					data = generateRandom(
						(new SecureRandom()).nextInt(24)
							+ 5);
				    }

				    iv = generateRandom(32);

				    if (a.getCurEncryptionMode()
					    .compareTo("HC256") == 0) {
					iv = generateRandom(16);
				    }
				} else if (a.getCurEncryptionMode()
					.contains("512")) {
				    // If we don't have padding enabled
				    if (a.getCurPaddingMode()
					    .compareTo("NOPADDING") == 0) {
					data = generateRandom(64);
				    } else {
					data = generateRandom(
						(new SecureRandom()).nextInt(24)
							+ 5);
				    }

				    iv = generateRandom(64);
				} else if (a.getCurEncryptionMode()
					.contains("1024")) {
				    // If we don't have padding enabled
				    if (a.getCurPaddingMode()
					    .compareTo("NOPADDING") == 0) {
					data = generateRandom(128);
				    } else {
					data = generateRandom(
						(new SecureRandom()).nextInt(24)
							+ 5);
				    }
				    iv = generateRandom(128);
				} else if (a.getCurEncryptionMode()
					.contains("CHACHA")
					|| a.getCurEncryptionMode()
						.compareTo("SALSA20") == 0
					|| a.getCurEncryptionMode()
						.contains("GRAINV1")) {
				    iv = generateRandom(8);
				} else if (a.getCurEncryptionMode()
					.contains("GRAIN128")) {
				    iv = generateRandom(12);
				} else if (a.getCurEncryptionMode()
					.contains("XSALSA20")) {
				    iv = generateRandom(24);
				}

				if (a.getCurEncryptionMode()
					.contains("THREE")) {
				    // THREEFISH always has a tweak/IV of 16
				    // bytes
				    // But for some strange reason its 32 bytes
				    // in BC
				    iv = generateRandom(32);
				}

				byte[] key;

				if (a.getCurEncryptionMode()
					.compareTo("DES") == 0) {
				    key = new byte[8];
				} else if (a.getCurEncryptionMode()
					.contains("256")
					|| a.getCurEncryptionMode()
						.contains("GOST28147")
					|| a.getCurEncryptionMode()
						.contains("XSALSA20")) {
				    key = new byte[32];
				    if (a.getCurEncryptionMode()
					    .compareTo("HC256") == 0) {
					key = new byte[16];
				    }
				} else if (a.getCurEncryptionMode()
					.contains("512")) {
				    key = new byte[64];
				} else if (a.getCurEncryptionMode()
					.contains("1024")) {
				    key = new byte[128];
				} else if (a.getCurEncryptionMode()
					.contains("GRAINV1")) {
				    key = new byte[10];
				} else {
				    key = new byte[16];
				}

				new SecureRandom().nextBytes(key);

				byte[] cipher = a.encrypt(data.getBytes(), key,
					iv.getBytes(), 1);

				byte[] decrypted = a.decrypt(cipher, key,
					iv.getBytes(), 1);

				String parsed = new String(decrypted);

				if (parsed.compareTo(data) == 0) {// All went
								  // well
				} else {
				    output.append("\nVerification Failed.\n");
				    output.append("Expected: " + data + "\n");
				    output.append("Got: " + parsed + "\n");
				}
			    } catch (Exception e) {
				output.append("failed");
				if (output.toString().contains("RSA")) {
				    // System.out.println(output);
				    // e.printStackTrace();

				}
			    } finally {
				if (!output.toString().contains("failed")) {
				    System.out.println(output);
				}
			    }
			}
		    }
		    for (String asymmetricCipherMode : CryptionHandler.asymmetricCipherModes) {
			a.setCurAsymmetricCipherMode(asymmetricCipherMode);
			StringBuffer output = new StringBuffer();
			if (a.getCurEncryptionMode().compareTo("RSA") == 0
			/*
			 * || a.getCurEncryptionMode() .compareTo("ELGAMAL") ==
			 * 0 This mode takes WAYYY too long.
			 */) {
			    try {
				output.append(a.getCurEncryptionMode() + "/"
					+ a.getCurAsymmetricCipherMode() + "/"
					+ a.getCurPaddingMode());

				// Now we generate suitable test data
				String data = generateRandom(16);
				String iv = generateRandom(8);

				final KeyPairGenerator keyGen = KeyPairGenerator
					.getInstance(a.getCurEncryptionMode());
				keyGen.initialize(1024);
				final KeyPair key = keyGen.generateKeyPair();

				byte[] cipher = a.encrypt(data.getBytes(),
					key.getPublic().getEncoded(),
					iv.getBytes(), 1);

				byte[] decrypted = a.decrypt(cipher,
					key.getPrivate().getEncoded(),
					iv.getBytes(), 1);

				String parsed = new String(decrypted);

				if (parsed.compareTo(data) == 0) {// All went
								  // well
				} else {
				    output.append("\nVerification Failed.\n");
				    output.append("Expected: " + data + "\n");
				    output.append("Got: " + parsed + "\n");
				}
			    } catch (Exception e) {
				output.append("failed");
				if (output.toString().contains("ELGAMAL")) {
				    System.out.println(output);
				    e.printStackTrace();

				}
			    } finally {
				if (!output.toString().contains("failed")) {
				    System.out.println(output);
				}
			    }
			}

		    }
		}
	    }

	} catch (Exception e) {

	}

	for (String digestMode : CryptionHandler.digestModes) {
	    a.setCurDigestMode(digestMode);

	    SecureRandom b = new SecureRandom();

	    byte[] message = new byte[32];
	    b.nextBytes(message);

	    try {
		if (Arrays.areEqual(a.calculateDigest(message),
			a.calculateDigest(message))) {
		    System.out.println(a.getCurDigestMode() + " :" + new String(
			    Hex.encode(a.calculateDigest(message))));
		}
	    } catch (Exception e) {
	    }

	}

	// Try Scrypt
	try {
	    SecureRandom b = new SecureRandom();
	    byte[] message = new byte[32];
	    b.nextBytes(message);

	    byte[] salt = new byte[32];
	    b.nextBytes(message);

	    System.out.println("Scrypt: " + new String(
		    Hex.encode(a.Scrypt(message, salt, 1024, 1, 1, 32))));
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	// Try out every MAC combination to find valid ones
	for (String MACMode : CryptionHandler.MACModes) {
	    a.setCurMACMode(MACMode);
	    for (String digestMode : CryptionHandler.digestModes) {
		a.setCurDigestMode(digestMode);
		SecureRandom c = new SecureRandom();
		byte[] data = new byte[32];
		c.nextBytes(data);

		byte[] key = new byte[16];
		c.nextBytes(key);

		try {
		    System.out.println(a.getCurMACMode() + "/"
			    + a.getCurDigestMode() + " :" + new String(
				    Hex.encode(a.calculateMAC(data, key))));
		} catch (InvalidKeyException e) {
		    // TODO Auto-generated catch block
		    // e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
		    // TODO Auto-generated catch block
		    // e.printStackTrace();
		} catch (NoSuchProviderException e) {
		    // TODO Auto-generated catch block
		    // e.printStackTrace();
		}

	    }
	}

	for (String signatureMode : CryptionHandler.signatureModes) {
	    a.setCurSignatureMode(signatureMode);

	    String cryptionMode = "";
	    for (String encryptionMode : CryptionHandler.encryptionModes) {
		if (signatureMode.contains(encryptionMode)) {
		    cryptionMode = encryptionMode;
		    a.setCurEncryptionMode(cryptionMode);
		}
	    }
	    KeyPairGenerator keyGenerator;
	    try {
		keyGenerator = KeyPairGenerator.getInstance(cryptionMode);
		SecureRandom rng = new SecureRandom();
		// TODO: Set keysize based on algorithm
		keyGenerator.initialize(256, rng);

		KeyPair keyPair = keyGenerator.generateKeyPair();

		SecureRandom b = new SecureRandom();
		byte[] message = new byte[32];
		b.nextBytes(message);

		byte[] sig = a.signMessage(message,
			keyPair.getPrivate().getEncoded());

		System.out
			.println(
				a.getCurSignatureMode() + " | Verified? "
					+ a.verifyMessage(message, keyPair
						.getPublic().getEncoded(),
					sig));
	    } catch (Exception e) {

	    }
	}

	for (String PBEMode : CryptionHandler.PBEModes) {
	    a.setCurPBEMode(PBEMode);

	    SecureRandom b = new SecureRandom();
	    byte[] message = new byte[32];
	    b.nextBytes(message);

	    byte[] salt = new byte[8];
	    b.nextBytes(salt);
	    // TODO: Set keysize/salt based on algorithm
	    char[] key = generateRandom(16).toCharArray();

	    try {
		byte[] encryptedData = a.encryptPBE(message, key, salt, 16);

		byte[] unEncryptedData = a.decryptPBE(encryptedData, key, salt,
			16);

		if (new String(Hex.encode(unEncryptedData))
			.compareTo(new String(Hex.encode(message))) == 0) {
		    System.out.println(a.getCurPBEMode() + " | Verified? true");
		}
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
	    }
	}
    }

    /**
     * @param byteLength
     * @return
     */
    @SuppressWarnings("nls")
    public static String generateRandom(int byteLength) {
	StringBuffer randomString = new StringBuffer();
	SecureRandom r = new SecureRandom();
	String characterSet = "qwertyuiopasdfghjklzxcvbnm1234567890";

	int length = byteLength;
	while (length != 0) {
	    length--;
	    randomString.append(
		    characterSet.charAt(r.nextInt(characterSet.length())));
	}

	return randomString.toString();
    }
}
