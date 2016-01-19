/**
 * 
 */
package client;

import java.math.BigInteger;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;

import coin.WalletTransaction;
import net.miginfocom.swing.MigLayout;

/**
 * @author Amir Eslampanah
 * 
 */
public class OverviewPane extends JComponent {

    /**
     * 
     */
    private final JLabel balance, unconfirmed, transaction, recent;
    private String str_Balance, str_Unconfirmed, str_Transaction;

    private TransactionLabel lbl_TransactionRecent;

    /**
     * 
     */
    public OverviewPane() {
	this.balance = new JLabel("Balance:     "); //$NON-NLS-1$
	this.unconfirmed = new JLabel("Unconfirmed: "); //$NON-NLS-1$
	this.transaction = new JLabel("Number of Transactions: "); //$NON-NLS-1$
	this.recent = new JLabel("Recent Transactions"); //$NON-NLS-1$

	this.setLayout(new MigLayout());

	this.add(this.balance, "wrap");
	this.add(this.unconfirmed, "wrap");
	this.add(this.transaction);

	this.add(this.recent, "cell 2 0 , gapleft 300");
	// this.add(lbl_TransactionRecent, "cell 2 1 , gapleft 300");

    }

    /**
     * @param bal
     */
    public void setBalance(BigInteger bal) {
	this.str_Balance = bal.toString();
    }

    /**
     * @param unconfirmed
     */
    public void setUncofirmed(BigInteger unconfirmed) {
	this.str_Unconfirmed = unconfirmed.toString();
    }

    /**
     * @param trans
     */
    public void setTransactions(BigInteger trans) {
	this.str_Transaction = trans.toString();
    }

    /**
     * @param trans
     */
    public void setRecent(ArrayList<WalletTransaction> trans) {
	this.lbl_TransactionRecent = new TransactionLabel(trans);
    }

    /**
     * @return the str_Balance
     */
    public String getStr_Balance() {
	return this.str_Balance;
    }

    /**
     * @param str_Balance
     *            the str_Balance to set
     */
    public void setStr_Balance(String str_Balance) {
	this.str_Balance = str_Balance;
    }

    /**
     * @return the str_Unconfirmed
     */
    public String getStr_Unconfirmed() {
	return this.str_Unconfirmed;
    }

    /**
     * @param str_Unconfirmed
     *            the str_Unconfirmed to set
     */
    public void setStr_Unconfirmed(String str_Unconfirmed) {
	this.str_Unconfirmed = str_Unconfirmed;
    }

    /**
     * @return the str_Transaction
     */
    public String getStr_Transaction() {
	return this.str_Transaction;
    }

    /**
     * @param str_Transaction
     *            the str_Transaction to set
     */
    public void setStr_Transaction(String str_Transaction) {
	this.str_Transaction = str_Transaction;
    }

    /**
     * @return the lbl_TransactionRecent
     */
    public TransactionLabel getLbl_TransactionRecent() {
	return this.lbl_TransactionRecent;
    }

    /**
     * @param lbl_TransactionRecent
     *            the lbl_TransactionRecent to set
     */
    public void setLbl_TransactionRecent(
	    TransactionLabel lbl_TransactionRecent) {
	this.lbl_TransactionRecent = lbl_TransactionRecent;
    }

    /**
     * @return the balance
     */
    public JLabel getBalance() {
	return this.balance;
    }

    /**
     * @return the unconfirmed
     */
    public JLabel getUnconfirmed() {
	return this.unconfirmed;
    }

    /**
     * @return the transaction
     */
    public JLabel getTransaction() {
	return this.transaction;
    }

    /**
     * @return the recent
     */
    public JLabel getRecent() {
	return this.recent;
    }

}
