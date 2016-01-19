package client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import core.Main;
import net.miginfocom.swing.MigLayout;

/**
 * @author Amir Eslampanah
 * 
 */
public class ReceivePane extends JComponent
	implements ActionListener, TableModelListener {

    JTable table_AddressBookRecv;
    JButton btn_newAddress, btn_copyAddress, btn_qrCode, btn_signMessage;

    DefaultTableModel mod_table;

    /**
     * 
     */
    public ReceivePane() {
	this.setLayout(new MigLayout());

	this.table_AddressBookRecv = new JTable();
	Dimension sz_table_AddressBookRecv = new Dimension(800, 400);
	this.table_AddressBookRecv.setPreferredSize(sz_table_AddressBookRecv);

	this.btn_newAddress = new JButton("New Address"); //$NON-NLS-1$
	this.btn_copyAddress = new JButton("Copy Address"); //$NON-NLS-1$
	this.btn_qrCode = new JButton("Show QR Code"); //$NON-NLS-1$
	this.btn_signMessage = new JButton("Sign Message"); //$NON-NLS-1$

	this.mod_table = new DefaultTableModel();

	this.table_AddressBookRecv.setModel(this.mod_table);

	this.mod_table.addColumn("Label"); //$NON-NLS-1$
	this.mod_table.addColumn("Address"); //$NON-NLS-1$

	// this.mod_table.addRow(initLabel);

	JScrollPane tableScrollPane = new JScrollPane(
		this.table_AddressBookRecv);
	tableScrollPane.setPreferredSize(sz_table_AddressBookRecv);

	this.btn_newAddress.addActionListener(this);
	this.btn_copyAddress.addActionListener(this);
	this.btn_qrCode.addActionListener(this);
	this.btn_signMessage.addActionListener(this);

	Dimension sz_newAddress = new Dimension(100, 40);
	this.btn_newAddress.setPreferredSize(sz_newAddress);

	Dimension sz_copyAddress = new Dimension(100, 40);
	this.btn_copyAddress.setPreferredSize(sz_copyAddress);

	Dimension sz_qrCode = new Dimension(100, 40);
	this.btn_qrCode.setPreferredSize(sz_qrCode);

	Dimension sz_signMessage = new Dimension(100, 40);
	this.btn_signMessage.setPreferredSize(sz_signMessage);

	ImageIcon img_newAddress = new ImageIcon("images/btn_addrecipient.png"); //$NON-NLS-1$

	img_newAddress = new ImageIcon(img_newAddress.getImage()
		.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	this.btn_newAddress.setIcon(img_newAddress);

	ImageIcon img_copy = new ImageIcon("images/copy.png"); //$NON-NLS-1$

	img_copy = new ImageIcon(img_copy.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_copyAddress.setIcon(img_copy);

	ImageIcon img_qrCode = new ImageIcon("images/qrcode.png"); //$NON-NLS-1$

	img_qrCode = new ImageIcon(img_qrCode.getImage().getScaledInstance(32,
		32, java.awt.Image.SCALE_SMOOTH));

	this.btn_qrCode.setIcon(img_qrCode);

	ImageIcon img_signMessage = new ImageIcon("images/sign.png"); //$NON-NLS-1$

	img_signMessage = new ImageIcon(img_signMessage.getImage()
		.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	this.btn_signMessage.setIcon(img_signMessage);

	this.table_AddressBookRecv.setToolTipText(
		"Here are your GoldCoin (GLD) addresses for receiving payments. You may want to give a different one to each sender so you can keep track of who is paying you."); //$NON-NLS-1$

	this.btn_newAddress.setToolTipText("Create a new address"); //$NON-NLS-1$
	this.btn_copyAddress.setToolTipText(
		"Copy the currently selected address to the system clipboard"); //$NON-NLS-1$
	this.btn_qrCode.setToolTipText(
		"Show QR Code for the currently selected address"); //$NON-NLS-1$
	this.btn_signMessage
		.setToolTipText("Sign a message to prove you own this address"); //$NON-NLS-1$

	this.add(tableScrollPane, "wrap"); //$NON-NLS-1$

	JPanel buttonsGroup = new JPanel();

	buttonsGroup.setLayout(new MigLayout());

	buttonsGroup.add(this.btn_newAddress);
	buttonsGroup.add(this.btn_copyAddress, "gapleft 10"); //$NON-NLS-1$
	buttonsGroup.add(this.btn_qrCode, "gapleft 10"); //$NON-NLS-1$
	buttonsGroup.add(this.btn_signMessage, "gapleft 10"); //$NON-NLS-1$

	this.mod_table.addTableModelListener(this);

	this.add(buttonsGroup);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String s = "Receive Pane: " + e.getActionCommand(); //$NON-NLS-1$

	System.out.println(s);
	Main.daemon.getCommandHandler().guiCommand(s);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
	String s = "Receive Pane: " + e.getSource(); //$NON-NLS-1$

	System.out.println(s);
	Main.daemon.getCommandHandler().guiCommand(s);

    }

    /**
     * @return the table_AddressBookRecv
     */
    public JTable getTable_AddressBookRecv() {
	return table_AddressBookRecv;
    }

    /**
     * @param table_AddressBookRecv
     *            the table_AddressBookRecv to set
     */
    public void setTable_AddressBookRecv(JTable table_AddressBookRecv) {
	this.table_AddressBookRecv = table_AddressBookRecv;
    }

    /**
     * @return the btn_newAddress
     */
    public JButton getBtn_newAddress() {
	return btn_newAddress;
    }

    /**
     * @param btn_newAddress
     *            the btn_newAddress to set
     */
    public void setBtn_newAddress(JButton btn_newAddress) {
	this.btn_newAddress = btn_newAddress;
    }

    /**
     * @return the btn_copyAddress
     */
    public JButton getBtn_copyAddress() {
	return btn_copyAddress;
    }

    /**
     * @param btn_copyAddress
     *            the btn_copyAddress to set
     */
    public void setBtn_copyAddress(JButton btn_copyAddress) {
	this.btn_copyAddress = btn_copyAddress;
    }

    /**
     * @return the btn_qrCode
     */
    public JButton getBtn_qrCode() {
	return btn_qrCode;
    }

    /**
     * @param btn_qrCode
     *            the btn_qrCode to set
     */
    public void setBtn_qrCode(JButton btn_qrCode) {
	this.btn_qrCode = btn_qrCode;
    }

    /**
     * @return the btn_signMessage
     */
    public JButton getBtn_signMessage() {
	return btn_signMessage;
    }

    /**
     * @param btn_signMessage
     *            the btn_signMessage to set
     */
    public void setBtn_signMessage(JButton btn_signMessage) {
	this.btn_signMessage = btn_signMessage;
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
