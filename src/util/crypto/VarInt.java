package util.crypto;

import java.math.BigInteger;

import util.Utils;

/**
 * A variable-length encoded integer using Satoshis encoding.
 */
public class VarInt {
    private final long value;
    private final int originallyEncodedSize;
    private final static long INT_MASK = 0xffffffffL;

    /**
     * @param value
     */
    public VarInt(long value) {
	this.value = value;
	this.originallyEncodedSize = getSizeInBytes();
    }

    // Bitcoin has its own varint format, known in the C++ source as
    // "compact size".
    /**
     * @param buf
     * @param offset
     */
    public VarInt(byte[] buf, int offset) {
	int first = 0xFF & buf[offset];
	long val;
	if (first < 253) {
	    // 8 bits.
	    val = first;
	    this.originallyEncodedSize = 1;
	} else if (first == 253) {
	    // 16 bits.
	    val = (0xFF & buf[offset + 1]) | ((0xFF & buf[offset + 2]) << 8);
	    this.originallyEncodedSize = 3;
	} else if (first == 254) {
	    // 32 bits.
	    val = Utils.readUint32(buf, offset + 1);
	    this.originallyEncodedSize = 5;
	} else {
	    // 64 bits.
	    val = Utils.readUint32(buf, offset + 1)
		    | (Utils.readUint32(buf, offset + 5) << 32);
	    this.originallyEncodedSize = 9;
	}
	this.value = val;
    }

    /**
     * Gets the number of bytes used to encode this originally if deserialized
     * from a byte array. Otherwise returns the minimum encoded size
     * 
     * @return
     */
    public int getOriginalSizeInBytes() {
	return this.originallyEncodedSize;
    }

    /**
     * Gets the minimum encoded size of the value stored in this VarInt
     * 
     * @return
     */
    public int getSizeInBytes() {
	return sizeOf(this.value);
    }

    /**
     * Gets the minimum encoded size of the given value.
     */
    public static int sizeOf(int value) {
	if (value < 253)
	    return 1;
	else if (value < 65536)
	    return 3; // 1 marker + 2 data bytes
	return 5; // 1 marker + 4 data bytes
    }

    /**
     * Gets the minimum encoded size of the given value.
     */
    public static int sizeOf(long value) {
	if (Utils.isLessThanUnsigned(value, 253))
	    return 1;
	else if (Utils.isLessThanUnsigned(value, 65536))
	    return 3; // 1 marker + 2 data bytes
	else if (Utils.isLessThanUnsigned(value, getUnsignedIntegerMaxValue()))
	    return 5; // 1 marker + 4 data bytes
	else
	    return 9; // 1 marker + 8 data bytes
    }

    /**
     * @return
     */
    public byte[] encode() {
	return encodeBE();
    }

    /**
     * @return
     */
    public byte[] encodeBE() {
	if (Utils.isLessThanUnsigned(this.value, 253)) {
	    return new byte[] { (byte) this.value };
	} else if (Utils.isLessThanUnsigned(this.value, 65536)) {
	    return new byte[] { (byte) 253, (byte) (this.value),
		    (byte) (this.value >> 8) };
	} else if (Utils.isLessThanUnsigned(this.value,
		VarInt.getUnsignedIntegerMaxValue())) {
	    byte[] bytes = new byte[5];
	    bytes[0] = (byte) 254;
	    Utils.uint32ToByteArrayLE(this.value, bytes, 1);
	    return bytes;
	} else {
	    byte[] bytes = new byte[9];
	    bytes[0] = (byte) 255;
	    Utils.uint32ToByteArrayLE(this.value, bytes, 1);
	    Utils.uint32ToByteArrayLE(this.value >>> 32, bytes, 5);
	    return bytes;
	}
    }

    /**
     * @return The maximum unsigned integer.
     */
    public static long getUnsignedIntegerMaxValue() {
	return (-1 & 0xffffffff) & INT_MASK;
    }

    /**
     * @return The BigInteger representation of this variable integer..
     */
    public BigInteger getValue() {
	return BigInteger.valueOf(this.value);
    }
}