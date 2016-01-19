package util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

//Renamed BouncyCastle Library
//Had to repackage/recompile the library myself as "spongycastle" doesn't include everything.
import org.mightyfish.crypto.digests.RIPEMD160Digest;
import org.mightyfish.crypto.generators.SCrypt;
import org.mightyfish.util.encoders.Hex;

import coin.Transaction;
import coin.Transaction.Outpoint;
import coin.Transaction.TransactionInput;
import coin.Transaction.TransactionOutput;
import util.crypto.Base58;
import util.crypto.Script;
import util.crypto.Sha256Hash;
import util.crypto.VarInt;

/**
 * A collection of various utility methods that are helpful for working with the
 * Litecoin protocol. To enable debug logging from the library, run with
 * -Dlitecoinj.logging=true on your command line.
 * 
 * Modified from the version provided from BitCoinJ (used primarily for legacy
 * support)
 */
public class Utils {
    private static final MessageDigest digest;

    static {
	try {
	    digest = MessageDigest.getInstance("SHA-256"); //$NON-NLS-1$
	} catch (NoSuchAlgorithmException e) {
	    throw new RuntimeException(e); // Can't happen.
	}
    }

    /**
     * The string that prefixes all text messages signed using Litecoin keys.
     */
    public static final String LITECOIN_SIGNED_MESSAGE_HEADER = "Litecoin Signed Message:\n"; //$NON-NLS-1$

    // TODO: Replace this nanocoins business with something better.

    /**
     * How many "nanocoins" there are in a Litecoin.
     * <p/>
     * A nanocoin is the smallest unit that can be transferred using Litecoin.
     * The term nanocoin is very misleading, though, because there are only 100
     * million of them in a coin (whereas one would expect 1 billion.
     */
    public static final BigInteger COIN = new BigInteger("100000000", 10); //$NON-NLS-1$

    /**
     * How many "nanocoins" there are in 0.01 Litecoins.
     * <p/>
     * A nanocoin is the smallest unit that can be transferred using Litecoin.
     * The term nanocoin is very misleading, though, because there are only 100
     * million of them in a coin (whereas one would expect 1 billion).
     */
    public static final BigInteger CENT = new BigInteger("1000000", 10); //$NON-NLS-1$

    /**
     * Convert an amount expressed in the way humans are used to into nanocoins.
     * 
     * @param coins
     * @param cents
     * @return
     */
    public static BigInteger toNanoCoins(int coins, int cents) {

	if (cents >= 100) {
	    throw new IllegalArgumentException(
		    "Can't have X coins and >= 100 cents as well."); //$NON-NLS-1$
	}

	BigInteger bi = BigInteger.valueOf(coins).multiply(COIN);
	bi = bi.add(BigInteger.valueOf(cents).multiply(CENT));
	return bi;
    }

    /**
     * The regular {@link java.math.BigInteger#toByteArray()} method isn't quite
     * what we often need: it appends a leading zero to indicate that the number
     * is positive and may need padding.
     * 
     * @param b
     *            the integer to format into a byte array
     * @param numBytes
     *            the desired size of the resulting byte array
     * @return numBytes byte long array.
     */
    public static byte[] bigIntegerToBytes(BigInteger b, int numBytes) {
	if (b == null) {
	    return null;
	}
	byte[] bytes = new byte[numBytes];
	byte[] biBytes = b.toByteArray();
	int start = (biBytes.length == numBytes + 1) ? 1 : 0;
	int length = Math.min(biBytes.length, numBytes);
	System.arraycopy(biBytes, start, bytes, numBytes - length, length);
	return bytes;
    }

    /**
     * Convert an amount expressed in the way humans are used to into nanocoins.
     * <p>
     * <p/>
     * This takes string in a format understood by
     * {@link BigDecimal#BigDecimal(String)}, for example "0", "1", "0.10",
     * "1.23E3", "1234.5E-5".
     * 
     * @param coins
     * @return
     * 
     * @throws ArithmeticException
     *             if you try to specify fractional nanocoins
     */
    public static BigInteger toNanoCoins(String coins) {
	return new BigDecimal(coins).movePointRight(8).toBigIntegerExact();
    }

    /**
     * @param val
     * @param out
     * @param offset
     */
    public static void uint32ToByteArrayBE(long val, byte[] out, int offset) {
	out[offset + 0] = (byte) (0xFF & (val >> 24));
	out[offset + 1] = (byte) (0xFF & (val >> 16));
	out[offset + 2] = (byte) (0xFF & (val >> 8));
	out[offset + 3] = (byte) (0xFF & (val >> 0));
    }

    /**
     * @param val
     * @param out
     * @param offset
     */
    public static void uint32ToByteArrayLE(long val, byte[] out, int offset) {
	out[offset + 0] = (byte) (0xFF & (val >> 0));
	out[offset + 1] = (byte) (0xFF & (val >> 8));
	out[offset + 2] = (byte) (0xFF & (val >> 16));
	out[offset + 3] = (byte) (0xFF & (val >> 24));
    }

    /**
     * @param val
     * @param stream
     * @throws IOException
     */
    public static void uint32ToByteStreamLE(long val, OutputStream stream)
	    throws IOException {
	stream.write((int) (0xFF & (val >> 0)));
	stream.write((int) (0xFF & (val >> 8)));
	stream.write((int) (0xFF & (val >> 16)));
	stream.write((int) (0xFF & (val >> 24)));
    }

    /**
     * @param val
     * @param stream
     * @throws IOException
     */
    public static void int64ToByteStreamLE(long val, OutputStream stream)
	    throws IOException {
	stream.write((int) (0xFF & (val >> 0)));
	stream.write((int) (0xFF & (val >> 8)));
	stream.write((int) (0xFF & (val >> 16)));
	stream.write((int) (0xFF & (val >> 24)));
	stream.write((int) (0xFF & (val >> 32)));
	stream.write((int) (0xFF & (val >> 40)));
	stream.write((int) (0xFF & (val >> 48)));
	stream.write((int) (0xFF & (val >> 56)));
    }

    /**
     * @param val
     * @param stream
     * @throws IOException
     */
    public static void uint64ToByteStreamLE(BigInteger val, OutputStream stream)
	    throws IOException {
	byte[] bytes = val.toByteArray();
	if (bytes.length > 8) {
	    throw new RuntimeException(
		    "Input too large to encode into a uint64"); //$NON-NLS-1$
	}
	bytes = reverseBytes(bytes);
	stream.write(bytes);
	if (bytes.length < 8) {
	    for (int i = 0; i < 8 - bytes.length; i++)
		stream.write(0);
	}
    }

    /**
     * See {@link Utils#doubleDigest(byte[], int, int)}.
     * 
     * @param input
     * @return
     */
    public static byte[] doubleDigest(byte[] input) {
	return doubleDigest(input, 0, input.length);
    }

    /**
     * @param input
     * @return
     */
    public static byte[] scryptDigest(byte[] input) {
	try {
	    return SCrypt.generate(input, input, 1024, 1, 1, 32);
	} catch (Exception e) {
	    return null;
	}
    }

    /**
     * Calculates the SHA-256 hash of the given byte range, and then hashes the
     * resulting hash again. This is standard procedure in Litecoin. The
     * resulting hash is in big endian form.
     * 
     * @param input
     * @param offset
     * @param length
     * @return
     */
    public static byte[] doubleDigest(byte[] input, int offset, int length) {
	synchronized (digest) {
	    digest.reset();
	    digest.update(input, offset, length);
	    byte[] first = digest.digest();
	    return digest.digest(first);
	}
    }

    /**
     * @param input
     * @param offset
     * @param length
     * @return
     */
    public static byte[] singleDigest(byte[] input, int offset, int length) {
	synchronized (digest) {
	    digest.reset();
	    digest.update(input, offset, length);
	    return digest.digest();
	}
    }

    /**
     * Calculates SHA256(SHA256(byte range 1 + byte range 2)).
     * 
     * @param input1
     * @param offset1
     * @param length1
     * @param input2
     * @param offset2
     * @param length2
     * @return
     */
    public static byte[] doubleDigestTwoBuffers(byte[] input1, int offset1,
	    int length1, byte[] input2, int offset2, int length2) {
	synchronized (digest) {
	    digest.reset();
	    digest.update(input1, offset1, length1);
	    digest.update(input2, offset2, length2);
	    byte[] first = digest.digest();
	    return digest.digest(first);
	}
    }

    /**
     * Work around lack of unsigned types in Java.
     * 
     * @param n1
     * @param n2
     * @return
     */
    public static boolean isLessThanUnsigned(long n1, long n2) {
	return (n1 < n2) ^ ((n1 < 0) != (n2 < 0));
    }

    /**
     * Returns the given byte array hex encoded.
     * 
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes) {
	StringBuffer buf = new StringBuffer(bytes.length * 2);
	for (byte b : bytes) {
	    String s = Integer.toString(0xFF & b, 16);
	    if (s.length() < 2)
		buf.append('0');
	    buf.append(s);
	}
	return buf.toString();
    }

    /**
     * Returns a copy of the given byte array in reverse order.
     * 
     * @param bytes
     * @return
     */
    public static byte[] reverseBytes(byte[] bytes) {
	// We could use the XOR trick here but it's easier to understand if we
	// don't. If we find this is really a
	// performance issue the matter can be revisited.
	byte[] buf = new byte[bytes.length];
	for (int i = 0; i < bytes.length; i++)
	    buf[i] = bytes[bytes.length - 1 - i];
	return buf;
    }

    /**
     * Returns a copy of the given byte array with the bytes of each double-word
     * (4 bytes) reversed.
     * 
     * @param bytes
     *            length must be divisible by 4.
     * @param trimLength
     *            trim output to this length. If positive, must be divisible by
     *            4.
     * @return
     */
    public static byte[] reverseDwordBytes(byte[] bytes, int trimLength) {
	if (bytes.length % 4 != 0) {
	    throw new IllegalArgumentException("Bytes must be divsible by 4."); //$NON-NLS-1$
	}
	if (trimLength >= 0 && trimLength % 4 != 0) {
	    throw new IllegalArgumentException(
		    "Positive trim-length must be divisble by 4."); //$NON-NLS-1$
	}

	byte[] rev = new byte[trimLength >= 0 && bytes.length > trimLength
		? trimLength : bytes.length];

	for (int i = 0; i < rev.length; i += 4) {
	    System.arraycopy(bytes, i, rev, i, 4);
	    for (int j = 0; j < 4; j++) {
		rev[i + j] = bytes[i + 3 - j];
	    }
	}
	return rev;
    }

    /**
     * @param bytes
     * @param offset
     * @return
     */
    public static long readUint32(byte[] bytes, int offset) {
	return ((bytes[offset++] & 0xFFL) << 0)
		| ((bytes[offset++] & 0xFFL) << 8)
		| ((bytes[offset++] & 0xFFL) << 16)
		| ((bytes[offset] & 0xFFL) << 24);
    }

    /**
     * @param bytes
     * @param offset
     * @return
     */
    public static BigInteger readUint64(byte[] bytes, int offset) {
	// Java does not have an unsigned 64 bit type. So scrape it off the wire
	// then flip.
	byte[] valbytes = new byte[8];
	System.arraycopy(bytes, offset, valbytes, 0, 8);
	valbytes = Utils.reverseBytes(valbytes);
	return new BigInteger(valbytes);
    }

    /**
     * @param bytes
     * @param offset
     * @return
     */
    public static long readInt64(byte[] bytes, int offset) {
	return ((bytes[offset++] & 0xFFL) << 0)
		| ((bytes[offset++] & 0xFFL) << 8)
		| ((bytes[offset++] & 0xFFL) << 16)
		| ((bytes[offset++] & 0xFFL) << 24)
		| ((bytes[offset++] & 0xFFL) << 32)
		| ((bytes[offset++] & 0xFFL) << 40)
		| ((bytes[offset++] & 0xFFL) << 48)
		| ((bytes[offset] & 0xFFL) << 56);
    }

    /**
     * @param bytes
     * @param offset
     * @return
     */
    public static long readUint32BE(byte[] bytes, int offset) {
	return ((bytes[offset + 0] & 0xFFL) << 24)
		| ((bytes[offset + 1] & 0xFFL) << 16)
		| ((bytes[offset + 2] & 0xFFL) << 8)
		| ((bytes[offset + 3] & 0xFFL) << 0);
    }

    /**
     * @param bytes
     * @param offset
     * @return
     */
    public static int readUint16BE(byte[] bytes, int offset) {
	return ((bytes[offset] & 0xff) << 8) | bytes[offset + 1] & 0xff;
    }

    /**
     * Calculates RIPEMD160(SHA256(input)). This is used in Address
     * calculations.
     * 
     * @param input
     * @return
     */
    public static byte[] sha256hash160(byte[] input) {
	try {
	    byte[] sha256 = MessageDigest.getInstance("SHA-256").digest(input); //$NON-NLS-1$
	    RIPEMD160Digest digest = new RIPEMD160Digest();
	    digest.update(sha256, 0, sha256.length);
	    byte[] out = new byte[20];
	    digest.doFinal(out, 0);
	    return out;
	} catch (NoSuchAlgorithmException e) {
	    throw new RuntimeException(e); // Cannot happen.
	}
    }

    /**
     * Returns the given value in nanocoins as a 0.12 type string. More digits
     * after the decimal place will be used if necessary, but two will always be
     * present.
     * 
     * @param value
     * @return
     */
    public static String litecoinValueToFriendlyString(BigInteger value) {
	// TODO: This API is crap. This method should go away when we
	// encapsulate money values.
	boolean negative = value.compareTo(BigInteger.ZERO) < 0;
	if (negative)
	    value = value.negate();
	BigDecimal bd = new BigDecimal(value, 8);
	String formatted = bd.toPlainString(); // Don't use scientific notation.
	int decimalPoint = formatted.indexOf("."); //$NON-NLS-1$
	// Drop unnecessary zeros from the end.
	int toDelete = 0;
	for (int i = formatted.length() - 1; i > decimalPoint + 2; i--) {
	    if (formatted.charAt(i) == '0')
		toDelete++;
	    else
		break;
	}
	return (negative ? "-" : "") //$NON-NLS-1$ //$NON-NLS-2$
		+ formatted.substring(0, formatted.length() - toDelete);
    }

    /**
     * <p>
     * Returns the given value as a plain string denominated in BTC. The result
     * is unformatted with no trailing zeroes. For instance, an input value of
     * BigInteger.valueOf(150000) nanocoin gives an output string of "0.0015"
     * BTC
     * </p>
     * 
     * @param value
     *            The value in nanocoins to convert to a string (denominated in
     *            BTC)
     * @return
     * @throws IllegalArgumentException
     *             If the input value is null
     */
    public static String litecoinValueToPlainString(BigInteger value) {
	if (value == null) {
	    throw new IllegalArgumentException("Value cannot be null"); //$NON-NLS-1$
	}

	BigDecimal valueInBTC = new BigDecimal(value)
		.divide(new BigDecimal(Utils.COIN));
	return valueInBTC.toPlainString();
    }

    /**
     * MPI encoded numbers are produced by the OpenSSL BN_bn2mpi function. They
     * consist of a 4 byte big endian length field, followed by the stated
     * number of bytes representing the number in big endian format (with a sign
     * bit).
     * 
     * @param hasLength
     *            can be set to false if the given array is missing the 4 byte
     *            length field
     */
    public static BigInteger decodeMPI(byte[] mpi, boolean hasLength) {
	byte[] buf;
	if (hasLength) {
	    int length = (int) readUint32BE(mpi, 0);
	    buf = new byte[length];
	    System.arraycopy(mpi, 4, buf, 0, length);
	} else
	    buf = mpi;
	if (buf.length == 0)
	    return BigInteger.ZERO;
	boolean isNegative = (buf[0] & 0x80) == 0x80;
	if (isNegative)
	    buf[0] &= 0x7f;
	BigInteger result = new BigInteger(buf);
	return isNegative ? result.negate() : result;
    }

    /**
     * MPI encoded numbers are produced by the OpenSSL BN_bn2mpi function. They
     * consist of a 4 byte big endian length field, followed by the stated
     * number of bytes representing the number in big endian format (with a sign
     * bit).
     * 
     * @param value
     * 
     * @param includeLength
     *            indicates whether the 4 byte length field should be included
     * @return
     */
    public static byte[] encodeMPI(BigInteger value, boolean includeLength) {
	if (value.equals(BigInteger.ZERO)) {
	    if (!includeLength)
		return new byte[] {};
	    return new byte[] { 0x00, 0x00, 0x00, 0x00 };
	}
	boolean isNegative = value.compareTo(BigInteger.ZERO) < 0;
	if (isNegative)
	    value = value.negate();
	byte[] array = value.toByteArray();
	int length = array.length;
	if ((array[0] & 0x80) == 0x80)
	    length++;
	if (includeLength) {
	    byte[] result = new byte[length + 4];
	    System.arraycopy(array, 0, result, length - array.length + 3,
		    array.length);
	    uint32ToByteArrayBE(length, result, 0);
	    if (isNegative)
		result[4] |= 0x80;
	    return result;
	}
	byte[] result;
	if (length != array.length) {
	    result = new byte[length];
	    System.arraycopy(array, 0, result, 1, array.length);
	} else
	    result = array;
	if (isNegative)
	    result[0] |= 0x80;
	return result;
    }

    /**
     * <p>
     * The "compact" format is a representation of a whole number N using an
     * unsigned 32 bit number similar to a floating point format. The most
     * significant 8 bits are the unsigned exponent of base 256. This exponent
     * can be thought of as "number of bytes of N". The lower 23 bits are the
     * mantissa. Bit number 24 (0x800000) represents the sign of N. Therefore, N
     * = (-1^sign) * mantissa * 256^(exponent-3).
     * </p>
     *
     * <p>
     * Satoshi's original implementation used BN_bn2mpi() and BN_mpi2bn(). MPI
     * uses the most significant bit of the first byte as sign. Thus
     * 0x1234560000 is compact 0x05123456 and 0xc0de000000 is compact
     * 0x0600c0de. Compact 0x05c0de00 would be -0x40de000000.
     * </p>
     *
     * <p>
     * Bitcoin only uses this "compact" format for encoding difficulty targets,
     * which are unsigned 256bit quantities. Thus, all the complexities of the
     * sign bit and using base 256 are probably an implementation accident.
     * </p>
     */
    public static BigInteger decodeCompactBits(long compact) {
	int size = ((int) (compact >> 24)) & 0xFF;
	byte[] bytes = new byte[4 + size];
	bytes[3] = (byte) size;
	if (size >= 1)
	    bytes[4] = (byte) ((compact >> 16) & 0xFF);
	if (size >= 2)
	    bytes[5] = (byte) ((compact >> 8) & 0xFF);
	if (size >= 3)
	    bytes[6] = (byte) ((compact >> 0) & 0xFF);
	return decodeMPI(bytes, true);
    }

    public static BigInteger decodeCompactBits(byte[] compact) {

	// First byte is the number of digits in the base 256 representation
	// including the prepended zero if it is present
	int size = compact[0] & 0xFF;

	BigInteger total = BigInteger.ZERO;

	if ((compact[1] & 0xFF) == 0) {// Check for prepended zero
	    // First digit is greater than 127
	    // First digit is compact[2]
	    if (size >= 2) {
		total = total.add(BigInteger.valueOf(256).pow(size - 1)
			.multiply(BigInteger.valueOf(compact[2] & 0xFF)));
		if (size >= 3) {
		    total = total.add(BigInteger.valueOf(256).pow(size - 2)
			    .multiply(BigInteger.valueOf(compact[3] & 0xFF)));
		}
	    }

	} else {
	    // First digit is not greater than 127
	    // If it is greater than 127 we presume it to be negative
	    // and assume the target is zero
	    // First digit is compact[1]

	    if ((compact[1] & 0xFF) > 127) {
		total = BigInteger.valueOf(0);
	    } else {
		total = total.add(BigInteger.valueOf(256).pow(size)
			.multiply(BigInteger.valueOf(compact[1] & 0xFF)));

		if (size >= 2) {
		    total = total.add(BigInteger.valueOf(256).pow(size - 1)
			    .multiply(BigInteger.valueOf(compact[2] & 0xFF)));
		    if (size >= 3) {
			total = total.add(
				BigInteger.valueOf(256).pow(size - 2).multiply(
					BigInteger.valueOf(compact[3] & 0xFF)));
		    }
		}
	    }

	}
	return total;
    }

    /**
     * If non-null, overrides the return value of now().
     */
    public static volatile Date mockTime;

    /**
     * Advances (or rewinds) the mock clock by the given number of seconds.
     * 
     * @param seconds
     * @return
     */
    public static Date rollMockClock(int seconds) {
	if (mockTime == null)
	    mockTime = new Date();
	mockTime = new Date(mockTime.getTime() + (seconds * 1000));
	return mockTime;
    }

    /**
     * Returns the current time, or a mocked out equivalent.
     * 
     * @return
     */
    public static Date now() {
	if (mockTime != null)
	    return mockTime;
	return new Date();
    }

    /**
     * @param in
     * @param length
     * @return
     */
    public static byte[] copyOf(byte[] in, int length) {
	byte[] out = new byte[length];
	System.arraycopy(in, 0, out, 0, Math.min(length, in.length));
	return out;
    }

    /**
     * Attempts to parse the given string as arbitrary-length hex or base58 and
     * then return the results, or null if neither parse was successful.
     * 
     * @param data
     * @return
     */
    public static byte[] parseAsHexOrBase58(String data) {
	try {
	    return Hex.decode(data);
	} catch (Exception e) {
	    // Didn't decode as hex, try base58.
	    try {
		return Base58.decodeChecked(data);
	    } catch (Exception e1) {
		return null;
	    }
	}
    }

    /**
     * @return
     */
    public static boolean isWindows() {
	return System.getProperty("os.name").toLowerCase().contains("win"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * <p>
     * Given a textual message, returns a byte buffer formatted as follows:
     * </p>
     * 
     * <tt><p>[24] "Litecoin Signed Message:\n" [message.length as a varint] message</p></tt>
     * 
     * @param message
     * @return
     */
    public static byte[] formatMessageForSigning(String message) {
	VarInt size = new VarInt(message.length());
	int totalSize = 1 + LITECOIN_SIGNED_MESSAGE_HEADER.length()
		+ size.getOriginalSizeInBytes() + message.length();
	byte[] result = new byte[totalSize];
	int cursor = 0;
	result[cursor++] = (byte) LITECOIN_SIGNED_MESSAGE_HEADER.length();
	byte[] bytes = LITECOIN_SIGNED_MESSAGE_HEADER
		.getBytes(Charset.forName("UTF-8")); //$NON-NLS-1$
	System.arraycopy(bytes, 0, result, cursor, bytes.length);
	cursor += bytes.length;
	bytes = size.encode();
	System.arraycopy(bytes, 0, result, cursor, bytes.length);
	cursor += bytes.length;
	bytes = message.getBytes(Charset.forName("UTF-8")); //$NON-NLS-1$
	System.arraycopy(bytes, 0, result, cursor, bytes.length);
	return result;
    }

    // 00000001, 00000010, 00000100, 00001000, ...
    private static final int bitMask[] = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20,
	    0x40, 0x80 };

    // Checks if the given bit is set in data
    /**
     * @param data
     * @param index
     * @return
     */
    public static boolean checkBitLE(byte[] data, int index) {
	return (data[index >>> 3] & bitMask[7 & index]) != 0;
    }

    // Sets the given bit in data to one
    /**
     * @param data
     * @param index
     */
    public static void setBitLE(byte[] data, int index) {
	data[index >>> 3] |= bitMask[7 & index];
    }

    // Loads transaction data from a byte array
    /**
     * @param transactionData
     * @param offset
     * @param numTransactions
     * @return
     * @throws Exception
     */
    @SuppressWarnings("nls")
    public static BigFastList<Transaction> readTransactions(
	    byte[] transactionData, int offset, BigInteger numTransactions)
		    throws Exception {

	int offset2 = offset;

	// BigFastList of Transactions
	BigFastList<Transaction> transactions = new BigFastList<Transaction>();

	for (BigInteger n = BigInteger.valueOf(0); n
		.compareTo(numTransactions) < 0; n = n.add(BigInteger.ONE)) {

	    // Transaction object
	    Transaction transaction = new Transaction();

	    // First four bytes are the transaction data format version
	    byte[] formatVersion = { transactionData[0 + offset2],
		    transactionData[1 + offset2], transactionData[2 + offset2],
		    transactionData[3 + offset2] };

	    long formatVer = Utils.readUint32(formatVersion, 0);

	    transaction.setTransactionDataFormatVersion(
		    BigInteger.valueOf(formatVer));

	    offset2 += 4;

	    // Next is the number of transaction inputs as a variable integer

	    VarInt tInputs = new VarInt(transactionData, offset2);

	    transaction.setTransactionInputs(tInputs.getValue());

	    offset2 += tInputs.getOriginalSizeInBytes();

	    BigFastList<TransactionInput> transactionInputs = transaction
		    .getTransactionIn();

	    // Now we go and read all the inputs..

	    for (BigInteger x = BigInteger.valueOf(0); x.compareTo(
		    tInputs.getValue()) < 0; x = x.add(BigInteger.ONE)) {

		// Transaction Input object
		TransactionInput transactionInput = transaction.new TransactionInput();

		Outpoint previous = transaction.new Outpoint();

		// Check if this is a coinBase transaction (first transaction's
		// only input)
		if (n.compareTo(BigInteger.ZERO) == 0
			&& x.compareTo(BigInteger.ZERO) == 0) {
		    previous.setHash(null);
		} else {

		    // Field 1: Previous Transaction hash
		    // Size: 32 Bytes (char array)
		    byte[] prevHash = new byte[32];

		    for (byte y = 0; y < 32; y++) {
			prevHash[y] = transactionData[y + offset2];
		    }

		    Sha256Hash prevHash256 = new Sha256Hash(prevHash);

		    previous.setHash(new StringBuffer(prevHash256.toString()));
		}

		offset2 += 32;

		// Field 2: Previous Txout-index
		// Size: 4 Bytes

		byte[] tx_Out = { transactionData[0 + offset2],
			transactionData[1 + offset2],
			transactionData[2 + offset2],
			transactionData[3 + offset2] };

		long txOut = Utils.readUint32(tx_Out, 0);

		previous.setIndex(BigInteger.valueOf(txOut));

		transactionInput.setPrevious_output(previous);

		offset2 += 4;

		// Field 3: Txin-script length
		// Size: VarInt

		VarInt tx_In_Script_Length = new VarInt(transactionData,
			offset2);

		transactionInput
			.setScript_length(tx_In_Script_Length.getValue());

		offset2 += tx_In_Script_Length.getOriginalSizeInBytes();

		// Field 4: Txin-script / scriptSig
		// Size: Field 3 num of bytes

		// Check if this is a coinBase transaction (first transaction's
		// only input) as coinBase transactions input scripts may not be
		// valid

		Script TxInScript = null;

		if (n.compareTo(BigInteger.ZERO) == 0
			&& x.compareTo(BigInteger.ZERO) == 0) {
		} else {
		    TxInScript = new Script(null, transactionData, offset2,
			    tx_In_Script_Length.getValue().intValueExact());

		    transactionInput.setScript(TxInScript);
		}

		offset2 += tx_In_Script_Length.getValue().intValueExact();

		// Field 5: sequence_no
		// Size: 4 bytes

		long seq_num = Utils.readUint32(transactionData, offset2);

		offset2 += 4;

		transactionInput.setSequenceNum(BigInteger.valueOf(seq_num));

		transactionInputs.add(transactionInput);

	    }

	    // BigFastList of transactionOutputs

	    BigFastList<TransactionOutput> transactionOutputs = transaction
		    .getTransactionOut();

	    // Next is the number of transaction outputs as a variable integer

	    VarInt tOutputs = new VarInt(transactionData, offset2);

	    transaction.setTransactionOutputs(tOutputs.getValue());

	    offset2 += tOutputs.getOriginalSizeInBytes();

	    // Now we go and read all the outputs..
	    for (BigInteger x = BigInteger.valueOf(0); x.compareTo(
		    tOutputs.getValue()) < 0; x = x.add(BigInteger.ONE)) {
		// TransactionOutput Object

		TransactionOutput transactionOutput = transaction.new TransactionOutput();

		// Field 1: Non negative integer giving the number of
		// Satoshis(BTC/10^8) to be transfered
		// Size: 8 Bytes

		// TODO: Check if Uint64 or Int64
		transactionOutput.setTransactionValue(
			Utils.readUint64(transactionData, offset2));
		offset2 += 8;

		// Field 2: TxOut-script length
		// Size: VarInt

		VarInt tx_Out_Script_Length = new VarInt(transactionData,
			offset2);

		transactionOutput
			.setPkScriptLength(tx_Out_Script_Length.getValue());

		offset2 += tx_Out_Script_Length.getOriginalSizeInBytes();

		// Field 3: Script
		// Size: Field 2 num of bytes

		Script TxOutScript = new Script(null, transactionData, offset2,
			tx_Out_Script_Length.getValue().intValueExact());

		transactionOutput.setPkScript(TxOutScript);

		offset2 += tx_Out_Script_Length.getValue().intValueExact();
		// Add the output
		transactionOutputs.add(transactionOutput);
	    }

	    transaction.setTransactionIn(transactionInputs);
	    transaction.setTransactionOut(transactionOutputs);

	    // Now we read the lock time (4 bytes)
	    /*
	     * A time (Unix epoch time) or block number. See the locktime
	     * parsing rules.
	     */
	    long lock_time = Utils.readUint32(transactionData, offset2);

	    transaction.setLockTime(BigInteger.valueOf(lock_time));

	    offset2 += 4;

	    transactions.add(transaction);

	}

	return transactions;

    }

    /**
     * Use BigDecimalMath instead..
     * 
     * @param base
     * @param input
     * @param precision
     * @return
     *
     * 	public BigDecimal log(BigDecimal base, BigDecimal input, int
     *         precision) { BigDecimal result = BigDecimal.valueOf(0);
     *         BigDecimal b = (BigDecimal) Utils.cloneObject(base); BigDecimal
     *         in = (BigDecimal) Utils.cloneObject(input);
     * 
     *         while (in.compareTo(b) > 0) { result =
     *         result.add(BigDecimal.ONE);
     * 
     *         in = in.divide(b, precision, RoundingMode.HALF_UP); }
     * 
     *         BigDecimal fraction = (BigDecimal.valueOf(0.5)); in =
     *         in.multiply(in);
     * 
     *         while (((result.add(fraction)).compareTo(result)) > 0 &&
     *         in.compareTo(BigDecimal.ONE) > 0) { if (in.compareTo(b) > 0) { in
     *         = in.divide(b, precision, RoundingMode.HALF_UP); result =
     *         result.add(fraction); }
     * 
     *         in = in.multiply(in); fraction =
     *         fraction.divide(BigDecimal.valueOf(2.0), precision,
     *         RoundingMode.HALF_UP);
     * 
     *         }
     * 
     *         return result;
     * 
     *         }
     */

    // Author: WillingLearner @
    // http://stackoverflow.com/questions/869033/how-do-i-copy-an-object-in-java
    private static Object cloneObject(Object obj) {
	try {
	    Object clone = obj.getClass().newInstance();
	    for (Field field : obj.getClass().getDeclaredFields()) {
		field.setAccessible(true);
		if (field.get(obj) == null
			|| Modifier.isFinal(field.getModifiers())) {
		    continue;
		}
		if (field.getType().isPrimitive()
			|| field.getType().equals(String.class)
			|| field.getType().getSuperclass().equals(Number.class)
			|| field.getType().equals(Boolean.class)) {
		    field.set(clone, field.get(obj));
		} else {
		    Object childObj = field.get(obj);
		    if (childObj == obj) {
			field.set(clone, clone);
		    } else {
			field.set(clone, cloneObject(field.get(obj)));
		    }
		}
	    }
	    return clone;
	} catch (Exception e) {
	    return null;
	}
    }

    /**
     * Returns the larger bigInteger
     * 
     * If they are equal it doesn't matter which is returned
     * 
     * @param a
     * @param b
     * @return
     */
    public static BigInteger max(BigInteger a, BigInteger b) {
	return (a.compareTo(b) > 0) ? (a) : (b);
    }

    /**
     * Returns the smaller bigInteger
     * 
     * If they are equal it doesn't matter which is returned
     * 
     * @param a
     * @param b
     * @return
     */
    public static BigInteger min(BigInteger a, BigInteger b) {
	return (a.compareTo(b) < 0) ? (a) : (b);
    }

    /**
     * Returns the larger bigInteger
     * 
     * If they are equal it doesn't matter which is returned
     * 
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal max(BigDecimal a, BigDecimal b) {
	return (a.compareTo(b) > 0) ? (a) : (b);
    }

    /**
     * Returns the smaller bigInteger
     * 
     * If they are equal it doesn't matter which is returned
     * 
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal min(BigDecimal a, BigDecimal b) {
	return (a.compareTo(b) < 0) ? (a) : (b);
    }

}