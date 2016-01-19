package coin;

import java.math.BigDecimal;
import java.util.Date;

import client.TransactionDetails;

/**
 * @author Amir Eslampanah
 * 
 */
public class WalletTransaction {

    private Date dateTime;
    private StringBuffer type;
    private StringBuffer address;
    private TransactionDetails details;

    private BigDecimal value;

    /**
     * 
     */
    public WalletTransaction() {
    }

    /**
     * @param dateTime1
     * @param type1
     * @param address1
     * @param value1
     * @param details1
     */
    public WalletTransaction(Date dateTime1, StringBuffer type1,
	    StringBuffer address1, BigDecimal value1,
	    TransactionDetails details1) {
	this.dateTime = dateTime1;
	this.type = type1;
	this.address = address1;
	this.value = value1;
	this.details = details1;
    }

    /**
     * @return the dateTime
     */
    public Date getDateTime() {
	return this.dateTime;
    }

    /**
     * @param dateTime1
     *            the dateTime to set
     */
    public void setDateTime(Date dateTime1) {
	this.dateTime = dateTime1;
    }

    /**
     * @return the type
     */
    public StringBuffer getType() {
	return this.type;
    }

    /**
     * @param type1
     *            the type to set
     */
    public void setType(StringBuffer type1) {
	this.type = type1;
    }

    /**
     * @return the address
     */
    public StringBuffer getAddress() {
	return this.address;
    }

    /**
     * @param address1
     *            the address to set
     */
    public void setAddress(StringBuffer address1) {
	this.address = address1;
    }

    /**
     * @return the details
     */
    public TransactionDetails getDetails() {
	return this.details;
    }

    /**
     * @param details1
     *            the details to set
     */
    public void setDetails(TransactionDetails details1) {
	this.details = details1;
    }

    /**
     * @return the value
     */
    public BigDecimal getValue() {
	return this.value;
    }

    /**
     * @param value1
     *            the value to set
     */
    public void setValue(BigDecimal value1) {
	this.value = value1;
    }

}
