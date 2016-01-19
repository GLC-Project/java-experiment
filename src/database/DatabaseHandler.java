package database;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mightyfish.util.Arrays;
import org.mightyfish.util.encoders.Hex;

import coin.Block;
import coin.BlockHeader;
import coin.Coin;
import coin.Transaction.TransactionInput;
import coin.Transaction.TransactionOutput;
import util.BigFastList;
import util.Utils;
import util.Exceptions.ScriptException;
import util.Exceptions.VerificationException;
import util.crypto.Sha256Hash;
import util.crypto.VarInt;

/**
 * @author Amir Eslampanah
 * 
 */
public class DatabaseHandler {

    private ExcaliburDB primaryDatabase = null;

    /**
     * Current coin that we are dealing with
     * 
     * This reference is what keeps everything from going hay wire when the
     * coins are switched. Each instance of this class is tied to a particular
     * coin.
     */
    private Coin coin = null;

    /**
     * @param coin1
     * 
     */
    public DatabaseHandler(Coin coin1) {
	this.primaryDatabase = new ExcaliburDB();
	this.coin = coin1;
    }

    /**
     * @param homeDirectory
     * @throws IOException
     */
    public DatabaseHandler(Coin coin1, StringBuffer homeDirectory)
	    throws IOException {

	this.primaryDatabase = new ExcaliburDB();
	this.coin = coin1;
	/*
	 * Nodes collect new transactions into a block, hash them into a hash
	 * tree, and scan through nonce values to make the block's hash satisfy
	 * proof-of-work requirements. When they solve the proof-of-work, they
	 * broadcast the block to everyone and the block is added to the block
	 * chain. The first transaction in the block is a special one that
	 * creates a new coin owned by the creator of the block.
	 * 
	 * Blocks are appended to blk0001.dat files on disk. Their location on
	 * disk is indexed by CBlockIndex objects in memory. (prior design)
	 * 
	 * Since this is where we'd normally load blkindex.dat, we must first
	 * index all the locations of blocks on disk and then load them. Except
	 * that it's easier to read the blocks raw.
	 */
	try {
	    // Try to load the old database.
	    this.loadDB();

	    // Parse Blocks
	    this.parseBlocks();

	    /*
	     * // Once loaded we rename the old database. File file = new
	     * File(this.getPath() + File.separator + "blk0001.dat");
	     * //$NON-NLS-1$ file.renameTo(new File(this.getPath() +
	     * File.separator + "blk0001-BACKUP.dat")); //$NON-NLS-1$
	     */

	} catch (IOException e) {
	    e.printStackTrace();
	}

	// Once the block locations are loaded we stop and await additional
	// blocks from network

	/*
	 * data.setData(replacementData.getBytes("UTF-8")); //$NON-NLS-1$ // No
	 * transaction handle is used on the cursor read or write // methods.
	 * cursor.putCurrent(data);
	 */

    }

    /**
     * @return the primaryDatabase
     */
    public ExcaliburDB getPrimaryDatabase() {
	return this.primaryDatabase;
    }

    /**
     * @param primaryDatabase
     *            the primaryDatabase to set
     */
    public void setPrimaryDatabase(ExcaliburDB primaryDatabase) {
	this.primaryDatabase = primaryDatabase;
    }

    /**
     * This handles database loads that are using the old format and not
     * ExcaliburDB
     * 
     * @throws IOException
     */
    @SuppressWarnings("nls")
    public void loadDB() throws IOException {
	// A special tool called DBDump needs to be
	// used to even load blkindex.dat as it is in a platform dependent
	// format

	/*
	 * // Dump the old Database to a readable format String[] input = {
	 * "-h", this.getPath().toString(), "-s", "blockindex" }; //$NON-NLS-1$
	 * //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ try {
	 * 
	 * DbDump.main(input); } catch (Exception e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); }
	 */

	// Easier and more efficient to load the blocks raw!

	Path file = Paths.get(this.getPath() + File.separator + "blk0001.dat"); //$NON-NLS-1$

	RandomAccessFile aFile = new RandomAccessFile(file.toFile(), "r"); //$NON-NLS-1$

	FileChannel inChannel = aFile.getChannel();

	ByteBuffer buf = ByteBuffer.allocate(4);

	BigFastList<byte[]> blocks = new BigFastList<byte[]>();

	long numBlocks = 0;

	try {
	    while (inChannel.read(buf) != -1) {
		// Here we do the actual reading

		buf.flip(); // Flip the buffer as it is backwards.

		// We know the the first 4 bytes of a block are our network's
		// magic number

		// Some debugging code
		/*
		 * if (inChannel.position() < 5) {
		 * 
		 * System.out.println(Utils.bytesToHexString(buf.array()));
		 * System.out.println(Utils.bytesToHexString(this.getCoin()
		 * .getNetworkBytes()));
		 * 
		 * System.out.println(Arrays.areEqual(buf.array(), Main
		 * .getDaemon().getNetworkBytes())); break; }
		 */

		// Read one byte at a time until we find a magic number
		// and a valid block size

		while (!Arrays.areEqual(buf.array(),
			this.getCoin().getNetworkBytes())) {
		    inChannel.position(inChannel.position() - buf.limit() + 1);
		    inChannel.read(buf);
		}
		// Now that we've found a magic number we must check for a valid
		// block size

		inChannel.read(buf);

		BigInteger numToRead = BigInteger
			.valueOf(Utils.readUint32(buf.array(), 0));

		// System.out.println("Num to Read " + numToRead.toString());

		// Check if blocksize is invalid. If invalid read next 4 bytes
		// starting one byte ahead from what was previously read
		if (numToRead.compareTo(this.getCoin().getMAX_BLOCK_SIZE()) > 0
			|| numToRead.compareTo(BigInteger.ZERO) <= 0) {
		    inChannel.position(
			    inChannel.position() - (buf.limit() + 4) + 1);
		    continue;
		}

		// Now we should have a valid block size(hopefully) we can read
		// from there

		buf = ByteBuffer.allocate(numToRead.intValueExact());
		inChannel.read(buf);
		// System.out.println(numBlocks += 1);
		blocks.add(buf.array());

		// Lastly we set the buffer back to a 4 byte buffer
		buf = ByteBuffer.allocate(4);

	    }

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	aFile.close();
	inChannel.close();

	this.loadBlocks(blocks);
    }

    /**
     * Will return the de-serialized version of the block header without
     * modifying the database
     * 
     * @param block
     * @return
     */
    public BlockHeader deserializeBlockHeader(byte[] block) {

	// Create a new blockHeader that we will fill.
	BlockHeader bH = new BlockHeader();

	int offset = 0;

	// Next we read the block version number normally a uint32_t
	byte[] versionNum = { block[0], block[1], block[2], block[3] };

	bH.setnVersion(BigInteger.valueOf(Utils.readUint32(versionNum, 0)));

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

	Sha256Hash prevHash256 = new Sha256Hash(Utils.reverseBytes(prevHash));
	bH.setPrevBlockHash(prevHash256);

	offset += 32;

	// Next is the merkle root hash as a char[32]
	byte[] merkleRoot = new byte[32];

	for (byte x = 0; x < 32; x++) {
	    merkleRoot[x] = block[offset + x];
	}

	Sha256Hash merkleHash256 = new Sha256Hash(
		Utils.reverseBytes(merkleRoot));
	bH.setMerkleRootHash(merkleHash256);

	offset += 32;

	// Next is the unix timestamp indicating when the block was
	// mined
	// as a uint32_t
	byte[] timestamp = new byte[4];
	for (byte x = 0; x < 4; x++) {
	    timestamp[x] = block[offset + x];
	}
	bH.setnTime(BigInteger.valueOf(Utils.readUint32(timestamp, 0)));

	offset += 4;

	// Next is the calculated difficulty.. aka bits..
	// as a uint32_t
	byte[] bits = new byte[4];
	for (byte x = 0; x < 4; x++) {
	    bits[x] = block[offset + x];
	}
	// bH.setnBits(Utils.decodeCompactBits(Utils.reverseBytes(bits)));
	bH.setnBits(BigInteger.valueOf(Utils.readUint32(bits, 0)));
	// System.out.println(Hex.encode(bH.getnBits().toByteArray())
	// .toString());

	offset += 4;

	// Next is the nonce value used to generate this block..
	// as a uint32_t
	byte[] nonce = new byte[4];
	for (byte x = 0; x < 4; x++) {
	    nonce[x] = block[offset + x];
	}
	bH.setnNonce(BigInteger.valueOf(Utils.readUint32(nonce, 0)));

	offset += 4;
	return bH;
    }

    /**
     * Loads a single block and then returns its object form.
     * 
     * Warning!- Changes to the returned block propagate to the blockchain
     * version.
     * 
     * @param block
     * @return
     */

    @SuppressWarnings("nls")
    public Block loadBlock(byte[] block) {
	// Create a new block that we will fill.
	Block b = new Block();
	b.setHeader(null);

	try {

	    // Create a new blockHeader that we will fill.
	    BlockHeader bH = new BlockHeader();

	    int offset = 0;

	    // Next we read the block version number normally a uint32_t
	    byte[] versionNum = { block[0], block[1], block[2], block[3] };

	    bH.setnVersion(BigInteger.valueOf(Utils.readUint32(versionNum, 0)));

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
	    bH.setPrevBlockHash(prevHash256);

	    offset += 32;

	    // Next is the merkle root hash as a char[32]
	    byte[] merkleRoot = new byte[32];

	    for (byte x = 0; x < 32; x++) {
		merkleRoot[x] = block[offset + x];
	    }

	    Sha256Hash merkleHash256 = new Sha256Hash(
		    Utils.reverseBytes(merkleRoot));
	    bH.setMerkleRootHash(merkleHash256);

	    offset += 32;

	    // Next is the unix timestamp indicating when the block was
	    // mined
	    // as a uint32_t
	    byte[] timestamp = new byte[4];
	    for (byte x = 0; x < 4; x++) {
		timestamp[x] = block[offset + x];
	    }
	    bH.setnTime(BigInteger.valueOf(Utils.readUint32(timestamp, 0)));

	    offset += 4;

	    // Next is the calculated difficulty.. aka bits..
	    // as a uint32_t
	    byte[] bits = new byte[4];
	    for (byte x = 0; x < 4; x++) {
		bits[x] = block[offset + x];
	    }
	    // bH.setnBits(Utils.decodeCompactBits(Utils.reverseBytes(bits)));
	    bH.setnBits(BigInteger.valueOf(Utils.readUint32(bits, 0)));
	    // System.out.println(Hex.encode(bH.getnBits().toByteArray())
	    // .toString());

	    offset += 4;

	    // Next is the nonce value used to generate this block..
	    // as a uint32_t
	    byte[] nonce = new byte[4];
	    for (byte x = 0; x < 4; x++) {
		nonce[x] = block[offset + x];
	    }
	    bH.setnNonce(BigInteger.valueOf(Utils.readUint32(nonce, 0)));

	    offset += 4;

	    // Next is the number of transaction entries
	    // as a varInt (loading this may be somewhat tricky)
	    // Max size for this is 9 bytes so thats how many we need to
	    // check (doubled for safety/avoidance of serialization issues)
	    // (note that this is not necessarily the size of the
	    // actual varInt!!)
	    byte[] txn_Count = new byte[18];
	    try {
		for (byte x = 0; x < 18; x++) {
		    txn_Count[x] = block[offset + x];
		}
	    } catch (Exception e) {// We read too far?

	    }

	    VarInt numEntries = new VarInt(txn_Count, 0);

	    b.setTransCounter(numEntries.getValue());

	    // Next we use the size of the varInt to determine our offset
	    // that we will use to load the actual transactions

	    offset += numEntries.getOriginalSizeInBytes();

	    byte[] transactions = new byte[(block.length) - (offset)];

	    // Copy the transactions alone to our new byte array
	    for (int x = 0; x < transactions.length; x++) {
		transactions[x] = block[(offset) + x];
	    }

	    // De-serialize and assign to an arraylist in new transaction
	    // format

	    try {
		b.setTransactions(Utils.readTransactions(transactions, 0,
			numEntries.getValue()));
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		try {
		    System.in.read();
		} catch (IOException x) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }

	    b.setHeader(bH);

	    if (!verifyBlock(b)) {
		throw new VerificationException(
			"A block with hash: " + b.getHash().toString()
				+ " did not pass the syntax check. \n"
				+ " Discarding.. \n");
	    }

	} catch (Exception e) {// If this occurs the block was not valid in some
			       // way
	    e.printStackTrace();

	}

	// Now we add the block to our coin's blockchain

	if (b.getHeader() != null) {
	    this.getCoin().getBlockChain().add(b);

	    return b;
	}
	return null;

    }

    /**
     * This method checks for out of bounds cases or invalid syntax.
     * 
     * It does not check for hash validity or chain validity. That happens in
     * the checkBlock method in ExcaliburDB
     * 
     * @param b
     * @return True if the block is syntactically valid(at a glance), throws an
     *         exception otherwise.
     * @throws VerificationException
     * 
     */
    @SuppressWarnings("nls")
    public boolean verifyBlock(Block b) throws VerificationException {

	// Since we are potentially throwing exceptions, it doesn't really
	// matter if we use else if
	// but we use it here anyways for good conventions sake
	if (b == null || b.getHeader() == null || b.getHash() == null
		|| b.getNetworkID() == null
		|| (b.getTransCounter().compareTo(BigInteger.ONE) >= 0
			&& b.getTransactions() == null)) {
	    // Generally this happens when de-serialization fails for some
	    // reason
	    // Usually a network error or a bad block
	    throw new VerificationException(
		    "A block that had one or more null fields was discarded! \n");

	} else if (!(b.getNetworkID().compareTo(
		new BigInteger(this.getCoin().getNetworkBytes())) == 0)) {
	    throw new VerificationException(
		    "A block that had an incorrect network number was discarded! \n"
			    + "Network Number: " + b.getNetworkID().toString()
			    + " was found.. " + "Expected: "
			    + new BigInteger(this.getCoin().getNetworkBytes())
				    .toString()
			    + "\n");
	} else if (b.getBlockSize()
		.compareTo(this.getCoin().getMAX_BLOCK_SIZE()) > 0) {
	    throw new VerificationException(
		    "A block that had exceeded the coin's max block size was discarded! \n"
			    + "Block Size: " + b.getBlockSize().toString()
			    + " was found.. " + "Expected Maximum Size of: "
			    + this.getCoin().getMAX_BLOCK_SIZE().toString());
	}

	else if (b.getHeader().getnTime()
		.compareTo(BigInteger.valueOf(18000)) < 0) {
	    throw new VerificationException(
		    "A block that had an invalid timestamp was discarded! \n"
			    + "TimeStamp : "
			    + b.getHeader().getnTime().toString()
			    + " was found.. "
			    + "Expected Minimum Unix Time: 18000");
	}

	return true;
    }

    /**
     * Searches through our nodes for this block's prevHash
     * 
     * If found, adds this block as a node to that node's subnodes
     * 
     * @param b
     */
    public void processOrphan(Block b) {

    }

    public void processOrphans() {

    }

    /**
     * 
     * @param blocks
     */

    @SuppressWarnings("nls")
    private void loadBlocks(BigFastList<byte[]> blocks) {

	long numBlocksToChain = 0;

	for (byte[] block : blocks) {
	    // Create a new block that we will fill.
	    Block b = new Block();
	    // Create a new blockHeader that we will fill.
	    BlockHeader bH = new BlockHeader();

	    int offset = 0;

	    // Next we read the block version number normally a uint32_t
	    byte[] versionNum = { block[0], block[1], block[2], block[3] };

	    bH.setnVersion(BigInteger.valueOf(Utils.readUint32(versionNum, 0)));

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
	    bH.setPrevBlockHash(prevHash256);

	    offset += 32;

	    // Next is the merkle root hash as a char[32]
	    byte[] merkleRoot = new byte[32];

	    for (byte x = 0; x < 32; x++) {
		merkleRoot[x] = block[offset + x];
	    }

	    Sha256Hash merkleHash256 = new Sha256Hash(
		    Utils.reverseBytes(merkleRoot));
	    bH.setMerkleRootHash(merkleHash256);

	    offset += 32;

	    // Next is the unix timestamp indicating when the block was
	    // mined
	    // as a uint32_t
	    byte[] timestamp = new byte[4];
	    for (byte x = 0; x < 4; x++) {
		timestamp[x] = block[offset + x];
	    }
	    bH.setnTime(BigInteger.valueOf(Utils.readUint32(timestamp, 0)));

	    offset += 4;

	    // Next is the calculated difficulty.. aka bits..
	    // as a uint32_t
	    byte[] bits = new byte[4];
	    for (byte x = 0; x < 4; x++) {
		bits[x] = block[offset + x];
	    }
	    // bH.setnBits(Utils.decodeCompactBits(Utils.reverseBytes(bits)));
	    bH.setnBits(BigInteger.valueOf(Utils.readUint32(bits, 0)));
	    // System.out.println(Hex.encode(bH.getnBits().toByteArray())
	    // .toString());

	    offset += 4;

	    // Next is the nonce value used to generate this block..
	    // as a uint32_t
	    byte[] nonce = new byte[4];
	    for (byte x = 0; x < 4; x++) {
		nonce[x] = block[offset + x];
	    }
	    bH.setnNonce(BigInteger.valueOf(Utils.readUint32(nonce, 0)));

	    offset += 4;

	    // Next is the number of transaction entries
	    // as a varInt (loading this may be somewhat tricky)
	    // Max size for this is 9 bytes so thats how many we need to
	    // check (doubled for safety/avoidance of serialization issues)
	    // (note that this is not necessarily the size of the
	    // actual varInt!!)
	    byte[] txn_Count = new byte[18];
	    try {
		for (byte x = 0; x < 18; x++) {
		    txn_Count[x] = block[offset + x];
		}
	    } catch (Exception e) {// We read too far?

	    }

	    VarInt numEntries = new VarInt(txn_Count, 0);

	    b.setTransCounter(numEntries.getValue());

	    // Next we use the size of the varInt to determine our offset
	    // that we will use to load the actual transactions

	    offset += numEntries.getOriginalSizeInBytes();

	    byte[] transactions = new byte[(block.length) - (offset)];

	    // Copy the transactions alone to our new byte array
	    for (int x = 0; x < transactions.length; x++) {
		transactions[x] = block[(offset) + x];
	    }

	    // De-serialize and assign to an arraylist in new transaction
	    // format

	    try {
		numBlocksToChain += 1;
		b.setTransactions(Utils.readTransactions(transactions, 0,
			numEntries.getValue()));
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		numBlocksToChain -= 1;
		try {
		    System.in.read();
		} catch (IOException x) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }

	    b.setHeader(bH);

	    // Now we add the block to our coin's blockchain
	    this.getCoin().getBlockChain().add(b);

	}

	System.out.println(
		"Number of Blocks sent for parsing (Includes orphans): "
			+ numBlocksToChain);

    }

    /**
     * @param blockNum
     */
    @SuppressWarnings("nls")
    public void debugBlock(BigInteger blockNum) {
	System.out.println("Debug Block via Num");
	// Some sanity checking
	try {
	    System.out.println("Current Hash: " + this.getCoin().getBlockChain()
		    .get(blockNum.intValueExact()).getHash().toString());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	try {
	    System.out.println("Current Hash (SCRYPT): " + this.getCoin()
		    .getBlockChain().get(blockNum.intValueExact())
		    .getScryptHash().toString());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Prev Hash: " + this.getCoin().getBlockChain()
		    .get(blockNum.intValueExact()).getHeader()
		    .getPrevBlockHash());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Merkle Root: " + this.getCoin().getBlockChain()
		    .get(blockNum.intValueExact()).getHeader()
		    .getMerkleRootHash());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Nbits/Difficulty Bits: " + this.getCoin()
		    .getBlockChain().get(blockNum.intValueExact()).getHeader()
		    .getnBits().toString());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Nonce : " + this.getCoin().getBlockChain()
		    .get(blockNum.intValueExact()).getHeader().getnNonce());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Timestamp : " + this.getCoin().getBlockChain()
		    .get(blockNum.intValueExact()).getHeader().getnTime());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Version : " + this.getCoin().getBlockChain()
		    .get(blockNum.intValueExact()).getHeader().getnVersion());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Number of Transactions: " + this.getCoin()
		    .getBlockChain().get(blockNum.intValueExact())
		    .getTransCounter().intValueExact());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Number of Transactions in List: " + this
		    .getCoin().getBlockChain().get(blockNum.intValueExact())
		    .getTransactions().size());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    for (coin.Transaction trans : this.getCoin().getBlockChain()
		    .get(blockNum.intValueExact()).getTransactions()) {
		try {
		    System.out.println("Transaction Data Format Version: "
			    + trans.getTransactionDataFormatVersion());
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		try {
		    System.out.println("Number of Transaction Inputs: "
			    + trans.getTransactionInputs().toString());
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		try {
		    for (TransactionInput input : trans.getTransactionIn()) {
			try {
			    System.out.println("Previous Outpoint Index: "
				    + input.getPrevious_output().getIndex()
					    .toString());
			} catch (Exception e) {
			    // TODO Auto-generated catch block
			    // UINT32_MAX value if this is coinbase
			    // UINT32_MAX
			    e.printStackTrace();
			}
			try {
			    System.out.println("Previous Outpoint Hash: "
				    + input.getPrevious_output().getHash()
					    .toString());
			} catch (Exception e) {
			    // TODO Auto-generated catch block
			    // This is null for a coinbase transaction most of
			    // the time
			    e.printStackTrace();
			}
			try {
			    System.out.println("Script Length: "
				    + input.getScript_length().toString());
			} catch (Exception e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			try {
			    System.out
				    .println(
					    "Script From Address: " + Hex
						    .encode(input.getScript()
							    .getFromAddress()
							    .getHash160())
						    .toString());
			} catch (ScriptException e) {
			    // TODO Auto-generated catch block
			    // This is null for a coinbase transaction most of
			    // the time
			    e.printStackTrace();
			}
			try {
			    System.out
				    .println(
					    "Script To Address: " + Hex
						    .encode(input.getScript()
							    .getToAddress()
							    .getHash160())
						    .toString());
			} catch (ScriptException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			try {
			    System.out
				    .println("Script To Address(pubkey): " + Hex
					    .encode(input.getScript()
						    .getPubKeyHash())
					    .toString());
			} catch (ScriptException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
		    }
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		try {
		    System.out.println("Number of Transaction Outputs: "
			    + trans.getTransactionInputs().toString());
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		try {
		    for (TransactionOutput output : trans.getTransactionOut()) {
			try {
			    System.out.println("Transaction Value: "
				    + output.getTransactionValue());
			} catch (Exception e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}

			try {
			    System.out.println("PK Script Length: "
				    + output.getPkScriptLength());
			} catch (Exception e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			try {
			    System.out.println("PK Script From Address: " + Hex
				    .encode(output.getPkScript()
					    .getFromAddress().getHash160())
				    .toString());
			} catch (ScriptException e) {
			    // TODO Auto-generated catch block
			    // This is null for a coinbase transaction most of
			    // the time
			    e.printStackTrace();
			}
			try {
			    System.out
				    .println(
					    "PK Script To Address: " + Hex
						    .encode(output.getPkScript()
							    .getToAddress()
							    .getHash160())
						    .toString());
			} catch (ScriptException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			try {
			    System.out.println(
				    "PK Script To Address(pubkey): " + Hex
					    .encode(output.getPkScript()
						    .getPubKeyHash())
					    .toString());
			} catch (ScriptException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}

		    }
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	System.out.println("Debug Block Via Search Height");
	// Find the block we are looking for
	Block b = this.getPrimaryDatabase().searchBlock(blockNum);

	// Some sanity checking
	try {
	    System.out.println("Current Hash: " + b.getHash().toString());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	try {
	    System.out.println(
		    "Current Hash (SCRYPT): " + b.getScryptHash().toString());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out
		    .println("Prev Hash: " + b.getHeader().getPrevBlockHash());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println(
		    "Merkle Root: " + b.getHeader().getMerkleRootHash());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Nbits/Difficulty Bits: "
		    + b.getHeader().getnBits().toString());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Nonce : " + b.getHeader().getnNonce());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Timestamp : " + b.getHeader().getnTime());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Version : " + b.getHeader().getnVersion());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Number of Transactions: "
		    + b.getTransCounter().intValueExact());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println("Number of Transactions in List: "
		    + b.getTransactions().size());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    for (coin.Transaction trans : this.getCoin().getBlockChain()
		    .get(blockNum.intValueExact()).getTransactions()) {
		try {
		    System.out.println("Transaction Data Format Version: "
			    + trans.getTransactionDataFormatVersion());
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		try {
		    System.out.println("Number of Transaction Inputs: "
			    + trans.getTransactionInputs().toString());
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		try {
		    for (TransactionInput input : trans.getTransactionIn()) {
			try {
			    System.out.println("Previous Outpoint Index: "
				    + input.getPrevious_output().getIndex()
					    .toString());
			} catch (Exception e) {
			    // TODO Auto-generated catch block
			    // UINT32_MAX value if this is coinbase
			    // UINT32_MAX
			    e.printStackTrace();
			}
			try {
			    System.out.println("Previous Outpoint Hash: "
				    + input.getPrevious_output().getHash()
					    .toString());
			} catch (Exception e) {
			    // TODO Auto-generated catch block
			    // This is null for a coinbase transaction most of
			    // the time
			    e.printStackTrace();
			}
			try {
			    System.out.println("Script Length: "
				    + input.getScript_length().toString());
			} catch (Exception e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			try {
			    System.out
				    .println(
					    "Script From Address: " + Hex
						    .encode(input.getScript()
							    .getFromAddress()
							    .getHash160())
						    .toString());
			} catch (ScriptException e) {
			    // TODO Auto-generated catch block
			    // This is null for a coinbase transaction most of
			    // the time
			    e.printStackTrace();
			}
			try {
			    System.out
				    .println(
					    "Script To Address: " + Hex
						    .encode(input.getScript()
							    .getToAddress()
							    .getHash160())
						    .toString());
			} catch (ScriptException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			try {
			    System.out
				    .println("Script To Address(pubkey): " + Hex
					    .encode(input.getScript()
						    .getPubKeyHash())
					    .toString());
			} catch (ScriptException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
		    }
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		try {
		    System.out.println("Number of Transaction Outputs: "
			    + trans.getTransactionInputs().toString());
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		try {
		    for (TransactionOutput output : trans.getTransactionOut()) {
			try {
			    System.out.println("Transaction Value: "
				    + output.getTransactionValue());
			} catch (Exception e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}

			try {
			    System.out.println("PK Script Length: "
				    + output.getPkScriptLength());
			} catch (Exception e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			try {
			    System.out.println("PK Script From Address: " + Hex
				    .encode(output.getPkScript()
					    .getFromAddress().getHash160())
				    .toString());
			} catch (ScriptException e) {
			    // TODO Auto-generated catch block
			    // This is null for a coinbase transaction most of
			    // the time
			    e.printStackTrace();
			}
			try {
			    System.out
				    .println(
					    "PK Script To Address: " + Hex
						    .encode(output.getPkScript()
							    .getToAddress()
							    .getHash160())
						    .toString());
			} catch (ScriptException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			try {
			    System.out.println(
				    "PK Script To Address(pubkey): " + Hex
					    .encode(output.getPkScript()
						    .getPubKeyHash())
					    .toString());
			} catch (ScriptException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}

		    }
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    /**
     * 
     */
    public void parseBlocks() {
	// Now we parse the blocklist into a blockchain
	BigInteger numParsed = BigInteger.ONE;
	for (Block b : this.getCoin().getBlockChain()) {
	    this.primaryDatabase.addBlock(b);
	    numParsed = numParsed.add(BigInteger.ONE);

	    System.out.println("Parsed : " + numParsed.toString());

	    if (numParsed.compareTo(BigInteger.valueOf(500)) == 0) {
		break;
	    }
	}

	this.getPrimaryDatabase().getBlockChain()
		.restructureChain(BigInteger.valueOf(0));
    }

    /*
     * private BigFastList<Integer> getOffsets(byte[] buf) {
     * BigFastList<Integer> offsets = new BigFastList<Integer>();
     * 
     * // Search the buffer for our magic number byte[] networkBytes =
     * Main.daemon.getNetworkBytes();
     * 
     * for (int k = 0; k < buf.length; k++) { if (k + 3 < buf.length) { if
     * (buf[k] == networkBytes[0] && buf[k + 1] == networkBytes[1] && buf[k + 2]
     * == networkBytes[2] && buf[k + 3] == networkBytes[3]) {
     * offsets.add(Integer.valueOf(k)); } } } return null; }
     */

    /**
     * @return
     */
    public StringBuffer getPath() {
	StringBuffer OS = new StringBuffer(
		System.getProperty("os.name").toUpperCase()); //$NON-NLS-1$

	if (OS.toString().contains("WIN")) { //$NON-NLS-1$
	    return new StringBuffer(System.getenv("APPDATA") + File.separator //$NON-NLS-1$
		    + "GoldCoin (GLD)"); //$NON-NLS-1$
	} else if (OS.toString().contains("MAC")) { //$NON-NLS-1$
	    return new StringBuffer(
		    System.getProperty("user.home") + File.separator + "Library" //$NON-NLS-1$ //$NON-NLS-2$
			    + File.separator + "Application Support" //$NON-NLS-1$
			    + File.separator + "GoldCoin (GLD)"); //$NON-NLS-1$
	} else if (OS.toString().contains("NUX") //$NON-NLS-1$
		|| OS.toString().contains("NIX")) { //$NON-NLS-1$
	    return new StringBuffer(System.getProperty("user.home") //$NON-NLS-1$
		    + File.separator + ".goldcoin"); //$NON-NLS-1$
	}

	return new StringBuffer(System.getProperty("user.dir") + File.separator //$NON-NLS-1$
		+ "GoldCoin (GLD)"); //$NON-NLS-1$
    }

    /**
     * @param str
     * @return
     */
    public static byte[] stringToBytesASCII(String str) {
	byte[] b = new byte[str.length()];
	for (int i = 0; i < b.length; i++) {
	    b[i] = (byte) str.charAt(i);
	}
	return b;
    }

    /**
     * @return the coin
     */
    public Coin getCoin() {
	return coin;
    }

    /**
     * @param coin
     *            the coin to set
     */
    public void setCoin(Coin coin) {
	this.coin = coin;
    }
}