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
public class AddressPane extends JComponent
	implements ActionListener, TableModelListener {

    JTable table_AddressBookRecv;
    JButton btn_newAddress, btn_copyAddress, btn_qrCode, btn_verifyMessage,
	    btn_delete;

    DefaultTableModel mod_table;

    /**
     * 
     */
    public AddressPane() {
	this.setLayout(new MigLayout());

	this.table_AddressBookRecv = new JTable();

	Dimension sz_table_AddressBookRecv = new Dimension(800, 400);
	this.table_AddressBookRecv.setPreferredSize(sz_table_AddressBookRecv);

	this.btn_newAddress = new JButton("New Address"); //$NON-NLS-1$
	this.btn_copyAddress = new JButton("Copy Address"); //$NON-NLS-1$
	this.btn_qrCode = new JButton("Show QR Code"); //$NON-NLS-1$
	this.btn_verifyMessage = new JButton("Verify Message"); //$NON-NLS-1$
	this.btn_delete = new JButton("Delete"); //$NON-NLS-1$

	Dimension sz_btn_newAddress = new Dimension(130, 40);
	Dimension sz_btn_copyAddress = new Dimension(130, 40);
	Dimension sz_btn_qrCode = new Dimension(150, 40);
	Dimension sz_btn_verifyMessage = new Dimension(150, 40);
	Dimension sz_btn_delete = new Dimension(100, 40);

	this.btn_newAddress.setPreferredSize(sz_btn_newAddress);
	this.btn_copyAddress.setPreferredSize(sz_btn_copyAddress);
	this.btn_qrCode.setPreferredSize(sz_btn_qrCode);
	this.btn_verifyMessage.setPreferredSize(sz_btn_verifyMessage);
	this.btn_delete.setPreferredSize(sz_btn_delete);

	this.btn_newAddress.addActionListener(this);
	this.btn_copyAddress.addActionListener(this);
	this.btn_qrCode.addActionListener(this);
	this.btn_verifyMessage.addActionListener(this);
	this.btn_delete.addActionListener(this);

	this.mod_table = new DefaultTableModel();

	this.table_AddressBookRecv.setModel(this.mod_table);

	this.mod_table.addColumn("Label"); //$NON-NLS-1$
	this.mod_table.addColumn("Address"); //$NON-NLS-1$

	// this.mod_table.addRow(initLabel);

	JScrollPane tableScrollPane = new JScrollPane(
		this.table_AddressBookRecv);
	tableScrollPane.setPreferredSize(sz_table_AddressBookRecv);

	this.table_AddressBookRecv
		.setToolTipText("Double click to edit address or label"); //$NON-NLS-1$
	this.btn_newAddress.setToolTipText("Create a new address"); //$NON-NLS-1$
	this.btn_copyAddress.setToolTipText(
		"Copies the currently selected address to clipboard"); //$NON-NLS-1$
	this.btn_qrCode.setToolTipText(
		"Show the QR code for the currently selected address"); //$NON-NLS-1$
	this.btn_verifyMessage.setToolTipText(
		"Verify the message to ensure it was signed by the currently selected address"); //$NON-NLS-1$
	this.btn_delete.setToolTipText(
		"Delete the currently selected address from the list. Only sending addresses can be deleted"); //$NON-NLS-1$

	ImageIcon img_btn_newAddress = new ImageIcon(
		"images/btn_addrecipient.png"); //$NON-NLS-1$

	img_btn_newAddress = new ImageIcon(img_btn_newAddress.getImage()
		.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	this.btn_newAddress.setIcon(img_btn_newAddress);

	ImageIcon img_btn_copyAddress = new ImageIcon("images/copy.png"); //$NON-NLS-1$

	img_btn_copyAddress = new ImageIcon(img_btn_copyAddress.getImage()
		.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	this.btn_copyAddress.setIcon(img_btn_copyAddress);

	ImageIcon img_btn_qrCode = new ImageIcon("images/qrcode.png"); //$NON-NLS-1$

	img_btn_qrCode = new ImageIcon(img_btn_qrCode.getImage()
		.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	this.btn_qrCode.setIcon(img_btn_qrCode);

	ImageIcon img_btn_verifyMessage = new ImageIcon("images/verify.png"); //$NON-NLS-1$

	img_btn_verifyMessage = new ImageIcon(img_btn_verifyMessage.getImage()
		.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	this.btn_verifyMessage.setIcon(img_btn_verifyMessage);

	ImageIcon img_btn_delete = new ImageIcon("images/btn_clear.png"); //$NON-NLS-1$

	img_btn_delete = new ImageIcon(img_btn_delete.getImage()
		.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	this.btn_delete.setIcon(img_btn_delete);

	this.add(tableScrollPane, "wrap"); //$NON-NLS-1$

	JPanel buttonGroup = new JPanel();

	buttonGroup.add(this.btn_newAddress);
	buttonGroup.add(this.btn_copyAddress, "gapleft 10"); //$NON-NLS-1$
	buttonGroup.add(this.btn_qrCode, "gapleft 10"); //$NON-NLS-1$
	buttonGroup.add(this.btn_verifyMessage, "gapleft 10"); //$NON-NLS-1$
	buttonGroup.add(this.btn_delete, "gapleft"); //$NON-NLS-1$

	this.mod_table.addTableModelListener(this);

	this.add(buttonGroup);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String s = "Address Pane: " + e.getActionCommand(); //$NON-NLS-1$

	System.out.println(s);
	Main.daemon.getCommandHandler().guiCommand(s);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
	// TODO Auto-generated method stub
	String s = "Address Pane Table: " + e.getSource(); //$NON-NLS-1$

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
     * @return the btn_verifyMessage
     */
    public JButton getBtn_verifyMessage() {
	return btn_verifyMessage;
    }

    /**
     * @param btn_verifyMessage
     *            the btn_verifyMessage to set
     */
    public void setBtn_verifyMessage(JButton btn_verifyMessage) {
	this.btn_verifyMessage = btn_verifyMessage;
    }

    /**
     * @return the btn_delete
     */
    public JButton getBtn_delete() {
	return btn_delete;
    }

    /**
     * @param btn_delete
     *            the btn_delete to set
     */
    public void setBtn_delete(JButton btn_delete) {
	this.btn_delete = btn_delete;
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
