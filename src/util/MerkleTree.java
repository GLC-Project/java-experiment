package util;

import java.math.BigInteger;
import java.util.Objects;

import net.Connection;
import util.Exceptions.VerificationException;
import util.crypto.Sha256Hash;

/**
 * @author Amir Eslampanah
 * 
 *         Copyright 2012 The Bitcoin Developers
 * 
 *         Licensed under: Apache 2.0 License
 * 
 *         Copyright 2015 Amir Eslampanah
 *
 *         Licensed under: MIT License
 */
public class MerkleTree {

    private BigInteger transactionCount = null;
    private byte[] flags = null;
    private BigFastList<Sha256Hash> hashes = null;

    private final Connection curCon;

    /**
     * @param transactionCount1
     * @param flags1
     * @param hashes1
     */
    public MerkleTree(BigInteger transactionCount1, byte[] flags1,
	    BigFastList<Sha256Hash> hashes1, Connection con) {
	// TODO Auto-generated constructor stub
	this.transactionCount = transactionCount1;
	this.flags = flags1;
	this.hashes = hashes1;
	this.curCon = con;
    }

    // helper function to efficiently calculate the number of nodes at given
    // height in the merkle tree
    private BigInteger getTreeWidth(BigInteger bigInteger) {
	return (this.getTransactionCount()
		.add((BigInteger.ONE.shiftLeft(bigInteger.intValueExact())))
		.subtract(BigInteger.ONE))
			.shiftRight(bigInteger.intValueExact());

    }

    // Checked
    // helper function to efficiently calculate the number of nodes at given
    // height in the merkle tree
    private static BigInteger getTreeWidth(BigInteger transactionCount,
	    BigInteger height) {
	// Addition occurs BEFORE bitshift
	return (transactionCount
		.add(BigInteger.ONE.shiftLeft(height.intValueExact()))
		.subtract(BigInteger.ONE)).shiftRight(height.intValueExact());
    }

    private static class ValuesUsed {
	public ValuesUsed() {
	    // TODO Auto-generated constructor stub
	}

	public BigInteger bitsUsed = BigInteger.ZERO,
		hashesUsed = BigInteger.ZERO;
    }

    // Checked except for return
    // recursive function that traverses tree nodes, consuming the bits and
    // hashes produced by TraverseAndBuild.
    // it returns the hash of the respective node.
    /**
     * @param height
     * @param pos
     * @param used
     * @param matchedHashes
     * @return
     * @throws VerificationException
     */
    @SuppressWarnings("nls")
    public Sha256Hash recursiveExtractHashes(BigInteger height, BigInteger pos,
	    ValuesUsed used, BigFastList<Sha256Hash> matchedHashes)
		    throws VerificationException {
	if (used.bitsUsed.compareTo(
		BigInteger.valueOf(this.getFlags().length * 8)) >= 0) {
	    // overflowed the bits array - failure
	    throw new VerificationException(
		    "MerkleTree overflowed its bits array");
	}
	boolean parentOfMatch = Utils.checkBitLE(this.getFlags(),
		(used.bitsUsed = used.bitsUsed.add(BigInteger.ONE))
			.intValueExact());
	if (height.compareTo(BigInteger.ZERO) == 0 || !parentOfMatch) {
	    // if at height 0, or nothing interesting below, use stored hash and
	    // do not descend
	    if (used.hashesUsed.compareTo(
		    BigInteger.valueOf(this.getHashes().size())) >= 0) {
		// overflowed the hash array - failure
		throw new VerificationException(
			"MerkleTree overflowed its hash array");
	    }

	    Sha256Hash hash = this.hashes
		    .get((used.hashesUsed = used.hashesUsed.add(BigInteger.ONE))
			    .intValueExact());
	    if (height.compareTo(BigInteger.ZERO) == 0 && parentOfMatch) // in
									 // case
									 // of
									 // height
									 // 0,
									 // we
									 // have
									 // a
		// matched txid
		matchedHashes.add(hash);
	    return hash;
	}
	// otherwise, descend into the subtrees to extract matched txids and
	// hashes
	byte[] left = recursiveExtractHashes(height.subtract(BigInteger.ONE),
		pos.multiply(BigInteger.valueOf(2)), used, matchedHashes)
			.getBytes();

	byte[] right;

	if (pos.multiply(BigInteger.valueOf(2)).add(BigInteger.ONE)
		.compareTo(getTreeWidth(this.transactionCount,
			height.subtract(BigInteger.ONE))) < 0) {
	    right = recursiveExtractHashes(height.subtract(BigInteger.ONE),
		    pos.multiply(BigInteger.valueOf(2)).add(BigInteger.ONE),
		    used, matchedHashes).getBytes();
	} else {
	    right = left;
	}
	// and combine them before returning
	return new Sha256Hash(Utils.reverseBytes(
		Utils.doubleDigestTwoBuffers(Utils.reverseBytes(left), 0, 32,
			Utils.reverseBytes(right), 0, 32)));
    }

    // Checked
    /**
     * Calculates a PMT given the list of leaf hashes and which leaves need to
     * be included. The relevant interior hashes are calculated and a new PMT
     * returned.
     */
    public static MerkleTree buildFromLeaves(byte[] includeBits,
	    BigFastList<Sha256Hash> allLeafHashes, Connection con) {
	// Calculate height of the tree.
	int height = 0;
	while (getTreeWidth(BigInteger.valueOf(allLeafHashes.size()),
		BigInteger.valueOf(height)).compareTo(BigInteger.ONE) > 0)
	    height++;
	BigFastList<Boolean> bitList = new BigFastList<Boolean>();
	BigFastList<Sha256Hash> hashes = new BigFastList<Sha256Hash>();
	traverseAndBuild(height, 0, allLeafHashes, includeBits, bitList,
		hashes);
	byte[] bits = new byte[(int) Math.ceil(bitList.size() / 8.0)];
	for (int i = 0; i < bitList.size(); i++)
	    if (bitList.get(i).booleanValue())
		Utils.setBitLE(bits, i);

	return new MerkleTree(BigInteger.valueOf(allLeafHashes.size()), bits,
		hashes, con);
    }

    // Checked
    private static void traverseAndBuild(int height, int i,
	    BigFastList<Sha256Hash> allLeafHashes, byte[] includeBits,
	    BigFastList<Boolean> bitList,
	    BigFastList<Sha256Hash> hashesResult) {

	boolean parentOfMatch = false;
	// Is this node a parent of at least one matched hash?
	for (int p = i << height; p < (i + 1) << height
		&& p < allLeafHashes.size(); p++) {
	    if (Utils.checkBitLE(includeBits, p)) {
		parentOfMatch = true;
		break;
	    }
	}
	// Store as a flag bit.
	bitList.add(Boolean.valueOf(parentOfMatch));
	if (height == 0 || !parentOfMatch) {
	    // If at height 0, or nothing interesting below, store hash and
	    // stop.
	    hashesResult.add(calcHash(height, i, allLeafHashes));
	} else {
	    // Otherwise descend into the subtrees.
	    int h = height - 1;
	    int p = i * 2;
	    traverseAndBuild(h, p, allLeafHashes, includeBits, bitList,
		    hashesResult);

	    if (BigInteger.valueOf(p + 1)
		    .compareTo(getTreeWidth(
			    BigInteger.valueOf(allLeafHashes.size()),
			    BigInteger.valueOf(h))) < 0) {
		traverseAndBuild(h, p + 1, allLeafHashes, includeBits, bitList,
			hashesResult);
	    }

	}

    }

    // checked
    private static Sha256Hash calcHash(int height, int i,
	    BigFastList<Sha256Hash> allLeafHashes) {
	if (height == 0) {
	    // Hash at height 0 is just the regular tx hash itself.
	    return allLeafHashes.get(i);
	}
	int h = height - 1;
	int p = i * 2;
	Sha256Hash left = calcHash(h, p, allLeafHashes);
	// Calculate right hash if not beyond the end of the array - copy left
	// hash otherwise.
	Sha256Hash right;
	if (BigInteger.valueOf(p + 1).compareTo(
		getTreeWidth(BigInteger.valueOf(allLeafHashes.size()),
			BigInteger.valueOf(h))) < 0) {
	    right = calcHash(h, p + 1, allLeafHashes);
	} else {
	    right = left;
	}

	// and combine them before returning
	return combineLeftRight(left.getBytes(), right.getBytes());
    }

    // checked
    private static Sha256Hash combineLeftRight(byte[] left, byte[] right) {
	return new Sha256Hash(Utils.reverseBytes(
		Utils.doubleDigestTwoBuffers(Utils.reverseBytes(left), 0, 32,
			Utils.reverseBytes(right), 0, 32)));
    }

    // checked
    /**
     * Extracts tx hashes that are in this merkle tree and returns the merkle
     * root of this tree.
     * 
     * The returned root should be checked against the merkle root contained in
     * the block header for security.
     * 
     * @param matchedHashes
     *            A list which will contain the matched txn (will be cleared)
     *            Required to be a LinkedHashSet in order to retain order or
     *            transactions in the block
     * @return the merkle root of this merkle tree
     * @throws VerificationException
     * @throws ProtocolException
     *             if this partial merkle tree is invalid
     */
    @SuppressWarnings("nls")
    public Sha256Hash getTxnHashAndMerkleRoot(
	    BigFastList<Sha256Hash> matchedHashes)
		    throws VerificationException {
	matchedHashes.clear();

	// An empty set will not work
	if (this.getTransactionCount().compareTo(BigInteger.ZERO) == 0)
	    throw new VerificationException(
		    "Got a MerkleTree with 0 transactions");
	// check for excessively high numbers of transactions
	if (this.getTransactionCount().compareTo(this.getCurCon().getCurCoin()
		.getMAX_BLOCK_SIZE().divide(BigInteger.valueOf(60))) > 0) // 60
									  // is
									  // the
									  // lower
									  // bound
									  // for
									  // the
									  // size
									  // of
									  // a
									  // serialized
									  // CTransaction
	    throw new VerificationException(
		    "Got a MerkleTree with more transactions than is possible");
	// there can never be more hashes provided than one for every txid
	if (BigInteger.valueOf(this.hashes.size())
		.compareTo(this.transactionCount) > 0)
	    throw new VerificationException(
		    "Got a MerkleTree with more hashes than transactions");
	// there must be at least one bit per node in the partial tree, and at
	// least one node per hash
	if (this.flags.length * 8 < this.hashes.size())
	    throw new VerificationException(
		    "Got a MerkleTree with fewer matched bits than hashes");
	// calculate height of tree
	BigInteger height = BigInteger.ZERO;
	while (getTreeWidth(this.transactionCount, height)
		.compareTo(BigInteger.valueOf(1)) > 0)
	    height = height.add(BigInteger.ONE);
	// traverse the partial tree
	ValuesUsed used = new ValuesUsed();
	Sha256Hash merkleRoot = recursiveExtractHashes(height,
		BigInteger.valueOf(0), used, matchedHashes);
	// verify that all bits were consumed (except for the padding caused by
	// serializing it as a byte sequence)
	if ((used.bitsUsed.add(BigInteger.valueOf(7)))
		.divide(BigInteger.valueOf(8))
		.compareTo(BigInteger.valueOf(this.flags.length)) != 0 ||
		// verify that all hashes were consumed
		used.hashesUsed
			.compareTo(BigInteger.valueOf(this.hashes.size())) != 0)
	    throw new VerificationException(
		    "Got a MerkleTree that didn't need all the data it provided");

	return merkleRoot;
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (o == null || getClass() != o.getClass())
	    return false;
	MerkleTree other = (MerkleTree) o;
	return this.transactionCount == other.transactionCount
		&& this.hashes.equals(other.hashes)
		&& java.util.Arrays.equals(this.flags, other.flags);

    }

    // Unsure
    @Override
    public int hashCode() {
	return Objects.hash(this.transactionCount, this.hashes, this.flags);
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
	return "PartialMerkleTree{" + "transactionCount="
		+ this.transactionCount + ", matchedChildBits="
		+ java.util.Arrays.toString(this.flags) + ", hashes="
		+ this.hashes + '}';
    }

    /**
     * @return the transactionCount
     */
    public BigInteger getTransactionCount() {
	return this.transactionCount;
    }

    /**
     * @param transactionCount1
     *            the transactionCount to set
     */
    public void setTransactionCount(BigInteger transactionCount1) {
	this.transactionCount = transactionCount1;
    }

    /**
     * @return the flags
     */
    public byte[] getFlags() {
	return this.flags;
    }

    /**
     * @param flags1
     *            the flags to set
     */
    public void setFlags(byte[] flags1) {
	this.flags = flags1;
    }

    /**
     * @return the hashes
     */
    public BigFastList<Sha256Hash> getHashes() {
	return this.hashes;
    }

    /**
     * @param hashes1
     *            the hashes to set
     */
    public void setHashes(BigFastList<Sha256Hash> hashes1) {
	this.hashes = hashes1;
    }

    /**
     * @return the curCon
     */
    public Connection getCurCon() {
	return this.curCon;
    }

}
