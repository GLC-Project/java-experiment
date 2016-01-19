package util.crypto;

/**
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.mightyfish.util.encoders.Hex;

import util.Utils;

/**
 * A Sha256Hash just wraps a byte[] so that equals and hashcode work correctly,
 * allowing it to be used as keys in a map. It also checks that the length is
 * correct and provides a bit more type safety.
 */
public class Sha256Hash implements Serializable {
    private final byte[] bytes;
    /**
     * 
     */
    public static final Sha256Hash ZERO_HASH = new Sha256Hash(new byte[32]);

    /**
     * Creates a Sha256Hash by wrapping the given byte array. It must be 32
     * bytes long.
     * 
     * @param rawHashBytes
     */
    public Sha256Hash(byte[] rawHashBytes) {
	if (rawHashBytes.length != 32) {
	    try {
		throw new Exception("Given byte array must be 32 bytes long!"); //$NON-NLS-1$
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	this.bytes = rawHashBytes;

    }

    /**
     * Creates a Sha256Hash by decoding the given hex string. It must be 64
     * characters long.
     * 
     * @param hexString
     */
    public Sha256Hash(String hexString) {
	if (hexString.length() != 64) {
	    try {
		throw new Exception(
			"Given hexstring must be 64 characters long!"); //$NON-NLS-1$
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	this.bytes = Hex.decode(hexString);
    }

    /**
     * Calculates the (one-time) hash of contents and returns it as a new
     * wrapped hash.
     * 
     * @param contents
     * @return
     */
    public static Sha256Hash create(byte[] contents) {
	try {
	    MessageDigest digest = MessageDigest.getInstance("SHA-256"); //$NON-NLS-1$
	    return new Sha256Hash(digest.digest(contents));
	} catch (NoSuchAlgorithmException e) {
	    throw new RuntimeException(e); // Cannot happen.
	}
    }

    /**
     * Calculates the hash of the hash of the contents. This is a standard
     * operation in Litecoin.
     * 
     * @param contents
     * @return
     */
    public static Sha256Hash createDouble(byte[] contents) {
	return new Sha256Hash(Utils.doubleDigest(contents));
    }

    /**
     * Returns a hash of the given files contents. Reads the file fully into
     * memory before hashing so only use with small files.
     * 
     * @param f
     * @return
     * 
     * @throws IOException
     */
    public static Sha256Hash hashFileContents(File f) throws IOException {
	FileInputStream in = new FileInputStream(f);
	try {
	    return create(Sha256Hash.toByteArray(in));
	} finally {
	    in.close();
	}
    }

    /**
     * Returns true if the hashes are equal.
     */
    @Override
    public boolean equals(Object other) {
	if (!(other instanceof Sha256Hash))
	    return false;
	return Arrays.equals(this.bytes, ((Sha256Hash) other).bytes);
    }

    /**
     * Hash code of the byte array as calculated by {@link Arrays#hashCode()}.
     * Note the difference between a SHA256 secure bytes and the type of
     * quick/dirty bytes used by the Java hashCode method which is designed for
     * use in bytes tables.
     */
    @Override
    public int hashCode() {
	// Use the last 4 bytes, not the first 4 which are often zeros in
	// Litecoin.
	return (this.bytes[31] & 0xFF) | ((this.bytes[30] & 0xFF) << 8)
		| ((this.bytes[29] & 0xFF) << 16)
		| ((this.bytes[28] & 0xFF) << 24);
    }

    @Override
    public String toString() {
	return Utils.bytesToHexString(this.bytes);
    }

    /**
     * Returns the bytes interpreted as a positive integer.
     * 
     * @return
     */
    public BigInteger toBigInteger() {
	return new BigInteger(1, this.bytes);
    }

    /**
     * @return
     */
    public byte[] getBytes() {
	return this.bytes;
    }

    /**
     * @return
     */
    public Sha256Hash duplicate() {
	return new Sha256Hash(this.bytes);
    }

    /**
     * @param is
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(FileInputStream is) throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	int reads = is.read();
	while (reads != -1) {
	    baos.write(reads);
	    reads = is.read();
	}
	return baos.toByteArray();
    }

}