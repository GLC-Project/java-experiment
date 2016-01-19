package util;

import java.math.BigInteger;

/**
 * @author A
 * 
 *         Holds client constants
 *
 */
public class Constants {

    /**
     * Not Found Message
     */
    public final static BigInteger NOT_FOUND = BigInteger.valueOf(0);

    /**
     * The hash is a TXID.
     * 
     * Also indicates a receiving type of TXID
     */
    public final static BigInteger MSG_TX = BigInteger.valueOf(1);

    /**
     * The hash is of a block header.
     * 
     * Also indicates a receiving type of Block
     */
    public final static BigInteger MSG_BLOCK = BigInteger.valueOf(2);

    /**
     * The hash is of a block header; identical to MSG_BLOCK. When used in a
     * getdata message, this indicates the response should be a merkleblock
     * message rather than a block message (but this only works if a bloom
     * filter was previously configured). Only for use in getdata messages.
     * 
     */
    public final static BigInteger MSG_FILTERED_BLOCK = BigInteger.valueOf(3);

    /**
     * Indicates a receiving type of Block Header
     */
    public final static BigInteger MSG_BLOCK_HEADER = BigInteger.valueOf(4);

    public Constants() {
	// TODO Auto-generated constructor stub
    }

}
