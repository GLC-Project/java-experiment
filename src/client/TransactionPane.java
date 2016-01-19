package client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import core.Main;
import net.miginfocom.swing.MigLayout;

/**
 * @author Amir Eslampanah
 * 
 */
public class TransactionPane extends JComponent
	implements ActionListener, TableModelListener {

    JTable table_transactions;
    JComboBox combo_sortDate, combo_sortType;

    JTextField txt_searchAddressOrLabel, txt_searchMinAmount;
    DefaultTableModel mod_table;

    /**
     * 
     */
    public TransactionPane() {
	this.setLayout(new MigLayout());
	this.combo_sortDate = new JComboBox();
	this.combo_sortType = new JComboBox();

	Dimension sz_combo = new Dimension(150, 40);

	this.combo_sortDate.setPreferredSize(sz_combo);
	this.combo_sortType.setPreferredSize(sz_combo);

	Dimension sz_txt_searchAddressOrLabel = new Dimension(150, 40);
	Dimension sz_txt_searchMinAmount = new Dimension(150, 40);

	this.txt_searchAddressOrLabel = new JTextField();
	this.txt_searchMinAmount = new JTextField();

	this.txt_searchAddressOrLabel
		.setPreferredSize(sz_txt_searchAddressOrLabel);
	this.txt_searchMinAmount.setPreferredSize(sz_txt_searchMinAmount);

	this.table_transactions = new JTable();

	Dimension sz_table_transactions = new Dimension(800, 400);

	this.table_transactions.setPreferredSize(sz_table_transactions);

	this.mod_table = new DefaultTableModel();

	this.table_transactions.setModel(this.mod_table);

	this.mod_table.addColumn("Status"); //$NON-NLS-1$
	this.mod_table.addColumn("Date"); //$NON-NLS-1$
	this.mod_table.addColumn("Type"); //$NON-NLS-1$
	this.mod_table.addColumn("Address"); //$NON-NLS-1$
	this.mod_table.addColumn("Amount"); //$NON-NLS-1$

	// this.mod_table.addRow(initLabel);

	JScrollPane tableScrollPane = new JScrollPane(this.table_transactions);
	tableScrollPane.setPreferredSize(sz_table_transactions);

	JPanel topBar = new JPanel();

	topBar.setLayout(new MigLayout());

	topBar.add(this.combo_sortDate, "gapleft 150"); //$NON-NLS-1$
	topBar.add(this.combo_sortType);
	topBar.add(this.txt_searchAddressOrLabel);
	topBar.add(this.txt_searchMinAmount);

	this.combo_sortDate.setToolTipText("Select date range to sort by"); //$NON-NLS-1$
	this.combo_sortType.setToolTipText("Select transaction type"); //$NON-NLS-1$
	this.txt_searchAddressOrLabel.setToolTipText(
		"Enter an address or label to search for and hit enter"); //$NON-NLS-1$
	this.txt_searchMinAmount.setToolTipText(
		"Enter a minimum amount to search for and hit enter"); //$NON-NLS-1$
	this.table_transactions
		.setToolTipText("Your wallet transactions are listed here"); //$NON-NLS-1$

	this.add(topBar, "wrap");//$NON-NLS-1$

	this.add(tableScrollPane);

	this.combo_sortDate.addActionListener(this);
	this.combo_sortType.addActionListener(this);
	this.txt_searchAddressOrLabel.addActionListener(this);
	this.txt_searchMinAmount.addActionListener(this);

	this.mod_table.addTableModelListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String s = "Transaction Pane: " + e.getActionCommand(); //$NON-NLS-1$

	System.out.println(s);
	Main.daemon.getCommandHandler().guiCommand(s);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
	String s = "Transaction Pane Table: " + e.getSource(); //$NON-NLS-1$

	System.out.println(s);
	Main.daemon.getCommandHandler().guiCommand(s);

    }

    /**
     * @return the table_transactions
     */
    public JTable getTable_transactions() {
	return table_transactions;
    }

    /**
     * @param table_transactions
     *            the table_transactions to set
     */
    public void setTable_transactions(JTable table_transactions) {
	this.table_transactions = table_transactions;
    }

    /**
     * @return the combo_sortDate
     */
    public JComboBox getCombo_sortDate() {
	return combo_sortDate;
    }

    /**
     * @param combo_sortDate
     *            the combo_sortDate to set
     */
    public void setCombo_sortDate(JComboBox combo_sortDate) {
	this.combo_sortDate = combo_sortDate;
    }

    /**
     * @return the combo_sortType
     */
    public JComboBox getCombo_sortType() {
	return combo_sortType;
    }

    /**
     * @param combo_sortType
     *            the combo_sortType to set
     */
    public void setCombo_sortType(JComboBox combo_sortType) {
	this.combo_sortType = combo_sortType;
    }

    /**
     * @return the txt_searchAddressOrLabel
     */
    public JTextField getTxt_searchAddressOrLabel() {
	return txt_searchAddressOrLabel;
    }

    /**
     * @param txt_searchAddressOrLabel
     *            the txt_searchAddressOrLabel to set
     */
    public void setTxt_searchAddressOrLabel(
	    JTextField txt_searchAddressOrLabel) {
	this.txt_searchAddressOrLabel = txt_searchAddressOrLabel;
    }

    /**
     * @return the txt_searchMinAmount
     */
    public JTextField getTxt_searchMinAmount() {
	return txt_searchMinAmount;
    }

    /**
     * @param txt_searchMinAmount
     *            the txt_searchMinAmount to set
     */
    public void setTxt_searchMinAmount(JTextField txt_searchMinAmount) {
	this.txt_searchMinAmount = txt_searchMinAmount;
    }

    /**
     * @return the mod_table
     */
    public DefaultTableModel getMod_table() {
	return mod_table;
    }

    /**
     * @param mod_table
     *            the mod_table to set
     */
    public void setMod_table(DefaultTableModel mod_table) {
	this.mod_table = mod_table;
    }

}
