/**
 * 
 */
package client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import core.Main;
import net.miginfocom.swing.MigLayout;

/**
 * @author Amir Eslampanah
 * 
 */
public class SendPane extends JComponent implements ActionListener {

    JLabel lbl_payTo, lbl_label, lbl_amount, lbl_balance;
    JButton btn_chooseAddress, btn_paste, btn_clear;
    JButton btn_addRecipient, btn_clearAll, btn_send;

    JTextField txt_address, txt_label, txt_amount;

    JComboBox combo_denomination;

    /**
     * Labels for send pane.
     */
    public SendPane() {
	this.setLayout(new MigLayout());

	this.lbl_payTo = new JLabel("Pay To:    "); //$NON-NLS-1$
	this.lbl_label = new JLabel("Label:       "); //$NON-NLS-1$
	this.lbl_amount = new JLabel("Amount:  "); //$NON-NLS-1$
	this.lbl_balance = new JLabel("Balance: "); //$NON-NLS-1$

	this.btn_chooseAddress = new JButton();
	this.btn_paste = new JButton();
	this.btn_clear = new JButton();

	Dimension sz_chooseAddress = new Dimension(20, 20);
	Dimension sz_paste = new Dimension(20, 20);
	Dimension sz_clear = new Dimension(20, 20);

	this.btn_chooseAddress.setPreferredSize(sz_chooseAddress);
	this.btn_paste.setPreferredSize(sz_paste);
	this.btn_clear.setPreferredSize(sz_clear);

	ImageIcon img_chooseAddress = new ImageIcon(
		"images/btn_addressbook.png"); //$NON-NLS-1$
	img_chooseAddress = new ImageIcon(img_chooseAddress.getImage()
		.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	this.btn_chooseAddress.setIcon(img_chooseAddress);

	ImageIcon img_paste = new ImageIcon("images/btn_paste.png"); //$NON-NLS-1$
	img_paste = new ImageIcon(img_paste.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_paste.setIcon(img_paste);

	ImageIcon img_clear = new ImageIcon("images/btn_clear.png"); //$NON-NLS-1$
	img_clear = new ImageIcon(img_clear.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_clear.setIcon(img_clear);

	this.btn_addRecipient = new JButton();
	this.btn_clearAll = new JButton();
	this.btn_send = new JButton();

	this.btn_addRecipient.setText("Add Recipient"); //$NON-NLS-1$
	this.btn_clearAll.setText("Clear All"); //$NON-NLS-1$
	this.btn_send.setText("Send"); //$NON-NLS-1$

	ImageIcon img_addRecipient = new ImageIcon(
		"images/btn_addrecipient.png"); //$NON-NLS-1$

	img_addRecipient = new ImageIcon(img_addRecipient.getImage()
		.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	this.btn_addRecipient.setIcon(img_addRecipient);

	this.btn_clearAll.setIcon(img_clear);

	ImageIcon img_send = new ImageIcon("images/btn_send.png"); //$NON-NLS-1$

	img_send = new ImageIcon(img_send.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_send.setIcon(img_send);

	Dimension sz_recipient = new Dimension(130, 40);
	Dimension sz_clearAll = new Dimension(100, 40);
	Dimension sz_send = new Dimension(50, 40);

	this.btn_addRecipient.setPreferredSize(sz_recipient);
	this.btn_clearAll.setPreferredSize(sz_clearAll);
	this.btn_send.setPreferredSize(sz_send);

	this.txt_address = new JTextField();
	this.txt_label = new JTextField();
	this.txt_amount = new JTextField();

	Dimension sz_address = new Dimension(400, 20);
	Dimension sz_label = new Dimension(400, 20);
	Dimension sz_amount = new Dimension(400, 20);

	this.txt_address.setPreferredSize(sz_address);
	this.txt_label.setPreferredSize(sz_label);
	this.txt_amount.setPreferredSize(sz_amount);

	this.combo_denomination = new JComboBox();

	this.txt_address
		.setToolTipText("Enter the address you wish to send coins to"); //$NON-NLS-1$
	this.txt_label
		.setToolTipText("Enter a label for this address (optional)"); //$NON-NLS-1$
	this.txt_amount.setToolTipText("Enter the amount you wish to send"); //$NON-NLS-1$

	this.btn_addRecipient
		.setToolTipText("Add the address to your address book"); //$NON-NLS-1$
	this.btn_chooseAddress
		.setToolTipText("Choose an address from your address book"); //$NON-NLS-1$
	this.btn_clear.setToolTipText("Clear address field"); //$NON-NLS-1$
	this.btn_clearAll.setToolTipText(
		"Clear all fields currently displayed on this window"); //$NON-NLS-1$
	this.btn_paste.setToolTipText("Paste an address from clipboard"); //$NON-NLS-1$
	this.btn_send.setToolTipText("Send coins"); //$NON-NLS-1$

	this.combo_denomination
		.setToolTipText("Select what denomination this amount is in"); //$NON-NLS-1$

	JPanel group1 = new JPanel();

	group1.setLayout(new MigLayout());

	group1.add(this.lbl_payTo);
	group1.add(this.txt_address);
	group1.add(this.btn_chooseAddress, "gapleft 0"); //$NON-NLS-1$
	group1.add(this.btn_paste, "gapleft 0"); //$NON-NLS-1$
	group1.add(this.btn_clear, "gapleft 0"); //$NON-NLS-1$

	this.add(group1, "wrap"); //$NON-NLS-1$

	JPanel group2 = new JPanel();

	group2.setLayout(new MigLayout());

	group2.add(this.lbl_label);
	group2.add(this.txt_label);

	this.add(group2, "wrap"); //$NON-NLS-1$

	JPanel group3 = new JPanel();

	group3.setLayout(new MigLayout());

	group3.add(this.lbl_amount);
	group3.add(this.txt_amount);
	group3.add(this.combo_denomination);

	this.add(group3, "wrap"); //$NON-NLS-1$

	JPanel group4 = new JPanel();

	group4.add(this.btn_addRecipient);
	group4.add(this.btn_clearAll);
	group4.add(this.lbl_balance);

	this.add(group4, "newline 220px"); //$NON-NLS-1$

	this.add(this.btn_send); // $NON-NLS-1$

	this.btn_chooseAddress.addActionListener(this);
	this.btn_paste.addActionListener(this);
	this.btn_clear.addActionListener(this);
	this.btn_addRecipient.addActionListener(this);
	this.btn_clearAll.addActionListener(this);
	this.btn_send.addActionListener(this);

	this.btn_chooseAddress.setActionCommand("Choose address"); //$NON-NLS-1$
	this.btn_paste.setActionCommand("Paste address"); //$NON-NLS-1$
	this.btn_clear.setActionCommand("Clear address field"); //$NON-NLS-1$

	this.combo_denomination.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String s = "Send Pane: " + e.getActionCommand(); //$NON-NLS-1$

	System.out.println(s);
	Main.daemon.getCommandHandler().guiCommand(s);

    }

    /**
     * @return the lbl_payTo
     */
    public JLabel getLbl_payTo() {
	return lbl_payTo;
    }

    /**
     * @param lbl_payTo
     *            the lbl_payTo to set
     */
    public void setLbl_payTo(JLabel lbl_payTo) {
	this.lbl_payTo = lbl_payTo;
    }

    /**
     * @return the lbl_label
     */
    public JLabel getLbl_label() {
	return lbl_label;
    }

    /**
     * @param lbl_label
     *            the lbl_label to set
     */
    public void setLbl_label(JLabel lbl_label) {
	this.lbl_label = lbl_label;
    }

    /**
     * @return the lbl_amount
     */
    public JLabel getLbl_amount() {
	return lbl_amount;
    }

    /**
     * @param lbl_amount
     *            the lbl_amount to set
     */
    public void setLbl_amount(JLabel lbl_amount) {
	this.lbl_amount = lbl_amount;
    }

    /**
     * @return the lbl_balance
     */
    public JLabel getLbl_balance() {
	return lbl_balance;
    }

    /**
     * @param lbl_balance
     *            the lbl_balance to set
     */
    public void setLbl_balance(JLabel lbl_balance) {
	this.lbl_balance = lbl_balance;
    }

    /**
     * @return the btn_chooseAddress
     */
    public JButton getBtn_chooseAddress() {
	return btn_chooseAddress;
    }

    /**
     * @param btn_chooseAddress
     *            the btn_chooseAddress to set
     */
    public void setBtn_chooseAddress(JButton btn_chooseAddress) {
	this.btn_chooseAddress = btn_chooseAddress;
    }

    /**
     * @return the btn_paste
     */
    public JButton getBtn_paste() {
	return btn_paste;
    }

    /**
     * @param btn_paste
     *            the btn_paste to set
     */
    public void setBtn_paste(JButton btn_paste) {
	this.btn_paste = btn_paste;
    }

    /**
     * @return the btn_clear
     */
    public JButton getBtn_clear() {
	return btn_clear;
    }

    /**
     * @param btn_clear
     *            the btn_clear to set
     */
    public void setBtn_clear(JButton btn_clear) {
	this.btn_clear = btn_clear;
    }

    /**
     * @return the btn_addRecipient
     */
    public JButton getBtn_addRecipient() {
	return btn_addRecipient;
    }

    /**
     * @param btn_addRecipient
     *            the btn_addRecipient to set
     */
    public void setBtn_addRecipient(JButton btn_addRecipient) {
	this.btn_addRecipient = btn_addRecipient;
    }

    /**
     * @return the btn_clearAll
     */
    public JButton getBtn_clearAll() {
	return btn_clearAll;
    }

    /**
     * @param btn_clearAll
     *            the btn_clearAll to set
     */
    public void setBtn_clearAll(JButton btn_clearAll) {
	this.btn_clearAll = btn_clearAll;
    }

    /**
     * @return the btn_send
     */
    public JButton getBtn_send() {
	return btn_send;
    }

    /**
     * @param btn_send
     *            the btn_send to set
     */
    public void setBtn_send(JButton btn_send) {
	this.btn_send = btn_send;
    }

    /**
     * @return the txt_address
     */
    public JTextField getTxt_address() {
	return txt_address;
    }

    /**
     * @param txt_address
     *            the txt_address to set
     */
    public void setTxt_address(JTextField txt_address) {
	this.txt_address = txt_address;
    }

    /**
     * @return the txt_label
     */
    public JTextField getTxt_label() {
	return txt_label;
    }

    /**
     * @param txt_label
     *            the txt_label to set
     */
    public void setTxt_label(JTextField txt_label) {
	this.txt_label = txt_label;
    }

    /**
     * @return the txt_amount
     */
    public JTextField getTxt_amount() {
	return txt_amount;
    }

    /**
     * @param txt_amount
     *            the txt_amount to set
     */
    public void setTxt_amount(JTextField txt_amount) {
	this.txt_amount = txt_amount;
    }

    /**
     * @return the combo_denomination
     */
    public JComboBox getCombo_denomination() {
	return combo_denomination;
    }

    /**
     * @param combo_denomination
     *            the combo_denomination to set
     */
    public void setCombo_denomination(JComboBox combo_denomination) {
	this.combo_denomination = combo_denomination;
    }
}
