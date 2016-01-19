package coin;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

import database.DatabaseHandler;
import net.MessagingHandler;
import util.BigFastList;

/**
 * @author Amir Eslampanah
 * 
 */
public class Coin {

    private Wallet coinWallet;

    private MessagingHandler messagingHandler;

    /*
     * Mappings:
     * 
     * 
     * Legend:
     * 
     * -> Direct Mapping <==> Nature Change
     * 
     * ==================
     * 
     * mapBlockIndex <==> blockChain {Map is no longer needed as arrayList has
     * built in functions to do the same things} hashGenesisBlock ->
     * genesisBlockHash vnProofOfWorkLimit -> proofOfWorkLimit
     * 
     * pindexGenesisBlock <==> blockChain nBestHeight -> bestBlockChainHeight
     * bnBestChainWork -> bestBlockChainWork bnBestInvalidWork ->
     * bestInvalidWork hashBestChain -> bestHashChain pIndexBest <==> blockChain
     * {the best block is the last block in the currently accepted chain}
     * 
     * nTimeBestReceived -> bestNetworkTime
     * 
     * CMedianFilter <==> peerBlockHeightSamples mapOrphanBlocks <==>
     * orphanBlocks mapOrphanBlocksByPrev <==> orphanBlocks
     * mapOrphanTransactions <==> orphanTransactions (not implemented)
     * mapOrphanTransactionsByPrev <==> orphanTransactions (not implemented)
     * 
     * strMessageMagic -> goldSignature
     */

    /*
     * We have a ZFS style mentality here where we don't want to hit any limits
     * in the future.
     * 
     * For this reason we are always using expandable types.
     * 
     * This has the disadvantage of increased memory footprint and latency;
     * however the difference is negligible on modern hardware compared to what
     * we gain in functionality and code longevity.
     * 
     * Also these strings should be externalized at some later date if they
     * aren't already.. atm I'd rather have one file (jar) to deal with.
     */

    // Our Genesis block
    StringBuffer genesisHexHash = new StringBuffer(
	    "dced3542896ed537cb06f9cb064319adb0da615f64dd8c5e5bad974398f44b24"); //$NON-NLS-1$
    BigInteger genesisBlockHash;

    // Proof of work limit
    // GoldCoin (GLD): starting difficulty is stated to be 1/2^12 (same as
    // litecoin)
    // The target is what is decribed here.. which is 2^256 shifted right by 20
    // bits
    // This makes the maximum target 2^236

    // This is the same code as litecoin though (Tilde inverts zero's bits
    // becoming 2^256 as it is unsigned then it is bitshifted to the right by 20
    // bits)
    // so whomever said GoldCoin was insta-mined at the start is a moron.
    // GoldCoin's start was identical to litecoin's.

    BigInteger proofOfWorkLimit = BigInteger.valueOf(2).pow(256).shiftRight(20);

    // Max Target is 2^224 though it gets truncated to (2^16 - 1) * 2^208
    // due to the original bitcoin client storing it as a float
    BigDecimal maxTarget = BigDecimal.valueOf(2).pow(16)
	    .subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(2).pow(208));

    BigFastList<Block> blockChain = new BigFastList<Block>();

    BigInteger bestBlockChainHeight = BigInteger.valueOf(-1);
    BigInteger bestBlockChainWork = BigInteger.ZERO;
    BigInteger bestInvalidWork = BigInteger.ZERO;
    // BigInteger bestHashChain = BigInteger.ZERO;

    BigInteger bestNetworkTime = BigInteger
	    .valueOf(System.currentTimeMillis() / 1000L);

    BigFastList<BigInteger> peerBlockHeightSamples = new BigFastList<BigInteger>();

    BigFastList<Block> orphanBlocks = new BigFastList<Block>();

    StringBuffer goldSignature = new StringBuffer(
	    "GoldCoin (GLD) Signed Message:\n"); //$NON-NLS-1$

    BigInteger CENT = BigInteger.valueOf(1000000);
    BigInteger COIN = BigInteger.valueOf(100000000);
    BigInteger MIN_TX_FEE = BigInteger.valueOf(10000000);
    BigInteger MIN_RELAY_TX_FEE = this.MIN_TX_FEE;
    BigInteger MAX_MONEY = BigInteger.valueOf(123423900).multiply(this.COIN);
    BigInteger MAX_BLOCK_SIZE = BigInteger.valueOf(1000000);
    BigInteger MAX_BLOCK_SIZE_GEN = this.MAX_BLOCK_SIZE
	    .divide(BigInteger.valueOf(2));
    BigInteger MAX_BLOCK_SIGOPS = this.MAX_BLOCK_SIZE
	    .divide(BigInteger.valueOf(50));
    BigInteger MAX_ORPHAN_TRANSACTIONS = this.MAX_BLOCK_SIZE
	    .divide(BigInteger.valueOf(100));
    BigInteger LOCKTIME_THRESHOLD = BigInteger.valueOf(500000000L);

    // The last block in the current longest chain.
    // This block is what allows us to know what chain we are currently on as
    // well as what transactions are relevant
    Block lastBlock = null;

    // 0xfd, 0xc2, 0xb4, 0xdd

    String[] networkNum = { "fd", "c2", "b4", "dd" }; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

    private DatabaseHandler databaseHandler;

    byte[] networkBytes = { new BigInteger(this.networkNum[0], 16).byteValue(),
	    new BigInteger(this.networkNum[1], 16).byteValue(),
	    new BigInteger(this.networkNum[2], 16).byteValue(),
	    new BigInteger(this.networkNum[3], 16).byteValue() };

    /**
     * 
     */
    public Coin() {
	this.genesisBlockHash = new BigInteger(this.genesisHexHash.toString(),
		16);

	this.messagingHandler = new MessagingHandler();
    }

    /**
     * 
     */
    public void initializeCoinDB() {
	this.databaseHandler = new DatabaseHandler(this);

	try {
	    this.databaseHandler = new DatabaseHandler(this,
		    new StringBuffer(this.databaseHandler.getPath()));

	    this.databaseHandler.debugBlock(BigInteger.valueOf(6));

	} catch (Exception e) {
	    e.printStackTrace();
	}

	// Create log file
	File log = new File(
		this.databaseHandler.getPath() + File.separator + "log.txt"); //$NON-NLS-1$
	log.mkdirs();
    }

    /**
     * Hashes this block header with this coin's algorithm and returns true if
     * and only if the block header's hash matches its hash
     * 
     * @param blockHeader
     * @return
     */
    public boolean confirmBlockHash(BlockHeader blockHeader) {
	return false;
    }

    /**
     * @return the genesisHexHash
     */
    public StringBuffer getGenesisHexHash() {
	return this.genesisHexHash;
    }

    /**
     * @param genesisHexHash1
     *            the genesisHexHash to set
     */
    public void setGenesisHexHash(StringBuffer genesisHexHash1) {
	this.genesisHexHash = genesisHexHash1;
    }

    /**
     * @return the genesisBlockHash
     */
    public BigInteger getGenesisBlockHash() {
	return this.genesisBlockHash;
    }

    /**
     * @param genesisBlockHash1
     *            the genesisBlockHash to set
     */
    public void setGenesisBlockHash(BigInteger genesisBlockHash1) {
	this.genesisBlockHash = genesisBlockHash1;
    }

    /**
     * @return the proofOfWorkLimit
     */
    public BigInteger getProofOfWorkLimit() {
	return this.proofOfWorkLimit;
    }

    /**
     * @param proofOfWorkLimit1
     *            the proofOfWorkLimit to set
     */
    public void setProofOfWorkLimit(BigInteger proofOfWorkLimit1) {
	this.proofOfWorkLimit = proofOfWorkLimit1;
    }

    /**
     * @return the maxTarget
     */
    public BigDecimal getMaxTarget() {
	return maxTarget;
    }

    /**
     * @param maxTarget
     *            the maxTarget to set
     */
    public void setMaxTarget(BigDecimal maxTarget) {
	this.maxTarget = maxTarget;
    }

    /**
     * @return the blockChain
     */
    public BigFastList<Block> getBlockChain() {
	return this.blockChain;
    }

    /**
     * @param blockChain1
     *            the blockChain to set
     */
    public void setBlockChain(BigFastList<Block> blockChain1) {
	this.blockChain = blockChain1;
    }

    /**
     * @return the bestBlockChainHeight
     */
    public BigInteger getBestBlockChainHeight() {
	return this.bestBlockChainHeight;
    }

    /**
     * @param bestBlockChainHeight1
     *            the bestBlockChainHeight to set
     */
    public void setBestBlockChainHeight(BigInteger bestBlockChainHeight1) {
	this.bestBlockChainHeight = bestBlockChainHeight1;
    }

    /**
     * @return the bestBlockChainWork
     */
    public BigInteger getBestBlockChainWork() {
	return this.bestBlockChainWork;
    }

    /**
     * @param bestBlockChainWork1
     *            the bestBlockChainWork to set
     */
    public void setBestBlockChainWork(BigInteger bestBlockChainWork1) {
	this.bestBlockChainWork = bestBlockChainWork1;
    }

    /**
     * @return the bestInvalidWork
     */
    public BigInteger getBestInvalidWork() {
	return this.bestInvalidWork;
    }

    /**
     * @param bestInvalidWork1
     *            the bestInvalidWork to set
     */
    public void setBestInvalidWork(BigInteger bestInvalidWork1) {
	this.bestInvalidWork = bestInvalidWork1;
    }

    /**
     * @return the bestNetworkTime
     */
    public BigInteger getBestNetworkTime() {
	return this.bestNetworkTime;
    }

    /**
     * @param bestNetworkTime1
     *            the bestNetworkTime to set
     */
    public void setBestNetworkTime(BigInteger bestNetworkTime1) {
	this.bestNetworkTime = bestNetworkTime1;
    }

    /**
     * @return the peerBlockHeightSamples
     */
    public BigFastList<BigInteger> getPeerBlockHeightSamples() {
	return this.peerBlockHeightSamples;
    }

    /**
     * @param peerBlockHeightSamples1
     *            the peerBlockHeightSamples to set
     */
    public void setPeerBlockHeightSamples(
	    BigFastList<BigInteger> peerBlockHeightSamples1) {
	this.peerBlockHeightSamples = peerBlockHeightSamples1;
    }

    /**
     * @return the orphanBlocks
     */
    public BigFastList<Block> getOrphanBlocks() {
	return this.orphanBlocks;
    }

    /**
     * @param orphanBlocks1
     *            the orphanBlocks to set
     */
    public void setOrphanBlocks(BigFastList<Block> orphanBlocks1) {
	this.orphanBlocks = orphanBlocks1;
    }

    /**
     * @return the goldSignature
     */
    public StringBuffer getGoldSignature() {
	return this.goldSignature;
    }

    /**
     * @param goldSignature1
     *            the goldSignature to set
     */
    public void setGoldSignature(StringBuffer goldSignature1) {
	this.goldSignature = goldSignature1;
    }

    /**
     * @return the cENT
     */
    public BigInteger getCENT() {
	return this.CENT;
    }

    /**
     * @param cENT
     *            the cENT to set
     */
    public void setCENT(BigInteger cENT) {
	this.CENT = cENT;
    }

    /**
     * @return the cOIN
     */
    public BigInteger getCOIN() {
	return this.COIN;
    }

    /**
     * @param cOIN
     *            the cOIN to set
     */
    public void setCOIN(BigInteger cOIN) {
	this.COIN = cOIN;
    }

    /**
     * @return the mIN_TX_FEE
     */
    public BigInteger getMIN_TX_FEE() {
	return this.MIN_TX_FEE;
    }

    /**
     * @param mIN_TX_FEE
     *            the mIN_TX_FEE to set
     */
    public void setMIN_TX_FEE(BigInteger mIN_TX_FEE) {
	this.MIN_TX_FEE = mIN_TX_FEE;
    }

    /**
     * @return the mIN_RELAY_TX_FEE
     */
    public BigInteger getMIN_RELAY_TX_FEE() {
	return this.MIN_RELAY_TX_FEE;
    }

    /**
     * @param mIN_RELAY_TX_FEE
     *            the mIN_RELAY_TX_FEE to set
     */
    public void setMIN_RELAY_TX_FEE(BigInteger mIN_RELAY_TX_FEE) {
	this.MIN_RELAY_TX_FEE = mIN_RELAY_TX_FEE;
    }

    /**
     * @return the mAX_MONEY
     */
    public BigInteger getMAX_MONEY() {
	return this.MAX_MONEY;
    }

    /**
     * @param mAX_MONEY
     *            the mAX_MONEY to set
     */
    public void setMAX_MONEY(BigInteger mAX_MONEY) {
	this.MAX_MONEY = mAX_MONEY;
    }

    /**
     * @return the mAX_BLOCK_SIZE
     */
    public BigInteger getMAX_BLOCK_SIZE() {
	return this.MAX_BLOCK_SIZE;
    }

    /**
     * @param mAX_BLOCK_SIZE
     *            the mAX_BLOCK_SIZE to set
     */
    public void setMAX_BLOCK_SIZE(BigInteger mAX_BLOCK_SIZE) {
	this.MAX_BLOCK_SIZE = mAX_BLOCK_SIZE;
    }

    /**
     * @return the mAX_BLOCK_SIZE_GEN
     */
    public BigInteger getMAX_BLOCK_SIZE_GEN() {
	return this.MAX_BLOCK_SIZE_GEN;
    }

    /**
     * @param mAX_BLOCK_SIZE_GEN
     *            the mAX_BLOCK_SIZE_GEN to set
     */
    public void setMAX_BLOCK_SIZE_GEN(BigInteger mAX_BLOCK_SIZE_GEN) {
	this.MAX_BLOCK_SIZE_GEN = mAX_BLOCK_SIZE_GEN;
    }

    /**
     * @return the mAX_BLOCK_SIGOPS
     */
    public BigInteger getMAX_BLOCK_SIGOPS() {
	return this.MAX_BLOCK_SIGOPS;
    }

    /**
     * @param mAX_BLOCK_SIGOPS
     *            the mAX_BLOCK_SIGOPS to set
     */
    public void setMAX_BLOCK_SIGOPS(BigInteger mAX_BLOCK_SIGOPS) {
	this.MAX_BLOCK_SIGOPS = mAX_BLOCK_SIGOPS;
    }

    /**
     * @return the mAX_ORPHAN_TRANSACTIONS
     */
    public BigInteger getMAX_ORPHAN_TRANSACTIONS() {
	return this.MAX_ORPHAN_TRANSACTIONS;
    }

    /**
     * @param mAX_ORPHAN_TRANSACTIONS
     *            the mAX_ORPHAN_TRANSACTIONS to set
     */
    public void setMAX_ORPHAN_TRANSACTIONS(BigInteger mAX_ORPHAN_TRANSACTIONS) {
	this.MAX_ORPHAN_TRANSACTIONS = mAX_ORPHAN_TRANSACTIONS;
    }

    /**
     * @return the lOCKTIME_THRESHOLD
     */
    public BigInteger getLOCKTIME_THRESHOLD() {
	return this.LOCKTIME_THRESHOLD;
    }

    /**
     * @param lOCKTIME_THRESHOLD
     *            the lOCKTIME_THRESHOLD to set
     */
    public void setLOCKTIME_THRESHOLD(BigInteger lOCKTIME_THRESHOLD) {
	this.LOCKTIME_THRESHOLD = lOCKTIME_THRESHOLD;
    }

    /**
     * @return the networkNum
     */
    public String[] getNetworkNum() {
	return this.networkNum;
    }

    /**
     * @param networkNum1
     *            the networkNum to set
     */
    public void setNetworkNum(String[] networkNum1) {
	this.networkNum = networkNum1;
    }

    /**
     * @return the networkBytes
     */
    public byte[] getNetworkBytes() {
	return this.networkBytes;
    }

    /**
     * @param networkBytes1
     *            the networkBytes to set
     */
    public void setNetworkBytes(byte[] networkBytes1) {
	this.networkBytes = networkBytes1;
    }

    /**
     * @return the coinWallet
     */
    public Wallet getCoinWallet() {
	return this.coinWallet;
    }

    /**
     * @param coinWallet1
     *            the coinWallet to set
     */
    public void setCoinWallet(Wallet coinWallet1) {
	this.coinWallet = coinWallet1;
    }

    /**
     * @return the lastBlock
     */
    public Block getLastBlock() {
	return this.lastBlock;
    }

    /**
     * @param lastBlock1
     *            the lastBlock to set
     */
    public void setLastBlock(Block lastBlock1) {
	this.lastBlock = lastBlock1;
    }

    /**
     * @return the databaseHandler
     */
    public DatabaseHandler getDatabaseHandler() {
	return this.databaseHandler;
    }

    /**
     * @param databaseHandler1
     *            the databaseHandler to set
     */
    public void setDatabaseHandler(DatabaseHandler databaseHandler1) {
	this.databaseHandler = databaseHandler1;
    }

    /**
     * @return the messagingHandler
     */
    public MessagingHandler getMessagingHandler() {
	return this.messagingHandler;
    }

    /**
     * @param messagingHandler1
     *            the messagingHandler to set
     */
    public void setMessagingHandler(MessagingHandler messagingHandler1) {
	this.messagingHandler = messagingHandler1;
    }

}
