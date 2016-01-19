package net;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import coin.Coin;
import util.BigFastList;

/**
 * @author Amir Eslampanah
 * 
 */
public class ConnectionHandler {
    private final NioServer server = new NioServer();
    private final BigFastList<Connection> connections = new BigFastList<Connection>();
    private final Coin curCoin;

    /**
     * @param coin
     * 
     */
    public ConnectionHandler(Coin coin) {

	this.curCoin = coin;

	this.server.addTcpBinding(new InetSocketAddress(8121));// Connect

	this.server.addTcpBinding(new InetSocketAddress(8122));// fson-RPC

	this.server.addNioServerListener(new NioServer.Adapter() {
	    @Override
	    public void tcpDataReceived(NioServer.Event evt) {// Receive Data
		ByteBuffer buff = evt.getInputBuffer();

	    }
	});

	this.server.start();

    }

    /**
     * 
     */
    public void checkConnections() {
	// First we check to see if we have at least 16 valid connections
	for (Connection c : this.connections) {
	    if (!c.isValid()) {
		// Remove this connection
		this.connections.remove(c);

		// Add another connection
		// This is done in a separate thread so as to not disturb the
		// rest of the program
		Thread connectionFinder = new Thread(new Runnable() {
		    @Override
		    public void run() {
			for (Connection c1 : getConnections()) {
			    if (c1.isValid()) {
				addConnections(c1.getPeerList());
			    }
			}
		    }
		});
		connectionFinder.start();
	    }
	}

    }

    /**
     * @param peerList
     */
    public void addConnections(BigFastList<StringBuffer> peerList) {
	// Go through the peerList of a node we are directly connected to

	for (StringBuffer s : peerList) {
	    // Create a new connection object
	    Connection c = new Connection(s, this.getCurCoin());
	    // Check if the connection can be established
	    if (c.isValid() && !checkDuplicate(c)) {

		// Add a connection and break
		this.connections.add(c);
		// We break after only one successful connection is added to
		// ensure node diversity
		break;
	    } // otherwise try with the next IP
	}

    }

    /**
     * Checks if the specified connection already exists by comparing IP
     * addresses
     * 
     * @param c
     * @return true if the connection is a duplicate, false otherwise
     */
    public boolean checkDuplicate(Connection c) {
	for (Connection b : this.connections) {
	    if (c.getStringIpAddress().toString()
		    .compareTo(b.getStringIpAddress().toString()) == 0) {
		return true;
	    }
	}
	return false;
    }

    /**
     * @return
     */
    public NioServer getServer() {
	return this.server;
    }

    /**
     * @return
     */
    public BigFastList<Connection> getConnections() {
	return this.connections;
    }

    /**
     * @return the curCoin
     */
    public Coin getCurCoin() {
	return curCoin;
    }

}
