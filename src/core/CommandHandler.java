package core;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import client.BlankPane;
import client.SignWindow;
import coin.AddressKeyPair;
import coin.Coin;
import net.miginfocom.swing.MigLayout;
import util.Exceptions.AddressFormatException;
import util.Exceptions.WrongNetworkException;
import util.crypto.Address;

/**
 * @author Amir Eslampanah
 * 
 */
public class CommandHandler {

    private final Coin curCoin;

    /**
     * 
     */
    public CommandHandler(Coin coin) {
	this.curCoin = coin;
    }

    /**
     * @param action
     */
    public void guiCommand(String action) {

	if (action.contains("Send Pane: Choose address")) { //$NON-NLS-1$
	    // Open address book pane as a JDialog to a new JFrame
	    JDialog dialog = new JDialog();
	    dialog.setLayout(new MigLayout());

	    dialog.setTitle("Choose Sending Address"); //$NON-NLS-1$

	    BlankPane addressBook = new BlankPane("Choose Sending Address", //$NON-NLS-1$
		    dialog);
	    addressBook.setLayout(new MigLayout());

	    addressBook.add(Main.getGui().getCurAddressPane());

	    // Add an OK button to the bottom of the JDialog
	    JButton btn_okButton = new JButton("OK"); //$NON-NLS-1$

	    btn_okButton.addActionListener(addressBook);

	    addressBook.add(btn_okButton);

	    dialog.getContentPane().add(addressBook, "wrap"); //$NON-NLS-1$

	    dialog.pack();

	    dialog.setVisible(true);
	} else if (action.contains("Send Pane: Paste address")) { //$NON-NLS-1$
	    // Get clipboard data

	    String data = ""; //$NON-NLS-1$
	    try {
		data = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
			.getData(DataFlavor.stringFlavor);
	    } catch (HeadlessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (UnsupportedFlavorException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    // Paste address from system clipboard into sendPane's address field
	    Main.getGui().getCurSendPane().getTxt_address().setText(data);

	    // Set focus to label
	    Main.getGui().getCurSendPane().getTxt_label()
		    .requestFocusInWindow();

	} else if (action.contains("Send Pane: Clear address field")) { //$NON-NLS-1$
	    // Clear sendPane's address field
	    Main.getGui().getCurSendPane().getTxt_address().setText(""); //$NON-NLS-1$

	    // Set focus to address
	    Main.getGui().getCurSendPane().getTxt_address()
		    .requestFocusInWindow();

	} else if (action.contains("Send Pane: Add Recipient")) { //$NON-NLS-1$
	    // Add a new recipient address to our address book using the address
	    // and label field

	    AddressKeyPair pair = new AddressKeyPair(true);

	    pair.setLabel(new StringBuffer(
		    Main.getGui().getCurSendPane().getTxt_label().getText()));
	    try {
		pair.setAddress(new Address(null, Main.getGui().getCurSendPane()
			.getTxt_address().getText()));
	    } catch (WrongNetworkException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (AddressFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    Main.getDaemon().getCoinDetails().getCoinWallet()
		    .getAddressKeyPairs().add(pair);

	} else if (action.contains("Send Pane: Clear All")) { //$NON-NLS-1$
	    // Clear all fields in the sendPane

	    Main.getGui().getCurSendPane().getTxt_address().setText(""); //$NON-NLS-1$
	    Main.getGui().getCurSendPane().getTxt_amount().setText(""); //$NON-NLS-1$
	    Main.getGui().getCurSendPane().getTxt_label().setText(""); //$NON-NLS-1$

	} else if (action.contains("Send Pane: Send")) { //$NON-NLS-1$
	    // Send amount in sendPane's amount field to the address in
	    // sendPane's address field

	    String[] command = { "send", //$NON-NLS-1$
		    Main.getGui().getCurSendPane().getTxt_address().getText(),
		    Main.getGui().getCurSendPane().getTxt_amount().getText(),
		    Main.getGui().getCurSendPane().getTxt_label().getText() };

	    this.processCommand(command);

	} else if (action.contains("Receive Pane: New Address")) { //$NON-NLS-1$

	    //
	    AddressKeyPair pair = new AddressKeyPair(true);

	    // Create a new JDialog box
	    JDialog dialog = new JDialog();
	    dialog.setLayout(new MigLayout());

	    dialog.setTitle("Add Receiving Address"); //$NON-NLS-1$

	    // Create label and address labels
	    JLabel lbl_label = new JLabel("Label"); //$NON-NLS-1$
	    JLabel lbl_address = new JLabel("Address"); //$NON-NLS-1$

	    // Create label and address fields
	    JTextField txt_label = new JTextField();
	    JTextField txt_address = new JTextField();

	    txt_address.setEditable(false);

	    // Create an OK and Cancel button
	    JButton btn_ok = new JButton("OK"); //$NON-NLS-1$
	    JButton btn_cancel = new JButton("Cancel"); //$NON-NLS-1$

	    Object[] items = { txt_label, txt_address };

	    // Create our pane
	    BlankPane addReceiving = new BlankPane("Add Receiving Address", //$NON-NLS-1$
		    dialog, items);
	    addReceiving.setLayout(new MigLayout());

	    btn_ok.addActionListener(addReceiving);
	    btn_cancel.addActionListener(addReceiving);

	    Dimension fieldSize = new Dimension(300, 20);

	    txt_label.setPreferredSize(fieldSize);
	    txt_address.setPreferredSize(fieldSize);

	    addReceiving.add(lbl_label);
	    addReceiving.add(txt_label, "wrap"); //$NON-NLS-1$

	    addReceiving.add(lbl_address);
	    addReceiving.add(txt_address, "wrap"); //$NON-NLS-1$

	    addReceiving.add(btn_ok);
	    addReceiving.add(btn_cancel, "wrap"); //$NON-NLS-1$

	    Dimension windowSize = new Dimension(465, 155);

	    dialog.setPreferredSize(windowSize);

	    dialog.add(addReceiving);

	    dialog.pack();

	    dialog.setLocationRelativeTo(Main.getGui().getCurAddressPane());

	    dialog.setVisible(true);
	} else if (action.contains("Receive Pane: Copy Address")) { //$NON-NLS-1$

	    // Access system clipboard
	    Clipboard clipBoard = Toolkit.getDefaultToolkit()
		    .getSystemClipboard();

	    // Check if more than one row selected
	    if (Main.getGui().getCurReceivePane().getTable_AddressBookRecv()
		    .getSelectedRowCount() == 1) {
		// Copy selected record's address to clipboard
		clipBoard.setContents(new StringSelection((String) Main.getGui()
			.getCurReceivePane().getMod_table()
			.getValueAt(Main.getGui().getCurReceivePane()
				.getTable_AddressBookRecv().getSelectedRow(),
				1)),
			null);
	    } else if (Main.getGui().getCurReceivePane()
		    .getTable_AddressBookRecv().getSelectedRowCount() >= 1) {
		int[] rows = Main.getGui().getCurReceivePane()
			.getTable_AddressBookRecv().getSelectedRows();

		StringBuffer addresses = new StringBuffer();

		for (int row : rows) {
		    addresses.append((String) Main.getGui().getCurReceivePane()
			    .getMod_table().getValueAt(row, 1));
		    addresses.append('\n');
		}

		clipBoard.setContents(new StringSelection(addresses.toString()),
			null);

	    }

	} else if (action.contains("Receive Pane: Show QR Code")) { //$NON-NLS-1$
	    // Show QR code in a new J-dialog for selected address/record

	} else if (action.contains("Receive Pane: Sign Message")) { //$NON-NLS-1$

	    // Open the Sign Window
	    SignWindow sw = Main.getGui().getCurSignWindow();
	    Dimension sz_sw = new Dimension(720, 450);
	    sw.setPreferredSize(sz_sw);
	    sw.pack();
	    sw.setLocationRelativeTo(Main.getGui());
	    sw.setAlwaysOnTop(true);
	    sw.setVisible(true);

	    // Fill address field with selected address
	    Main.getGui().getCurSignWindow().getTxt_addressSign()
		    .setText((String) Main.getGui().getCurReceivePane()
			    .getMod_table()
			    .getValueAt(Main.getGui().getCurReceivePane()
				    .getTable_AddressBookRecv()
				    .getSelectedRow(), 1));

	} else if (action.contains("Address Pane: New Address")) { //$NON-NLS-1$

	    //
	    AddressKeyPair pair = new AddressKeyPair(true);

	    // Create a new JDialog box
	    JDialog dialog = new JDialog();
	    dialog.setLayout(new MigLayout());

	    dialog.setTitle("Add Receiving Address"); //$NON-NLS-1$

	    // Create label and address labels
	    JLabel lbl_label = new JLabel("Label"); //$NON-NLS-1$
	    JLabel lbl_address = new JLabel("Address"); //$NON-NLS-1$

	    // Create label and address fields
	    JTextField txt_label = new JTextField();
	    JTextField txt_address = new JTextField();

	    txt_address.setEditable(false);

	    // Create an OK and Cancel button
	    JButton btn_ok = new JButton("OK"); //$NON-NLS-1$
	    JButton btn_cancel = new JButton("Cancel"); //$NON-NLS-1$

	    Object[] items = { txt_label, txt_address };

	    // Create our pane
	    BlankPane addReceiving = new BlankPane("Add Receiving Address", //$NON-NLS-1$
		    dialog, items);
	    addReceiving.setLayout(new MigLayout());

	    btn_ok.addActionListener(addReceiving);
	    btn_cancel.addActionListener(addReceiving);

	    Dimension fieldSize = new Dimension(300, 20);

	    txt_label.setPreferredSize(fieldSize);
	    txt_address.setPreferredSize(fieldSize);

	    addReceiving.add(lbl_label);
	    addReceiving.add(txt_label, "wrap"); //$NON-NLS-1$

	    addReceiving.add(lbl_address);
	    addReceiving.add(txt_address, "wrap"); //$NON-NLS-1$

	    addReceiving.add(btn_ok);
	    addReceiving.add(btn_cancel, "wrap"); //$NON-NLS-1$

	    Dimension windowSize = new Dimension(465, 155);

	    dialog.setPreferredSize(windowSize);

	    dialog.add(addReceiving);

	    dialog.pack();

	    dialog.setLocationRelativeTo(Main.getGui().getCurAddressPane());

	    dialog.setVisible(true);

	} else if (action.contains("Address Pane: Copy Address")) { //$NON-NLS-1$
	    // Copy selected record's address to clipboard

	    // Access system clipboard
	    Clipboard clipBoard = Toolkit.getDefaultToolkit()
		    .getSystemClipboard();

	    // Check if more than one row selected
	    if (Main.getGui().getCurAddressPane().getTable_AddressBookRecv()
		    .getSelectedRowCount() == 1) {
		// Copy selected record's address to clipboard
		clipBoard.setContents(new StringSelection((String) Main.getGui()
			.getCurAddressPane().getMod_table()
			.getValueAt(Main.getGui().getCurAddressPane()
				.getTable_AddressBookRecv().getSelectedRow(),
				1)),
			null);
	    } else if (Main.getGui().getCurAddressPane()
		    .getTable_AddressBookRecv().getSelectedRowCount() >= 1) {
		int[] rows = Main.getGui().getCurAddressPane()
			.getTable_AddressBookRecv().getSelectedRows();

		StringBuffer addresses = new StringBuffer();

		for (int row : rows) {
		    addresses.append((String) Main.getGui().getCurAddressPane()
			    .getMod_table().getValueAt(row, 1));
		    addresses.append('\n');
		}

		clipBoard.setContents(new StringSelection(addresses.toString()),
			null);

	    }

	} else if (action.contains("Address Pane: Show QR Code")) { //$NON-NLS-1$
	    // Show QR code in a new J-dialog for selected address/record

	} else if (action.contains("Address Pane: Verify Message")) { //$NON-NLS-1$
	    // Open the Sign Window
	    SignWindow sw = Main.getGui().getCurSignWindow();
	    Dimension sz_sw = new Dimension(720, 450);
	    sw.setPreferredSize(sz_sw);
	    // Select Verify Tab
	    sw.showVerifyPane();
	    sw.pack();
	    sw.setLocationRelativeTo(Main.getGui());
	    sw.setAlwaysOnTop(true);
	    sw.setVisible(true);

	    // Fill address field with selected address
	    Main.getGui().getCurSignWindow().getTxt_addressSign()
		    .setText((String) Main.getGui().getCurAddressPane()
			    .getMod_table()
			    .getValueAt(Main.getGui().getCurAddressPane()
				    .getTable_AddressBookRecv()
				    .getSelectedRow(), 1));
	} else if (action.contains("Address Pane: Delete")) { //$NON-NLS-1$
	    // Check if more than one row selected
	    if (Main.getGui().getCurAddressPane().getTable_AddressBookRecv()
		    .getSelectedRowCount() == 1) {
		// Remove row
		Main.getGui().getCurAddressPane().getMod_table()
			.removeRow(Main.getGui().getCurAddressPane()
				.getTable_AddressBookRecv().getSelectedRow());

	    } else if (Main.getGui().getCurAddressPane()
		    .getTable_AddressBookRecv().getSelectedRowCount() >= 1) {
		int[] rows = Main.getGui().getCurAddressPane()
			.getTable_AddressBookRecv().getSelectedRows();

		for (int row : rows) {
		    // Remove row
		    Main.getGui().getCurAddressPane().getMod_table()
			    .removeRow(row);
		}

	    }

	} else if (action.contains("Mining Pane: Start Mining")) { //$NON-NLS-1$
	    // Start mining with settings listed on miningPane
	    Main.getDaemon().getMiningHandler().start();

	} else if (action.contains("Sign Window: Choose address - sign")) { //$NON-NLS-1$
	    // Open address book pane as a JDialog to a new JFrame
	    JDialog dialog = new JDialog();
	    dialog.setLayout(new MigLayout());

	    dialog.setTitle("Choose Sending Address"); //$NON-NLS-1$

	    BlankPane addressBook = new BlankPane(
		    "Sign Window: Choose Sending Address", dialog); //$NON-NLS-1$
	    addressBook.setLayout(new MigLayout());

	    addressBook.add(Main.getGui().getCurAddressPane());

	    // Add an OK button to the bottom of the JDialog
	    JButton btn_okButton = new JButton("OK"); //$NON-NLS-1$

	    btn_okButton.addActionListener(addressBook);

	    addressBook.add(btn_okButton);

	    dialog.getContentPane().add(addressBook, "wrap"); //$NON-NLS-1$

	    dialog.pack();

	    dialog.setVisible(true);

	} else if (action.contains("Sign Window: Paste address - sign")) { //$NON-NLS-1$
	    // Paste address from system clipboard to address field in
	    // signWindow - signTab
	    // Get clipboard data

	    String data = ""; //$NON-NLS-1$
	    try {
		data = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
			.getData(DataFlavor.stringFlavor);
	    } catch (HeadlessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (UnsupportedFlavorException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    // Paste address from system clipboard into sendPane's address field
	    Main.getGui().getCurSignWindow().getTxt_addressSign().setText(data);

	    // Set focus to message
	    Main.getGui().getCurSignWindow().getTxt_messageSign()
		    .requestFocusInWindow();

	} else if (action.contains("Sign Window: Copy signature - sign")) { //$NON-NLS-1$
	    // Copy generated signature from signWindow - signTab to system
	    // clipboard
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
		    new StringSelection(Main.getGui().getCurSignWindow()
			    .getTxt_signatureSign().getText()),
		    null);

	} else if (action.contains("Sign Window: Clear All")) { //$NON-NLS-1$
	    // Clear all fields in signWindow
	    Main.getGui().getCurSignWindow().getTxt_addressSign().setText("");
	    Main.getGui().getCurSignWindow().getTxt_messageSign().setText("");
	    Main.getGui().getCurSignWindow().getTxt_signatureSign().setText("");
	    Main.getGui().getCurSignWindow().getTxt_addressVerify().setText("");
	    Main.getGui().getCurSignWindow().getTxt_messageVerify().setText("");
	    Main.getGui().getCurSignWindow().getTxt_signatureVerify()
		    .setText("");

	} else if (action.contains("Sign Window: Sign Message")) { //$NON-NLS-1$
	    // Generate signature from fields in signWindow - signTab

	} else if (action.contains("Sign Window: Paste address - verify")) { //$NON-NLS-1$
	    // Paste an address from clipboard into signWindow - verifyTab's
	    // address field

	    // Get clipboard data

	    String data = ""; //$NON-NLS-1$
	    try {
		data = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
			.getData(DataFlavor.stringFlavor);
	    } catch (HeadlessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (UnsupportedFlavorException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    // Paste address from system clipboard into sendPane's address field
	    Main.getGui().getCurSignWindow().getTxt_addressVerify()
		    .setText(data);

	    // Set focus to message
	    Main.getGui().getCurSignWindow().getTxt_addressVerify()
		    .requestFocusInWindow();

	} else if (action.contains("Sign Window: Copy signature - verify")) { //$NON-NLS-1$
	    // Copy signature from field in signWindow - verifyTab

	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
		    new StringSelection(Main.getGui().getCurSignWindow()
			    .getTxt_signatureVerify().getText()),
		    null);

	} else if (action.contains("Sign Window: Verify Message")) { //$NON-NLS-1$
	    // Attempt to verify the message

	} else if (action.contains("Pass Window: OK")) { //$NON-NLS-1$

	    // Check if passwords match and if so replace current password with
	    // new password(reconfirm match)
	    // for currently active wallet
	    Main.getGui().getCurPassWindow().getTxt_enterPass();

	} else if (action.contains("Pass Window: Cancel")) { //$NON-NLS-1$
	    // Close change password window

	} else if (action.contains(
		"Options Window: Start GoldCoin (GLD) on system login")) { //$NON-NLS-1$
	    // CheckBox (invert previous setting)

	    // Set goldcoin to start on system login

	} else if (action
		.contains("Options Window: Detach database at shutdown")) { //$NON-NLS-1$
	    // CheckBox (invert previous setting)

	    // Doesn't do anything at the moment.

	} else if (action.contains("Options Window: OK")) { //$NON-NLS-1$
	    // Update all settings with values from fields

	    // Close window

	} else if (action.contains("Options Window: Cancel")) { //$NON-NLS-1$
	    // Close window

	} else if (action.contains("Options Window: Apply")) { //$NON-NLS-1$
	    // Update all settings with values from fields

	} else if (action.contains("Options Window: Map port using UPnP")) { //$NON-NLS-1$
	    // CheckBox (invert previous setting)

	    // Try to port forward automatically

	} else if (action
		.contains("Options Window: Connect through Socks proxy")) { //$NON-NLS-1$
	    // CheckBox (invert previous setting)

	    // Use the specified proxy

	} else if (action.contains(
		"Options Window: Minimize to the tray instead of the taskbar")) { //$NON-NLS-1$

	    // CheckBox (invert previous setting)

	    // When minimize button is pressed.. minimize to tray instead of the
	    // taskbar.

	} else if (action.contains("Options Window: Minimize on close")) { //$NON-NLS-1$

	    // CheckBox (invert previous setting)

	    // Minimize instead of closing when the close button is pressed

	} else if (action.contains(
		"Options Window: Display addresses in transaction list")) { //$NON-NLS-1$

	    // CheckBox (invert previous setting)

	    // Display addresses instead of labels in the transaction list
	} else if (action.contains("Debug Window: Open")) { //$NON-NLS-1$

	    // Opens the client logfile
	} else if (action.contains("Debug Window: Show")) { //$NON-NLS-1$

	    // Shows what commandline options the client was launched with
	} else if (action.contains("Debug Window: Testnet Mode")) { //$NON-NLS-1$
	    // CheckBox (invert previous setting)

	    // Enables testnet mode
	} else if (action.contains("Debug Window: Clear - console")) { //$NON-NLS-1$

	    // Clear's the console window on console tab for debug window

	} else if (action.contains("About Window: OK")) { //$NON-NLS-1$
	    // Close about window.
	}

	System.out.println("Command Received"); //$NON-NLS-1$
	System.out.println("Command: " + action); //$NON-NLS-1$

	// processCommand(command);
    }

    /**
     * @param command
     */
    public void processCommand(String[] command) {

	// Format for send is : send address amount [label]
	if (command[0].compareTo("send") == 0) { //$NON-NLS-1$

	}

    }

    /**
     * @return the curCoin
     */
    public Coin getCurCoin() {
	return curCoin;
    }

}
