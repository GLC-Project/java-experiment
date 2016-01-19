package net;

import java.math.BigInteger;
import java.nio.CharBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import coin.Block;
import coin.BlockHeader;
import coin.Coin;
import core.Main;
import util.BigFastList;
import util.Constants;
import util.Utils;
import util.crypto.Sha256Hash;
import util.crypto.VarInt;

/**
 * @author Amir Eslampanah
 * 
 */
public class Message {

    /**
     * This class contains all functions for Bitcoin-Satoshi style messaging as
     * well as GoldCoin-Amir style messaging.
     * 
     * A message contains a header along with either a data message or control
     * message
     * 
     */
    MessageHeader messageHeader;
    byte[] messageBytes, headerBytes;

    /**
     * Current coin that we are dealing with
     * 
     * This reference is what keeps everything from going hay wire when the
     * coins are switched. Each instance of this class is tied to a particular
     * coin.
     */
    private Coin coin = null;

    /**
     * By default the client is in Legacy communications(satoshi) mode.
     * 
     * This needs to be set to false in order to use V2 protocol(amir) mode.
     */
    boolean legacy = true;

    /**
     * @param coin1
     * 
     */
    public Message(Coin coin1) {
	this.coin = coin1;

	this.messageHeader = new MessageHeader();

    }

    /**
     * @param coin1
     * @param entireBytes
     */
    public Message(Coin coin1, byte[] entireBytes) {

	this.coin = coin1;

	this.messageHeader = new MessageHeader();

	for (int x = 0; x < this.headerBytes.length; x++) {
	    this.headerBytes[x] = entireBytes[x];
	}

	this.messageHeader.processHeader(this.getHeaderBytes());

	this.messageBytes = new byte[entireBytes.length - 24];

	for (int x = 24; x < entireBytes.length; x++) {
	    this.messageBytes[x - 24] = entireBytes[x - 24];
	}

    }

    /**
     * When processing is necessary before the response is sent this method is
     * called by the recvMessage method.
     * 
     * If null is returned the sending method is not called (applies to receive
     * only messages)
     * 
     * @param commandName
     * @param args
     */
    @SuppressWarnings("nls")
    public Object[] processMessage(StringBuffer commandName, Object[] args) {

	Object[] args2 = null;

	if (commandName.toString().compareTo("block") == 0) {
	    /*
	     * The block message transmits a single serialized block.
	     * 
	     * It is sent in reply to a getdata message which had an inventory
	     * type of MSG_BLOCK and the header hash of the particular block
	     * being requested.
	     */

	    // Restructure the chain
	    // This will link any orphans as well

	    // Because of how this works, it will be less CPU intensive the
	    // closer to the end of the chain this block is.

	    Block b = (Block) args[0];

	    // Add the block to the database
	    this.getCoin().getDatabaseHandler().getPrimaryDatabase()
		    .addBlock(b);

	    // Restructure chain
	    Block newMain = this.getCoin().getDatabaseHandler()
		    .getPrimaryDatabase().restructureChain(new StringBuffer(
			    b.getHeader().getPrevBlockHash().toString()));

	    if (newMain != null) {
		this.getCoin().setLastBlock(newMain);
	    } else {
		// Only happens if the block is an orphan

	    }

	    // No expected reply so we don't bother with the sendMessage method
	    // here
	} else if (commandName.toString().compareTo("getblocks") == 0) {
	    /*
	     * The getblocks message requests an inv message that provides block
	     * header hashes starting from a particular point in the block
	     * chain. It allows a peer which has been disconnected or started
	     * for the first time to get the data it needs to request the blocks
	     * it hasn’t seen.
	     * 
	     * Peers which have been disconnected may have stale blocks in their
	     * locally-stored block chain, so the getblocks message allows the
	     * requesting peer to provide the receiving peer with multiple
	     * header hashes at various heights on their local chain. This
	     * allows the receiving peer to find, within that list, the last
	     * header hash they had in common and reply with all subsequent
	     * header hashes.
	     * 
	     * Note: the receiving peer itself may respond with an inv message
	     * containing header hashes of stale blocks. It is up to the
	     * requesting peer to poll all of its peers to find the best block
	     * chain.
	     * 
	     * If the receiving peer does not find a common header hash within
	     * the list, it will assume the last common block was the genesis
	     * block (block zero), so it will reply with an inv message
	     * containing header hashes starting with block one (the first block
	     * after the genesis block).
	     */

	    BigFastList<char[]> locatorHashes = (BigFastList<char[]>) args[0];

	    char[] stopHash = (char[]) args[1];

	    // Here we try to find a locator hash.

	    // Once a locator hash has been found we don't need to check the
	    // others because they go deeper in the chain(which we don't need to
	    // go once we have found one)

	    // They will all be part of the same chain anyways
	    // and a client that is missing a block in the chain will assuredly
	    // use the getData function with the prevHash of the block after the
	    // block they are missing
	    // or just use an initial locator hash that is right before the
	    // block they are missing

	    // Essentially we are just checking to see if we have any of these
	    // header hashes
	    // If we do, we send all the header hashes after them.

	    for (char[] hash : locatorHashes) {
		Block b = this.getCoin().getDatabaseHandler()
			.getPrimaryDatabase()
			.searchBlock(new StringBuffer(String.valueOf(hash)));

		if (b != null) {
		    BigFastList<Block> bList = this.getCoin()
			    .getDatabaseHandler().getPrimaryDatabase()
			    .getAllSubBlocks(
				    new StringBuffer(String.valueOf(hash)));

		    // Now we need to prune this list and remove everything
		    // after our stop hash
		    // If our stop hash is zero, we send 500 maximum

		    BigFastList<Block> bList2 = bList
			    .prune(BigInteger.valueOf(500));

		    // Now we make sure we don't add any cache/memory leaks
		    bList.clear();
		    bList = null;

		    // Now we send this list to sendMessage for sending

		    BigFastList<InventoryVector> invs = new BigFastList<InventoryVector>();

		    for (Block b2 : bList2) {
			InventoryVector inv = new InventoryVector(
				new StringBuffer(b2.getHash().toString()),
				BigInteger.valueOf(2), true);// 2 corresponds to
							     // MSG_BLOCK which
							     // means this
							     // inventory
							     // relates to a
							     // block hash

		    }

		    args2 = new Object[1];

		    args2[0] = invs;
		}
	    }
	} else if (commandName.toString().compareTo("getdata") == 0) {
	    /*
	     * The getdata message requests one or more data objects from
	     * another node. The objects are requested by an inventory, which
	     * the requesting node typically previously received by way of an
	     * inv message.
	     * 
	     * The response to a getdata message can be a tx message, block
	     * message, merkleblock message, or notfound message.
	     * 
	     * This message cannot be used to request arbitrary data, such as
	     * historic transactions no longer in the memory pool or relay set.
	     * Full nodes may not even be able to provide older blocks if
	     * they’ve pruned old transactions from their block database. For
	     * this reason, the getdata message should usually only be used to
	     * request data from a node which previously advertised it had that
	     * data by sending an inv message. * The format and maximum size
	     * limitations of the getdata message are identical to the inv
	     * message; only the message header differs.
	     */

	    BigFastList<InventoryVector> invVectors = (BigFastList<InventoryVector>) args[0];

	    // Since the responses can be of different types, we follow the
	    // following format
	    // ID -> Object -> ID -> Object
	    // So in our list we have trailing identifiers that help our
	    // MessageHandler determine the command that will be used for
	    // sending
	    // Types are defined in constants class

	    BigFastList<Object> list = new BigFastList<Object>();

	    // process the requests individually
	    for (InventoryVector v : invVectors) {
		// First we check the request type
		if (v.getType().compareTo(Constants.MSG_TX) == 0) {
		    // We're looking for a transaction.. and we don't care if
		    // its in the main chain or not
		    list.add(Constants.MSG_TX);
		    list.add(this.getCoin().getDatabaseHandler()
			    .getPrimaryDatabase()
			    .searchForTransaction(v.getHash(), false));

		} else if (v.getType().compareTo(Constants.MSG_BLOCK) == 0) {
		    // We're looking for a block
		    list.add(Constants.MSG_BLOCK);
		    list.add(this.getCoin().getDatabaseHandler()
			    .getPrimaryDatabase().searchBlock(v.getHash()));

		} else if (v.getType()
			.compareTo(Constants.MSG_FILTERED_BLOCK) == 0) {
		    // We're looking for a block with the specified filter

		    list.add(Constants.MSG_FILTERED_BLOCK);

		    list.add(this.getCoin().getDatabaseHandler()
			    .getPrimaryDatabase().searchBlock(v.getHash()));

		}
	    }

	    if (list.size() == 0) {
		list.add(Constants.NOT_FOUND);
	    }

	    args2 = new Object[list.size()];

	    for (BigInteger x = BigInteger.ZERO; x
		    .compareTo(BigInteger.valueOf(list.size())) != 0; x = x
			    .add(BigInteger.ONE)) {
		args[x.intValue()] = list.get(x.intValue());

	    }
	} else if (commandName.toString().compareTo("getheaders") == 0) {
	    /*
	     * The getheaders message requests a headers message that provides
	     * block headers starting from a particular point in the block
	     * chain. It allows a peer which has been disconnected or started
	     * for the first time to get the headers it hasn’t seen yet.
	     * 
	     * The getheaders message is nearly identical to the getblocks
	     * message, with one minor difference: the inv reply to the
	     * getblocks message will include no more than 500 block header
	     * hashes; the headers reply to the getheaders message will include
	     * as many as 2,000 block headers
	     */

	    BigFastList<char[]> locatorHashes = (BigFastList<char[]>) args[0];

	    for (char[] hashC : locatorHashes) {
		StringBuffer hash = new StringBuffer(String.valueOf(hashC));

		BigFastList<Block> result = this.getCoin().getDatabaseHandler()
			.getPrimaryDatabase().getAllSubBlocks(hash);

		if (result == null) {
		    continue;
		}
		args2 = new Object[2];
		args2[0] = result;
		args2[1] = args[1]; // stophash
	    }
	} else if (commandName.toString().compareTo("headers") == 0) {
	    BigFastList<BlockHeader> headers = (BigFastList<BlockHeader>) args[0];

	    for (BlockHeader h : headers) {
		this.getCoin().getMessagingHandler().getReceivingQueue()
			.add(Constants.MSG_BLOCK_HEADER);
		this.getCoin().getMessagingHandler().getReceivingQueue().add(h);
	    }
	} else if (commandName.toString().compareTo("inv") == 0) {

	    /*
	     * The inv message (inventory message) transmits one or more
	     * inventories of objects known to the transmitting peer. It can be
	     * sent unsolicited to announce new transactions or blocks, or it
	     * can be sent in reply to a getblocks message or mempool message.
	     * 
	     * The receiving peer can compare the inventories from an inv
	     * message against the inventories it has already seen, and then use
	     * a follow-up message to request unseen objects.
	     */

	    BigFastList<InventoryVector> invVectors = (BigFastList<InventoryVector>) args[0];

	    // process the inventories individually
	    for (InventoryVector v : invVectors) {
		for (InventoryVector i : this.getCoin().getMessagingHandler()
			.getInventoryList()) {
		    if (i.getHash().toString()
			    .compareTo(v.getHash().toString()) == 0) {
			// We already have it
		    } else {
			// Check to see if its hash is a TX or BlockHeader hash
			// that we already have in our database
			if (this.getCoin().getDatabaseHandler()
				.getPrimaryDatabase()
				.searchBlock(v.getHash()) == null
				&& this.getCoin().getDatabaseHandler()
					.getPrimaryDatabase()
					.searchForTransaction(v.getHash(),
						false) == null) {
			    // If it already exists then we add it to the list
			    // of seen inventories

			    this.getCoin().getMessagingHandler()
				    .getInventoryList().add(v);
			} else {
			    // Otherwise we queue up a request for this
			    // inventory (be it a TX or block)

			    this.getCoin().getMessagingHandler()
				    .getRequestQueue().add(v.getType());

			    this.getCoin().getMessagingHandler()
				    .getRequestQueue().add(v.getHash());

			    // Add it to the list so that we don't end up adding
			    // it to the request queue multiple times
			    this.getCoin().getMessagingHandler()
				    .getInventoryList().add(v);
			}
		    }
		}
	    }

	    // No direct response
	} else if (commandName.toString().compareTo("mempool") == 0) {
	    /*
	     * Added in protocol version 60002.
	     * 
	     * The mempool message requests the TXIDs of transactions that the
	     * receiving node has verified as valid but which have not yet
	     * appeared in a block. That is, transactions which are in the
	     * receiving node’s memory pool. The response to the mempool message
	     * is one or more inv messages containing the TXIDs in the usual
	     * inventory format.
	     * 
	     * Sending the mempool message is mostly useful when a program first
	     * connects to the network. Full nodes can use it to quickly gather
	     * most or all of the unconfirmed transactions available on the
	     * network; this is especially useful for miners trying to gather
	     * transactions for their transaction fees. SPV clients can set a
	     * filter before sending a mempool to only receive transactions that
	     * match that filter; this allows a recently-started client to get
	     * most or all unconfirmed transactions related to its wallet.
	     * 
	     * The inv response to the mempool message is, at best, one node’s
	     * view of the network—not a complete list of unconfirmed
	     * transactions on the network. Here are some additional reasons the
	     * list might not be complete:
	     * 
	     * Before Bitcoin Core 0.9.0, the response to the mempool message
	     * was only one inv message. An inv message is limited to 50,000
	     * inventories, so a node with a memory pool larger than 50,000
	     * entries would not send everything. Later versions of Bitcoin Core
	     * send as many inv messages as needed to reference its complete
	     * memory pool.
	     * 
	     * The mempool message is not currently fully compatible with the
	     * filterload message’s BLOOM_UPDATE_ALL and
	     * BLOOM_UPDATE_P2PUBKEY_ONLY flags. Mempool transactions are not
	     * sorted like in-block transactions, so a transaction (tx2)
	     * spending an output can appear before the transaction (tx1)
	     * containing that output, which means the automatic filter update
	     * mechanism won’t operate until the second-appearing transaction
	     * (tx1) is seen—missing the first-appearing transaction (tx2). It
	     * has been proposed in Bitcoin Core issue #2381 that the
	     * transactions should be sorted before being processed by the
	     * filter.
	     * 
	     * There is no payload in a mempool message. See the message header
	     * section for an example of a message without a payload.
	     */

	    // Queue inventory response
	    // Response is
	    // The TXIDs of transactions that the
	    // receiving node has verified as valid but which have not yet
	    // appeared in a block. That is, transactions which are in the
	    // receiving node’s memory pool.

	    args2 = new Object[1];
	    args2[0] = this.getCoin().getMessagingHandler()
		    .getOrphanTransactions();

	} else if (commandName.toString().compareTo("merkleblock") == 0) {
	    /*
	     * Added in protocol version 70001 as described by BIP37.
	     * 
	     * The merkleblock message is a reply to a getdata message which
	     * requested a block using the inventory type MSG_MERKLEBLOCK. It is
	     * only part of the reply: if any matching transactions are found,
	     * they will be sent separately as tx messages.
	     * 
	     * If a filter has been previously set with the filterload message,
	     * the merkleblock message will contain the TXIDs of any
	     * transactions in the requested block that matched the filter, as
	     * well as any parts of the block’s merkle tree necessary to connect
	     * those transactions to the block header’s merkle root. The message
	     * also contains a complete copy of the block header to allow the
	     * client to hash it and confirm its proof of work.
	     */

	    BlockHeader bH = (BlockHeader) args[0];
	    BigInteger numTrans = (BigInteger) args[1];// Number of transactions
						       // even those not matched
						       // by filter
	    BigFastList<Sha256Hash> hashes = (BigFastList<Sha256Hash>) args[2];// Hashes
									       // of
									       // transactions/merkle
									       // nodes
									       // that
									       // matched
									       // the
									       // filter
	    BigFastList<Byte> flags = (BigFastList<Byte>) args[3];// Flags used
								  // to
								  // determine
								  // the
								  // position of
								  // hashes in
								  // the merkle
								  // tree

	}

	return args2;

    }

    /**
     * De-serializes messages
     * 
     * Calls the processMessage method for data interaction procedures.
     * 
     * There is no functional reason for the separate methods other than to make
     * the code easier to read/modify
     * 
     * @throws Exception
     * 
     */
    @SuppressWarnings("nls")
    public Object[] recvMessage() throws Exception {

	String commandNameString = this.messageHeader.getCommandName()
		.toString().replace("\0", "");

	StringBuffer commandName = new StringBuffer(commandNameString);

	Object[] argsToProcess = null;

	// Here begin the data commands
	if (commandName.toString().compareTo("block") == 0) {
	    /*
	     * The block message transmits a single serialized block.
	     * 
	     * It is sent in reply to a getdata message which had an inventory
	     * type of MSG_BLOCK and the header hash of the particular block
	     * being requested.
	     */

	    // Deserialize
	    Block b = Main.getDaemon().getCoinDetails().getDatabaseHandler()
		    .loadBlock(this.messageBytes);

	    // Set for processing
	    argsToProcess = new Object[1];
	    argsToProcess[0] = b;

	} else if (commandName.toString().compareTo("getblocks") == 0) {
	    /*
	     * The getblocks message requests an inv message that provides block
	     * header hashes starting from a particular point in the block
	     * chain. It allows a peer which has been disconnected or started
	     * for the first time to get the data it needs to request the blocks
	     * it hasn’t seen.
	     * 
	     * Peers which have been disconnected may have stale blocks in their
	     * locally-stored block chain, so the getblocks message allows the
	     * requesting peer to provide the receiving peer with multiple
	     * header hashes at various heights on their local chain. This
	     * allows the receiving peer to find, within that list, the last
	     * header hash they had in common and reply with all subsequent
	     * header hashes.
	     * 
	     * Note: the receiving peer itself may respond with an inv message
	     * containing header hashes of stale blocks. It is up to the
	     * requesting peer to poll all of its peers to find the best block
	     * chain.
	     * 
	     * If the receiving peer does not find a common header hash within
	     * the list, it will assume the last common block was the genesis
	     * block (block zero), so it will reply with an inv message
	     * containing header hashes starting with block one (the first block
	     * after the genesis block).
	     */

	    int offset = 0;

	    // First 4 bytes are the protocol version as uint32_t

	    offset += 4;

	    // Next is a varInt representing the number of locator block hash
	    // entries

	    byte[] numHashes = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numHashes[y] = this.messageBytes[offset + y];
	    }

	    VarInt numLocator = new VarInt(numHashes, 0);

	    offset += numLocator.getOriginalSizeInBytes();

	    // Next are the block locator hashes (starting blocks to search for)
	    // Each one is 32 bytes long as a char[32]
	    // They number as many as the value above

	    BigFastList<char[]> locatorHashes = new BigFastList<char[]>();

	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numLocator.getValue()) != 0; x = x.add(BigInteger.ONE)) {

		char[] locatorHash = new char[32];

		System.arraycopy(this.messageBytes, offset, locatorHash, 0, 32);

		locatorHashes.add(locatorHash);
		offset += 32;
	    }

	    // Next is the stop location hash (up to what block to send)
	    // If this hash is set to all zeros then the client will send 500
	    // blocks maximum
	    // Otherwise it will send only the selected range
	    // 32 bytes long as a char[32]

	    char[] stopHash = new char[32];

	    System.arraycopy(this.messageBytes, offset, stopHash, 0, 32);

	    // Set for processing
	    argsToProcess = new Object[2];
	    argsToProcess[0] = locatorHashes;
	    argsToProcess[1] = stopHash;

	} else if (commandName.toString().compareTo("getdata") == 0) {
	    /*
	     * The getdata message requests one or more data objects from
	     * another node. The objects are requested by an inventory, which
	     * the requesting node typically previously received by way of an
	     * inv message.
	     * 
	     * The response to a getdata message can be a tx message, block
	     * message, merkleblock message, or notfound message.
	     * 
	     * This message cannot be used to request arbitrary data, such as
	     * historic transactions no longer in the memory pool or relay set.
	     * Full nodes may not even be able to provide older blocks if
	     * they’ve pruned old transactions from their block database. For
	     * this reason, the getdata message should usually only be used to
	     * request data from a node which previously advertised it had that
	     * data by sending an inv message. * The format and maximum size
	     * limitations of the getdata message are identical to the inv
	     * message; only the message header differs.
	     */

	    int offset = 0;

	    // First is a varInt representing the numbers of inventory entries

	    byte[] numInv = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numInv[y] = this.messageBytes[y];
	    }

	    VarInt numInvs = new VarInt(numInv, 0);

	    offset += numInvs.getOriginalSizeInBytes();

	    BigFastList<InventoryVector> invVectors = new BigFastList<InventoryVector>();

	    // Followed by an array of inventory vectors of type inv_vect[]
	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numInvs.getValue()) != 0; x = x.add(BigInteger.ONE)) {

		InventoryVector invVector = new InventoryVector(true);
		invVector.readLegacy(this.messageBytes, offset);

		invVectors.add(invVector);

		offset += 36;
	    }

	    // Set for processing
	    argsToProcess = new Object[1];
	    argsToProcess[0] = invVectors;

	} else if (commandName.toString().compareTo("getheaders") == 0) {
	    /*
	     * The getheaders message requests a headers message that provides
	     * block headers starting from a particular point in the block
	     * chain. It allows a peer which has been disconnected or started
	     * for the first time to get the headers it hasn’t seen yet.
	     * 
	     * The getheaders message is nearly identical to the getblocks
	     * message, with one minor difference: the inv reply to the
	     * getblocks message will include no more than 500 block header
	     * hashes; the headers reply to the getheaders message will include
	     * as many as 2,000 block headers
	     */

	    int offset = 0;

	    // First 4 bytes are the protocol version as uint32_t

	    offset += 4;

	    // Next is a varInt representing the number of locator block hash
	    // entries

	    byte[] numHashes = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numHashes[y] = this.messageBytes[offset + y];
	    }

	    VarInt numLocator = new VarInt(numHashes, 0);

	    offset += numLocator.getOriginalSizeInBytes();

	    // Next are the block locator hashes (starting blocks to search for)
	    // Each one is 32 bytes long as a char[32]
	    // They number as many as the value above

	    BigFastList<char[]> locatorHashes = new BigFastList<char[]>();

	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numLocator.getValue()) != 0; x = x.add(BigInteger.ONE)) {

		char[] locatorHash = new char[32];

		System.arraycopy(this.messageBytes, offset, locatorHash, 0, 32);

		locatorHashes.add(locatorHash);
		offset += 32;
	    }

	    // Next is the stop location hash (up to what block to send)
	    // If this hash is set to all zeros then the client will send 2000
	    // blocks maximum
	    // Otherwise it will send only the selected range

	    char[] stopHash = new char[32];

	    System.arraycopy(this.messageBytes, offset, stopHash, 0, 32);

	    // Set for processing
	    argsToProcess = new Object[2];
	    argsToProcess[0] = locatorHashes;
	    argsToProcess[1] = stopHash;

	} else if (commandName.toString().compareTo("headers") == 0) {
	    /*
	     * The headers message sends one or more block headers to a node
	     * which previously requested certain headers with a getheaders
	     * message.
	     */

	    int offset = 0;

	    // Number of block headers as a varInt

	    byte[] numHeader = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numHeader[y] = this.messageBytes[offset + y];
	    }

	    VarInt numHeaders = new VarInt(numHeader, 0);

	    offset += numHeaders.getOriginalSizeInBytes();

	    // Block headers

	    BigFastList<BlockHeader> headers = new BigFastList<BlockHeader>();

	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numHeaders.getValue()) != 0; x = x.add(BigInteger.ONE)) {
		headers.add(new BlockHeader(this.messageBytes, offset));
		offset += 80;// Headers are 80 bytes in legacy
	    }

	    // Set for processing
	    argsToProcess = new Object[1];
	    argsToProcess[0] = headers;

	} else if (commandName.toString().compareTo("inv") == 0) {
	    /*
	     * The inv message (inventory message) transmits one or more
	     * inventories of objects known to the transmitting peer. It can be
	     * sent unsolicited to announce new transactions or blocks, or it
	     * can be sent in reply to a getblocks message or mempool message.
	     * 
	     * The receiving peer can compare the inventories from an inv
	     * message against the inventories it has already seen, and then use
	     * a follow-up message to request unseen objects.
	     */

	    int offset = 0;

	    // Number of inventory entries as a varInt - Maximum 50,000 entries

	    byte[] numInv = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numInv[y] = this.messageBytes[offset + y];
	    }

	    VarInt numInvs = new VarInt(numInv, 0);

	    offset += numInvs.getOriginalSizeInBytes();

	    BigFastList<InventoryVector> invVectors = new BigFastList<InventoryVector>();

	    // Followed by an array of inventory vectors of type inv_vect[]
	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numInvs.getValue()) != 0; x = x.add(BigInteger.ONE)) {

		InventoryVector invVector = new InventoryVector(true);
		invVector.readLegacy(this.messageBytes, offset);

		invVectors.add(invVector);

		offset += 36;
	    }

	    // Set for processing
	    argsToProcess = new Object[1];
	    argsToProcess[0] = invVectors;

	} else if (commandName.toString().compareTo("mempool") == 0) {
	    /*
	     * Added in protocol version 60002.
	     * 
	     * The mempool message requests the TXIDs of transactions that the
	     * receiving node has verified as valid but which have not yet
	     * appeared in a block. That is, transactions which are in the
	     * receiving node’s memory pool. The response to the mempool message
	     * is one or more inv messages containing the TXIDs in the usual
	     * inventory format.
	     * 
	     * Sending the mempool message is mostly useful when a program first
	     * connects to the network. Full nodes can use it to quickly gather
	     * most or all of the unconfirmed transactions available on the
	     * network; this is especially useful for miners trying to gather
	     * transactions for their transaction fees. SPV clients can set a
	     * filter before sending a mempool to only receive transactions that
	     * match that filter; this allows a recently-started client to get
	     * most or all unconfirmed transactions related to its wallet.
	     * 
	     * The inv response to the mempool message is, at best, one node’s
	     * view of the network—not a complete list of unconfirmed
	     * transactions on the network. Here are some additional reasons the
	     * list might not be complete:
	     * 
	     * Before Bitcoin Core 0.9.0, the response to the mempool message
	     * was only one inv message. An inv message is limited to 50,000
	     * inventories, so a node with a memory pool larger than 50,000
	     * entries would not send everything. Later versions of Bitcoin Core
	     * send as many inv messages as needed to reference its complete
	     * memory pool.
	     * 
	     * The mempool message is not currently fully compatible with the
	     * filterload message’s BLOOM_UPDATE_ALL and
	     * BLOOM_UPDATE_P2PUBKEY_ONLY flags. Mempool transactions are not
	     * sorted like in-block transactions, so a transaction (tx2)
	     * spending an output can appear before the transaction (tx1)
	     * containing that output, which means the automatic filter update
	     * mechanism won’t operate until the second-appearing transaction
	     * (tx1) is seen—missing the first-appearing transaction (tx2). It
	     * has been proposed in Bitcoin Core issue #2381 that the
	     * transactions should be sorted before being processed by the
	     * filter.
	     * 
	     * There is no payload in a mempool message. See the message header
	     * section for an example of a message without a payload.
	     */

	    // No payload

	    // Queue inventory response
	    // Response is
	    // The TXIDs of transactions that the
	    // receiving node has verified as valid but which have not yet
	    // appeared in a block. That is, transactions which are in the
	    // receiving node’s memory pool.
	} else if (commandName.toString().compareTo("merkleblock") == 0) {
	    /*
	     * Added in protocol version 70001 as described by BIP37.
	     * 
	     * The merkleblock message is a reply to a getdata message which
	     * requested a block using the inventory type MSG_MERKLEBLOCK. It is
	     * only part of the reply: if any matching transactions are found,
	     * they will be sent separately as tx messages.
	     * 
	     * If a filter has been previously set with the filterload message,
	     * the merkleblock message will contain the TXIDs of any
	     * transactions in the requested block that matched the filter, as
	     * well as any parts of the block’s merkle tree necessary to connect
	     * those transactions to the block header’s merkle root. The message
	     * also contains a complete copy of the block header to allow the
	     * client to hash it and confirm its proof of work.
	     */

	    int offset = 0;

	    // First is the 80 byte block header
	    BlockHeader bH = this.getCoin().getDatabaseHandler()
		    .deserializeBlockHeader(this.messageBytes);

	    offset += 80;

	    // Next is the transaction count - a 4 byte value as a uint32_t
	    BigInteger transCount = BigInteger
		    .valueOf(Utils.readUint32(this.messageBytes, offset));

	    // Next is the number of hashes in the following field as a varInt

	    // Number of hashes as a varInt

	    byte[] numHash = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numHash[y] = this.messageBytes[offset + y];
	    }

	    VarInt numHashes = new VarInt(numHash, 0);

	    offset += numHashes.getOriginalSizeInBytes();

	    BigInteger hashCount = numHashes.getValue();

	    BigFastList<Sha256Hash> hashes = new BigFastList<Sha256Hash>();

	    // Next are one or more hashes of both transactions and merkle nodes
	    // in internal byte order. Each hash is 32 bits.
	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numHashes.getValue()) != 0; x = x.add(BigInteger.ONE)) {

		/*
		 * TODO: Customize this depending on the coin protocol used.
		 */

		Sha256Hash hashToAdd = Sha256Hash
			.createDouble(Arrays.copyOf(this.messageBytes, 4));

		hashes.add(hashToAdd);

		offset += 32;
	    }

	    // Next is the number of flag bytes in the following field as a
	    // varInt
	    byte[] numFlag = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numFlag[y] = this.messageBytes[offset + y];
	    }

	    VarInt numFlags = new VarInt(numFlag, 0);

	    offset += numFlags.getOriginalSizeInBytes();

	    // Next are the flags as bytes. A sequence of bits packed eight in a
	    // byte with the least significant bit first. May be padded to the
	    // nearest byte boundary but must not contain any more bits than
	    // that.
	    // as described in
	    // https://bitcoin.org/en/developer-reference#merkleblock

	    BigFastList<Byte> flags = new BigFastList<Byte>();

	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numFlags.getValue()) != 0; x = x.add(BigInteger.ONE)) {

		flags.add(Byte.valueOf(this.messageBytes[offset]));
		offset += 1;
	    }

	    argsToProcess = new Object[4];
	    argsToProcess[0] = bH;
	    argsToProcess[1] = transCount;
	    argsToProcess[2] = hashes;
	    argsToProcess[3] = flags;

	} else if (commandName.toString().compareTo("notfound") == 0) {
	    /*
	     * Added in protocol version 70001.
	     * 
	     * The notfound message is a reply to a getdata message which
	     * requested an object the receiving node does not have available
	     * for relay. (Nodes are not expected to relay historic transactions
	     * which are no longer in the memory pool or relay set. Nodes may
	     * also have pruned spent transactions from older blocks, making
	     * them unable to send those blocks.)
	     * 
	     * The format and maximum size limitations of the notfound message
	     * are identical to the inv message; only the message header
	     * differs.
	     */
	    int offset = 0;

	    // Number of inventory entries as a varInt - Maximum 50,000 entries
	    // Next is the number of flag bytes in the following field as a
	    // varInt
	    byte[] numNotFound = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numNotFound[y] = this.messageBytes[offset + y];
	    }

	    VarInt numInvs = new VarInt(numNotFound, 0);

	    offset += numInvs.getOriginalSizeInBytes();

	    // Inventory vectors as a inv_vect[]

	    BigFastList<InventoryVector> invVectors = new BigFastList<InventoryVector>();

	    // Followed by an array of inventory vectors of type inv_vect[]
	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numInvs.getValue()) != 0; x = x.add(BigInteger.ONE)) {

		InventoryVector invVector = new InventoryVector(true);
		invVector.readLegacy(this.messageBytes, offset);

		invVectors.add(invVector);

		offset += 36;
	    }

	    argsToProcess = new Object[1];
	    argsToProcess[0] = invVectors;

	} else if (commandName.toString().compareTo("tx") == 0) {
	    /*
	     * The tx message transmits a single transaction in the raw
	     * transaction format. It can be sent in a variety of situations;
	     * 
	     * Transaction Response: Bitcoin Core and BitcoinJ will send it in
	     * response to a getdata message that requests the transaction with
	     * an inventory type of MSG_TX.
	     * 
	     * MerkleBlock Response: Bitcoin Core will send it in response to a
	     * getdata message that requests a merkle block with an inventory
	     * type of MSG_MERKLEBLOCK. (This is in addition to sending a
	     * merkleblock message.) Each tx message in this case provides a
	     * matched transaction from that block.
	     * 
	     * Unsolicited: BitcoinJ will send a tx message unsolicited for
	     * transactions it originates.
	     * 
	     * For an example hexdump of the raw transaction format, see the raw
	     * transaction section.
	     */

	    // No payload other than the raw transaction
	    int offset = 0;

	    // Set for processing
	    argsToProcess = new Object[1];
	    argsToProcess[0] = Utils
		    .readTransactions(this.messageBytes, offset, BigInteger.ONE)
		    .get(0);

	}

	// Here begin the network commands
	else if (commandName.toString().compareTo("addr") == 0) {
	    /*
	     * The addr (IP address) message relays connection information for
	     * peers on the network. Each peer which wants to accept incoming
	     * connections creates an addr message providing its connection
	     * information and then sends that message to its peers unsolicited.
	     * Some of its peers send that information to their peers (also
	     * unsolicited), some of which further distribute it, allowing
	     * decentralized peer discovery for any program already on the
	     * network.
	     * 
	     * An addr message may also be sent in response to a getaddr
	     * message.
	     */
	    int offset = 0;

	    // First is the number of IP addresses - up to a maximum of 1000
	    // As a varInt

	    byte[] numIP = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numIP[y] = this.messageBytes[offset + y];
	    }

	    VarInt numIPs = new VarInt(numIP, 0);

	    offset += numIPs.getOriginalSizeInBytes();

	    // Next are the IP addresses themselves each with the following
	    // format

	    BigFastList<Connection> addressesToAdd = new BigFastList<Connection>();

	    for (BigInteger y = BigInteger.ZERO; y.compareTo(
		    numIPs.getValue()) != 0; y = y.add(BigInteger.ONE)) {

		Connection addressToAdd = new Connection();

		// First is the unix time the node was last connected to (by the
		// node that sent the inital addr message)
		// as a uint_32

		addressToAdd.setLastConnectTime(
			Utils.readUint32(this.messageBytes, offset));

		offset += 4;

		// Next is the services the node advertised in its version
		// message
		// as a uint64_t

		addressToAdd.setServices(
			Utils.readUint64(this.messageBytes, offset));

		offset += 8;

		// IPv6 address in big endian byte order. IPv4 addresses can be
		// provided as IPv4-mapped IPv6 addresses
		// as a 16 byte char array
		char[] data = new char[16];

		for (int x = 0; x < offset + 16; x++) {
		    data[x] = (char) this.messageBytes[offset + x];
		}

		addressToAdd.setStringIpAddress(
			new StringBuffer(String.valueOf(data)));

		offset += 16;

		// Port number in big endian byte order. Note that Bitcoin Core
		// will
		// only connect to nodes with non-standard port numbers as a
		// last
		// resort for finding peers. This is to prevent anyone from
		// trying
		// to use the network to disrupt non-Bitcoin services that run
		// on
		// other ports.
		// As a uint16_t
		addressToAdd.setPort(BigInteger.valueOf(
			Utils.readUint16BE(this.messageBytes, offset)));

		offset += 2;

		addressesToAdd.add(addressToAdd);

	    }

	    // Set for processing
	    argsToProcess = new Object[1];
	    argsToProcess[0] = addressesToAdd;

	} else if (commandName.toString().compareTo("alert") == 0) {
	    /*
	     * Added in protocol version 311.
	     * 
	     * The alert message warns nodes of problems that may affect them or
	     * the rest of the network. Each alert message is signed using a key
	     * controlled by respected community members, mostly Bitcoin Core
	     * developers.
	     * 
	     * To ensure all nodes can validate and forward alert messages,
	     * encapsulation is used. Developers create an alert using the data
	     * structure appropriate for the versions of the software they want
	     * to notify; then they serialize that data and sign it. The
	     * serialized data and its signature make up the outer alert
	     * message—allowing nodes which don’t understand the data structure
	     * to validate the signature and relay the alert to nodes which do
	     * understand it. The nodes which actually need the message can
	     * decode the serialized data to access the inner alert message.
	     */

	    int offset = 0;

	    Alert alert = new Alert();

	    // The number of bytes in following alert field. As a varInt

	    byte[] numByte = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numByte[y] = this.messageBytes[offset + y];
	    }

	    VarInt numBytes = new VarInt(numByte, 0);

	    offset += numBytes.getOriginalSizeInBytes();

	    // The serialized alert. As an unsigned char array.
	    // Here is the format for this alert

	    // Alert format version. As a uInt32_t.
	    alert.setVersion(BigInteger
		    .valueOf(Utils.readUint32(this.messageBytes, offset)));

	    offset += 4;

	    // The time beyond which nodes should stop relaying this alert. Unix
	    // epoch time format. As a uInt64_t.
	    alert.setRelayUntil(Utils.readUint64(this.messageBytes, offset));

	    offset += 8;

	    // The time beyond which this alert is no longer in effect and
	    // should be ignored. Unix epoch time format. As a uInt64_t.

	    alert.setExpiration(Utils.readUint64(this.messageBytes, offset));

	    offset += 8;

	    // A unique ID number for this alert. As a uInt32_t.

	    alert.setID(BigInteger
		    .valueOf(Utils.readUint32(this.messageBytes, offset)));

	    offset += 4;

	    // All alerts with an ID number less than or equal to this number
	    // should be canceled: deleted and not accepted in the future.
	    // As a uInt32_t

	    alert.setCancel(BigInteger
		    .valueOf(Utils.readUint32(this.messageBytes, offset)));

	    offset += 4;

	    // The number of IDs in the following setCancel field. May be zero.
	    // As a varInt.

	    byte[] numCancel = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numCancel[y] = this.messageBytes[offset + y];
	    }

	    VarInt numCancels = new VarInt(numCancel, 0);

	    offset += numCancels.getOriginalSizeInBytes();

	    // Alert IDs which should be canceled. Each alert ID is a separate
	    // uInt32_t number.

	    BigFastList<BigInteger> alertIDs = new BigFastList<BigInteger>();

	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numCancels.getValue()) != 0; x = x.add(BigInteger.ONE)) {
		alertIDs.add(BigInteger
			.valueOf(Utils.readUint32(this.messageBytes, offset)));

		offset += 4;
	    }

	    alert.setCancelAlertIDs(alertIDs);

	    // This alert only applies to protocol versions greater than or
	    // equal to this version. Nodes running other protocol versions
	    // should still relay it. As a uInt32_t.

	    alert.setMinVer(BigInteger
		    .valueOf(Utils.readUint32(this.messageBytes, offset)));

	    offset += 4;

	    // This alert only applies to protocol versions less than or equal
	    // to this version. Nodes running other protocol versions should
	    // still relay it. As a uInt32_t.

	    alert.setMaxVer(BigInteger
		    .valueOf(Utils.readUint32(this.messageBytes, offset)));

	    offset += 4;

	    // The number of user agent strings in the following setUser_agent
	    // field. May be zero. As a varInt.

	    byte[] numAgent = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numAgent[y] = this.messageBytes[offset + y];
	    }

	    VarInt numAgents = new VarInt(numAgent, 0);

	    offset += numAgents.getOriginalSizeInBytes();

	    // If this field is empty, it has no effect on the alert. If there
	    // is at least one entry is this field, this alert only applies to
	    // programs with a user agent that exactly matches one of the
	    // strings in this field. Each entry in this field is a compactSize
	    // uint followed by a string—the uint indicates how many bytes are
	    // in the following string. This field was originally called
	    // setSubVer; since BIP14, it applies to user agent strings as
	    // defined in the version message.

	    BigFastList<StringBuffer> usrAgents = new BigFastList<StringBuffer>();

	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numAgents.getValue()) != 0; x = x.add(BigInteger.ONE)) {

		byte[] agentLen = new byte[9];

		for (byte y = 0; y < 9; y++) {
		    agentLen[y] = this.messageBytes[offset + y];
		}

		VarInt agentLength = new VarInt(agentLen, 0);

		offset += agentLength.getOriginalSizeInBytes();

		StringBuffer agent = new StringBuffer();

		for (BigInteger z = BigInteger.ZERO; z.compareTo(
			numAgents.getValue()) != 0; z = z.add(BigInteger.ONE)) {
		    agent.append((char) (this.messageBytes[offset] & 0xFF));
		    offset += 1;
		}

		usrAgents.add(agent);
	    }

	    alert.setApplicableUserAgents(usrAgents);

	    // The number of bytes in the following comment field. May be zero.
	    // As a varInt.

	    byte[] numBytes2 = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numBytes2[y] = this.messageBytes[offset + y];
	    }

	    VarInt numByte2 = new VarInt(numBytes2, 0);

	    offset += numByte2.getOriginalSizeInBytes();

	    // A comment on the alert that is not displayed. As a string.

	    StringBuffer comment = new StringBuffer();

	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numByte2.getValue()) != 0; x = x.add(BigInteger.ONE)) {
		comment.append((char) (this.messageBytes[offset] & 0xFF));
		offset += 1;
	    }

	    alert.setComment(comment);

	    // The number of bytes in the following statusBar field. May be
	    // zero. As a varInt

	    byte[] numBytes3 = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numBytes3[y] = this.messageBytes[offset + y];
	    }

	    VarInt numByte3 = new VarInt(numBytes3, 0);

	    offset += numByte3.getOriginalSizeInBytes();

	    // The alert message that is displayed to the user.

	    StringBuffer alertMsg = new StringBuffer();

	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numByte3.getValue()) != 0; x = x.add(BigInteger.ONE)) {
		alertMsg.append((char) (this.messageBytes[offset] & 0xFF));
		offset += 1;
	    }

	    alert.setAlert(alertMsg);

	    // Relative priority compared to other alerts
	    // as a uInt32_t

	    alert.setPriority(BigInteger
		    .valueOf(Utils.readUint32(this.messageBytes, offset)));

	    // The number of bytes in the following reserved field. May be zero.
	    byte[] numBytes4 = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numBytes4[y] = this.messageBytes[offset + y];
	    }

	    VarInt numByte4 = new VarInt(numBytes4, 0);

	    offset += numByte4.getOriginalSizeInBytes();

	    // Reserved for future use. Originally called RPC Error.
	    StringBuffer reserved = new StringBuffer();

	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numByte4.getValue()) != 0; x = x.add(BigInteger.ONE)) {
		reserved.append((char) (this.messageBytes[offset] & 0xFF));
		offset += 1;
	    }

	    alert.setReserved(reserved);

	    // The number of bytes in the following signature field.
	    byte[] numBytes5 = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numBytes5[y] = this.messageBytes[offset + y];
	    }

	    VarInt numByte5 = new VarInt(numBytes5, 0);

	    offset += numByte5.getOriginalSizeInBytes();

	    // A DER-encoded ECDSA (secp256k1) signature of the alert signed
	    // with the developer’s alert key. As an unsigned char array.

	    StringBuffer sig = new StringBuffer();

	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numByte5.getValue()) != 0; x = x.add(BigInteger.ONE)) {
		sig.append((char) (this.messageBytes[offset] & 0xFF));
		offset += 1;
	    }

	    // Set for processing

	    argsToProcess = new Object[2];
	    argsToProcess[0] = alert;
	    argsToProcess[1] = sig;

	} else if (commandName.toString().compareTo("filteradd") == 0) {
	    /*
	     * Added in protocol version 70001 as described by BIP37.
	     * 
	     * The filteradd message tells the receiving peer to add a single
	     * element to a previously-set bloom filter, such as a new public
	     * key. The element is sent directly to the receiving peer; the peer
	     * then uses the parameters set in the filterload message to add the
	     * element to the bloom filter.
	     * 
	     * Because the element is sent directly to the receiving peer, there
	     * is no obfuscation of the element and none of the
	     * plausible-deniability privacy provided by the bloom filter.
	     * Clients that want to maintain greater privacy should recalculate
	     * the bloom filter themselves and send a new filterload message
	     * with the recalculated bloom filter.
	     */

	    int offset = 0;

	    // The number of bytes in the following element field. As a varInt.
	    byte[] numBytes = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numBytes[y] = this.messageBytes[offset + y];
	    }

	    VarInt numByte = new VarInt(numBytes, 0);

	    offset += numByte.getOriginalSizeInBytes();

	    // The element to add to the current filter. Maximum of 520 bytes,
	    // which is the maximum size of an element which can be pushed onto
	    // the stack in a pubkey or signature script. Elements must be sent
	    // in the byte order they would use when appearing in a raw
	    // transaction; for example, hashes should be sent in internal byte
	    // order. As a uint8_t[]
	    ShortBuffer b = ShortBuffer.allocate(numByte.getValue().intValue());

	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numByte.getValue()) != 0; x = x.add(BigInteger.ONE)) {
		b.put((short) (this.messageBytes[offset] & 0xFF));
		offset += 1;
	    }

	    // Note: a filteradd message will not be accepted unless a filter
	    // was previously set with the filterload message.

	    // Set for processing
	    argsToProcess = new Object[1];
	    argsToProcess[0] = b;

	} else if (commandName.toString().compareTo("filterclear") == 0) {
	    /*
	     * Added in protocol version 70001 as described by BIP37.
	     * 
	     * The filterclear message tells the receiving peer to remove a
	     * previously-set bloom filter. This also undoes the effect of
	     * setting the relay field in the version message to 0, allowing
	     * unfiltered access to inv messages announcing new transactions.
	     * 
	     * Bitcoin Core does not require a filterclear message before a
	     * replacement filter is loaded with filterload. It also doesn’t
	     * require a filterload message before a filterclear message.
	     * 
	     * There is no payload in a filterclear message. See the message
	     * header section for an example of a message without a payload.
	     */
	} else if (commandName.toString().compareTo("filterload") == 0) {
	    /*
	     * Added in protocol version 70001 as described by BIP37.
	     * 
	     * The filterload message tells the receiving peer to filter all
	     * relayed transactions and requested merkle blocks through the
	     * provided filter. This allows clients to receive transactions
	     * relevant to their wallet plus a configurable rate of false
	     * positive transactions which can provide plausible-deniability
	     * privacy.
	     */

	    int offset = 0;

	    // Number of bytes in the following filter bit field. As a uint8_t[]

	    BigInteger numBytes = BigInteger
		    .valueOf(this.messageBytes[offset] & 0xFF);

	    offset += 8;

	    // A bit field of arbitrary byte-aligned size. The maximum size is
	    // 36,000 bytes.
	    // As a uint8_t[]

	    ShortBuffer b = ShortBuffer.allocate(numBytes.intValue());

	    for (BigInteger x = BigInteger.ZERO; x
		    .compareTo(numBytes) != 0; x = x.add(BigInteger.ONE)) {
		b.put((short) (this.messageBytes[offset] & 0xFF));
		offset += 1;
	    }

	    // The number of hash functions to use in this filter. The maximum
	    // value allowed in this field is 50. As a uint32_t.

	    BigInteger numHash = BigInteger
		    .valueOf(Utils.readUint32(this.messageBytes, offset));

	    offset += 4;

	    // An arbitrary value to add to the seed value in the hash function
	    // used by the bloom filter. As a uint32_t.
	    BigInteger value = BigInteger
		    .valueOf(Utils.readUint32(this.messageBytes, offset));

	    offset += 4;

	    // A set of flags that control how outpoints corresponding to a
	    // matched pubkey script are added to the filter. See the table in
	    // the Updating A Bloom Filter subsection below. As a uint8_t.

	    BigInteger flags = BigInteger
		    .valueOf((short) (this.messageBytes[offset] & 0xFF));

	    // Set for processing

	    argsToProcess = new Object[4];
	    argsToProcess[0] = b;
	    argsToProcess[1] = numHash;
	    argsToProcess[2] = value;
	    argsToProcess[3] = flags;

	} else if (commandName.toString().compareTo("getaddr") == 0) {
	    /*
	     * The getaddr message requests an addr message from the receiving
	     * node, preferably one with lots of IP addresses of other receiving
	     * nodes. The transmitting node can use those IP addresses to
	     * quickly update its database of available nodes rather than
	     * waiting for unsolicited addr messages to arrive over time.
	     * 
	     * There is no payload in a getaddr message. See the message header
	     * section for an example of a message without a payload.
	     */

	} else if (commandName.toString().compareTo("ping") == 0) {
	    /*
	     * The ping message helps confirm that the receiving peer is still
	     * connected. If a TCP/IP error is encountered when sending the ping
	     * message (such as a connection timeout), the transmitting node can
	     * assume that the receiving node is disconnected. The response to a
	     * ping message is the pong message.
	     * 
	     * Before protocol version 60000, the ping message had no payload.
	     * As of protocol version 60001 and all later versions, the message
	     * includes a single field, the nonce.
	     */

	    int offset = 0;

	    // Random nonce assigned to this ping message. The responding pong
	    // message will include this nonce to identify the ping message to
	    // which it is replying. As a uint64_t.
	    BigInteger nonce = Utils.readUint64(this.messageBytes, offset);

	    // Set for processing

	    argsToProcess = new Object[1];

	    argsToProcess[0] = nonce;

	} else if (commandName.toString().compareTo("pong") == 0) {
	    /*
	     * Added in protocol version 60001 as described by BIP31.
	     * 
	     * The pong message replies to a ping message, proving to the
	     * pinging node that the ponging node is still alive. Bitcoin Core
	     * will, by default, disconnect from any clients which have not
	     * responded to a ping message within 20 minutes.
	     * 
	     * To allow nodes to keep track of latency, the pong message sends
	     * back the same nonce received in the ping message it is replying
	     * to.
	     * 
	     * The format of the pong message is identical to the ping message;
	     * only the message header differs.
	     */

	    int offset = 0;

	    // Nonce assigned to the ping message that was sent. As a uint64_t.
	    BigInteger nonce = Utils.readUint64(this.messageBytes, offset);

	    // Set for processing

	    argsToProcess = new Object[1];

	    argsToProcess[0] = nonce;

	} else if (commandName.toString().compareTo("reject") == 0) {
	    /*
	     * Added in protocol version 70002 as described by BIP61.
	     * 
	     * The reject message informs the receiving node that one of its
	     * previous messages has been rejected.
	     */

	    int offset = 0;

	    // The number of bytes in the following message field. As a varInt.

	    byte[] numBytes = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numBytes[y] = this.messageBytes[offset + y];
	    }

	    VarInt numByte = new VarInt(numBytes, 0);

	    offset += numByte.getOriginalSizeInBytes();

	    CharBuffer c = CharBuffer.allocate(numByte.getValue().intValue());

	    // The type of message rejected as ASCII text without null padding.
	    // For example: “tx”, “block”, or “version”. As a string.
	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numByte.getValue()) != 0; x = x.add(BigInteger.ONE)) {
		c.put((char) (this.messageBytes[offset] & 0xFF));

		offset += 1;
	    }

	    StringBuffer msgType = new StringBuffer(c.toString());

	    // The reject message code. See the table below. As a char.

	    Character code = Character
		    .valueOf((char) (this.messageBytes[offset] & 0xFF));

	    // The number of bytes in the following reason field. May be 0x00 if
	    // a text reason isn’t provided. As a varInt.

	    byte[] numBytes2 = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numBytes2[y] = this.messageBytes[offset + y];
	    }

	    VarInt numByte2 = new VarInt(numBytes2, 0);

	    offset += numByte2.getOriginalSizeInBytes();

	    // The reason for the rejection in ASCII text. This should not be
	    // displayed to the user; it is only for debugging purposes. As a
	    // string.

	    CharBuffer c1 = CharBuffer.allocate(numByte.getValue().intValue());

	    // The type of message rejected as ASCII text without null padding.
	    // For example: “tx”, “block”, or “version”. As a string.
	    for (BigInteger x = BigInteger.ZERO; x.compareTo(
		    numByte2.getValue()) != 0; x = x.add(BigInteger.ONE)) {
		c1.put((char) (this.messageBytes[offset] & 0xFF));

		offset += 1;
	    }

	    StringBuffer reason = new StringBuffer(c1.toString());

	    // Optional additional data provided with the rejection. For
	    // example, most rejections of tx messages or block messages include
	    // the hash of the rejected transaction or block header.

	    // If it exists its a char[32]

	    CharBuffer c2 = CharBuffer
		    .allocate(this.messageBytes.length - offset);

	    for (BigInteger x = BigInteger.ZERO; x.compareTo(BigInteger
		    .valueOf(this.messageBytes.length - offset)) != 0; x = x
			    .add(BigInteger.ONE)) {
		c2.put((char) (this.messageBytes[offset] & 0xFF));

		offset += 1;
	    }

	    StringBuffer additionalData = new StringBuffer(c2.toString());

	    // Set for processing

	    argsToProcess = new Object[4];
	    argsToProcess[0] = msgType;
	    argsToProcess[1] = code;
	    argsToProcess[2] = reason;
	    argsToProcess[3] = additionalData;

	} else if (commandName.toString().compareTo("verack") == 0) {
	    /*
	     * The verack message acknowledges a previously-received version
	     * message, informing the connecting node that it can begin to send
	     * other messages. The verack message has no payload; for an example
	     * of a message with no payload, see the message headers section.
	     */

	} else if (commandName.toString().compareTo("version") == 0) {
	    /*
	     * The version message provides information about the transmitting
	     * node to the receiving node at the beginning of a connection.
	     * Until both peers have exchanged version messages, no other
	     * messages will be accepted.
	     * 
	     * If a version message is accepted, the receiving node should send
	     * a verack message—but no node should send a verack message before
	     * initializing its half of the connection by first sending a
	     * version message.
	     */

	    int offset = 0;

	    // The highest protocol version understood by the transmitting node.
	    // See the protocol version section. As a uint32_t.

	    BigInteger maxProtocol = BigInteger
		    .valueOf(Utils.readUint32(this.messageBytes, offset));

	    offset += 4;

	    // The services supported by the transmitting node encoded as a
	    // bitfield. See the list of service codes below. As a uint64_t.

	    BigInteger services = Utils.readUint64(this.messageBytes, offset);

	    offset += 8;

	    // The current Unix epoch time according to the transmitting node’s
	    // clock. Because nodes will reject blocks with timestamps more than
	    // two hours (or 45 seconds for GLD) in the future, this field can
	    // help other nodes to
	    // determine that their clock is wrong. As a uint64_t.

	    BigInteger time = Utils.readUint64(this.messageBytes, offset);

	    offset += 8;

	    // The services supported by the receiving node as perceived by the
	    // transmitting node. Same format as the ‘services’ field above.
	    // Bitcoin Core will attempt to provide accurate information.
	    // BitcoinJ will, by default, always send 0. As a uint64_t.

	    BigInteger estimatedServices = Utils.readUint64(this.messageBytes,
		    offset);

	    offset += 8;

	    // The IPv6 address of the receiving node as perceived by the
	    // transmitting node in big endian byte order. IPv4 addresses can be
	    // provided as IPv4-mapped IPv6 addresses. Bitcoin Core will attempt
	    // to provide accurate information. BitcoinJ will, by default,
	    // always return ::ffff:127.0.0.1. As a char array of 16 bytes.

	    byte[] address = new byte[16];

	    System.arraycopy(this.messageBytes, offset, address, 0, 16);

	    CharBuffer c = CharBuffer.allocate(16);

	    for (byte b : address) {
		c.put((char) (b & 0xFF));
	    }

	    StringBuffer receivingAddress = new StringBuffer(c.toString());

	    offset += 16;

	    // The port number of the receiving node as perceived by the
	    // transmitting node in big endian byte order. As a uint16_t.

	    BigInteger receivingPort = BigInteger
		    .valueOf(Utils.readUint16BE(this.messageBytes, offset));

	    offset += 2;

	    // The services supported by the transmitting node. Should be
	    // identical to the ‘services’ field above. As a uint64_t.

	    Utils.readUint64(this.messageBytes, offset);

	    offset += 8;

	    // The IPv6 address of the transmitting node in big endian byte
	    // order. IPv4 addresses can be provided as IPv4-mapped IPv6
	    // addresses. Set to ::ffff:127.0.0.1 if unknown. As a char array.

	    byte[] address2 = new byte[16];

	    System.arraycopy(this.messageBytes, offset, address2, 0, 16);

	    CharBuffer c2 = CharBuffer.allocate(16);

	    for (byte b : address2) {
		c2.put((char) (b & 0xFF));
	    }

	    StringBuffer transmittingAddress = new StringBuffer(c2.toString());

	    offset += 16;

	    // The port number of the transmitting node in big endian byte
	    // order. As a uint16_t.

	    BigInteger transmittingPort = BigInteger
		    .valueOf(Utils.readUint16BE(this.messageBytes, offset));

	    offset += 2;

	    // A random nonce which can help a node detect a connection to
	    // itself. If the nonce is 0, the nonce field is ignored. If the
	    // nonce is anything else, a node should terminate the connection on
	    // receipt of a version message with a nonce it previously sent.
	    // As a uint64_t.

	    BigInteger nonce = Utils.readUint64(this.messageBytes, offset);

	    offset += 8;

	    // Number of bytes in following user_agent field. If 0x00, no user
	    // agent field is sent. As a varInt.

	    byte[] numBytes = new byte[9];

	    for (byte y = 0; y < 9; y++) {
		numBytes[y] = this.messageBytes[offset + y];
	    }

	    VarInt numByte2 = new VarInt(numBytes, 0);

	    offset += numByte2.getOriginalSizeInBytes();

	    // User agent as defined by BIP14. Previously called subVer.
	    // As a string.

	    byte[] agent = new byte[numByte2.getValue().intValue()];

	    System.arraycopy(this.messageBytes, offset, agent, 0,
		    numByte2.getValue().intValue());

	    CharBuffer c3 = CharBuffer.allocate(numByte2.getValue().intValue());

	    for (byte b : agent) {
		c3.put((char) (b & 0xFF));
	    }

	    StringBuffer userAgent = new StringBuffer(c3.toString());

	    offset += numByte2.getValue().intValue();

	    // The height of the transmitting node’s best block chain or, in the
	    // case of an SPV client, best block header chain.
	    // As a uint32_t.

	    BigInteger bestHeight = BigInteger
		    .valueOf(Utils.readUint32(this.messageBytes, offset));

	    offset += 4;

	    // Added in protocol version 70001 as described by BIP37.
	    // Transaction relay flag. If 0x00, no inv messages or tx messages
	    // announcing new transactions should be sent to this client until
	    // it sends a filterload message or filterclear message. If 0x01,
	    // this node wants inv messages and tx messages announcing new
	    // transactions. As a bool.

	    Boolean bool = Boolean
		    .valueOf(String.valueOf(this.messageBytes[offset]));

	    // Set for processing

	    argsToProcess = new Object[12];
	    argsToProcess[0] = maxProtocol;
	    argsToProcess[1] = services;
	    argsToProcess[2] = time;
	    argsToProcess[3] = estimatedServices;
	    argsToProcess[4] = receivingAddress;
	    argsToProcess[5] = receivingPort;
	    argsToProcess[6] = transmittingAddress;
	    argsToProcess[7] = transmittingPort;
	    argsToProcess[8] = nonce;
	    argsToProcess[9] = userAgent;
	    argsToProcess[10] = bestHeight;
	    argsToProcess[11] = bool;

	}

	return this.processMessage(new StringBuffer(commandName),
		argsToProcess);

    }

    /**
     * @return the messageHeader
     */
    public MessageHeader getMessageHeader() {
	return this.messageHeader;
    }

    /**
     * @param messageHeader
     *            the messageHeader to set
     */
    public void setMessageHeader(MessageHeader messageHeader) {
	this.messageHeader = messageHeader;
    }

    /**
     * @return the messageBytes
     */
    public byte[] getMessageBytes() {
	return this.messageBytes;
    }

    /**
     * @param messageBytes
     *            the messageBytes to set
     */
    public void setMessageBytes(byte[] messageBytes) {
	this.messageBytes = messageBytes;
    }

    /**
     * @return the headerBytes
     */
    public byte[] getHeaderBytes() {
	return this.headerBytes;
    }

    /**
     * @param headerBytes
     *            the headerBytes to set
     */
    public void setHeaderBytes(byte[] headerBytes) {
	this.headerBytes = headerBytes;
    }

    /**
     * @return the legacy
     */
    public boolean isLegacy() {
	return legacy;
    }

    /**
     * @param legacy
     *            the legacy to set
     */
    public void setLegacy(boolean legacy) {
	this.legacy = legacy;
    }

    /**
     * @return the coin
     */
    public Coin getCoin() {
	return coin;
    }

    public class InventoryVector {

	// Identifies the object type linked to this inventory
	// Typically a uint32_t (4 bytes unsigned)

	/*
	 * Currently implemented object types are:
	 * 
	 * 0 ERROR Any data of with this number may be ignored
	 * 
	 * 1 MSG_TX Hash is related to a transaction
	 * 
	 * 2 MSG_BLOCK Hash is related to a data block
	 * 
	 * 3 MSG_FILTERED_BLOCK Hash of a block header; identical to MSG_BLOCK.
	 * When used in a getdata message, this indicates the reply should be a
	 * merkleblock message rather than a block message; this only works if a
	 * bloom filter has been set.
	 */
	BigInteger type;

	// Hash of this object
	// Typically a char[32]
	StringBuffer hash;

	// Is this object operating in legacy mode?
	boolean legacy;

	public InventoryVector(boolean legacy1) {
	    this.legacy = legacy1;
	}

	public InventoryVector(StringBuffer hash, BigInteger type,
		boolean legacy1) {
	    this.hash = hash;
	    this.type = type;
	    this.legacy = legacy1;
	}

	/**
	 * @return the type
	 */
	public BigInteger getType() {
	    return this.type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(BigInteger type) {
	    this.type = type;
	}

	/**
	 * @return the hash
	 */
	public StringBuffer getHash() {
	    return this.hash;
	}

	/**
	 * @param hash
	 *            the hash to set
	 */
	public void setHash(StringBuffer hash) {
	    this.hash = hash;
	}

	/**
	 * Reads the values into this InventoryVector
	 * 
	 * Assumes legacy format
	 * 
	 * @param bytes
	 */
	public void readLegacy(byte[] bytes, int offset) {
	    // First 4 bytes are a uin32_t
	    this.setType(BigInteger.valueOf(Utils.readUint32(bytes, offset)));

	    // Next 32 bytes are a char[32]
	    char[] hash1 = new char[32];

	    System.arraycopy(bytes, offset + 4, hash1, 0, 32);

	    this.setHash(new StringBuffer(String.valueOf(hash1)));
	}

	/**
	 * @return the legacy
	 */
	public boolean isLegacy() {
	    return legacy;
	}

	/**
	 * @param legacy
	 *            the legacy to set
	 */
	public void setLegacy(boolean legacy) {
	    this.legacy = legacy;
	}

	/*
	 * Returns the raw size of the fields in this object
	 */
	public BigInteger getSize() {
	    if (this.isLegacy()) {
		return BigInteger.valueOf(36);
	    }
	    return BigInteger.valueOf(this.getType().bitLength()
		    + this.getHash().toString().getBytes().length * 8);
	}
    }

    private class MessageHeader {

	/*
	 * Magic bytes indicating the originating network; used to seek to next
	 * message when stream state is unknown.
	 */
	StringBuffer startString;

	/*
	 * ASCII string which identifies what message type is contained in the
	 * payload. Followed by nulls (0x00) to pad out byte count; for example:
	 * version\0\0\0\0\0.
	 */
	StringBuffer commandName;

	/*
	 * Number of bytes in payload. The current maximum number of bytes
	 * (MAX_SIZE) allowed in the payload by Bitcoin Core is 32 MiB—messages
	 * with a payload size larger than this will be dropped or rejected.
	 */
	BigInteger payloadSize;

	/*
	 * Added in protocol version 209.
	 * 
	 * First 4 bytes of SHA256(SHA256(payload)) in internal byte order.
	 * 
	 * If payload is empty, as in verack and getaddr messages, the checksum
	 * is always 0x5df6e0e2 (SHA256(SHA256(<empty string>))).
	 */
	StringBuffer checkSum;

	public MessageHeader() {
	}

	public MessageHeader(StringBuffer startString1,
		StringBuffer commandName1, BigInteger payloadSize1,
		StringBuffer checkSum1) {

	    this.startString = startString1;
	    this.commandName = commandName1;
	    this.payloadSize = payloadSize1;
	    this.checkSum = checkSum1;
	}

	/*
	 * Takes a byte array in, translates and disseminates
	 */
	public void processHeader(byte[] headerBytes1) {
	    char[] startString1 = { (char) (headerBytes1[0] & 0xFF),
		    (char) (headerBytes1[1] & 0xFF),
		    (char) (headerBytes1[2] & 0xFF),
		    (char) (headerBytes1[3] & 0xFF) };

	    this.startString = new StringBuffer(String.valueOf(startString1));

	    char[] commandName1 = new char[12];

	    for (int x = 4; x < 16; x++) {
		commandName1[x - 4] = (char) (headerBytes1[x] & 0xFF);
	    }

	    this.payloadSize = BigInteger
		    .valueOf(Utils.readUint32(headerBytes1, 16));

	    char[] checkSum1 = new char[4];

	    for (int x = 0; x < 4; x++) {
		checkSum1[x] = (char) (headerBytes1[x] & 0xFF);
	    }

	    this.checkSum = new StringBuffer(String.valueOf(checkSum1));

	}

	/**
	 * @return the startString
	 */
	public StringBuffer getStartString() {
	    return this.startString;
	}

	/**
	 * @param startString
	 *            the startString to set
	 */
	public void setStartString(StringBuffer startString) {
	    this.startString = startString;
	}

	/**
	 * @return the commandName
	 */
	public StringBuffer getCommandName() {
	    return this.commandName;
	}

	/**
	 * @param commandName
	 *            the commandName to set
	 */
	public void setCommandName(StringBuffer commandName) {
	    this.commandName = commandName;
	}

	/**
	 * @return the payloadSize
	 */
	public BigInteger getPayloadSize() {
	    return this.payloadSize;
	}

	/**
	 * @param payloadSize
	 *            the payloadSize to set
	 */
	public void setPayloadSize(BigInteger payloadSize) {
	    this.payloadSize = payloadSize;
	}

	/**
	 * @return the checkSum
	 */
	public StringBuffer getCheckSum() {
	    return this.checkSum;
	}

	/**
	 * @param checkSum
	 *            the checkSum to set
	 */
	public void setCheckSum(StringBuffer checkSum) {
	    this.checkSum = checkSum;
	}

    }

}
