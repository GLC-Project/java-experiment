package client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Amir Eslampanah
 * 
 */
public class TransactionDetails {

    private BigInteger confirms;
    private Date date;
    private StringBuffer from, to, txId;
    private BigDecimal credit, netAmount;

    /**
     * 
     */
    public TransactionDetails() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param confirms1
     * @param date1
     * @param from1
     * @param to1
     * @param txId1
     * @param credit1
     * @param netAmount1
     */
    public TransactionDetails(BigInteger confirms1, Date date1,
	    StringBuffer from1, StringBuffer to1, StringBuffer txId1,
	    BigDecimal credit1, BigDecimal netAmount1) {
	this.confirms = confirms1;
	this.date = date1;
	this.from = from1;
	this.to = to1;
	this.txId = txId1;
	this.credit = credit1;
	this.netAmount = netAmount1;

    }

    /**
     * @return the confirms
     */
    public BigInteger getConfirms() {
	return this.confirms;
    }

    /**
     * @param confirms
     *            the confirms to set
     */
    public void setConfirms(BigInteger confirms) {
	this.confirms = confirms;
    }

    /**
     * @return the date
     */
    public Date getDate() {
	return this.date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(Date date) {
	this.date = date;
    }

    /**
     * @return the from
     */
    public StringBuffer getFrom() {
	return this.from;
    }

    /**
     * @param from
     *            the from to set
     */
    public void setFrom(StringBuffer from) {
	this.from = from;
    }

    /**
     * @return the to
     */
    public StringBuffer getTo() {
	return this.to;
    }

    /**
     * @param to
     *            the to to set
     */
    public void setTo(StringBuffer to) {
	this.to = to;
    }

    /**
     * @return the txId
     */
    public StringBuffer getTxId() {
	return this.txId;
    }

    /**
     * @param txId
     *            the txId to set
     */
    public void setTxId(StringBuffer txId) {
	this.txId = txId;
    }

    /**
     * @return the credit
     */
    public BigDecimal getCredit() {
	return this.credit;
    }

    /**
     * @param credit
     *            the credit to set
     */
    public void setCredit(BigDecimal credit) {
	this.credit = credit;
    }

    /**
     * @return the netAmount
     */
    public BigDecimal getNetAmount() {
	return this.netAmount;
    }

    /**
     * @param netAmount
     *            the netAmount to set
     */
    public void setNetAmount(BigDecimal netAmount) {
	this.netAmount = netAmount;
    }

}
