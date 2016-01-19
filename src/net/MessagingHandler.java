package net;

import coin.Transaction;
import net.Message.InventoryVector;
import util.BigFastList;

/**
 * @author Amir Eslampanah
 * 
 * 
 *         Contains the client's messaging system
 * 
 *         This class ties connection objects with the message class
 * 
 */
public class MessagingHandler {

    /**
     * Messages are classified under 4 distinct priority queues, namely,
     * 
     * Priority 1 - Developer Level communication (Network alerts mainly)
     * 
     * Priority 2 - All communication that is necessary for this client to
     * function (Messages sent out to enable this client's functionality and
     * replies for to those messages classify here)
     * 
     * Priority 3 - Communication requested by other clients (Transaction
     * relaying and getData replies)
     * 
     * Priority 4 - RPC communication (CommandLine requests)
     * 
     * This way in the event of a Denial-of-Service of this node situation, the
     * important messages will not be caught up in a backlog.
     */
    public final static byte PRIORITY_ONE = 1, PRIORITY_TWO = 2,
	    PRIORITY_THREE = 3, PRIORITY_FOUR = 4;

    /**
     * Here is our receiving queue, essentially whenever we get a header or
     * transaction or block or inventory in reply it goes into this list for
     * processing by this thread
     */

    // Since the responses can be of different types, we follow the
    // following format
    // ID -> Object -> ID -> Object

    // So in our list we have leading identifiers that help our
    // MessageHandler determine the command that will be used for
    // sending
    // Types are defined in constants class
    private final BigFastList<Object> receivingQueue = new BigFastList<Object>();

    /**
     * A list of inventories we have seen
     */
    private final BigFastList<InventoryVector> inventoryList = new BigFastList<InventoryVector>();

    /**
     * A list of transactions that have been verified but have not yet been
     * included in a block in the main chain
     */
    private final BigFastList<Transaction> orphanTransactions = new BigFastList<Transaction>();

    /**
     * A request queue (sending queue for items of interest)
     * 
     * Note this is just a list of items we are interested in requesting from
     * other clients
     */

    // Since the responses can be of different types, we follow the
    // following format
    // ID -> Object -> ID -> Object
    private final BigFastList<Object> requestQueue = new BigFastList<Object>();

    /**
     * 
     */
    public MessagingHandler() {
    }

    /**
     * This method sends replies to processed messages that were formed by
     * processMessage. Forwards these to the MessagingHandler class to establish
     * priority values
     * 
     * @param commandName
     * @param args
     */
    public void sendMessage(StringBuffer commandName, Object[] args) {
	if (commandName.toString().compareTo("getblocks") == 0) {
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

	}
    }

    /**
     * @return the receivingQueue
     */
    public BigFastList<Object> getReceivingQueue() {
	return this.receivingQueue;
    }

    /**
     * @return the inventoryList
     */
    public BigFastList<InventoryVector> getInventoryList() {
	return this.inventoryList;
    }

    /**
     * @return the sendingQueue
     */
    public BigFastList<Object> getRequestQueue() {
	return this.requestQueue;
    }

    /**
     * @return the orphanTransactions
     */
    public BigFastList<Transaction> getOrphanTransactions() {
	return orphanTransactions;
    }
}
