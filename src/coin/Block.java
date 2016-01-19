package coin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

import util.BigFastList;
import util.Utils;
import util.crypto.Sha256Hash;

/**
 * @author Amir Eslampanah
 * 
 */
public class Block {

    BigInteger networkID, blockSize, transCounter;

    BigFastList<Transaction> transactions = new BigFastList<Transaction>();

    BlockHeader header = new BlockHeader();

    /**
     * Empty Constructor.. Creates a new block without any assignments.
     */
    public Block() {
    }

    /**
     * Creates an empty block and assigns a block header to that block.
     * 
     * @param header1
     *            The header to assign to this new block.
     */
    public Block(BlockHeader header1) {
	this.header = header1;
    }

    /**
     * This is a getter method for the Network ID tag that goes before every
     * block to indicate which network that block belongs to.
     * 
     * For GoldCoin (GLD) this is: 0xfd, 0xc2, 0xb4, 0xdd or 0xFDC2B4DD
     * 
     * @return the networkID
     */
    public BigInteger getNetworkID() {
	return this.networkID;
    }

    /**
     * This is a setter method for the Network ID tag that goes before every
     * block to indicate which network it belongs to.
     * 
     * For GoldCoin (GLD) this is: 0xfd, 0xc2, 0xb4, 0xdd or 0xFDC2B4DD
     * 
     * @param networkID1
     *            the 4 byte networkID that goes before every block to indicate
     *            which network it belongs to.
     * 
     */
    public void setNetworkID(BigInteger networkID1) {
	this.networkID = networkID1;
    }

    /**
     * A getter method that returns the blockSize for a given block.
     * 
     * Typically 4 bytes, limiting the maximum block size to an unsigned 32 bit
     * integer.
     * 
     * This may change in the future..
     * 
     * @return blockSize number of bytes following up to end of the block.
     * 
     */
    public BigInteger getBlockSize() {
	return this.blockSize;
    }

    /**
     * A setter method that sets the blockSize for a given block.
     * 
     * Typically 4 bytes, limiting the maximum block size to an unsigned 32 bit
     * integer.
     * 
     * This may change in the future..
     * 
     * @param blockSize1
     *            number of bytes following up to end of the block.
     */
    public void setBlockSize(BigInteger blockSize1) {
	this.blockSize = blockSize1;
    }

    /**
     * A getter method that returns the number of transactions in a given block.
     * 
     * Typically 1-9 bytes | Positive integer indicating the number of
     * transactions in a block.
     * 
     * @return transCounter number of transactions in a given block.
     */
    public BigInteger getTransCounter() {
	return this.transCounter;
    }

    /**
     * A setter method that sets the number of transactions in a given block.
     * 
     * Typically 1-9 bytes | Positive integer indicating the number of
     * transactions in a block.
     * 
     * @param transCounter1
     *            number of transactions in a given block.
     */
    public void setTransCounter(BigInteger transCounter1) {
	this.transCounter = transCounter1;
    }

    /**
     * A getter method that gets the BigFastList of transactions in a given
     * block.
     * 
     * @return transactions BigFastList of transactions that are included in a
     *         given block.
     */
    public BigFastList<Transaction> getTransactions() {
	return this.transactions;
    }

    /**
     * A setter method that sets the BigFastList of transactions in a given
     * block.
     * 
     * @param transactions1
     *            BigFastList of transactions that are included in a given
     *            block.
     */
    public void setTransactions(BigFastList<Transaction> transactions1) {
	this.transactions = transactions1;
    }

    /**
     * A getter method that returns the block header.
     * 
     * The block header contains the following:
     * 
     * Block Version Number (Indicates on what version of the client this
     * particular block was mined) Previous Block Hash (SHA256D hash of the
     * previous block header) Merkle Root Hash (SHA256D hash based on all
     * transactions in the block) Time (since 1970-01-01T00:00 UTC) Bits
     * (Current difficulty target in a compact format, 32 bits) Nonce (32 bit
     * number that starts from 0 and counts the number of times a hash was
     * tried)
     * 
     * @return the header
     */
    public BlockHeader getHeader() {
	return this.header;
    }

    /**
     * A setter method that returns the block header.
     * 
     * The block header contains the following:
     * 
     * Block Version Number (Indicates on what version of the client this
     * particular block was mined) Previous Block Hash (SHA256D hash of the
     * previous block header) Merkle Root Hash (SHA256D hash based on all
     * transactions in the block) Time (since 1970-01-01T00:00 UTC) Bits
     * (Current difficulty target in a compact format, 32 bits) Nonce (32 bit
     * number that starts from 0 and counts the number of times a hash was
     * tried)
     * 
     * @param header1
     *            the header to set
     */
    public void setHeader(BlockHeader header1) {
	this.header = header1;
    }

    /**
     * Getter method that returns the current SHA256D block hash. This is the
     * hash of the block header only!
     * 
     * @return SHA256D hash of this block as an SHA256Hash object.
     */
    public Sha256Hash getHash() {
	return calcHash();
    }

    /**
     * Getter method that returns the current Scrypt block hash. This is the
     * hash of the block header only!
     * 
     * @return Scrypt hash of this block as an SHA256Hash object.
     */
    public Sha256Hash getScryptHash() {
	return calcScryptHash();
    }

    // default for testing
    private void writeHeader(OutputStream stream) throws IOException {

	Utils.uint32ToByteStreamLE(this.getHeader().getnVersion().longValue(),
		stream);
	stream.write(Utils
		.reverseBytes(this.getHeader().getPrevBlockHash().getBytes()));
	stream.write(Utils
		.reverseBytes(this.getHeader().getMerkleRootHash().getBytes()));
	Utils.uint32ToByteStreamLE(this.getHeader().getnTime().longValue(),
		stream);
	Utils.uint32ToByteStreamLE(this.getHeader().getnBits().longValue(),
		stream);
	Utils.uint32ToByteStreamLE(this.getHeader().getnNonce().longValue(),
		stream);
    }

    private Sha256Hash calcHash() {
	try {
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    writeHeader(bos);
	    return new Sha256Hash(
		    Utils.reverseBytes(Utils.doubleDigest(bos.toByteArray())));
	} catch (IOException e) {
	    throw new RuntimeException(e); // Cannot happen.
	}
    }

    private Sha256Hash calcScryptHash() {

	try {
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    writeHeader(bos);
	    return new Sha256Hash(
		    Utils.reverseBytes(Utils.scryptDigest(bos.toByteArray())));
	} catch (IOException e) {
	    throw new RuntimeException(e); // Cannot happen.
	}
    }

    /**
     * @param anotherBlock
     * @return 0 if the blocks are equal, -1 otherwise
     */
    public int compareTo(Block anotherBlock) {
	Sha256Hash a = this.getHash();
	Sha256Hash b = anotherBlock.getHash();

	if (a.equals(b)) {
	    return 0;
	}
	return -1;
    }

    /**
     * Compares this block's hash with the block parameter's previous block
     * 
     * @param anotherBlock
     * 
     * @return 0 if the blocks are equal, -1 otherwise
     */
    public int compareWithPrevOfParam(Block anotherBlock) {
	Sha256Hash a = this.getHash();

	Sha256Hash b = anotherBlock.getHeader().getPrevBlockHash();

	if (a.equals(b)) {
	    return 0;
	}
	return -1;
    }

    /**
     * Compares this block's previous hash with the block parameter's hash
     * 
     * @param anotherBlock
     * 
     * @return 0 if the blocks are equal, -1 otherwise
     */
    public int compareWithPrevOfCur(Block anotherBlock) {

	Sha256Hash a = this.getHeader().getPrevBlockHash();
	Sha256Hash b = anotherBlock.getHash();

	if (a.equals(b)) {
	    return 0;
	}
	return -1;
    }

    /**
     * Returns true if the hash of the block is OK (lower than difficulty
     * target).
     */
    private boolean checkProofOfWork() {
	// This part is key - it is what proves the block was as difficult to
	// make as it claims
	// to be. Note however that in the context of this function, the block
	// can claim to be
	// as difficult as it wants to be .... if somebody was able to take
	// control of our network
	// connection and fork us onto a different chain, they could send us
	// valid blocks with
	// ridiculously easy difficulty and this function would accept them.
	//
	// To prevent this attack from being possible, elsewhere we check that
	// the difficultyTarget
	// field is of the right value. This requires us to have the preceding
	// blocks.
	BigInteger target = Utils.decodeCompactBits(
		this.getHeader().getnBits().longValueExact());

	BigInteger h = getScryptHash().toBigInteger();
	if (h.compareTo(target) > 0) {
	    // Proof of work check failed!
	    return false;
	}
	return true;
    }

    /**
     * Finds a value of nonce that makes the blocks hash lower than the
     * difficulty target. This is called mining, but solve() is far too slow to
     * do real mining with. It exists only for unit testing purposes and is not
     * a part of the public API.
     * 
     * This can loop forever if a solution cannot be found solely by
     * incrementing nonce. It doesn't change extraNonce.
     */
    public void solve() {
	while (true) {
	    // Is our proof of work valid yet?
	    if (checkProofOfWork())
		return;
	    // No, so increment the nonce and try again.
	    this.getHeader().setnNonce(
		    this.getHeader().getnNonce().add(BigInteger.ONE));
	}
    }
}
