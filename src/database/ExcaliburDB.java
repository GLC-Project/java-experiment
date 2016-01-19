package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;

import util.BigFastList;
import coin.Block;
import coin.Transaction;
import coin.Transaction.TransactionInput;
import core.Main;

/**
 * ExcaliburDB is an extremely high performance dynamic NTree(BTree variant)
 * based disk-backed blocking NIO key-value store database.
 * 
 * It is developed alongside with GoldCoin (GLD) as this is it's primary use.
 * However, it can be used stand alone as the classes and functions are defined
 * in terms of generic objects. To do this, simply remove all local imports and
 * casts to (Block) and customize the comparison statements/ integrity checks to
 * suit your purpose.
 * 
 * @author Amir Eslampanah
 * 
 */
public class ExcaliburDB {
    /**
     * Path to the directory where the database file will be located.
     */
    StringBuffer path;

    /**
     * Our database file.
     */
    RandomAccessFile data;

    /**
     * Our database index file.
     */
    RandomAccessFile dataIndex;

    /**
     * Nio channel to our database files.
     */
    private FileChannel indexChannel, dataChannel;

    /**
     * Holds our block indices in the following format:
     * 
     * (ASCII Encoded) BlockHash, offset into file (in bits), offset into LZMA
     * Group Block (how many blocks down is it, 0 == first block)
     */
    BigFastList<StringBuffer> blockIndexArray = new BigFastList<StringBuffer>();

    /**
     * Our BlockChain..
     */
    BTree blockChain = new BTree();

    /**
     * ExcaliburDB by default uses a path and filename specific to GoldCoin
     * (GLD) If you wish to change this path and filename, use the overloaded
     * constructor instead of this one.
     */
    public ExcaliburDB() {
    }

    /**
     * ExcaliburDB can be initialized to default values using this command
     */
    public void initializeDB() {
	this.path = Main.getDaemon().getCoinDetails().getDatabaseHandler()
		.getPath();
	loadDB();
    }

    /**
     * Overloaded constructor for specifying a custom path to database file.
     * 
     * @param path1
     */
    public ExcaliburDB(String path1) {
	this.path = new StringBuffer(path1);
	loadDB();
    }

    /**
     * Method for queuing up blocks to add to the current chain
     */

    public void addBlock(Block b) {
	this.getBlockChain().addNode(new Node(b, false));

    }

    /**
     * Find a block with a given transaction
     * 
     * @param txIDHash
     *            The transaction TXID hash
     * @param mainChain
     *            Whether or not to omit orphan transactions
     * @return The block, otherwise null
     */

    public Block searchTransaction(StringBuffer txIDHash, boolean mainChain) {
	return (Block) this.getBlockChain().searchTransact(txIDHash, mainChain)
		.getBlock();
    }

    /**
     * Find a transaction given TX ID.
     * 
     * @param txIDHash
     *            The transaction TXID hash
     * @param mainChain
     *            Whether or not to omit orphan transactions
     * @return The transaction, otherwise null
     */
    public Transaction searchForTransaction(StringBuffer txIDHash,
	    boolean mainChain) {
	return this.getBlockChain().searchForTransact(
		txIDHash,
		(Block) this.getBlockChain()
			.searchTransact(txIDHash, mainChain).getBlock());
    }

    /**
     * Find a block given its hash
     * 
     * @param hash
     * @return
     */

    public Block searchBlock(StringBuffer hash) {
	return (Block) this.getBlockChain().searchNode(hash).getBlock();
    }

    /**
     * Find a block given its height in chain
     * 
     * @param height
     * @return
     */

    public Block searchBlock(BigInteger height) {
	return (Block) this.getBlockChain().searchNode(height).getBlock();
    }

    /**
     * Returns a list of all blocks after this hash that are part of the main
     * chain
     * 
     * @param hash
     * @return
     */
    public BigFastList<Block> getMainSubBlocks(StringBuffer hash) {
	return this.getBlockChain().getMainSubBlocks(hash);
    }

    /**
     * Returns a list of all blocks after this block height that are part of the
     * main chain
     * 
     * @param height
     * @return
     */
    public BigFastList<Block> getMainSubBlocks(BigInteger height) {
	return this.getBlockChain().getMainSubBlocks(height);
    }

    /**
     * Returns a list of all blocks after this block hash
     * 
     * @param hash
     * @return
     */
    public BigFastList<Block> getAllSubBlocks(StringBuffer hash) {
	return this.getBlockChain().getAllSubBlocks(hash);
    }

    /**
     * Returns a list of all blocks after this block height
     * 
     * @param height
     * @return
     */
    public BigFastList<Block> getAllSubBlocks(BigInteger height) {
	return this.getBlockChain().getAllSubBlocks(height);
    }

    /**
     * Restructure the chain starting from this block number
     * 
     * Another way to think of this is we are marking all blocks that are part
     * of the longest chain and de-marking all blocks that are no longer part of
     * the longest chain post this block number
     * 
     * @param fromBlockNum
     * @return
     */
    public Block restructureChain(BigInteger fromBlockNum) {
	return (Block) this.getBlockChain().restructureChain(fromBlockNum)
		.getBlock();
    }

    /**
     * 
     * Restructure the chain starting from this block hash
     * 
     * Another way to think of this is we are marking all blocks that are part
     * of the longest chain and de-marking all blocks that are no longer part of
     * the longest chain post this block hash
     * 
     * @param fromBlockNum
     * @return
     */
    public Block restructureChain(StringBuffer hash) {
	return (Block) this.getBlockChain()
		.restructureChain(this.getBlockChain().searchNode(hash))
		.getBlock();
    }

    private void loadDB() {
	try {
	    this.data = new RandomAccessFile(this.path.toString()
		    + File.separator + "Data.dat", "rwd"); //$NON-NLS-1$ //$NON-NLS-2$
	    this.dataIndex = new RandomAccessFile(this.path.toString()
		    + File.separator + "Index.dat", "rwd"); //$NON-NLS-1$ //$NON-NLS-2$

	    this.blockIndexArray = this.read();

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

    private void scheduleNewWrite() {

    }

    private void scheduleReWrite() {

    }

    private boolean checkIntegrity(BigFastList<StringBuffer> indices) {

	return false;
    }

    private BigFastList<StringBuffer> read() {
	// Whether or not we need to do an integrity check
	boolean integrityCheck = true;

	// Open our database index file
	this.indexChannel = ExcaliburDB.this.dataIndex.getChannel();

	// Open our database file
	this.dataChannel = ExcaliburDB.this.dataIndex.getChannel();

	ByteBuffer buf = ByteBuffer.allocate(262144);

	// holds our blockIndices
	BigFastList<StringBuffer> blockIndices = new BigFastList<StringBuffer>();

	try {
	    // First we read the index to see if we've already loaded part
	    // of
	    // the block chain
	    // If the index is not empty then we read the block locations
	    // into
	    // memory.
	    // Special care is taken to read an entire block location so as
	    // to
	    // avoid a partial read.

	    boolean firstCharRead = false;
	    StringBuffer temp2 = new StringBuffer();// Buffers entire block
						    // indices (in case they
						    // happen to fall
						    // between byte buffers)

	    while (this.indexChannel.read(buf) != -1) {
		// Here we do the actual reading

		buf.flip(); // Flip the buffer as it is backwards.
		CharBuffer temp = buf.asCharBuffer();

		if (!firstCharRead) {
		    // First we read whether or not the database has been
		    // integrity
		    // checked
		    // If not, then we know we have to schedule an integrity
		    // check,
		    // Otherwise we can skip it.
		    if (temp.get(0) == 't') {
			integrityCheck = false;
		    }
		    firstCharRead = true;
		}
		// Next we read

		// Now we'll want to assign the contents of the buffer to a
		// stringBuffer.
		temp2.append(temp.toString());

		// Look for the first block index
		int start = temp2.indexOf("==START=="); //$NON-NLS-1$
		int end = temp2.indexOf("==END=="); //$NON-NLS-1$

		if (end != -1) {
		    blockIndices.add(new StringBuffer(temp2.subSequence(
			    start + 8, end)));
		    temp2 = new StringBuffer(temp2.subSequence(end + 6,
			    temp2.length()));
		}

		// Lastly we clear the buffer to make room for more data to
		// be read from file.
		buf.clear();

	    }

	    // Now temp2 should be empty if it is not.. then we have an
	    // integrity problem
	    if (temp2.length() != 0 || !integrityCheck) {
		if (this.checkIntegrity(blockIndices)) {
		    this.scheduleNewWrite();
		    return blockIndices;
		}
		// We delete the blockchain files on disk and skip to
		// network phase
		this.scheduleReWrite();

	    }

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return null;
    }

    class BTree {
	private Node root = null; // root node of the NST

	public BTree() {
	}

	public BTree(Node root1) {
	    this.root = root1;
	}

	/**
	 * Tries to add a new node
	 * 
	 * @param newNode
	 * @return -1 if the block does not verify correctly, 0 if the block
	 *         does not have a parent, or 1 if the block was successfully
	 *         verified and added, or 2 if the block hash already exists.
	 */
	public int addNode(Node newNode) {

	    // If the root node is null then the first block that is added
	    // becomes root node or in the case of cryptocurrency, the genesis
	    // block
	    if (this.root == null) {
		// root block is always assumed to be part of the main chain
		// If for some reason you need multiple root blocks then you
		// will have to change the code
		newNode.setIsMainChain(true);
		this.root = newNode;
		return 0;
	    }

	    // If this new node's previous block is our current block
	    // Then we add it to the list of sub blocks
	    if (((Block) newNode.getBlock())
		    .compareWithPrevOfCur((Block) this.root.getBlock()) == 0) {
		this.root.addSubBlock(newNode);
		return 1;
	    }
	    // Otherwise, search the subnodes for this node's previous
	    // block
	    BigFastList<BigFastList<Node>> parents = new BigFastList<BigFastList<Node>>();

	    BigInteger numericIterator = BigInteger.ZERO;

	    parents.add(this.root.getSubBlocks());

	    while (!parents.isEmpty()) {
		BigFastList<Node> temp = parents.get(BigInteger.ZERO
			.intValueExact());

		for (BigInteger numericIterator2 = BigInteger.ZERO; numericIterator2
			.compareTo(BigInteger.valueOf(temp.size())) < 0; numericIterator2 = numericIterator2
			.add(BigInteger.ONE)) {

		    Node n = temp.get(numericIterator2.intValueExact());
		    parents.add(n.getSubBlocks());
		    if (((Block) newNode.getBlock())
			    .compareWithPrevOfCur((Block) n.getBlock()) == 0) {
			// Break out of the loop since we have found our
			// sub block.
			parents.clear();
			// Check if a valid subnode of identical hash exists

			for (Node n2 : n.getSubBlocks()) {
			    if (((Block) n2.getBlock())
				    .getHash()
				    .toString()
				    .compareTo(
					    ((Block) newNode.getBlock())
						    .getHash().toString()) == 0) {
				return 2;

			    }
			}

			if (checkBlock((Block) n.getBlock(),
				(Block) newNode.getBlock())) {
			    n.addSubBlock(newNode);
			    return 1;
			}
			return -1; // Block does not verify properly.

		    }
		}
		parents.remove(numericIterator.intValueExact());
	    }
	    return 0;// Block does not have a parent!
	}

	/**
	 * Tries to find a node given block hash as a hex string
	 * 
	 * @param hash
	 * @return
	 * 
	 */
	public Node searchNode(StringBuffer hash) {

	    // First find out if the

	    // If this new node's previous block is our current block
	    // Then we add it to the list of sub blocks
	    if (hash.toString().compareTo(
		    ((Block) this.root.getBlock()).getHash().toString()) == 0) {
		return this.root;
	    }
	    // Otherwise, search for the node
	    BigFastList<BigFastList<Node>> parents = new BigFastList<BigFastList<Node>>();

	    BigInteger numericIterator = BigInteger.ZERO;

	    parents.add(this.root.getSubBlocks());

	    while (!parents.isEmpty()) {
		BigFastList<Node> temp = parents.get(BigInteger.ZERO
			.intValueExact());

		for (BigInteger numericIterator2 = BigInteger.ZERO; numericIterator2
			.compareTo(BigInteger.valueOf(temp.size())) < 0; numericIterator2 = numericIterator2
			.add(BigInteger.ONE)) {

		    Node n = temp.get(numericIterator2.intValueExact());
		    parents.add(n.getSubBlocks());
		    if (hash.toString().compareTo(
			    ((Block) n.getBlock()).getHash().toString()) == 0) {
			// Break out of the loop since we have found our
			// sub block.
			parents.clear();
			return n;

		    }
		}
		parents.remove(numericIterator.intValueExact());
	    }
	    return null;// Block not found!
	}

	/**
	 * Tries to find a transaction given transaction hash as a hex string
	 * 
	 * Will return the entire Node that owns the transaction
	 * 
	 * Not just the transaction
	 * 
	 * @param hash
	 *            The txid hash for the transaction we are looking for
	 * @param mainChain
	 *            Whether to only look for nodes that are part of the main
	 *            chain (exclude orphaned transactions?)
	 * @return
	 * 
	 */
	public Node searchTransact(StringBuffer hash, boolean mainChain) {

	    // Does our genesis transaction(s?) match this hash?
	    Transaction t = this.searchForTransact(hash,
		    (Block) this.root.getBlock());
	    if (t != null) {
		return this.root;
	    }

	    // Otherwise, search for the node
	    BigFastList<BigFastList<Node>> parents = new BigFastList<BigFastList<Node>>();

	    BigInteger numericIterator = BigInteger.ZERO;

	    parents.add(this.root.getSubBlocks());

	    while (!parents.isEmpty()) {
		BigFastList<Node> temp = parents.get(BigInteger.ZERO
			.intValueExact());

		for (BigInteger numericIterator2 = BigInteger.ZERO; numericIterator2
			.compareTo(BigInteger.valueOf(temp.size())) < 0; numericIterator2 = numericIterator2
			.add(BigInteger.ONE)) {

		    Node n = temp.get(numericIterator2.intValueExact());
		    parents.add(n.getSubBlocks());
		    t = this.searchForTransact(hash, (Block) n.getBlock());

		    if (t != null) {
			if (mainChain) {
			    if (n.isMainChain) {
				// Break out of the loop since we have found our
				// transaction parent
				parents.clear();
				parents = null;
				return n;
			    }
			} else {
			    // Break out of the loop since we have found our
			    // transaction parent
			    parents.clear();
			    parents = null;
			    return n;
			}

		    }
		}
		parents.remove(numericIterator.intValueExact());
	    }
	    return null;// Block not found!
	}

	/**
	 * Search for a transaction within a given block
	 * 
	 * @param txIDHash
	 * @param b
	 * @return The transaction or null if the transaction is not found
	 */
	public Transaction searchForTransact(StringBuffer txIDHash, Block b) {
	    for (Transaction t : b.getTransactions()) {
		for (TransactionInput in : t.getTransactionIn()) {
		    if (in.getPrevious_output().getHash().toString()
			    .compareTo(txIDHash.toString()) == 0) {
			return t;
		    }
		}
	    }
	    return null;
	}

	/**
	 * Tries to find a node given a block height
	 * 
	 * This only works if there is a mainchain in place!
	 * 
	 * @param hash
	 * @return
	 * 
	 */
	public Node searchNode(BigInteger height) {

	    // First find out if the

	    // If height is zero return genesis node
	    if (height.compareTo(BigInteger.ZERO) == 0) {
		return this.root;
	    }
	    // Otherwise, search the subnode's for this block height
	    BigFastList<BigFastList<Node>> parents = new BigFastList<BigFastList<Node>>();

	    BigInteger numericIterator = BigInteger.ZERO;

	    BigInteger blockNum = BigInteger.ZERO;

	    parents.add(this.root.getSubBlocks());

	    while (!parents.isEmpty()) {
		BigFastList<Node> temp = parents.get(BigInteger.ZERO
			.intValueExact());

		for (BigInteger numericIterator2 = BigInteger.ZERO; numericIterator2
			.compareTo(BigInteger.valueOf(temp.size())) < 0; numericIterator2 = numericIterator2
			.add(BigInteger.ONE)) {

		    Node n = temp.get(numericIterator2.intValueExact());
		    if (n.isMainChain()) {

			// Increase the block count by one
			blockNum = blockNum.add(BigInteger.ONE);

			if (blockNum.compareTo(height) == 0) {
			    return n;
			}

			// If a main chain's block's sub blocks don't contain
			// blocks that are part of the main chain
			// Then that block is the end of the chain

			// Lets check all the sub blocks for this node

			boolean foundValidSubBlock = false;
			for (BigInteger numericIterator3 = BigInteger.ZERO; numericIterator3
				.compareTo(BigInteger.valueOf(n.getSubBlocks()
					.size())) < 0; numericIterator3 = numericIterator3
				.add(BigInteger.ONE)) {
			    if (n.getSubBlocks()
				    .get(numericIterator3.intValueExact())
				    .isMainChain()) {
				foundValidSubBlock = true;
				parents.add(n.getSubBlocks());
			    }

			}

			if (!foundValidSubBlock) {
			    // Break out of the loop since we didn't find that
			    // block
			    parents.clear();
			    return null;
			}
		    }
		}
		parents.remove(numericIterator.intValueExact());
	    }
	    return null;// Block not found!
	}

	/**
	 * Returns all sub blocks from a given height
	 * 
	 * That is every block after this given height regardless of whether
	 * stale or not
	 * 
	 * @param height
	 *            The height after which to return all sub blocks
	 * @return All the blocks as a BigFastList (no longer in tree form) or
	 *         null if invalid
	 */
	BigFastList<Block> getAllSubBlocks(BigInteger height) {

	    return this.getAllSubBlocks(new StringBuffer(((Block) this
		    .searchNode(height).getBlock()).getHash().toString()));
	}

	BigFastList<Block> getAllSubBlocks(StringBuffer hash) {

	    BigFastList<Block> allSubBlocks = new BigFastList<Block>();

	    Node r = this.searchNode(hash);

	    // We use the same process as searching for a node
	    // Except we instead add all the candidates to our list
	    // Then we return them
	    BigFastList<BigFastList<Node>> parents = new BigFastList<BigFastList<Node>>();

	    BigInteger numericIterator = BigInteger.ZERO;

	    parents.add(r.getSubBlocks());

	    while (!parents.isEmpty()) {
		BigFastList<Node> temp = parents.get(BigInteger.ZERO
			.intValueExact());

		for (BigInteger numericIterator2 = BigInteger.ZERO; numericIterator2
			.compareTo(BigInteger.valueOf(temp.size())) < 0; numericIterator2 = numericIterator2
			.add(BigInteger.ONE)) {

		    Node n = temp.get(numericIterator2.intValueExact());
		    parents.add(n.getSubBlocks());
		    allSubBlocks.add(n);
		}
		parents.remove(numericIterator.intValueExact());
	    }

	    return allSubBlocks;
	}

	BigFastList<Block> getMainSubBlocks(BigInteger height) {
	    return this.getMainSubBlocks(new StringBuffer(((Block) this
		    .searchNode(height).getBlock()).getHash().toString()));
	}

	BigFastList<Block> getMainSubBlocks(StringBuffer hash) {
	    BigFastList<Block> allSubBlocks = new BigFastList<Block>();

	    Node r = this.searchNode(hash);

	    // We use the same process as searching for a node
	    // Except we instead add all the candidates to our list
	    // Then we return them
	    BigFastList<BigFastList<Node>> parents = new BigFastList<BigFastList<Node>>();

	    BigInteger numericIterator = BigInteger.ZERO;

	    parents.add(r.getSubBlocks());

	    while (!parents.isEmpty()) {
		BigFastList<Node> temp = parents.get(BigInteger.ZERO
			.intValueExact());

		for (BigInteger numericIterator2 = BigInteger.ZERO; numericIterator2
			.compareTo(BigInteger.valueOf(temp.size())) < 0; numericIterator2 = numericIterator2
			.add(BigInteger.ONE)) {

		    Node n = temp.get(numericIterator2.intValueExact());
		    parents.add(n.getSubBlocks());

		    if (n.isMainChain()) {
			allSubBlocks.add(n);
		    }
		}
		parents.remove(numericIterator.intValueExact());
	    }

	    return allSubBlocks;
	}

	/*
	 * Goes through the block chain starting at the specified block number
	 * remarks all blocks after that block in height
	 * 
	 * If a longer chain is found after that block height than the existing
	 * chain then the longer chain will be marked and the old chain will be
	 * unmarked (for blocks greater than the specified height)
	 * 
	 * Note also that if there is no existing main chain at the given block
	 * height then the function will return without doing anything.
	 * 
	 * @param fromBlockNum What block to re-organize the chain from. Note
	 * this block will remain marked!
	 */
	public Node restructureChain(BigInteger fromBlockNum) {
	    // Search for this block in the main chain
	    Node n = this.searchNode(fromBlockNum);
	    return this.restructureChain(n);

	}

	/*
	 * Goes through the block chain starting at the specified block /
	 * remarks all blocks after that block in height
	 * 
	 * If a longer chain is found after that block height than the existing
	 * chain then the longer chain will be marked and the old chain will be
	 * unmarked (for blocks greater than the specified height)
	 * 
	 * Note also that if there is no existing main chain at the given block
	 * height then the function will return without doing anything.
	 * 
	 * @param fromBlockNum What block to re-organize the chain from. Note
	 * this block will remain marked!
	 * 
	 * @return n3 What is the last block in longest chain. Returns the last
	 * block in chain of one of the chains(with no preference) in the case
	 * of a tie chain-length situation
	 */
	public Node restructureChain(Node n) {
	    if (n == null) {// If null return null
		return null;
	    }

	    BigFastList<BigFastList<Node>> validSubBlocks = new BigFastList<BigFastList<Node>>();

	    BigFastList<Node> validSubBlockRoots = new BigFastList<Node>();

	    Node n2 = null;
	    Node n3 = null;
	    Node n4 = n;

	    validSubBlocks.add(n.getSubBlocks());
	    validSubBlockRoots.add(n);
	    BigInteger it = BigInteger.ZERO;

	    boolean lastBlockFound = false;

	    // Loop until we only have no candidates left
	    // The last block left will be the longest chain
	    while (!lastBlockFound) {
		// Look through each valid sub block for a hash who's previous
		// hash is the hash of the current node
		n = validSubBlockRoots.get(it.intValueExact());

		for (BigInteger numericIterator2 = BigInteger.ZERO; numericIterator2
			.compareTo(BigInteger.valueOf(validSubBlocks.get(
				it.intValueExact()).size())) < 0; numericIterator2 = numericIterator2
			.add(BigInteger.ONE)) {

		    // If the prevHash value of the subblock is equal to the
		    // current block
		    // Then we add that block's subblocks to our arraylist

		    n2 = validSubBlocks.get(it.intValueExact()).get(
			    numericIterator2.intValueExact());
		    if (getPrevHash(n2).toString().compareTo(
			    ((Block) n.getBlock()).getHash().toString()) == 0) {
			validSubBlocks.add(n2.getSubBlocks());
			validSubBlockRoots.add(n2);

			n3 = n2;
		    }

		}
		if (it.compareTo(BigInteger.valueOf(validSubBlockRoots.size() - 1)) < 0) {
		    it = it.add(BigInteger.ONE);
		} else {
		    lastBlockFound = true;

		}

	    }

	    // Now we set everything in our sub block and subroot block lists
	    // We set all their mainChain values to false

	    for (Node g : validSubBlockRoots) {
		g.setIsMainChain(false);
	    }

	    for (BigFastList<Node> f : validSubBlocks) {
		for (Node e : f) {
		    e.setIsMainChain(false);
		}
	    }

	    // n3 is our last block (or one of our last blocks - it doesn't
	    // really matter - whatever chain gets longer first will win)

	    // Now we work backwards setting everything that is in n3's
	    // hashchain
	    // We set n3's hashchain to mainChain is true
	    Node n5 = n3;
	    n3.setIsMainChain(true);

	    // Loop until we reach the inputed node
	    while (((Block) n5.getBlock()).getHash().toString()
		    .compareTo(((Block) n4.getBlock()).getHash().toString()) != 0) {
		n5 = searchNode(getPrevHash(n5));
		n5.setIsMainChain(true);
	    }

	    return n3;

	}

	public BigInteger countChainLength(Node n) {
	    return null;

	}

	public StringBuffer getPrevHash(Node n) {

	    return new StringBuffer(((Block) n.getBlock()).getHeader()
		    .getPrevBlockHash().toString());

	}

	/**
	 * @return the root
	 */
	public Node getRoot() {
	    return this.root;
	}

	/**
	 * Check the inputed block against a valid block
	 * 
	 * @param valid
	 *            The valid parent block
	 * @param toCheck
	 *            The child block to be checked
	 * @return
	 */
	public boolean checkBlock(Block valid, Block toCheck) {
	    // TODO: Do some checks to determine if the block is valid.
	    return true;
	}

    }

    class Node {
	private final Object block;
	private BigFastList<Node> subBlocks = new BigFastList<Node>();
	// Whether or not this is part of the main chain
	private boolean isMainChain;

	public Node(Object block1, boolean isMainChain1) {
	    this.block = block1;
	    this.isMainChain = isMainChain1;
	}

	/**
	 * @return the subBlocks
	 */
	public BigFastList<Node> getSubBlocks() {
	    return this.subBlocks;
	}

	/**
	 * @param subBlocks1
	 *            the subBlocks to set
	 */
	public void setSubBlocks(BigFastList<Node> subBlocks1) {
	    this.subBlocks = subBlocks1;
	}

	/**
	 * Add a sub block.
	 * 
	 * @param sub
	 */
	public void addSubBlock(Node sub) {
	    this.subBlocks.add(sub);
	}

	/**
	 * Remove a sub block.
	 * 
	 * Note: If there are multiple identical blocks for whatever reason,
	 * this will only remove the first!
	 * 
	 * @param sub
	 * 
	 */
	public void removeSubBlock(Node sub) {
	    this.subBlocks.remove(sub);
	}

	/**
	 * Remove a sub block at a particular index.
	 * 
	 * (Use getSubBlocks to find the index you are looking for in a search)
	 * 
	 * @param sub
	 * 
	 */
	public void removeSubBlock(int sub) {
	    this.subBlocks.remove(sub);
	}

	/**
	 * @param isMainChain1
	 *            Sets whether this is part of the main chain
	 */
	public void setIsMainChain(boolean isMainChain1) {
	    this.isMainChain = isMainChain1;
	}

	/**
	 * @return the block
	 */
	public Object getBlock() {
	    return this.block;
	}

	/**
	 * @return the isMainChain
	 */
	public boolean isMainChain() {
	    return this.isMainChain;
	}
    }

    /**
     * @return the blockChain
     */
    public BTree getBlockChain() {
	return blockChain;
    }

    /**
     * @param blockChain
     *            the blockChain to set
     */
    public void setBlockChain(BTree blockChain) {
	this.blockChain = blockChain;
    }

}
