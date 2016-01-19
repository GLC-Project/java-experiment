package net;

import java.math.BigInteger;

import util.BigFastList;

/**
 * This class is used for construction of alerts.
 * 
 * Author note: Turns out Alerts are complicated enough to merit their own class
 * :)
 * 
 * @author Amir Eslampanah
 * 
 */
public class Alert {

    /**
     * Format of the alert version
     * 
     * Legacy Note: Version 1 from protocol version 311 through at least
     * protocol version 70002
     */
    BigInteger version = BigInteger.ONE;

    /**
     * The time beyond which nodes should stop relaying this alert. Unix epoch
     * time format.
     */
    BigInteger relayUntil = null;

    /**
     * The time beyond which this alert is no longer in effect and should be
     * ignored.
     * 
     * Legacy Note: Unix epoch time format.
     */
    BigInteger expiration = null;

    /**
     * A unique ID number for this alert.
     */
    BigInteger ID = null;

    /**
     * All alerts with an ID number less than or equal to this number should be
     * canceled: deleted and not accepted in the future.
     */
    BigInteger cancel = null;

    /**
     * Alert IDs which should be canceled.
     * 
     * Legacy note: Each alert ID is a separate uint32_t number.
     */
    BigFastList<BigInteger> cancelAlertIDs = null;

    /**
     * This alert only applies to protocol versions greater than or equal to
     * this version. Nodes running other protocol versions should still relay
     * it.
     */
    BigInteger minVer = null;

    /**
     * This alert only applies to protocol versions less than or equal to this
     * version. Nodes running other protocol versions should still relay it.
     */
    BigInteger maxVer = null;

    /**
     * If this field is empty, it has no effect on the alert. If there is at
     * least one entry is this field, this alert only applies to programs with a
     * user agent that exactly matches one of the strings in this field.
     * 
     * Legacy Note: This field was originally called setSubVer; since BIP14, it
     * applies to user agent strings as defined in the version message.
     */
    BigFastList<StringBuffer> applicableUserAgents = null;

    /**
     * Relative priority compared to other alerts.
     */
    BigInteger priority = null;

    /**
     * A comment on the alert that is not displayed.
     */
    StringBuffer comment = null;

    /**
     * The alert message that is displayed to the user.
     */
    StringBuffer alert = null;

    /**
     * Reserved for future use.
     * 
     * Legacy Note: Originally called RPC Error.
     */
    StringBuffer reserved = null;

    /**
     * 
     */
    public Alert() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @return the version
     */
    public BigInteger getVersion() {
	return this.version;
    }

    /**
     * @param version1
     *            the version to set
     */
    public void setVersion(BigInteger version1) {
	this.version = version1;
    }

    /**
     * @return the relayUntil
     */
    public BigInteger getRelayUntil() {
	return this.relayUntil;
    }

    /**
     * @param relayUntil1
     *            the relayUntil to set
     */
    public void setRelayUntil(BigInteger relayUntil1) {
	this.relayUntil = relayUntil1;
    }

    /**
     * @return the expiration
     */
    public BigInteger getExpiration() {
	return this.expiration;
    }

    /**
     * @param expiration1
     *            the expiration to set
     */
    public void setExpiration(BigInteger expiration1) {
	this.expiration = expiration1;
    }

    /**
     * @return the iD
     */
    public BigInteger getID() {
	return this.ID;
    }

    /**
     * @param iD
     *            the iD to set
     */
    public void setID(BigInteger iD) {
	this.ID = iD;
    }

    /**
     * @return the cancel
     */
    public BigInteger getCancel() {
	return this.cancel;
    }

    /**
     * @param cancel1
     *            the cancel to set
     */
    public void setCancel(BigInteger cancel1) {
	this.cancel = cancel1;
    }

    /**
     * @return the minVer
     */
    public BigInteger getMinVer() {
	return this.minVer;
    }

    /**
     * @param minVer1
     *            the minVer to set
     */
    public void setMinVer(BigInteger minVer1) {
	this.minVer = minVer1;
    }

    /**
     * @return the maxVer
     */
    public BigInteger getMaxVer() {
	return this.maxVer;
    }

    /**
     * @param maxVer1
     *            the maxVer to set
     */
    public void setMaxVer(BigInteger maxVer1) {
	this.maxVer = maxVer1;
    }

    /**
     * @return the applicableUserAgents
     */
    public BigFastList<StringBuffer> getApplicableUserAgents() {
	return this.applicableUserAgents;
    }

    /**
     * @param applicableUserAgents1
     *            the applicableUserAgents to set
     */
    public void setApplicableUserAgents(
	    BigFastList<StringBuffer> applicableUserAgents1) {
	this.applicableUserAgents = applicableUserAgents1;
    }

    /**
     * @return the priority
     */
    public BigInteger getPriority() {
	return this.priority;
    }

    /**
     * @param priority1
     *            the priority to set
     */
    public void setPriority(BigInteger priority1) {
	this.priority = priority1;
    }

    /**
     * @return the comment
     */
    public StringBuffer getComment() {
	return this.comment;
    }

    /**
     * @param comment1
     *            the comment to set
     */
    public void setComment(StringBuffer comment1) {
	this.comment = comment1;
    }

    /**
     * @return the alert
     */
    public StringBuffer getAlert() {
	return this.alert;
    }

    /**
     * @param alert1
     *            the alert to set
     */
    public void setAlert(StringBuffer alert1) {
	this.alert = alert1;
    }

    /**
     * @return the reserved
     */
    public StringBuffer getReserved() {
	return this.reserved;
    }

    /**
     * @param reserved1
     *            the reserved to set
     */
    public void setReserved(StringBuffer reserved1) {
	this.reserved = reserved1;
    }

    /**
     * @return the cancelAlertIDs
     */
    public BigFastList<BigInteger> getCancelAlertIDs() {
	return this.cancelAlertIDs;
    }

    /**
     * @param cancelAlertIDs1
     *            the cancelAlertIDs to set
     */
    public void setCancelAlertIDs(BigFastList<BigInteger> cancelAlertIDs1) {
	this.cancelAlertIDs = cancelAlertIDs1;
    }

}
