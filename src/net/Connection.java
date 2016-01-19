package net;

import java.math.BigInteger;

import coin.Coin;
import util.BigFastList;

/**
 * @author Amir Eslampanah
 * 
 */
public class Connection {

    private StringBuffer stringIpAddress = new StringBuffer();
    private BigInteger protocolVersion;
    private BigInteger port;
    private BigInteger services;
    private long lastConnectTime;
    private Coin curCoin;

    /**
     * @param stringIpAddress1
     * @param coin
     */
    public Connection(StringBuffer stringIpAddress1, Coin coin) {
	this.stringIpAddress = stringIpAddress1;
	this.curCoin = coin;
    }

    /**
     * @param stringIpAddress1
     * @param port1
     * @param protocolVersion1
     * @param services1
     * @param lastConnectTime1
     */
    public Connection(StringBuffer stringIpAddress1, BigInteger port1,
	    BigInteger protocolVersion1, BigInteger services1,
	    long lastConnectTime1) {
	this.stringIpAddress = stringIpAddress1;
	this.port = port1;
	this.protocolVersion = protocolVersion1;
	this.services = services1;
	this.lastConnectTime = lastConnectTime1;
    }

    /**
     * @return
     */
    public boolean isValid() {
	// TODO Auto-generated method stub
	return false;
    }

    /**
     * @return
     */
    public BigFastList<StringBuffer> getPeerList() {
	// TODO Auto-generated method stub
	return null;
    }

    /**
     * @return the stringIpAddress
     */
    public StringBuffer getStringIpAddress() {
	return this.stringIpAddress;
    }

    /**
     * @param stringIpAddress1
     *            the stringIpAddress to set
     */
    public void setStringIpAddress(StringBuffer stringIpAddress1) {
	this.stringIpAddress = stringIpAddress1;
    }

    /**
     * @return the protocolVersion
     */
    public BigInteger getProtocolVersion() {
	return this.protocolVersion;
    }

    /**
     * @param protocolVersion1
     *            the protocolVersion to set
     */
    public void setProtocolVersion(BigInteger protocolVersion1) {
	this.protocolVersion = protocolVersion1;
    }

    /**
     * @return the port
     */
    public BigInteger getPort() {
	return this.port;
    }

    /**
     * @param port1
     *            the port to set
     */
    public void setPort(BigInteger port1) {
	this.port = port1;
    }

    /**
     * @return the lastConnectTime
     */
    public long getLastConnectTime() {
	return this.lastConnectTime;
    }

    /**
     * @param lastConnectTime1
     *            the lastConnectTime to set
     */
    public void setLastConnectTime(long lastConnectTime1) {
	this.lastConnectTime = lastConnectTime1;
    }

    /**
     * @return the services
     */
    public BigInteger getServices() {
	return this.services;
    }

    /**
     * @param services1
     *            the services to set
     */
    public void setServices(BigInteger services1) {
	this.services = services1;
    }

    /**
     * @return the curCoin
     */
    public Coin getCurCoin() {
	return this.curCoin;
    }

    /**
     * @param coin
     *            the curCoin to set
     */
    public void setCurCoin(Coin coin) {
	this.curCoin = coin;
    }

}
