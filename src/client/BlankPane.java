package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import core.Main;

/**
 * @author Amir Eslampanah
 * 
 */
public class BlankPane extends JPanel implements ActionListener {

    private String paneName;
    private Object caller;
    private Object[] items;

    /**
     * @param paneName
     */
    public BlankPane(String paneName, Object caller) {
	this.setPaneName(paneName);
	this.setCaller(caller);
    }

    /**
     * @param paneName
     */
    public BlankPane(String paneName, Object caller, Object[] items) {
	this.setPaneName(paneName);
	this.setCaller(caller);
	this.setItems(items);

    }

    /**
     * @return the paneName
     */
    public String getPaneName() {
	return this.paneName;
    }

    /**
     * @param paneName
     *            the paneName to set
     */
    public void setPaneName(String paneName) {
	this.paneName = paneName;
    }

    /**
     * @return the caller
     */
    public Object getCaller() {
	return this.caller;
    }

    /**
     * @param caller
     *            the caller to set
     */
    public void setCaller(Object caller) {
	this.caller = caller;
    }

    /**
     * @return the items
     */
    public Object[] getItems() {
	return items;
    }

    /**
     * @param items
     *            the items to set
     */
    public void setItems(Object[] items) {
	this.items = items;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	// TODO Auto-generated method stub
	String s = "Blank Pane: " + this.getPaneName() + " : " //$NON-NLS-1$ //$NON-NLS-2$
		+ e.getActionCommand();

	System.out.println(s);

	if (this.getPaneName().compareTo("Choose Sending Address") == 0) { //$NON-NLS-1$
	    if (e.getActionCommand().compareTo("OK") == 0) { //$NON-NLS-1$
		try {
		    // Copy selected address and label from window on OK press
		    // (if
		    // any
		    // selected)
		    // Paste into fields for address and label on sendPane

		    int rowValue = Main.getGui().getCurAddressPane()
			    .getTable_AddressBookRecv().getSelectedRow();

		    Main.getGui().getCurSendPane().getTxt_label()
			    .setText((String) Main.getGui().getCurAddressPane()
				    .getTable_AddressBookRecv()
				    .getValueAt(rowValue, 0));
		    Main.getGui().getCurSendPane().getTxt_address()
			    .setText((String) Main.getGui().getCurAddressPane()
				    .getTable_AddressBookRecv()
				    .getValueAt(rowValue, 1));
		} catch (Exception m) {

		}

		// Set selected field to amount

		Main.getGui().getCurSendPane().getTxt_amount()
			.requestFocusInWindow();

		// Close the dialog box
		JDialog d = (JDialog) this.getCaller();
		d.setVisible(false);

		// Dispose of dialog box
		d.dispose();

	    }
	} else if (this.getPaneName().compareTo("Add Receiving Address") == 0) { //$NON-NLS-1$

	    if (e.getActionCommand().compareTo("OK") == 0) { //$NON-NLS-1$

		// Convert both items' texts back into strings
		String[] strings = { (((JTextField) this.items[0]).getText()),
			(((JTextField) this.items[1]).getText()) };

		// On OK add both fields as a single record to the tables
		Main.getGui().getCurReceivePane().getMod_table()
			.addRow(strings);
		Main.getGui().getCurAddressPane().getMod_table()
			.addRow(strings);

		// Update table
		Main.getGui().getCurReceivePane().updateUI();
		Main.getGui().getCurAddressPane().updateUI();

		// Close the dialog box
		JDialog d = (JDialog) this.getCaller();
		d.setVisible(false);

		// Dispose of dialog box
		d.dispose();

	    } else if (e.getActionCommand().compareTo("Cancel") == 0) { //$NON-NLS-1$
		// Close the dialog box
		JDialog d = (JDialog) this.getCaller();
		d.setVisible(false);

		// Dispose of dialog box
		d.dispose();
	    }

	} else if (this.getPaneName()
		.compareTo("Sign Window: Choose Sending Address") == 0) { //$NON-NLS-1$

	    if (e.getActionCommand().compareTo("OK") == 0) { //$NON-NLS-1$
		try {
		    // Copy selected address from window on OK press
		    // (if
		    // any
		    // selected)
		    // Paste into fields for address on signWindow

		    int rowValue = Main.getGui().getCurAddressPane()
			    .getTable_AddressBookRecv().getSelectedRow();

		    Main.getGui().getCurSignWindow().getTxt_addressSign()
			    .setText((String) Main.getGui().getCurAddressPane()
				    .getTable_AddressBookRecv()
				    .getValueAt(rowValue, 1));
		} catch (Exception m) {

		}

		// Set selected field to message

		Main.getGui().getCurSignWindow().getTxt_messageSign()
			.requestFocusInWindow();

		// Close the dialog box
		JDialog d = (JDialog) this.getCaller();
		d.setVisible(false);

		// Dispose of dialog box
		d.dispose();

	    }

	}

    }
}
