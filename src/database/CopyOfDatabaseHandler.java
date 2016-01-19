package database;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.util.DbLoad;

/**
 * @author Amir Eslampanah
 * 
 */
public class CopyOfDatabaseHandler {

    private Environment mainEnvironment;
    private Database mainDatabase;

    /**
     * 
     */
    public CopyOfDatabaseHandler() {

    }

    /**
     * @param homeDirectory
     * @throws DatabaseException
     * @throws IOException
     */
    public CopyOfDatabaseHandler(StringBuffer homeDirectory)
	    throws DatabaseException, IOException {

	System.out
		.println("Opening environment in: " + homeDirectory.toString()); //$NON-NLS-1$

	EnvironmentConfig envConfig = new EnvironmentConfig();
	envConfig.setTransactionalVoid(true);

	this.mainEnvironment = new Environment(
		new File(homeDirectory.toString()), envConfig);
	this.loadDB();

	/*
	 * Open our main database
	 */

	DatabaseConfig dbConfig = new DatabaseConfig();

	dbConfig.setTransactionalVoid(true);

	this.mainDatabase = this.mainEnvironment.openDatabase(null, "main", //$NON-NLS-1$
		dbConfig);

	System.out.println("Database name: " //$NON-NLS-1$
		+ this.mainDatabase.getDatabaseName());

	Transaction txn = this.mainEnvironment.beginTransaction(null, null);
	Cursor cursor = null;
	PrintWriter writer = new PrintWriter("dumpunknown2.txt", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
	try {
	    // Use the transaction handle here
	    cursor = this.mainDatabase.openCursor(txn, null);
	    DatabaseEntry key = new DatabaseEntry(), data = new DatabaseEntry();

	    while (cursor.getNext(key, data,
		    LockMode.DEFAULT) == OperationStatus.SUCCESS) {

		// System.out.println(new String(key.getData()));

		writer.println(new String(key.getData()));
		writer.println("==========="); //$NON-NLS-1$
		writer.println(new String(data.getData()));
		writer.println("==========="); //$NON-NLS-1$

		// System.out.println(new String(key.getData()));
		/*
		 * Nodes collect new transactions into a block, hash them into a
		 * hash tree, and scan through nonce values to make the block's
		 * hash satisfy proof-of-work requirements. When they solve the
		 * proof-of-work, they broadcast the block to everyone and the
		 * block is added to the block chain. The first transaction in
		 * the block is a special one that creates a new coin owned by
		 * the creator of the block.
		 * 
		 * Blocks are appended to blk0001.dat files on disk. Their
		 * location on disk is indexed by CBlockIndex objects in memory.
		 * 
		 * Since this is where we load blkindex.dat, we must first index
		 * all the locations of blocks on disk and then load them.
		 */

		/*
		 * data.setData(replacementData.getBytes("UTF-8"));
		 * //$NON-NLS-1$ // No transaction handle is used on the cursor
		 * read or write // methods. cursor.putCurrent(data);
		 */
	    }

	    cursor.close();
	    cursor = null;
	    txn.commit();
	} catch (Exception e) {
	    e.printStackTrace();
	    if (txn != null) {
		txn.abort();
		txn = null;
	    }
	}
	writer.close();

    }

    /**
     * 
     * @throws DatabaseException
     * @throws IOException
     */
    public void loadDB() throws DatabaseException, IOException {
	/*
	 * namespace fs = boost::filesystem; // Windows < Vista: C:\Documents
	 * and Settings\Username\Application Data\GoldCoin (GLD) // Windows >=
	 * Vista: C:\Users\Username\AppData\Roaming\GoldCoin (GLD) // Mac:
	 * ~/Library/Application Support/GoldCoin (GLD) // Unix: ~/.goldcoin
	 * #ifdef WIN32 // Windows return GetSpecialFolderPath(CSIDL_APPDATA) /
	 * "GoldCoin (GLD)"; #else fs::path pathRet; char* pszHome =
	 * getenv("HOME"); if (pszHome == NULL || strlen(pszHome) == 0) pathRet
	 * = fs::path("/"); else pathRet = fs::path(pszHome); #ifdef MAC_OSX //
	 * Mac pathRet /= "Library/Application Support";
	 * fs::create_directory(pathRet); return pathRet / "GoldCoin (GLD)";
	 * #else // Unix return pathRet / ".goldcoin"; #endif #endif }
	 */
	Path file = null;
	BufferedReader bufferedReader = null;
	try {
	    file = Paths.get(this.getPath() + File.separator + "blkindex.txt"); //$NON-NLS-1$
	    BufferedInputStream inputStream = new BufferedInputStream(
		    Files.newInputStream(file));
	    bufferedReader = new BufferedReader(
		    new InputStreamReader(inputStream));

	    // TEST

	    /*
	     * char[] versionNum = new char[4]; char[] prevHash = new char[32];
	     * char[] mrkleHash = new char[32]; char[] time = new char[4];
	     * char[] numBit = new char[4]; char[] nonce = new char[4];
	     * 
	     * int offset = 0; while (true) {
	     * 
	     * bufferedReader.read(versionNum, offset, 4); ByteBuffer wrapped =
	     * ByteBuffer .wrap(stringToBytesASCII(new String(versionNum))); //
	     * big-endian
	     * 
	     * wrapped.order(ByteOrder.LITTLE_ENDIAN); // by // default
	     * 
	     * System.out.println(wrapped.getShort()); break;
	     * 
	     * // First 4 chars are the current version number as a signed int
	     * // (might actually be unsigned in actual stream)
	     * 
	     * // Now comes the 32 character previous block hash as a uint256
	     * 
	     * // Now comes the 32 character merkle root hash as a uint256
	     * 
	     * // Now comes the 4 character timestamp as an unsigned int
	     * 
	     * // Now comes the 4 character number of bits as an unsigned int
	     * 
	     * // Now comes the 4 character nonce value as an unsigned int
	     * 
	     * // Note to self: Transactions need to be loaded!
	     * 
	     * }
	     */

	} catch (IOException e) {
	    e.printStackTrace();
	}

	// The process for Below to work as intended is too complex, too many
	// encodes/unencodes/hashing and pairing
	// done. In fact it is near impossible to follow as there is virtually
	// zero documentation.
	// Probably why bitcoinj is a thin client. They couldn't figure it out
	// either.
	// What was satoshi or whomever did this thinking?? This is a horrible
	// ondisk format.

	/*
	 * // Dump the old Database to a readable format String[] input = {
	 * "-h", this.getPath().toString(), "-s", "blockindex" }; //$NON-NLS-1$
	 * //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ try {
	 * 
	 * DbDump.main(input); } catch (Exception e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); }
	 */

	DbLoad loader = new DbLoad();
	loader.setEnv(this.mainEnvironment);
	// this.env.getDatabaseNames().add("BlockChain"); //$NON-NLS-1$
	loader.setDbName("main"); //$NON-NLS-1$
	loader.setInputReader(bufferedReader);
	loader.setNoOverwrite(true);
	loader.setTextFileMode(true);
	loader.load();
    }

    /**
     * @throws DatabaseException
     */
    public void close() throws DatabaseException {
	this.mainEnvironment.close();
    }

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
     * @return
     */
    public final Environment getEnvironment() {
	return this.mainEnvironment;
    }

    public static byte[] stringToBytesASCII(String str) {
	byte[] b = new byte[str.length()];
	for (int i = 0; i < b.length; i++) {
	    b[i] = (byte) str.charAt(i);
	}
	return b;
    }
}