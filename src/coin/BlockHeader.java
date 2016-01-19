package coin;

import java.math.BigInteger;

import util.Utils;
import util.crypto.Sha256Hash;

/**
 * @author A
 * 
 */
public class BlockHeader {

    BigInteger nVersion, nTime, nBits, nNonce, nDoS;
    Sha256Hash prevBlockHash, merkleRootHash;

    /**
     * 
     */
    public BlockHeader() {
	// TODO Auto-generated constructor stub
    }

    /**
     * Create a BlockHeader from a byte stream
     * 
     * @param block
     * @param offset
     */

    public BlockHeader(byte[] block, int offset) {
	// Create a new block that we will fill.
	try {
	    // Next we read the block version number normally a uint32_t
	    byte[] versionNum = { block[0], block[1], block[2], block[3] };

	    this.setnVersion(BigInteger.valueOf(Utils.readUint32(versionNum, 0)));

	    /*
	     * try { System.in.read(); } catch (IOException e1) { // TODO
	     * Auto-generated catch block e1.printStackTrace(); }
	     */

	    offset += 4;

	    // Next is the previous block hash as a char[32]
	    byte[] prevHash = new byte[32];

	    for (byte x = 0; x < 32; x++) {
		prevHash[x] = block[offset + x];
	    }
	    /*
	     * try {
	     * 
	     * bH.setPrevBlockHash(Utils.parseAsHexOrBase58(new String(prevHash,
	     * "US-ASCII"))); } catch (Exception e) { e.printStackTrace(); }
	     */

	    Sha256Hash prevHash256 = new Sha256Hash(
		    Utils.reverseBytes(prevHash));
	    this.setPrevBlockHash(prevHash256);

	    offset += 32;

	    // Next is the merkle root hash as a char[32]
	    byte[] merkleRoot = new byte[32];

	    for (byte x = 0; x < 32; x++) {
		merkleRoot[x] = block[offset + x];
	    }

	    Sha256Hash merkleHash256 = new Sha256Hash(
		    Utils.reverseBytes(merkleRoot));
	    this.setMerkleRootHash(merkleHash256);

	    offset += 32;

	    // Next is the unix timestamp indicating when the block was
	    // mined
	    // as a uint32_t
	    byte[] timestamp = new byte[4];
	    for (byte x = 0; x < 4; x++) {
		timestamp[x] = block[offset + x];
	    }
	    this.setnTime(BigInteger.valueOf(Utils.readUint32(timestamp, 0)));

	    offset += 4;

	    // Next is the calculated difficulty.. aka bits..
	    // as a uint32_t
	    byte[] bits = new byte[4];
	    for (byte x = 0; x < 4; x++) {
		bits[x] = block[offset + x];
	    }
	    // bH.setnBits(Utils.decodeCompactBits(Utils.reverseBytes(bits)));
	    this.setnBits(BigInteger.valueOf(Utils.readUint32(bits, 0)));
	    // System.out.println(Hex.encode(bH.getnBits().toByteArray())
	    // .toString());

	    offset += 4;

	    // Next is the nonce value used to generate this block..
	    // as a uint32_t
	    byte[] nonce = new byte[4];
	    for (byte x = 0; x < 4; x++) {
		nonce[x] = block[offset + x];
	    }
	    this.setnNonce(BigInteger.valueOf(Utils.readUint32(nonce, 0)));

	} catch (Exception e) {// If this occurs the block was not valid in some
			       // way
	    e.printStackTrace();

	}
    }

    /**
     * Typically 4 bytes
     * 
     * @return nVersion Block Version Number
     */
    public BigInteger getnVersion() {
	return this.nVersion;
    }

    /**
     * Typically 4 bytes
     * 
     * @param nVersion1
     *            Block Version Number
     */
    public void setnVersion(BigInteger nVersion1) {
	this.nVersion = nVersion1;
    }

    /**
     * Typically 4 bytes
     * 
     * @return nTime Block TimeStamp
     */
    public BigInteger getnTime() {
	return this.nTime;
    }

    /**
     * Typically 4 bytes
     * 
     * @param nTime1
     *            Block TimeStamp
     */
    public void setnTime(BigInteger nTime1) {
	this.nTime = nTime1;
    }

    /**
     * Updated when difficulty is adjusted Typically 4 bytes
     * 
     * @return nBits Current target in compact format
     */
    public BigInteger getnBits() {
	return this.nBits;
    }

    /**
     * Updated when difficulty is adjusted Typically 4 bytes
     * 
     * @param nBits1
     *            Current target in compact format
     */
    public void setnBits(BigInteger nBits1) {
	this.nBits = nBits1;
    }

    /**
     * Typically 4 bytes Increments
     * 
     * @return nNonce Number of times a hash has been tried
     */
    public BigInteger getnNonce() {
	return this.nNonce;
    }

    /**
     * Typically 4 bytes Increments
     * 
     * @param nNonce1
     *            Number of times a hash has been tried
     */
    public void setnNonce(BigInteger nNonce1) {
	this.nNonce = nNonce1;
    }

    /**
     * Currently does nothing Included for legacy compatibility sake
     * 
     * Supposed Denial-of-Service Protection
     * 
     * @return nDoS The banscore(s)? of the one who relayed the block?
     */
    public BigInteger getnDoS() {
	return this.nDoS;
    }

    /**
     * Currently does nothing Included for legacy compatibility sake
     * 
     * Supposed Denial-of-Service Protection
     * 
     * @param nDoS1
     *            The banscore(s)? of the one who relayed the block?
     */
    public void setnDoS(BigInteger nDoS1) {
	this.nDoS = nDoS1;
    }

    /**
     * Typically 32 Bytes
     * 
     * Legacy stored as a character array, stored here as an Sha256Hash Object
     * 
     * @return prevBlockHash Previous linked block's hash value
     */
    public Sha256Hash getPrevBlockHash() {
	return this.prevBlockHash;
    }

    /**
     * Typically 32 Bytes
     * 
     * Legacy stored as a character array, stored here as an Sha256Hash Object
     * 
     * @param prevBlockHash1
     *            Previous linked block's hash value
     */
    public void setPrevBlockHash(Sha256Hash prevBlockHash1) {
	this.prevBlockHash = prevBlockHash1;
    }

    /**
     * Typically 32 Bytes
     * 
     * Legacy stored as a character array, stored here as an Sha256Hash Object
     * 
     * @return merkleRootHash Hash based on all the transactions in this block
     */
    public Sha256Hash getMerkleRootHash() {
	return this.merkleRootHash;
    }

    /**
     * Typically 32 Bytes
     * 
     * Legacy stored as a character array, stored here as an Sha256Hash Object
     * 
     * @param merkleRootHash1
     *            Hash based on all the transactions in this block
     */
    public void setMerkleRootHash(Sha256Hash merkleRootHash1) {
	this.merkleRootHash = merkleRootHash1;
    }
}
