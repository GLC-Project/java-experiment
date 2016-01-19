package client;

import java.util.ArrayList;

import javax.swing.JLabel;

import coin.WalletTransaction;
import net.miginfocom.swing.MigLayout;

/**
 * @author Amir Eslampanah
 * 
 */
public class TransactionLabel extends JLabel {

    /**
     * Shows recent transactions
     */
    public TransactionLabel(ArrayList<WalletTransaction> transactions) {
	// Get the last 3 transactions
	for (int x = 1; x <= 3; x++) {
	    WalletTransaction transact = transactions
		    .get(transactions.size() - x);
	    if (transactions.get(transactions.size() - x) != null) {
		JLabel date = new JLabel(transact.getDateTime().toString());
		JLabel coins = new JLabel(transact.getValue().toString());
		JLabel comment = new JLabel(transact.getValue().toString());

		this.setLayout(new MigLayout());

		this.add(date);
		this.add(coins, "wrap");
		this.add(comment);
	    }

	}
    }

    /**
     * @param transactions
     */
    public void update(ArrayList<WalletTransaction> transactions) {
	this.removeAll();
	// Get the last 3 transactions
	for (int x = 1; x <= 3; x++) {
	    WalletTransaction transact = transactions
		    .get(transactions.size() - x);
	    if (transactions.get(transactions.size() - x) != null) {
		JLabel date = new JLabel(transact.getDateTime().toString());
		JLabel coins = new JLabel(transact.getValue().toString());
		JLabel comment = new JLabel(transact.getValue().toString());

		this.setLayout(new MigLayout());

		this.add(date);
		this.add(coins, "wrap"); //$NON-NLS-1$
		this.add(comment);
	    }

	}
	this.repaint();
    }
}
