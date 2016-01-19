package client;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
public class DebugWindow extends JDialog implements ActionListener {

    private final JLabel lbl_info_core_message, lbl_info_network_message,
	    lbl_info_chain_message;

    private final JLabel lbl_info_core_clientNameTitle,
	    lbl_info_core_clientName, lbl_info_core_clientVersionTitle,
	    lbl_info_core_clientVersion, lbl_info_core_sslVersionTitle,
	    lbl_info_core_sslVersion, lbl_info_core_startTimeTitle,
	    lbl_info_core_startTime, lbl_info_network_numConTitle,
	    lbl_info_network_numCon, lbl_info_chain_numBlocksTitle,
	    lbl_info_chain_numBlocks, lbl_info_chain_estnumBlocksTitle,
	    lbl_info_chain_estnumBlocks, lbl_info_chain_lastBlockTimeTitle,
	    lbl_info_chain_lastBlockTime, lbl_debug_message, lbl_line_otions;

    private final JButton btn_open, btn_show;

    private final JTextField txt_console_screen, txt_console_input;
    private final JButton btn_console_clear;
    private final JCheckBox chk_testNet;

    /**
     * 
     */
    public DebugWindow() {
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

	this.setTitle("Debug Window"); //$NON-NLS-1$

	JTabbedPane debugTabs = new JTabbedPane();

	JPanel infoTab = new JPanel(new MigLayout());
	JPanel conTab = new JPanel(new MigLayout());

	infoTab.setName("Information"); //$NON-NLS-1$
	conTab.setName("Console"); //$NON-NLS-1$

	this.lbl_info_core_clientNameTitle = new JLabel("Client name"); //$NON-NLS-1$
	this.lbl_info_core_clientName = new JLabel();
	this.lbl_info_core_clientVersionTitle = new JLabel("Client version"); //$NON-NLS-1$
	this.lbl_info_core_clientVersion = new JLabel();
	this.lbl_info_core_sslVersionTitle = new JLabel(
		"Using OpenSSL version"); //$NON-NLS-1$
	this.lbl_info_core_sslVersion = new JLabel();
	this.lbl_info_core_startTimeTitle = new JLabel("Startup Time"); //$NON-NLS-1$
	this.lbl_info_core_startTime = new JLabel();
	this.lbl_info_network_numConTitle = new JLabel("Number of Connections"); //$NON-NLS-1$
	this.lbl_info_network_numCon = new JLabel();
	this.lbl_info_chain_numBlocksTitle = new JLabel(
		"Current number of blocks"); //$NON-NLS-1$
	this.lbl_info_chain_numBlocks = new JLabel();
	this.lbl_info_chain_estnumBlocksTitle = new JLabel(
		"Estimated total blocks"); //$NON-NLS-1$
	this.lbl_info_chain_estnumBlocks = new JLabel();
	this.lbl_info_chain_lastBlockTimeTitle = new JLabel("Last block time"); //$NON-NLS-1$
	this.lbl_info_chain_lastBlockTime = new JLabel();
	this.lbl_debug_message = new JLabel("Debug logfile"); //$NON-NLS-1$
	this.lbl_line_otions = new JLabel("Command-line options"); //$NON-NLS-1$

	this.lbl_info_core_message = new JLabel("GoldCoin (GLD) Core"); //$NON-NLS-1$
	this.lbl_info_network_message = new JLabel("Network"); //$NON-NLS-1$
	this.lbl_info_chain_message = new JLabel("Block chain"); //$NON-NLS-1$
	this.btn_open = new JButton("Open"); //$NON-NLS-1$
	this.btn_show = new JButton("Show"); //$NON-NLS-1$
	this.txt_console_screen = new JTextField();
	this.txt_console_input = new JTextField();
	this.btn_console_clear = new JButton();
	this.chk_testNet = new JCheckBox("Testnet Mode"); //$NON-NLS-1$

	this.btn_open.setToolTipText("Open debug log file"); //$NON-NLS-1$
	this.btn_show
		.setToolTipText("Show currently applied command-line options"); //$NON-NLS-1$
	this.txt_console_screen.setToolTipText("Console status displays here"); //$NON-NLS-1$
	this.txt_console_input.setToolTipText("Enter console commands here"); //$NON-NLS-1$
	this.btn_console_clear
		.setToolTipText("Clear all text in console and console input"); //$NON-NLS-1$
	this.chk_testNet.setToolTipText("Switch to testing network"); //$NON-NLS-1$

	infoTab.add(this.lbl_info_core_message, "wrap"); //$NON-NLS-1$
	infoTab.add(this.lbl_info_core_clientNameTitle);
	infoTab.add(this.lbl_info_core_clientName, "wrap"); //$NON-NLS-1$
	infoTab.add(this.lbl_info_core_clientVersionTitle);
	infoTab.add(this.lbl_info_core_clientVersion, "wrap"); //$NON-NLS-1$
	infoTab.add(this.lbl_info_core_sslVersionTitle);
	infoTab.add(this.lbl_info_core_sslVersion, "wrap"); //$NON-NLS-1$
	infoTab.add(this.lbl_info_core_startTimeTitle);
	infoTab.add(this.lbl_info_core_startTime, "wrap"); //$NON-NLS-1$
	infoTab.add(this.lbl_info_network_message, "wrap"); //$NON-NLS-1$
	infoTab.add(this.lbl_info_network_numConTitle);
	infoTab.add(this.lbl_info_network_numCon, "wrap"); //$NON-NLS-1$
	infoTab.add(this.chk_testNet, "wrap"); //$NON-NLS-1$
	infoTab.add(this.lbl_info_chain_message, "wrap"); //$NON-NLS-1$
	infoTab.add(this.lbl_info_chain_numBlocksTitle);
	infoTab.add(this.lbl_info_chain_numBlocks, "wrap"); //$NON-NLS-1$
	infoTab.add(this.lbl_info_chain_estnumBlocksTitle);
	infoTab.add(this.lbl_info_chain_estnumBlocks, "wrap"); //$NON-NLS-1$
	infoTab.add(this.lbl_info_chain_lastBlockTimeTitle);
	infoTab.add(this.lbl_info_chain_lastBlockTime, "wrap"); //$NON-NLS-1$

	infoTab.add(this.lbl_debug_message, "wrap"); //$NON-NLS-1$
	infoTab.add(this.btn_open, "wrap"); //$NON-NLS-1$
	infoTab.add(this.lbl_line_otions, "wrap"); //$NON-NLS-1$
	infoTab.add(this.btn_show);

	Dimension sz_txt_console_screen = new Dimension(600, 400);
	Dimension sz_txt_console_input = new Dimension(600, 32);
	Dimension sz_btn_console_clear = new Dimension(32, 32);

	this.txt_console_screen.setPreferredSize(sz_txt_console_screen);
	this.txt_console_input.setPreferredSize(sz_txt_console_input);
	this.btn_console_clear.setPreferredSize(sz_btn_console_clear);

	ImageIcon img_open = new ImageIcon("images/btn_open.png"); //$NON-NLS-1$
	img_open = new ImageIcon(img_open.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_show = new ImageIcon("images/debug.png"); //$NON-NLS-1$
	img_show = new ImageIcon(img_show.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_clear = new ImageIcon("images/btn_clear.png"); //$NON-NLS-1$
	img_clear = new ImageIcon(img_clear.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_open.setIcon(img_open);
	this.btn_show.setIcon(img_show);
	this.btn_console_clear.setIcon(img_clear);

	this.btn_open.addActionListener(this);
	this.btn_show.addActionListener(this);
	this.chk_testNet.addActionListener(this);
	this.btn_console_clear.addActionListener(this);

	this.btn_console_clear.setActionCommand("Clear - console"); //$NON-NLS-1$

	conTab.add(this.txt_console_screen, "wrap"); //$NON-NLS-1$
	conTab.add(this.txt_console_input);
	conTab.add(this.btn_console_clear);

	Dimension sz_infoTab = new Dimension(800, 400);
	Dimension sz_conTab = new Dimension(800, 400);

	infoTab.setPreferredSize(sz_infoTab);
	conTab.setPreferredSize(sz_conTab);

	debugTabs.add(infoTab);
	debugTabs.add(conTab);

	this.add(debugTabs);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String s = "Debug Window: " + e.getActionCommand(); //$NON-NLS-1$

	System.out.println(s);
	Main.daemon.getCommandHandler().guiCommand(s);
    }

    /**
     * @return the lbl_info_core_message
     */
    public JLabel getLbl_info_core_message() {
	return this.lbl_info_core_message;
    }

    /**
     * @return the lbl_info_network_message
     */
    public JLabel getLbl_info_network_message() {
	return this.lbl_info_network_message;
    }

    /**
     * @return the lbl_info_chain_message
     */
    public JLabel getLbl_info_chain_message() {
	return this.lbl_info_chain_message;
    }

    /**
     * @return the lbl_info_core_clientNameTitle
     */
    public JLabel getLbl_info_core_clientNameTitle() {
	return this.lbl_info_core_clientNameTitle;
    }

    /**
     * @return the lbl_info_core_clientName
     */
    public JLabel getLbl_info_core_clientName() {
	return this.lbl_info_core_clientName;
    }

    /**
     * @return the lbl_info_core_clientVersionTitle
     */
    public JLabel getLbl_info_core_clientVersionTitle() {
	return this.lbl_info_core_clientVersionTitle;
    }

    /**
     * @return the lbl_info_core_clientVersion
     */
    public JLabel getLbl_info_core_clientVersion() {
	return this.lbl_info_core_clientVersion;
    }

    /**
     * @return the lbl_info_core_sslVersionTitle
     */
    public JLabel getLbl_info_core_sslVersionTitle() {
	return this.lbl_info_core_sslVersionTitle;
    }

    /**
     * @return the lbl_info_core_sslVersion
     */
    public JLabel getLbl_info_core_sslVersion() {
	return this.lbl_info_core_sslVersion;
    }

    /**
     * @return the lbl_info_core_startTimeTitle
     */
    public JLabel getLbl_info_core_startTimeTitle() {
	return this.lbl_info_core_startTimeTitle;
    }

    /**
     * @return the lbl_info_core_startTime
     */
    public JLabel getLbl_info_core_startTime() {
	return this.lbl_info_core_startTime;
    }

    /**
     * @return the lbl_info_network_numConTitle
     */
    public JLabel getLbl_info_network_numConTitle() {
	return this.lbl_info_network_numConTitle;
    }

    /**
     * @return the lbl_info_network_numCon
     */
    public JLabel getLbl_info_network_numCon() {
	return this.lbl_info_network_numCon;
    }

    /**
     * @return the lbl_info_chain_numBlocksTitle
     */
    public JLabel getLbl_info_chain_numBlocksTitle() {
	return this.lbl_info_chain_numBlocksTitle;
    }

    /**
     * @return the lbl_info_chain_numBlocks
     */
    public JLabel getLbl_info_chain_numBlocks() {
	return this.lbl_info_chain_numBlocks;
    }

    /**
     * @return the lbl_info_chain_estnumBlocksTitle
     */
    public JLabel getLbl_info_chain_estnumBlocksTitle() {
	return this.lbl_info_chain_estnumBlocksTitle;
    }

    /**
     * @return the lbl_info_chain_estnumBlocks
     */
    public JLabel getLbl_info_chain_estnumBlocks() {
	return this.lbl_info_chain_estnumBlocks;
    }

    /**
     * @return the lbl_info_chain_lastBlockTimeTitle
     */
    public JLabel getLbl_info_chain_lastBlockTimeTitle() {
	return this.lbl_info_chain_lastBlockTimeTitle;
    }

    /**
     * @return the lbl_info_chain_lastBlockTime
     */
    public JLabel getLbl_info_chain_lastBlockTime() {
	return this.lbl_info_chain_lastBlockTime;
    }

    /**
     * @return the lbl_debug_message
     */
    public JLabel getLbl_debug_message() {
	return this.lbl_debug_message;
    }

    /**
     * @return the lbl_line_otions
     */
    public JLabel getLbl_line_otions() {
	return this.lbl_line_otions;
    }

    /**
     * @return the btn_open
     */
    public JButton getBtn_open() {
	return this.btn_open;
    }

    /**
     * @return the btn_show
     */
    public JButton getBtn_show() {
	return this.btn_show;
    }

    /**
     * @return the txt_console_screen
     */
    public JTextField getTxt_console_screen() {
	return this.txt_console_screen;
    }

    /**
     * @return the txt_console_input
     */
    public JTextField getTxt_console_input() {
	return this.txt_console_input;
    }

    /**
     * @return the btn_console_clear
     */
    public JButton getBtn_console_clear() {
	return this.btn_console_clear;
    }

    /**
     * @return the chk_testNet
     */
    public JCheckBox getChk_testNet() {
	return this.chk_testNet;
    }

}
