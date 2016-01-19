package core;

import coin.Coin;
import net.ConnectionHandler;
import util.events.CascadeEvent;
import util.events.DownloadEvent;
import util.events.ProcessEvent;
import util.events.RestructureEvent;
import util.events.VerifyEvent;

/**
 * @author Amir Eslampanah
 * 
 */
public class Daemon extends Thread {

    private static StringBuffer status = new StringBuffer("offline"); //$NON-NLS-1$

    private ConnectionHandler connectionHandler;

    private Coin coinDetails;
    private CommandHandler commandHandler;
    private MiningHandler miningHandler;

    /**
     * 
     */
    public Daemon() {

    }

    protected void pause() {
	// Given the clock nature of our daemon..
	// It is possible to actually pause it

	status = new StringBuffer("paused"); //$NON-NLS-1$
    }

    @Override
    public synchronized void start() {
	status = new StringBuffer("online"); //$NON-NLS-1$

	this.run();
    }

    protected void end() {
	status = new StringBuffer("offline"); //$NON-NLS-1$
    }

    protected void reload() {
	status = new StringBuffer("reloading"); //$NON-NLS-1$
    }

    @Override
    public void run() {
	// Initialize non-looping components
	this.coinDetails = new Coin();
	this.commandHandler = new CommandHandler(this.getCoinDetails());
	this.miningHandler = new MiningHandler(this.getCoinDetails());
	this.connectionHandler = new ConnectionHandler(this.getCoinDetails());

	// Start our database handler and logging component
	this.getCoinDetails().initializeCoinDB();

	// The main program loop
	// Handles primary and secondary events queued up in the event manager

	// You will see that we do not operate on fixed-tick intervals...
	// This is because fixed-tick intervals are not necessary in this
	// application,
	// Thus it's best to let the program run its cycles as fast as it can.

	// Variable to keep track of loop execution number:
	long loopNum = 0;

	while (true) {

	    // Sleep the thread if paused
	    // This means that the thread will lag by at most a second when
	    // un-paused
	    // Warning: Setting this value too low will burn CPU time.
	    if (Daemon.status.toString().compareTo("paused") == 0) { //$NON-NLS-1$
		try {
		    currentThread().wait(1000);
		} catch (InterruptedException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    } else if (Daemon.status.toString().compareTo("reloading") == 0) { //$NON-NLS-1$
		this.connectionHandler.getServer().stop();
		// TODO:
		// Here we update the loaded configuration without having to
		// restart the whole client

		this.connectionHandler.getServer().start();

	    } else if (Daemon.status.toString().compareTo("offline") == 0) { //$NON-NLS-1$
		try {
		    currentThread().join();
		    break;
		} catch (InterruptedException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }

	    // We do standard program routines here.
	    // System.out.println("Execution of Loop number: " + ++loopNum);

	    // First we check our connection count if its under what it should
	    // be we try to find more.
	    this.connectionHandler.checkConnections();

	    // Now we can download block chain updates and do transaction
	    // processing.
	    CascadeEvent routine = new CascadeEvent();
	    routine.addEvent(new DownloadEvent()); // Download any new blocks
						   // from peers
	    routine.addEvent(new ProcessEvent()); // Organize Blocks and
						  // transactions
	    routine.addEvent(new VerifyEvent()); // Verify all transactions and
						 // signatures, remove illegal
						 // data
	    routine.addEvent(new RestructureEvent()); // Restructure the
						      // blockchain and
						      // transaction record
	    if (routine.execute()) {
		// If successful, we write the database to disk

	    } else {
		// Otherwise, we load the database as it was before these
		// changes from the disk..
		// This shouldn't occur in normal operation..
		// but may happen if a system error occurs.

	    }

	}

    }

    /**
     * @return
     */
    public CommandHandler getCommandHandler() {
	return this.commandHandler;
    }

    /**
     * @param commandHandler1
     */
    public void setCommandHandler(CommandHandler commandHandler1) {
	this.commandHandler = commandHandler1;
    }

    /**
     * @return the connectionHandler
     */
    public ConnectionHandler getConnectionHandler() {
	return this.connectionHandler;
    }

    /**
     * @param connectionHandler1
     *            the connectionHandler to set
     */
    public void setConnectionHandler(ConnectionHandler connectionHandler1) {
	this.connectionHandler = connectionHandler1;
    }

    /**
     * @return the status
     */
    public static StringBuffer getStatus() {
	return status;
    }

    /**
     * @param status1
     *            the status to set
     */
    public static void setStatus(StringBuffer status1) {
	Daemon.status = status1;
    }

    /**
     * @return the coinDetails
     */
    public Coin getCoinDetails() {
	return this.coinDetails;
    }

    /**
     * @param coinDetails1
     *            the coinDetails to set
     */
    public void setCoinDetails(Coin coinDetails1) {
	this.coinDetails = coinDetails1;
    }

    /**
     * @return the miningHandler
     */
    public MiningHandler getMiningHandler() {
	return this.miningHandler;
    }

    /**
     * @param miningHandler1
     *            the miningHandler to set
     */
    public void setMiningHandler(MiningHandler miningHandler1) {
	this.miningHandler = miningHandler1;
    }

}
