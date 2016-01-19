package client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
public class MiningPane extends JComponent implements ActionListener {

    JLabel lbl_type, lbl_threads, lbl_scanTime, lbl_server, lbl_port,
	    lbl_username, lbl_password;
    JButton btn_startMining;

    JComboBox combo_type;
    JTextField txt_threads, txt_scanTime, txt_server, txt_port, txt_username,
	    txt_password;
    JCheckBox chk_debug;

    JTextField txt_output;

    /**
     * 
     */
    public MiningPane() {
	this.setLayout(new MigLayout());

	this.lbl_type = new JLabel("Type"); //$NON-NLS-1$
	this.lbl_threads = new JLabel("Threads"); //$NON-NLS-1$
	this.lbl_scanTime = new JLabel("Scantime"); //$NON-NLS-1$
	this.btn_startMining = new JButton("Start Mining"); //$NON-NLS-1$
	this.lbl_server = new JLabel("Server"); //$NON-NLS-1$
	this.lbl_port = new JLabel("Port"); //$NON-NLS-1$
	this.lbl_username = new JLabel("Username"); //$NON-NLS-1$
	this.lbl_password = new JLabel("Password"); //$NON-NLS-1$

	this.combo_type = new JComboBox();
	this.txt_threads = new JTextField();
	this.txt_scanTime = new JTextField();
	this.txt_server = new JTextField();
	this.txt_port = new JTextField();
	this.txt_username = new JTextField();
	this.txt_password = new JTextField();
	this.chk_debug = new JCheckBox("Debug Logging"); //$NON-NLS-1$
	this.txt_output = new JTextField();

	this.txt_output.setEditable(false);

	Dimension sz_btn_startMining = new Dimension(100, 40);
	Dimension sz_combo_type = new Dimension(300, 20);
	Dimension sz_txt_threads = new Dimension(300, 20);
	Dimension sz_txt_scanTime = new Dimension(300, 20);
	Dimension sz_txt_server = new Dimension(300, 20);
	Dimension sz_txt_port = new Dimension(300, 20);
	Dimension sz_txt_username = new Dimension(300, 20);
	Dimension sz_txt_password = new Dimension(300, 20);
	Dimension sz_txt_output = new Dimension(810, 250);

	this.btn_startMining.setPreferredSize(sz_btn_startMining);
	this.combo_type.setPreferredSize(sz_combo_type);
	this.txt_threads.setPreferredSize(sz_txt_threads);
	this.txt_scanTime.setPreferredSize(sz_txt_scanTime);
	this.txt_server.setPreferredSize(sz_txt_server);
	this.txt_port.setPreferredSize(sz_txt_port);
	this.txt_username.setPreferredSize(sz_txt_username);
	this.txt_password.setPreferredSize(sz_txt_password);
	this.txt_output.setPreferredSize(sz_txt_output);

	ImageIcon img_btn_startMining = new ImageIcon("images/mining.png"); //$NON-NLS-1$

	img_btn_startMining = new ImageIcon(img_btn_startMining.getImage()
		.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	this.btn_startMining.setIcon(img_btn_startMining);

	JPanel buttonGroup = new JPanel();

	buttonGroup.setLayout(new MigLayout());

	buttonGroup.add(this.lbl_type);
	buttonGroup.add(this.lbl_threads);
	buttonGroup.add(this.lbl_scanTime);

	buttonGroup.add(this.btn_startMining, "wrap"); //$NON-NLS-1$

	buttonGroup.add(this.combo_type);
	buttonGroup.add(this.txt_threads);
	buttonGroup.add(this.txt_scanTime);
	buttonGroup.add(this.chk_debug, "wrap"); //$NON-NLS-1$

	buttonGroup.add(this.lbl_server);
	buttonGroup.add(this.lbl_port);
	buttonGroup.add(this.lbl_username);
	buttonGroup.add(this.lbl_password, "wrap"); //$NON-NLS-1$

	buttonGroup.add(this.txt_server);
	buttonGroup.add(this.txt_port);
	buttonGroup.add(this.txt_username);
	buttonGroup.add(this.txt_password, "wrap"); //$NON-NLS-1$

	this.txt_threads.setToolTipText("Number of CPU threads to run"); //$NON-NLS-1$
	this.txt_scanTime.setToolTipText(
		"Maximum number of seconds to work on a given task"); //$NON-NLS-1$
	this.txt_server.setToolTipText("The IP address of the mining pool"); //$NON-NLS-1$
	this.txt_port.setToolTipText("The port of the mining pool"); //$NON-NLS-1$
	this.txt_username
		.setToolTipText("The username you use at the mining pool"); //$NON-NLS-1$
	this.txt_password
		.setToolTipText("The password you use at the mining pool"); //$NON-NLS-1$

	this.chk_debug.setToolTipText("Log miner output"); //$NON-NLS-1$

	this.txt_output.setToolTipText("Miner output"); //$NON-NLS-1$

	this.btn_startMining.setToolTipText("Begin mining for coins"); //$NON-NLS-1$

	this.combo_type.setToolTipText(
		"Choose whether to solomine or mine with others on a pool"); //$NON-NLS-1$

	this.combo_type.addActionListener(this);
	this.chk_debug.addActionListener(this);
	this.btn_startMining.addActionListener(this);

	this.add(buttonGroup, "wrap"); //$NON-NLS-1$
	this.add(this.txt_output);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String s = "Mining Pane: " + e.getActionCommand(); //$NON-NLS-1$

	System.out.println(s);
	Main.daemon.getCommandHandler().guiCommand(s);
    }

    /**
     * @return the lbl_type
     */
    public JLabel getLbl_type() {
	return lbl_type;
    }

    /**
     * @param lbl_type
     *            the lbl_type to set
     */
    public void setLbl_type(JLabel lbl_type) {
	this.lbl_type = lbl_type;
    }

    /**
     * @return the lbl_threads
     */
    public JLabel getLbl_threads() {
	return lbl_threads;
    }

    /**
     * @param lbl_threads
     *            the lbl_threads to set
     */
    public void setLbl_threads(JLabel lbl_threads) {
	this.lbl_threads = lbl_threads;
    }

    /**
     * @return the lbl_scanTime
     */
    public JLabel getLbl_scanTime() {
	return lbl_scanTime;
    }

    /**
     * @param lbl_scanTime
     *            the lbl_scanTime to set
     */
    public void setLbl_scanTime(JLabel lbl_scanTime) {
	this.lbl_scanTime = lbl_scanTime;
    }

    /**
     * @return the lbl_server
     */
    public JLabel getLbl_server() {
	return lbl_server;
    }

    /**
     * @param lbl_server
     *            the lbl_server to set
     */
    public void setLbl_server(JLabel lbl_server) {
	this.lbl_server = lbl_server;
    }

    /**
     * @return the lbl_port
     */
    public JLabel getLbl_port() {
	return lbl_port;
    }

    /**
     * @param lbl_port
     *            the lbl_port to set
     */
    public void setLbl_port(JLabel lbl_port) {
	this.lbl_port = lbl_port;
    }

    /**
     * @return the lbl_username
     */
    public JLabel getLbl_username() {
	return lbl_username;
    }

    /**
     * @param lbl_username
     *            the lbl_username to set
     */
    public void setLbl_username(JLabel lbl_username) {
	this.lbl_username = lbl_username;
    }

    /**
     * @return the lbl_password
     */
    public JLabel getLbl_password() {
	return lbl_password;
    }

    /**
     * @param lbl_password
     *            the lbl_password to set
     */
    public void setLbl_password(JLabel lbl_password) {
	this.lbl_password = lbl_password;
    }

    /**
     * @return the btn_startMining
     */
    public JButton getBtn_startMining() {
	return btn_startMining;
    }

    /**
     * @param btn_startMining
     *            the btn_startMining to set
     */
    public void setBtn_startMining(JButton btn_startMining) {
	this.btn_startMining = btn_startMining;
    }

    /**
     * @return the combo_type
     */
    public JComboBox getCombo_type() {
	return combo_type;
    }

    /**
     * @param combo_type
     *            the combo_type to set
     */
    public void setCombo_type(JComboBox combo_type) {
	this.combo_type = combo_type;
    }

    /**
     * @return the txt_threads
     */
    public JTextField getTxt_threads() {
	return txt_threads;
    }

    /**
     * @param txt_threads
     *            the txt_threads to set
     */
    public void setTxt_threads(JTextField txt_threads) {
	this.txt_threads = txt_threads;
    }

    /**
     * @return the txt_scanTime
     */
    public JTextField getTxt_scanTime() {
	return txt_scanTime;
    }

    /**
     * @param txt_scanTime
     *            the txt_scanTime to set
     */
    public void setTxt_scanTime(JTextField txt_scanTime) {
	this.txt_scanTime = txt_scanTime;
    }

    /**
     * @return the txt_server
     */
    public JTextField getTxt_server() {
	return txt_server;
    }

    /**
     * @param txt_server
     *            the txt_server to set
     */
    public void setTxt_server(JTextField txt_server) {
	this.txt_server = txt_server;
    }

    /**
     * @return the txt_port
     */
    public JTextField getTxt_port() {
	return txt_port;
    }

    /**
     * @param txt_port
     *            the txt_port to set
     */
    public void setTxt_port(JTextField txt_port) {
	this.txt_port = txt_port;
    }

    /**
     * @return the txt_username
     */
    public JTextField getTxt_username() {
	return txt_username;
    }

    /**
     * @param txt_username
     *            the txt_username to set
     */
    public void setTxt_username(JTextField txt_username) {
	this.txt_username = txt_username;
    }

    /**
     * @return the txt_password
     */
    public JTextField getTxt_password() {
	return txt_password;
    }

    /**
     * @param txt_password
     *            the txt_password to set
     */
    public void setTxt_password(JTextField txt_password) {
	this.txt_password = txt_password;
    }

    /**
     * @return the chk_debug
     */
    public JCheckBox getChk_debug() {
	return chk_debug;
    }

    /**
     * @param chk_debug
     *            the chk_debug to set
     */
    public void setChk_debug(JCheckBox chk_debug) {
	this.chk_debug = chk_debug;
    }

    /**
     * @return the txt_output
     */
    public JTextField getTxt_output() {
	return txt_output;
    }

    /**
     * @param txt_output
     *            the txt_output to set
     */
    public void setTxt_output(JTextField txt_output) {
	this.txt_output = txt_output;
    }
}
