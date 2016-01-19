package client;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
public class OptionsWindow extends JDialog implements ActionListener {

    private final JLabel lbl_main_message;

    private final JLabel lbl_main_payfee;

    private final JLabel lbl_net_proxyIP;

    private final JLabel lbl_net_port;

    private final JLabel lbl_net_socksVer;

    private final JLabel lbl_display_uiLang;

    private final JLabel lbl_display_units;

    private final JCheckBox chk_main_startOnBoot, chk_main_detachAtOff,
	    chk_net_mapUpnp, chk_net_socks;

    private final JCheckBox chk_window_minToTray;

    private final JCheckBox chk_window_minClose;

    private final JCheckBox chk_display_addresses;

    private final JComboBox combo_net_socksVer;

    private final JComboBox combo_display_uiLangSelect;

    private final JComboBox combo_display_unit;

    private final JComboBox combo_main_feeUnit;

    private final JTextField txt_main_fee;

    private final JTextField txt_net_IP;

    private final JTextField txt_net_port;

    private final JButton btn_ok, btn_apply, btn_cancel;

    /**
     * 
     */
    public OptionsWindow() {
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

	this.setTitle("Options"); //$NON-NLS-1$

	this.lbl_main_message = new JLabel(
		"<html><body style=\'width: 500px\'> Optional transaction fee per KB that helps make sure your transactions are processed quickly. Most transactions are 1KB. Fee of 0.1 GLD recommended."); //$NON-NLS-1$
	this.lbl_main_payfee = new JLabel("Pay Transaction fee"); //$NON-NLS-1$
	this.txt_main_fee = new JTextField();
	this.combo_main_feeUnit = new JComboBox();
	this.chk_main_startOnBoot = new JCheckBox(
		"Start GoldCoin (GLD) on system login"); //$NON-NLS-1$
	this.chk_main_detachAtOff = new JCheckBox(
		"Detach database at shutdown"); //$NON-NLS-1$
	this.chk_net_mapUpnp = new JCheckBox("Map port using UPnP"); //$NON-NLS-1$
	this.chk_net_socks = new JCheckBox("Connect through Socks proxy:"); //$NON-NLS-1$
	this.lbl_net_proxyIP = new JLabel("Proxy IP: "); //$NON-NLS-1$
	this.txt_net_IP = new JTextField();
	this.lbl_net_port = new JLabel("Port: "); //$NON-NLS-1$
	this.txt_net_port = new JTextField();
	this.lbl_net_socksVer = new JLabel("SOCKS Version:"); //$NON-NLS-1$
	this.combo_net_socksVer = new JComboBox();
	this.chk_window_minToTray = new JCheckBox(
		"Minimize to the tray instead of the taskbar"); //$NON-NLS-1$
	this.chk_window_minClose = new JCheckBox("Minimize on close"); //$NON-NLS-1$
	this.lbl_display_uiLang = new JLabel("User Interface Language: "); //$NON-NLS-1$
	this.combo_display_uiLangSelect = new JComboBox();
	this.lbl_display_units = new JLabel("Units to show amounts in:"); //$NON-NLS-1$
	this.combo_display_unit = new JComboBox();
	this.chk_display_addresses = new JCheckBox(
		"Display addresses in transaction list"); //$NON-NLS-1$
	this.btn_ok = new JButton("OK"); //$NON-NLS-1$
	this.btn_cancel = new JButton("Cancel"); //$NON-NLS-1$
	this.btn_apply = new JButton("Apply"); //$NON-NLS-1$

	Dimension sz_txt_main_fee = new Dimension(100, 25);
	Dimension sz_txt_net_IP = new Dimension(100, 25);
	Dimension sz_txt_net_port = new Dimension(100, 25);

	this.txt_main_fee.setPreferredSize(sz_txt_main_fee);
	this.txt_net_IP.setPreferredSize(sz_txt_net_IP);
	this.txt_net_port.setPreferredSize(sz_txt_net_port);

	JTabbedPane optionTabs = new JTabbedPane();

	JPanel mainTab = new JPanel(new MigLayout());
	JPanel networkTab = new JPanel(new MigLayout());
	JPanel windowTab = new JPanel(new MigLayout());
	JPanel displayTab = new JPanel(new MigLayout());

	mainTab.setName("Main"); //$NON-NLS-1$
	networkTab.setName("Network"); //$NON-NLS-1$
	windowTab.setName("Windows"); //$NON-NLS-1$
	displayTab.setName("Display"); //$NON-NLS-1$

	mainTab.add(this.lbl_main_message, "wrap"); //$NON-NLS-1$

	JPanel group1 = new JPanel(new MigLayout());
	group1.add(this.lbl_main_payfee);
	group1.add(this.txt_main_fee);
	group1.add(this.combo_main_feeUnit, "wrap"); //$NON-NLS-1$
	group1.add(this.chk_main_startOnBoot, "wrap"); //$NON-NLS-1$
	group1.add(this.chk_main_detachAtOff);

	mainTab.add(group1);

	networkTab.add(this.chk_net_mapUpnp, "wrap"); //$NON-NLS-1$
	networkTab.add(this.chk_net_socks, "wrap"); //$NON-NLS-1$

	JPanel group2 = new JPanel(new MigLayout());
	group2.add(this.lbl_net_proxyIP);
	group2.add(this.txt_net_IP);
	group2.add(this.lbl_net_port);
	group2.add(this.txt_net_port);
	group2.add(this.lbl_net_socksVer);
	group2.add(this.combo_net_socksVer);

	networkTab.add(group2);

	windowTab.add(this.chk_window_minToTray, "wrap"); //$NON-NLS-1$
	windowTab.add(this.chk_window_minClose);

	displayTab.add(this.lbl_display_uiLang);
	displayTab.add(this.combo_display_uiLangSelect, "wrap"); //$NON-NLS-1$
	displayTab.add(this.lbl_display_units);
	displayTab.add(this.combo_display_unit, "wrap"); //$NON-NLS-1$
	displayTab.add(this.chk_display_addresses);

	optionTabs.add(mainTab);
	optionTabs.add(networkTab);
	optionTabs.add(windowTab);
	optionTabs.add(displayTab);

	this.add(optionTabs, "wrap"); //$NON-NLS-1$

	JPanel group3 = new JPanel(new MigLayout());

	ImageIcon img_ok = new ImageIcon("images/ok.png"); //$NON-NLS-1$
	img_ok = new ImageIcon(img_ok.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));
	ImageIcon img_cancel = new ImageIcon("images/exit.png"); //$NON-NLS-1$
	img_cancel = new ImageIcon(img_cancel.getImage().getScaledInstance(32,
		32, java.awt.Image.SCALE_SMOOTH));
	ImageIcon img_apply = new ImageIcon("images/btn_send.png"); //$NON-NLS-1$
	img_apply = new ImageIcon(img_apply.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_ok.setIcon(img_ok);
	this.btn_cancel.setIcon(img_cancel);
	this.btn_apply.setIcon(img_apply);

	group3.add(this.btn_ok);
	group3.add(this.btn_cancel);
	group3.add(this.btn_apply);

	this.txt_main_fee.setToolTipText("Enter the default transaction fee"); //$NON-NLS-1$
	this.chk_main_startOnBoot
		.setToolTipText("Check to start GoldCoin (GLD) on login"); //$NON-NLS-1$
	this.chk_main_detachAtOff.setToolTipText(
		"Check to detach the database prior to system shut off"); //$NON-NLS-1$
	this.combo_main_feeUnit.setToolTipText(
		"Select what demonination the transaction fee should be in"); //$NON-NLS-1$

	this.chk_net_mapUpnp
		.setToolTipText("Check to attempt automatic port forwarding"); //$NON-NLS-1$
	this.chk_net_socks.setToolTipText("Check if you use a socks proxy"); //$NON-NLS-1$
	this.combo_net_socksVer.setToolTipText(
		"Select what version of socks the proxy is using"); //$NON-NLS-1$

	this.chk_window_minToTray.setToolTipText("Check to minimize to tray"); //$NON-NLS-1$
	this.chk_window_minClose.setToolTipText(
		"Check to minimize instead of closing when X is pressed"); //$NON-NLS-1$

	this.combo_display_uiLangSelect.setToolTipText(
		"Select what language the user interface should be in"); //$NON-NLS-1$
	this.combo_display_unit
		.setToolTipText("Select what unit to show amounts in"); //$NON-NLS-1$
	this.chk_display_addresses.setToolTipText(
		"Check to use addresses instead of labels in the transaction list"); //$NON-NLS-1$

	this.txt_net_IP.setToolTipText("Enter proxy IP here"); //$NON-NLS-1$
	this.txt_net_port.setToolTipText("Enter proxy port here"); //$NON-NLS-1$

	this.btn_ok.setToolTipText("OK"); //$NON-NLS-1$
	this.btn_cancel.setToolTipText("Cancel"); //$NON-NLS-1$
	this.btn_apply.setToolTipText("Apply"); //$NON-NLS-1$

	this.btn_ok.addActionListener(this);
	this.btn_cancel.addActionListener(this);
	this.btn_apply.addActionListener(this);
	this.chk_main_startOnBoot.addActionListener(this);
	this.chk_main_detachAtOff.addActionListener(this);
	this.combo_main_feeUnit.addActionListener(this);
	this.chk_net_mapUpnp.addActionListener(this);
	this.chk_net_socks.addActionListener(this);
	this.combo_net_socksVer.addActionListener(this);
	this.chk_window_minClose.addActionListener(this);
	this.chk_window_minToTray.addActionListener(this);
	this.combo_display_uiLangSelect.addActionListener(this);
	this.combo_display_unit.addActionListener(this);
	this.chk_display_addresses.addActionListener(this);

	this.add(group3);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String s = "Options Window: " + e.getActionCommand(); //$NON-NLS-1$

	System.out.println(s);
	Main.daemon.getCommandHandler().guiCommand(s);

    }

    /**
     * @return the lbl_main_message
     */
    public JLabel getLbl_main_message() {
	return lbl_main_message;
    }

    /**
     * @return the lbl_main_payfee
     */
    public JLabel getLbl_main_payfee() {
	return lbl_main_payfee;
    }

    /**
     * @return the lbl_net_proxyIP
     */
    public JLabel getLbl_net_proxyIP() {
	return lbl_net_proxyIP;
    }

    /**
     * @return the lbl_net_port
     */
    public JLabel getLbl_net_port() {
	return lbl_net_port;
    }

    /**
     * @return the lbl_net_socksVer
     */
    public JLabel getLbl_net_socksVer() {
	return lbl_net_socksVer;
    }

    /**
     * @return the lbl_display_uiLang
     */
    public JLabel getLbl_display_uiLang() {
	return lbl_display_uiLang;
    }

    /**
     * @return the lbl_display_units
     */
    public JLabel getLbl_display_units() {
	return lbl_display_units;
    }

    /**
     * @return the chk_main_startOnBoot
     */
    public JCheckBox getChk_main_startOnBoot() {
	return chk_main_startOnBoot;
    }

    /**
     * @return the chk_main_detachAtOff
     */
    public JCheckBox getChk_main_detachAtOff() {
	return chk_main_detachAtOff;
    }

    /**
     * @return the chk_net_mapUpnp
     */
    public JCheckBox getChk_net_mapUpnp() {
	return chk_net_mapUpnp;
    }

    /**
     * @return the chk_net_socks
     */
    public JCheckBox getChk_net_socks() {
	return chk_net_socks;
    }

    /**
     * @return the chk_window_minToTray
     */
    public JCheckBox getChk_window_minToTray() {
	return chk_window_minToTray;
    }

    /**
     * @return the chk_window_minClose
     */
    public JCheckBox getChk_window_minClose() {
	return chk_window_minClose;
    }

    /**
     * @return the chk_display_addresses
     */
    public JCheckBox getChk_display_addresses() {
	return chk_display_addresses;
    }

    /**
     * @return the combo_net_socksVer
     */
    public JComboBox getCombo_net_socksVer() {
	return combo_net_socksVer;
    }

    /**
     * @return the combo_display_uiLangSelect
     */
    public JComboBox getCombo_display_uiLangSelect() {
	return combo_display_uiLangSelect;
    }

    /**
     * @return the combo_display_unit
     */
    public JComboBox getCombo_display_unit() {
	return combo_display_unit;
    }

    /**
     * @return the combo_main_feeUnit
     */
    public JComboBox getCombo_main_feeUnit() {
	return combo_main_feeUnit;
    }

    /**
     * @return the txt_main_fee
     */
    public JTextField getTxt_main_fee() {
	return txt_main_fee;
    }

    /**
     * @return the txt_net_IP
     */
    public JTextField getTxt_net_IP() {
	return txt_net_IP;
    }

    /**
     * @return the txt_net_port
     */
    public JTextField getTxt_net_port() {
	return txt_net_port;
    }

    /**
     * @return the btn_ok
     */
    public JButton getBtn_ok() {
	return btn_ok;
    }

    /**
     * @return the btn_apply
     */
    public JButton getBtn_apply() {
	return btn_apply;
    }

    /**
     * @return the btn_cancel
     */
    public JButton getBtn_cancel() {
	return btn_cancel;
    }

}
