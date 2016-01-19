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
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import core.Main;
import net.miginfocom.swing.MigLayout;

/**
 * @author Amir Eslampanah
 * 
 */
public class SignWindow extends JDialog implements ActionListener {

    JLabel lbl_messageSign;
    JTextField txt_addressSign;
    JButton btn_addressbookSign, btn_pasteSign;
    JTextField txt_messageSign, txt_signatureSign;
    JButton btn_copySign, btn_sign, btn_clearSign;

    JLabel lbl_messageVerify;
    JTextField txt_addressVerify;
    JButton btn_addressbookVerify, btn_pasteVerify;
    JTextField txt_messageVerify, txt_signatureVerify;
    JButton btn_copyVerify, btn_verify, btn_clearVerify;

    JTabbedPane win = new JTabbedPane();

    /**
     * 
     */
    public SignWindow() {

	this.setLayout(new MigLayout());

	JPanel signTab = new JPanel(new MigLayout());
	JPanel verifyTab = new JPanel(new MigLayout());

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

	this.setTitle("Signature - Sign/Verify Message"); //$NON-NLS-1$

	this.lbl_messageSign = new JLabel(
		"<html><body style=\'width: 500px\'> You can sign messages with your address to prove that you own them. Be careful not to sign anything vague, as phishing attacks may try to trick you into signing your identity over to them. Only sign fully-detailed statements you agree to."); //$NON-NLS-1$

	this.txt_addressSign = new JTextField();

	this.btn_addressbookSign = new JButton();
	this.btn_pasteSign = new JButton();

	this.txt_messageSign = new JTextField();

	this.txt_signatureSign = new JTextField();

	this.btn_copySign = new JButton();

	this.btn_sign = new JButton("Sign Message"); //$NON-NLS-1$
	this.btn_clearSign = new JButton("Clear All"); //$NON-NLS-1$

	ImageIcon img_address = new ImageIcon("images/btn_addressbook.png"); //$NON-NLS-1$
	img_address = new ImageIcon(img_address.getImage().getScaledInstance(32,
		32, java.awt.Image.SCALE_SMOOTH));

	this.btn_addressbookSign.setIcon(img_address);

	ImageIcon img_paste = new ImageIcon("images/btn_paste.png"); //$NON-NLS-1$
	img_paste = new ImageIcon(img_paste.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_pasteSign.setIcon(img_paste);

	ImageIcon img_copy = new ImageIcon("images/copy.png"); //$NON-NLS-1$
	img_copy = new ImageIcon(img_copy.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_copySign.setIcon(img_copy);

	ImageIcon img_sign = new ImageIcon("images/sign.png"); //$NON-NLS-1$
	img_sign = new ImageIcon(img_sign.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_sign.setIcon(img_sign);

	ImageIcon img_clear = new ImageIcon("images/btn_clear.png"); //$NON-NLS-1$
	img_clear = new ImageIcon(img_clear.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_clearSign.setIcon(img_clear);

	// 720, 450

	Dimension sz_txt_addressSign = new Dimension(500, 30);
	Dimension sz_btn_addressbookSign = new Dimension(32, 32);
	Dimension sz_btn_pasteSign = new Dimension(32, 32);
	Dimension sz_txt_messageSign = new Dimension(680, 200);
	Dimension sz_txt_signatureSign = new Dimension(500, 30);
	Dimension sz_btn_copySign = new Dimension(32, 32);
	Dimension sz_btn_sign = new Dimension(150, 32);
	Dimension sz_btn_clearSign = new Dimension(150, 32);

	this.txt_addressSign.setToolTipText(
		"Enter an address (e.g. DztaVPtUd2M5TwfifBUaq5bLrAscYeMbyd)"); //$NON-NLS-1$
	this.txt_signatureSign.setToolTipText(
		"Click \"Sign Message\" to generate a signature"); //$NON-NLS-1$
	this.txt_messageSign.setToolTipText(
		"Enter the message you would like to sign here"); //$NON-NLS-1$

	this.txt_addressSign.setPreferredSize(sz_txt_addressSign);
	this.btn_addressbookSign.setPreferredSize(sz_btn_addressbookSign);
	this.btn_pasteSign.setPreferredSize(sz_btn_pasteSign);
	this.txt_messageSign.setPreferredSize(sz_txt_messageSign);
	this.txt_signatureSign.setPreferredSize(sz_txt_signatureSign);
	this.btn_copySign.setPreferredSize(sz_btn_copySign);
	this.btn_sign.setPreferredSize(sz_btn_sign);
	this.btn_clearSign.setPreferredSize(sz_btn_clearSign);

	signTab.add(this.lbl_messageSign, "wrap"); //$NON-NLS-1$

	JPanel group1 = new JPanel();

	group1.add(this.txt_addressSign);
	group1.add(this.btn_addressbookSign);
	group1.add(this.btn_pasteSign);

	signTab.add(group1, "wrap"); //$NON-NLS-1$

	signTab.add(this.txt_messageSign, "wrap"); //$NON-NLS-1$

	JPanel group2 = new JPanel();

	group2.add(this.txt_signatureSign);
	group2.add(this.btn_copySign);

	signTab.add(group2, "wrap"); //$NON-NLS-1$

	JPanel group3 = new JPanel();

	group3.add(this.btn_sign);
	group3.add(this.btn_clearSign);

	signTab.add(group3, "wrap"); //$NON-NLS-1$

	this.lbl_messageVerify = new JLabel(
		"<html><body style=\'width: 500px\'> You can sign messages with your address to prove that you own them. Be careful not to sign anything vague, as phishing attacks may try to trick you into signing your identity over to them. Only sign fully-detailed statements you agree to."); //$NON-NLS-1$

	this.txt_addressVerify = new JTextField();

	this.btn_addressbookVerify = new JButton();
	this.btn_pasteVerify = new JButton();

	this.txt_signatureVerify = new JTextField();

	this.txt_messageVerify = new JTextField();

	this.btn_copyVerify = new JButton();

	this.btn_verify = new JButton("Verify Message"); //$NON-NLS-1$
	this.btn_clearVerify = new JButton("Clear All"); //$NON-NLS-1$

	ImageIcon img_address1 = new ImageIcon("images/btn_addressbook.png"); //$NON-NLS-1$
	img_address1 = new ImageIcon(img_address1.getImage()
		.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	this.btn_addressbookVerify.setIcon(img_address1);

	ImageIcon img_paste1 = new ImageIcon("images/btn_paste.png"); //$NON-NLS-1$
	img_paste1 = new ImageIcon(img_paste1.getImage().getScaledInstance(32,
		32, java.awt.Image.SCALE_SMOOTH));

	this.btn_pasteVerify.setIcon(img_paste1);

	ImageIcon img_copy1 = new ImageIcon("images/copy.png"); //$NON-NLS-1$
	img_copy1 = new ImageIcon(img_copy1.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_copyVerify.setIcon(img_copy1);

	ImageIcon img_Verify = new ImageIcon("images/Verify.png"); //$NON-NLS-1$
	img_Verify = new ImageIcon(img_Verify.getImage().getScaledInstance(32,
		32, java.awt.Image.SCALE_SMOOTH));

	this.btn_verify.setIcon(img_Verify);

	ImageIcon img_clear1 = new ImageIcon("images/btn_clear.png"); //$NON-NLS-1$
	img_clear1 = new ImageIcon(img_clear1.getImage().getScaledInstance(32,
		32, java.awt.Image.SCALE_SMOOTH));

	this.btn_clearVerify.setIcon(img_clear1);

	this.btn_addressbookSign
		.setToolTipText("Choose an address from your address book"); //$NON-NLS-1$
	this.btn_pasteSign.setToolTipText("Paste an address from clipboard"); //$NON-NLS-1$
	this.btn_copySign.setToolTipText("Copy signature"); //$NON-NLS-1$
	this.btn_sign.setToolTipText("Sign message"); //$NON-NLS-1$
	this.btn_clearSign.setToolTipText("Clear message"); //$NON-NLS-1$

	this.btn_addressbookVerify
		.setToolTipText("Choose an address from your address book"); //$NON-NLS-1$
	this.btn_pasteVerify.setToolTipText("Paste an address from clipboard"); //$NON-NLS-1$
	this.btn_copyVerify.setToolTipText("Copy Signature"); //$NON-NLS-1$
	this.btn_verify.setToolTipText("Verify message"); //$NON-NLS-1$
	this.btn_clearVerify.setToolTipText("Clear message"); //$NON-NLS-1$

	// 720, 450

	Dimension sz_txt_addressVerify = new Dimension(500, 30);
	Dimension sz_btn_addressbookVerify = new Dimension(32, 32);
	Dimension sz_btn_pasteVerify = new Dimension(32, 32);
	Dimension sz_txt_messageVerify = new Dimension(680, 200);
	Dimension sz_txt_signatureVerify = new Dimension(500, 30);
	Dimension sz_btn_copyVerify = new Dimension(32, 32);
	Dimension sz_btn_Verify = new Dimension(150, 32);
	Dimension sz_btn_clearVerify = new Dimension(150, 32);

	this.txt_addressVerify.setToolTipText(
		"Enter an address (e.g. DztaVPtUd2M5TwfifBUaq5bLrAscYeMbyd)"); //$NON-NLS-1$
	this.txt_signatureVerify.setToolTipText("Enter a signature"); //$NON-NLS-1$
	this.txt_messageVerify.setToolTipText(
		"Enter the message you would like to verify here"); //$NON-NLS-1$

	this.btn_addressbookVerify.setPreferredSize(sz_btn_addressbookVerify);
	this.btn_pasteVerify.setPreferredSize(sz_btn_pasteVerify);
	this.txt_messageVerify.setPreferredSize(sz_txt_messageVerify);
	this.txt_signatureVerify.setPreferredSize(sz_txt_signatureVerify);
	this.txt_addressVerify.setPreferredSize(sz_txt_addressVerify);
	this.btn_copyVerify.setPreferredSize(sz_btn_copyVerify);
	this.btn_verify.setPreferredSize(sz_btn_Verify);
	this.btn_clearVerify.setPreferredSize(sz_btn_clearVerify);

	this.btn_addressbookSign.addActionListener(this);
	this.btn_addressbookVerify.addActionListener(this);
	this.btn_clearSign.addActionListener(this);
	this.btn_clearVerify.addActionListener(this);
	this.btn_copySign.addActionListener(this);
	this.btn_copyVerify.addActionListener(this);
	this.btn_pasteSign.addActionListener(this);
	this.btn_pasteVerify.addActionListener(this);
	this.btn_sign.addActionListener(this);
	this.btn_verify.addActionListener(this);

	this.btn_addressbookSign.setActionCommand("Choose address - sign"); //$NON-NLS-1$
	this.btn_addressbookVerify.setActionCommand("Choose address - verify"); //$NON-NLS-1$
	this.btn_pasteSign.setActionCommand("Paste address - sign"); //$NON-NLS-1$
	this.btn_pasteVerify.setActionCommand("Paste address - verify"); //$NON-NLS-1$
	this.btn_copySign.setActionCommand("Copy signature - sign"); //$NON-NLS-1$
	this.btn_copyVerify.setActionCommand("Copy signature - verify"); //$NON-NLS-1$

	verifyTab.add(this.lbl_messageVerify, "wrap"); //$NON-NLS-1$

	JPanel group1Verify = new JPanel();

	group1Verify.add(this.txt_addressVerify);
	group1Verify.add(this.btn_addressbookVerify);
	group1Verify.add(this.btn_pasteVerify);

	verifyTab.add(group1Verify, "wrap"); //$NON-NLS-1$

	verifyTab.add(this.txt_messageVerify, "wrap"); //$NON-NLS-1$

	JPanel group2Verify = new JPanel();

	group2Verify.add(this.txt_signatureVerify);
	group2Verify.add(this.btn_copyVerify);

	verifyTab.add(group2Verify, "wrap"); //$NON-NLS-1$

	JPanel group3Verify = new JPanel();

	group3Verify.add(this.btn_verify);
	group3Verify.add(this.btn_clearVerify);

	verifyTab.add(group3Verify, "wrap"); //$NON-NLS-1$

	this.win.addTab("Sign Message", signTab); //$NON-NLS-1$
	this.win.addTab("Verify Message", verifyTab); //$NON-NLS-1$

	this.add(this.win);

    }

    /**
     * 
     */
    public void showVerifyPane() {
	this.win.setSelectedIndex(1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String s = "Sign Window: " + e.getActionCommand(); //$NON-NLS-1$

	System.out.println(s);
	Main.daemon.getCommandHandler().guiCommand(s);
    }

    /**
     * @return the lbl_messageSign
     */
    public JLabel getLbl_messageSign() {
	return lbl_messageSign;
    }

    /**
     * @param lbl_messageSign
     *            the lbl_messageSign to set
     */
    public void setLbl_messageSign(JLabel lbl_messageSign) {
	this.lbl_messageSign = lbl_messageSign;
    }

    /**
     * @return the txt_addressSign
     */
    public JTextField getTxt_addressSign() {
	return txt_addressSign;
    }

    /**
     * @param txt_addressSign
     *            the txt_addressSign to set
     */
    public void setTxt_addressSign(JTextField txt_addressSign) {
	this.txt_addressSign = txt_addressSign;
    }

    /**
     * @return the btn_addressbookSign
     */
    public JButton getBtn_addressbookSign() {
	return btn_addressbookSign;
    }

    /**
     * @param btn_addressbookSign
     *            the btn_addressbookSign to set
     */
    public void setBtn_addressbookSign(JButton btn_addressbookSign) {
	this.btn_addressbookSign = btn_addressbookSign;
    }

    /**
     * @return the btn_pasteSign
     */
    public JButton getBtn_pasteSign() {
	return btn_pasteSign;
    }

    /**
     * @param btn_pasteSign
     *            the btn_pasteSign to set
     */
    public void setBtn_pasteSign(JButton btn_pasteSign) {
	this.btn_pasteSign = btn_pasteSign;
    }

    /**
     * @return the txt_messageSign
     */
    public JTextField getTxt_messageSign() {
	return txt_messageSign;
    }

    /**
     * @param txt_messageSign
     *            the txt_messageSign to set
     */
    public void setTxt_messageSign(JTextField txt_messageSign) {
	this.txt_messageSign = txt_messageSign;
    }

    /**
     * @return the txt_signatureSign
     */
    public JTextField getTxt_signatureSign() {
	return txt_signatureSign;
    }

    /**
     * @param txt_signatureSign
     *            the txt_signatureSign to set
     */
    public void setTxt_signatureSign(JTextField txt_signatureSign) {
	this.txt_signatureSign = txt_signatureSign;
    }

    /**
     * @return the btn_copySign
     */
    public JButton getBtn_copySign() {
	return btn_copySign;
    }

    /**
     * @param btn_copySign
     *            the btn_copySign to set
     */
    public void setBtn_copySign(JButton btn_copySign) {
	this.btn_copySign = btn_copySign;
    }

    /**
     * @return the btn_sign
     */
    public JButton getBtn_sign() {
	return btn_sign;
    }

    /**
     * @param btn_sign
     *            the btn_sign to set
     */
    public void setBtn_sign(JButton btn_sign) {
	this.btn_sign = btn_sign;
    }

    /**
     * @return the btn_clearSign
     */
    public JButton getBtn_clearSign() {
	return btn_clearSign;
    }

    /**
     * @param btn_clearSign
     *            the btn_clearSign to set
     */
    public void setBtn_clearSign(JButton btn_clearSign) {
	this.btn_clearSign = btn_clearSign;
    }

    /**
     * @return the lbl_messageVerify
     */
    public JLabel getLbl_messageVerify() {
	return lbl_messageVerify;
    }

    /**
     * @param lbl_messageVerify
     *            the lbl_messageVerify to set
     */
    public void setLbl_messageVerify(JLabel lbl_messageVerify) {
	this.lbl_messageVerify = lbl_messageVerify;
    }

    /**
     * @return the txt_addressVerify
     */
    public JTextField getTxt_addressVerify() {
	return txt_addressVerify;
    }

    /**
     * @param txt_addressVerify
     *            the txt_addressVerify to set
     */
    public void setTxt_addressVerify(JTextField txt_addressVerify) {
	this.txt_addressVerify = txt_addressVerify;
    }

    /**
     * @return the btn_addressbookVerify
     */
    public JButton getBtn_addressbookVerify() {
	return btn_addressbookVerify;
    }

    /**
     * @param btn_addressbookVerify
     *            the btn_addressbookVerify to set
     */
    public void setBtn_addressbookVerify(JButton btn_addressbookVerify) {
	this.btn_addressbookVerify = btn_addressbookVerify;
    }

    /**
     * @return the btn_pasteVerify
     */
    public JButton getBtn_pasteVerify() {
	return btn_pasteVerify;
    }

    /**
     * @param btn_pasteVerify
     *            the btn_pasteVerify to set
     */
    public void setBtn_pasteVerify(JButton btn_pasteVerify) {
	this.btn_pasteVerify = btn_pasteVerify;
    }

    /**
     * @return the txt_messageVerify
     */
    public JTextField getTxt_messageVerify() {
	return txt_messageVerify;
    }

    /**
     * @param txt_messageVerify
     *            the txt_messageVerify to set
     */
    public void setTxt_messageVerify(JTextField txt_messageVerify) {
	this.txt_messageVerify = txt_messageVerify;
    }

    /**
     * @return the txt_signatureVerify
     */
    public JTextField getTxt_signatureVerify() {
	return txt_signatureVerify;
    }

    /**
     * @param txt_signatureVerify
     *            the txt_signatureVerify to set
     */
    public void setTxt_signatureVerify(JTextField txt_signatureVerify) {
	this.txt_signatureVerify = txt_signatureVerify;
    }

    /**
     * @return the btn_copyVerify
     */
    public JButton getBtn_copyVerify() {
	return btn_copyVerify;
    }

    /**
     * @param btn_copyVerify
     *            the btn_copyVerify to set
     */
    public void setBtn_copyVerify(JButton btn_copyVerify) {
	this.btn_copyVerify = btn_copyVerify;
    }

    /**
     * @return the btn_verify
     */
    public JButton getBtn_verify() {
	return btn_verify;
    }

    /**
     * @param btn_verify
     *            the btn_verify to set
     */
    public void setBtn_verify(JButton btn_verify) {
	this.btn_verify = btn_verify;
    }

    /**
     * @return the btn_clearVerify
     */
    public JButton getBtn_clearVerify() {
	return btn_clearVerify;
    }

    /**
     * @param btn_clearVerify
     *            the btn_clearVerify to set
     */
    public void setBtn_clearVerify(JButton btn_clearVerify) {
	this.btn_clearVerify = btn_clearVerify;
    }

    /**
     * @return the win
     */
    public JTabbedPane getWin() {
	return win;
    }

    /**
     * @param win
     *            the win to set
     */
    public void setWin(JTabbedPane win) {
	this.win = win;
    }
}
