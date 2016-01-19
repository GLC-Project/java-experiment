package client;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import core.Main;
import net.miginfocom.swing.MigLayout;

/**
 * @author Amir Eslampanah
 * 
 */
public class PassWindow extends JDialog implements ActionListener {

    private final JLabel lbl_message, lbl_enterPass, lbl_newPass,
	    lbl_confirmPass;
    private final JTextField txt_enterPass, txt_newPass, txt_confirmPass;

    private final JButton btn_ok, btn_cancel;

    /**
     * 
     */
    public PassWindow() {
	this.setLayout(new MigLayout());

	// Favicon
	ImageIcon img_logo = new ImageIcon("images/goldcoin.png"); //$NON-NLS-1$
	Image img = img_logo.getImage();

	ImageIcon img_logo1 = new ImageIcon(
		img.getScaledInstance(256, 256, java.awt.Image.SCALE_SMOOTH));
	ImageIcon img_logo2 = new ImageIcon(
		img.getScaledInstance(128, 128, java.awt.Image.SCALE_SMOOTH));
	ImageIcon img_logo3 = new ImageIcon(
		img.getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH));
	ImageIcon img_logo4 = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));
	ImageIcon img_logo5 = new ImageIcon(
		img.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH));

	ArrayList<Image> imageList = new ArrayList<Image>();

	imageList.add(img_logo.getImage());
	imageList.add(img_logo1.getImage());
	imageList.add(img_logo2.getImage());
	imageList.add(img_logo3.getImage());
	imageList.add(img_logo4.getImage());
	imageList.add(img_logo5.getImage());

	this.setIconImages(imageList);

	this.setTitle("Change Passphrase"); //$NON-NLS-1$

	this.lbl_message = new JLabel(
		"Enter the old and new passphrase to the wallet."); //$NON-NLS-1$
	this.lbl_enterPass = new JLabel("Enter passphrase"); //$NON-NLS-1$
	this.lbl_newPass = new JLabel("New passphrase"); //$NON-NLS-1$
	this.lbl_confirmPass = new JLabel("Repeat new passphrase"); //$NON-NLS-1$

	this.txt_enterPass = new JTextField();
	this.txt_newPass = new JTextField();
	this.txt_confirmPass = new JTextField();

	this.btn_ok = new JButton("OK"); //$NON-NLS-1$
	this.btn_cancel = new JButton("Cancel"); //$NON-NLS-1$

	Dimension sz_txt_enterPass = new Dimension(800, 30);
	Dimension sz_txt_newPass = new Dimension(800, 30);
	Dimension sz_txt_confirmPass = new Dimension(800, 30);
	Dimension sz_btn_ok = new Dimension(100, 30);
	Dimension sz_btn_cancel = new Dimension(100, 30);

	this.txt_enterPass.setPreferredSize(sz_txt_enterPass);
	this.txt_newPass.setPreferredSize(sz_txt_newPass);
	this.txt_confirmPass.setPreferredSize(sz_txt_confirmPass);
	this.btn_ok.setPreferredSize(sz_btn_ok);
	this.btn_cancel.setPreferredSize(sz_btn_cancel);

	ImageIcon img_ok = new ImageIcon("images/ok.png"); //$NON-NLS-1$
	img_ok = new ImageIcon(img_ok.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_ok.setIcon(img_ok);

	ImageIcon img_cancel = new ImageIcon("images/exit.png"); //$NON-NLS-1$
	img_cancel = new ImageIcon(img_cancel.getImage().getScaledInstance(32,
		32, java.awt.Image.SCALE_SMOOTH));

	this.btn_cancel.setIcon(img_cancel);

	this.txt_enterPass.setToolTipText("Enter your current password"); //$NON-NLS-1$
	this.txt_newPass.setToolTipText("Enter your new password"); //$NON-NLS-1$
	this.txt_confirmPass.setToolTipText("Enter your new password again"); //$NON-NLS-1$
	this.btn_ok.setToolTipText("OK"); //$NON-NLS-1$
	this.btn_cancel.setToolTipText("Cancel"); //$NON-NLS-1$

	this.btn_cancel.addActionListener(this);
	this.btn_ok.addActionListener(this);

	this.add(this.lbl_message, "wrap"); //$NON-NLS-1$
	this.add(this.lbl_enterPass);
	this.add(this.txt_enterPass, "wrap"); //$NON-NLS-1$
	this.add(this.lbl_newPass);
	this.add(this.txt_newPass, "wrap"); //$NON-NLS-1$
	this.add(this.lbl_confirmPass);
	this.add(this.txt_confirmPass, "wrap"); //$NON-NLS-1$

	this.add(this.btn_ok, "gapleft 200"); //$NON-NLS-1$
	this.add(this.btn_cancel);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String s = "Pass Window: " + e.getActionCommand(); //$NON-NLS-1$

	System.out.println(s);
	Main.daemon.getCommandHandler().guiCommand(s);

    }

    /**
     * @return the lbl_message
     */
    public JLabel getLbl_message() {
	return lbl_message;
    }

    /**
     * @return the lbl_enterPass
     */
    public JLabel getLbl_enterPass() {
	return lbl_enterPass;
    }

    /**
     * @return the lbl_newPass
     */
    public JLabel getLbl_newPass() {
	return lbl_newPass;
    }

    /**
     * @return the lbl_confirmPass
     */
    public JLabel getLbl_confirmPass() {
	return lbl_confirmPass;
    }

    /**
     * @return the txt_enterPass
     */
    public JTextField getTxt_enterPass() {
	return txt_enterPass;
    }

    /**
     * @return the txt_newPass
     */
    public JTextField getTxt_newPass() {
	return txt_newPass;
    }

    /**
     * @return the txt_confirmPass
     */
    public JTextField getTxt_confirmPass() {
	return txt_confirmPass;
    }

    /**
     * @return the btn_ok
     */
    public JButton getBtn_ok() {
	return btn_ok;
    }

    /**
     * @return the btn_cancel
     */
    public JButton getBtn_cancel() {
	return btn_cancel;
    }

}
