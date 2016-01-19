package client;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

/**
 * @author Amir Eslampanah
 * 
 */
public class GUI extends JFrame
	implements WindowListener, ActionListener, ComponentListener {
    private final JPanel panel_coinTab;
    private final ArrayList<ArrayList<JComponent>> coinTabs = new ArrayList<ArrayList<JComponent>>();

    private AboutWindow curAboutWindow;
    private AddressPane curAddressPane;
    private DebugWindow curDebugWindow;
    private MiningPane curMiningPane;
    private OptionsWindow curOptionsWindow;
    private OverviewPane curOverviewPane;
    private PassWindow curPassWindow;
    private ReceivePane curReceivePane;
    private SendPane curSendPane;
    private SignWindow curSignWindow;
    private TransactionPane curTransactionPane;

    /**
     * 
     */
    public GUI() {

	super("Goldcoin (GLD) - Wallet"); //$NON-NLS-1$

	initMainFrame();

	// Tabbed Coin selection
	JTabbedPane tabbedPane = new JTabbedPane();
	tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	tabbedPane.setPreferredSize(new Dimension(850, 480));

	// TODO: Change this to be dynamic according to the number of coins
	// loaded by the daemon..

	// Preload the windows for each coin

	ArrayList<JDialog> goldWindows = new ArrayList<JDialog>();

	this.curAboutWindow = new AboutWindow();
	this.curDebugWindow = new DebugWindow();
	this.curOptionsWindow = new OptionsWindow();
	this.curPassWindow = new PassWindow();
	this.curSignWindow = new SignWindow();

	goldWindows.add(this.curAboutWindow);
	goldWindows.add(this.curDebugWindow);
	goldWindows.add(this.curOptionsWindow);
	goldWindows.add(this.curPassWindow);
	goldWindows.add(this.curSignWindow);

	ArrayList<JComponent> goldTabs = new ArrayList<JComponent>();

	this.coinTabs.add(goldTabs);

	OverviewPane goldCoinOverview = new OverviewPane();
	SendPane goldCoinSend = new SendPane();
	ReceivePane goldCoinReceive = new ReceivePane();
	TransactionPane goldCoinTransaction = new TransactionPane();
	AddressPane goldCoinAddress = new AddressPane();
	MiningPane goldCoinMining = new MiningPane();

	goldTabs.add(goldCoinOverview);
	goldTabs.add(goldCoinSend);
	goldTabs.add(goldCoinReceive);
	goldTabs.add(goldCoinTransaction);
	goldTabs.add(goldCoinAddress);
	goldTabs.add(goldCoinMining);

	this.curOverviewPane = goldCoinOverview;
	this.curSendPane = goldCoinSend;
	this.curReceivePane = goldCoinReceive;
	this.curTransactionPane = goldCoinTransaction;
	this.curAddressPane = goldCoinAddress;
	this.curMiningPane = goldCoinMining;

	this.panel_coinTab = new JPanel(new MigLayout());
	this.panel_coinTab.setName("GoldCoin (GLD)"); //$NON-NLS-1$
	this.panel_coinTab.add(new OverviewPane());

	this.panel_coinTab.addComponentListener(this);
	// panel_coinTab.add(new OverviewPane());

	/*
	 * for (JPanel e : coinTabs) { tabbedPane.addTab("GoldCoin (GLD)", null,
	 * e, //$NON-NLS-1$ "The Gold Standard of Digital Currency.");
	 * //$NON-NLS-1$ }
	 */

	tabbedPane.addTab("GoldCoin (GLD)", null, this.panel_coinTab, //$NON-NLS-1$
		"The Gold Standard of Digital Currency."); //$NON-NLS-1$

	/*
	 * 
	 * tabbedPane.addTab("Bitcoin (BTC)", null, coinTabs.get(1),
	 * //$NON-NLS-1$ "Vires in Numeris."); //$NON-NLS-1$
	 * 
	 * tabbedPane.addTab("Litecoin (LTC)", null, coinTabs.get(2),
	 * //$NON-NLS-1$ "The Gold Standard of Digital Currency.");
	 * //$NON-NLS-1$
	 * 
	 * tabbedPane.addTab("Dogecoin (DGC)", null, coinTabs.get(3),
	 * //$NON-NLS-1$ "The Gold Standard of Digital Currency.");
	 * //$NON-NLS-1$
	 * 
	 * tabbedPane.addTab("Peercoin (PPC)", null, coinTabs.get(4),
	 * //$NON-NLS-1$ "The Gold Standard of Digital Currency.");
	 * //$NON-NLS-1$
	 * 
	 * tabbedPane.addTab("Next (NXT)", null, coinTabs.get(5), //$NON-NLS-1$
	 * "The Gold Standard of Digital Currency."); //$NON-NLS-1$
	 * 
	 * tabbedPane.addTab("Maxcoin (MAX)", null, coinTabs.get(6),
	 * //$NON-NLS-1$ "The Gold Standard of Digital Currency.");
	 * //$NON-NLS-1$
	 * 
	 * tabbedPane.addTab("WorldCoin (WDC)", null, coinTabs.get(7),
	 * //$NON-NLS-1$ "The Gold Standard of Digital Currency.");
	 * //$NON-NLS-1$
	 * 
	 * tabbedPane.addTab("Zetacoin (ZET)", null, coinTabs.get(8),
	 * //$NON-NLS-1$ "The Gold Standard of Digital Currency.");
	 * //$NON-NLS-1$
	 * 
	 * tabbedPane.addTab("Vertcoin (VTC)", null, coinTabs.get(9),
	 * //$NON-NLS-1$ "The Gold Standard of Digital Currency.");
	 * //$NON-NLS-1$
	 */

	tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

	this.add(tabbedPane, "dock north, width 850"); //$NON-NLS-1$

	// this.add(new JCheckBox("check")); //$NON-NLS-1$

	JLabel wallet = new JLabel("Announcements: "); //$NON-NLS-1$

	this.add(wallet);

	this.setSize(new Dimension(860, 590));
	this.setLocationRelativeTo(null);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 
     */
    public void initMainFrame() {
	this.setLayout(new MigLayout());
	addWindowListener(this);

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

	// Menu Bar
	JMenuBar menuBar;
	JMenu fileMenu, submenu;
	JMenuItem menuItem;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;

	// Create the menu bar.
	menuBar = new JMenuBar();

	// File Menu
	fileMenu = new JMenu("File"); //$NON-NLS-1$
	fileMenu.setMnemonic(KeyEvent.VK_A);
	fileMenu.getAccessibleContext().setAccessibleDescription(
		"The file menu contains options to backup you wallet, export data, sign messages and verify them as well as exit the program."); //$NON-NLS-1$
	menuBar.add(fileMenu);

	// Resize our icons

	ImageIcon img_backup = new ImageIcon("images/backup.png"); //$NON-NLS-1$
	img = img_backup.getImage();
	img_backup = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_exportData = new ImageIcon("images/export.png"); //$NON-NLS-1$
	img = img_exportData.getImage();
	img_exportData = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_signMessage = new ImageIcon("images/sign.png"); //$NON-NLS-1$
	img = img_signMessage.getImage();
	img_signMessage = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_verifyMessage = new ImageIcon("images/verify.png"); //$NON-NLS-1$
	img = img_verifyMessage.getImage();
	img_verifyMessage = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_exit = new ImageIcon("images/exit.png"); //$NON-NLS-1$
	img = img_exit.getImage();
	img_exit = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	// a group of JMenuItems
	menuItem = new JMenuItem("Backup Wallet", img_backup); //$NON-NLS-1$
	menuItem.getAccessibleContext()
		.setAccessibleDescription("This doesn't really do anything"); //$NON-NLS-1$
	menuItem.setMnemonic(KeyEvent.VK_B);
	menuItem.addActionListener(this);

	fileMenu.add(menuItem);

	menuItem = new JMenuItem("Export Data", img_exportData); //$NON-NLS-1$
	menuItem.setMnemonic(KeyEvent.VK_D);
	menuItem.addActionListener(this);

	fileMenu.add(menuItem);

	menuItem = new JMenuItem("Sign Message", img_signMessage); //$NON-NLS-1$
	menuItem.setMnemonic(KeyEvent.VK_D);
	menuItem.addActionListener(this);

	fileMenu.add(menuItem);

	menuItem = new JMenuItem("Verify Message", img_verifyMessage); //$NON-NLS-1$
	menuItem.setMnemonic(KeyEvent.VK_D);
	menuItem.addActionListener(this);

	fileMenu.add(menuItem);

	fileMenu.addSeparator();

	menuItem = new JMenuItem("Exit", img_exit); //$NON-NLS-1$
	menuItem.setMnemonic(KeyEvent.VK_D);
	menuItem.addActionListener(this);

	fileMenu.add(menuItem);

	/*
	 * // a group of radio button menu items fileMenu.addSeparator();
	 * ButtonGroup group = new ButtonGroup(); rbMenuItem = new
	 * JRadioButtonMenuItem("A radio button menu item"); //$NON-NLS-1$
	 * rbMenuItem.setSelected(true); rbMenuItem.setMnemonic(KeyEvent.VK_R);
	 * group.add(rbMenuItem); fileMenu.add(rbMenuItem);
	 * 
	 * rbMenuItem = new JRadioButtonMenuItem("Another one"); //$NON-NLS-1$
	 * rbMenuItem.setMnemonic(KeyEvent.VK_O); group.add(rbMenuItem);
	 * fileMenu.add(rbMenuItem);
	 * 
	 * // a group of check box menu items fileMenu.addSeparator();
	 * cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
	 * //$NON-NLS-1$ cbMenuItem.setMnemonic(KeyEvent.VK_C);
	 * fileMenu.add(cbMenuItem);
	 * 
	 * cbMenuItem = new JCheckBoxMenuItem("Another one"); //$NON-NLS-1$
	 * cbMenuItem.setMnemonic(KeyEvent.VK_H); fileMenu.add(cbMenuItem);
	 * 
	 * // a submenu fileMenu.addSeparator(); submenu = new JMenu("A submenu"
	 * ); //$NON-NLS-1$ submenu.setMnemonic(KeyEvent.VK_S);
	 * 
	 * menuItem = new JMenuItem("An item in the submenu"); //$NON-NLS-1$
	 * menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
	 * ActionEvent.ALT_MASK)); submenu.add(menuItem);
	 * 
	 * menuItem = new JMenuItem("Another item"); //$NON-NLS-1$
	 * submenu.add(menuItem); fileMenu.add(submenu);
	 */

	// Build second menu in the menu bar.
	fileMenu = new JMenu("Settings"); //$NON-NLS-1$
	fileMenu.setMnemonic(KeyEvent.VK_N);
	fileMenu.getAccessibleContext().setAccessibleDescription(
		"This menu contains options to encrypt your wallet, change your wallet passphrase, and change overall program settings."); //$NON-NLS-1$
	menuBar.add(fileMenu);

	// resize Icons
	ImageIcon img_encrypt = new ImageIcon("images/encrypted.png"); //$NON-NLS-1$
	img = img_encrypt.getImage();
	img_encrypt = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_key = new ImageIcon("images/key.png"); //$NON-NLS-1$
	img = img_key.getImage();
	img_key = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_options = new ImageIcon("images/options.png"); //$NON-NLS-1$
	img = img_options.getImage();
	img_options = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	// Add menu items

	menuItem = new JMenuItem("Encrypt Wallet", img_encrypt); //$NON-NLS-1$
	menuItem.setMnemonic(KeyEvent.VK_D);
	menuItem.addActionListener(this);

	fileMenu.add(menuItem);

	menuItem = new JMenuItem("Change Passphrase", img_key); //$NON-NLS-1$
	menuItem.setMnemonic(KeyEvent.VK_D);
	menuItem.addActionListener(this);

	fileMenu.add(menuItem);

	fileMenu.addSeparator();

	menuItem = new JMenuItem("Options", img_options); //$NON-NLS-1$
	menuItem.addActionListener(this);

	fileMenu.add(menuItem);

	fileMenu = new JMenu("Help"); //$NON-NLS-1$
	fileMenu.setMnemonic(KeyEvent.VK_N);
	fileMenu.getAccessibleContext().setAccessibleDescription(
		"This menu contains credits/licenses as well as a developer console."); //$NON-NLS-1$
	menuBar.add(fileMenu);

	// Resize Icons
	ImageIcon img_debug = new ImageIcon("images/debug.png"); //$NON-NLS-1$
	img = img_debug.getImage();
	img_debug = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_about = new ImageIcon("images/about.png"); //$NON-NLS-1$
	img = img_about.getImage();
	img_about = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_license = new ImageIcon("images/license.png"); //$NON-NLS-1$
	img = img_license.getImage();
	img_license = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	menuItem = new JMenuItem("Debug Console", img_debug); //$NON-NLS-1$
	menuItem.setMnemonic(KeyEvent.VK_D);
	menuItem.addActionListener(this);

	fileMenu.add(menuItem);

	fileMenu.addSeparator();

	menuItem = new JMenuItem("About GoldCoin (GLD)", img_about); //$NON-NLS-1$
	menuItem.setMnemonic(KeyEvent.VK_D);
	menuItem.addActionListener(this);

	fileMenu.add(menuItem);

	menuItem = new JMenuItem("Licensed Works", img_license); //$NON-NLS-1$
	menuItem.setMnemonic(KeyEvent.VK_D);
	menuItem.addActionListener(this);

	fileMenu.add(menuItem);

	this.setJMenuBar(menuBar);

	// Function Bar
	JPanel panel_menuBar = new JPanel(new MigLayout());

	// Resize our icons

	ImageIcon img_overView = new ImageIcon("images/home.png"); //$NON-NLS-1$
	img = img_overView.getImage();
	img_overView = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_sendCoins = new ImageIcon("images/send.png"); //$NON-NLS-1$
	img = img_sendCoins.getImage();
	img_sendCoins = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_recvCoins = new ImageIcon("images/receive.png"); //$NON-NLS-1$
	img = img_recvCoins.getImage();
	img_recvCoins = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_transactions = new ImageIcon("images/transactions.png"); //$NON-NLS-1$
	img = img_transactions.getImage();
	img_transactions = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_addresses = new ImageIcon("images/addressbook.png"); //$NON-NLS-1$
	img = img_addresses.getImage();
	img_addresses = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_mining = new ImageIcon("images/mining.png"); //$NON-NLS-1$
	img = img_mining.getImage();
	img_mining = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	ImageIcon img_export = new ImageIcon("images/export.png"); //$NON-NLS-1$
	img = img_export.getImage();
	img_export = new ImageIcon(
		img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH));

	JButton btn_overView = new JButton("Overview", img_overView); //$NON-NLS-1$
	JButton btn_sendCoins = new JButton("Send coins", img_sendCoins); //$NON-NLS-1$
	JButton btn_recvCoins = new JButton("Receive coins", img_recvCoins); //$NON-NLS-1$
	JButton btn_transactions = new JButton("Transactions", //$NON-NLS-1$
		img_transactions);
	JButton btn_addresses = new JButton("Address Book", img_addresses); //$NON-NLS-1$
	JButton btn_mining = new JButton("Mining", img_mining); //$NON-NLS-1$
	JButton btn_export = new JButton("Export", img_export); //$NON-NLS-1$

	btn_overView.setPreferredSize(new Dimension(100, 30));
	btn_sendCoins.setPreferredSize(new Dimension(100, 30));
	btn_recvCoins.setPreferredSize(new Dimension(100, 30));
	btn_transactions.setPreferredSize(new Dimension(100, 30));
	btn_addresses.setPreferredSize(new Dimension(100, 30));
	btn_mining.setPreferredSize(new Dimension(100, 30));
	btn_export.setPreferredSize(new Dimension(100, 30));

	btn_overView.addActionListener(this);
	btn_sendCoins.addActionListener(this);
	btn_recvCoins.addActionListener(this);
	btn_transactions.addActionListener(this);
	btn_addresses.addActionListener(this);
	btn_mining.addActionListener(this);
	btn_export.addActionListener(this);

	panel_menuBar.add(btn_overView, ""); //$NON-NLS-1$
	panel_menuBar.add(btn_sendCoins, "gapleft 5"); //$NON-NLS-1$
	panel_menuBar.add(btn_recvCoins, "gapleft 5"); //$NON-NLS-1$
	panel_menuBar.add(btn_transactions, "gapleft 5"); //$NON-NLS-1$
	panel_menuBar.add(btn_addresses, "gapleft 5"); //$NON-NLS-1$
	panel_menuBar.add(btn_mining, "gapleft 5"); //$NON-NLS-1$
	panel_menuBar.add(btn_export, "gapleft 5"); //$NON-NLS-1$

	this.add(panel_menuBar, "dock north"); //$NON-NLS-1$
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand().contains("Overview")) { //$NON-NLS-1$
	    this.panel_coinTab.removeAll();
	    this.panel_coinTab.add(this.coinTabs.get(0).get(0));
	    this.panel_coinTab.updateUI();
	} else if (e.getActionCommand().contains("Send coins")) { //$NON-NLS-1$
	    this.panel_coinTab.removeAll();
	    this.panel_coinTab.add(this.coinTabs.get(0).get(1));
	    this.panel_coinTab.updateUI();
	} else if (e.getActionCommand().contains("Receive coins")) { //$NON-NLS-1$
	    this.panel_coinTab.removeAll();
	    this.panel_coinTab.add(this.coinTabs.get(0).get(2));
	    this.panel_coinTab.updateUI();
	} else if (e.getActionCommand().contains("Transactions")) { //$NON-NLS-1$
	    this.panel_coinTab.removeAll();
	    this.panel_coinTab.add(this.coinTabs.get(0).get(3));
	    this.panel_coinTab.updateUI();
	} else if (e.getActionCommand().contains("Address Book")) { //$NON-NLS-1$
	    this.panel_coinTab.removeAll();
	    this.panel_coinTab.add(this.coinTabs.get(0).get(4));
	    this.panel_coinTab.updateUI();
	} else if (e.getActionCommand().contains("Mining")) { //$NON-NLS-1$
	    this.panel_coinTab.removeAll();
	    this.panel_coinTab.add(this.coinTabs.get(0).get(5));
	    this.panel_coinTab.updateUI();
	} else if (e.getActionCommand().contains("Export")) { //$NON-NLS-1$
	} else if (e.getActionCommand().contains("Backup Wallet")) { //$NON-NLS-1$

	} else if (e.getActionCommand().contains("Export Data")) { //$NON-NLS-1$

	} else if (e.getActionCommand().contains("Sign Message")) { //$NON-NLS-1$
	    SignWindow sw = this.getCurSignWindow();
	    Dimension sz_sw = new Dimension(720, 450);
	    sw.setPreferredSize(sz_sw);
	    sw.pack();
	    sw.setLocationRelativeTo(this);
	    sw.setAlwaysOnTop(true);
	    sw.setVisible(true);
	} else if (e.getActionCommand().contains("Verify Message")) { //$NON-NLS-1$
	    SignWindow sw = this.getCurSignWindow();
	    Dimension sz_sw = new Dimension(720, 450);
	    sw.setPreferredSize(sz_sw);
	    sw.showVerifyPane();
	    sw.pack();
	    sw.setLocationRelativeTo(this);
	    sw.setAlwaysOnTop(true);
	    sw.setVisible(true);
	} else if (e.getActionCommand().contains("Change Passphrase")) { //$NON-NLS-1$
	    PassWindow pw = this.getCurPassWindow();
	    Dimension sz_pw = new Dimension(720, 200);
	    pw.setPreferredSize(sz_pw);
	    pw.pack();
	    pw.setLocationRelativeTo(this);
	    pw.setAlwaysOnTop(true);
	    pw.setVisible(true);

	} else if (e.getActionCommand().contains("Options")) { //$NON-NLS-1$
	    OptionsWindow ow = this.getCurOptionsWindow();
	    Dimension sz_ow = new Dimension(720, 270);
	    ow.setPreferredSize(sz_ow);
	    ow.pack();
	    ow.setLocationRelativeTo(this);
	    ow.setAlwaysOnTop(true);
	    ow.setVisible(true);

	} else if (e.getActionCommand().contains("Debug Console")) { //$NON-NLS-1$
	    DebugWindow dw = this.getCurDebugWindow();
	    Dimension sz_dw = new Dimension(720, 470);
	    dw.setPreferredSize(sz_dw);
	    dw.pack();
	    dw.setLocationRelativeTo(this);
	    dw.setAlwaysOnTop(true);
	    dw.setVisible(true);
	} else if (e.getActionCommand().contains("About GoldCoin (GLD)")) { //$NON-NLS-1$
	    AboutWindow aw = this.getCurAboutWindow();
	    Dimension sz_aw = new Dimension(700, 815);
	    aw.setPreferredSize(sz_aw);
	    aw.pack();
	    aw.setLocationRelativeTo(this);
	    aw.setAlwaysOnTop(true);
	    aw.setVisible(true);
	} else if (e.getActionCommand().contains("Licensed Works")) { //$NON-NLS-1$
	    AboutWindow aw = this.getCurAboutWindow();
	    Dimension sz_aw = new Dimension(700, 815);
	    aw.showLicensePane();
	    aw.setPreferredSize(sz_aw);
	    aw.pack();
	    aw.setLocationRelativeTo(this);
	    aw.setAlwaysOnTop(true);
	    aw.setVisible(true);
	}

	System.out.println(e.getActionCommand());
    }

    @Override
    public void windowClosing(WindowEvent e) {
	dispose();
	System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
	// TODO Auto-generated method stub

    }

    @Override
    public void componentMoved(ComponentEvent e) {
	// TODO Auto-generated method stub

    }

    @Override
    public void componentShown(ComponentEvent e) {
	// TODO Auto-generated method stub

	System.out.println(e.getComponent().getName());

    }

    @Override
    public void componentHidden(ComponentEvent e) {
	// TODO Auto-generated method stub

    }

    /**
     * @return the curAboutWindow
     */
    public AboutWindow getCurAboutWindow() {
	return this.curAboutWindow;
    }

    /**
     * @param curAboutWindow
     *            the curAboutWindow to set
     */
    public void setCurAboutWindow(AboutWindow curAboutWindow) {
	this.curAboutWindow = curAboutWindow;
    }

    /**
     * @return the curAddressPane
     */
    public AddressPane getCurAddressPane() {
	return this.curAddressPane;
    }

    /**
     * @param curAddressPane
     *            the curAddressPane to set
     */
    public void setCurAddressPane(AddressPane curAddressPane) {
	this.curAddressPane = curAddressPane;
    }

    /**
     * @return the curDebugWindow
     */
    public DebugWindow getCurDebugWindow() {
	return this.curDebugWindow;
    }

    /**
     * @param curDebugWindow
     *            the curDebugWindow to set
     */
    public void setCurDebugWindow(DebugWindow curDebugWindow) {
	this.curDebugWindow = curDebugWindow;
    }

    /**
     * @return the curMiningPane
     */
    public MiningPane getCurMiningPane() {
	return this.curMiningPane;
    }

    /**
     * @param curMiningPane
     *            the curMiningPane to set
     */
    public void setCurMiningPane(MiningPane curMiningPane) {
	this.curMiningPane = curMiningPane;
    }

    /**
     * @return the curOptionsWindow
     */
    public OptionsWindow getCurOptionsWindow() {
	return this.curOptionsWindow;
    }

    /**
     * @param curOptionsWindow
     *            the curOptionsWindow to set
     */
    public void setCurOptionsWindow(OptionsWindow curOptionsWindow) {
	this.curOptionsWindow = curOptionsWindow;
    }

    /**
     * @return the curOverviewPane
     */
    public OverviewPane getCurOverviewPane() {
	return this.curOverviewPane;
    }

    /**
     * @param curOverviewPane
     *            the curOverviewPane to set
     */
    public void setCurOverviewPane(OverviewPane curOverviewPane) {
	this.curOverviewPane = curOverviewPane;
    }

    /**
     * @return the curPassWindow
     */
    public PassWindow getCurPassWindow() {
	return this.curPassWindow;
    }

    /**
     * @param curPassWindow
     *            the curPassWindow to set
     */
    public void setCurPassWindow(PassWindow curPassWindow) {
	this.curPassWindow = curPassWindow;
    }

    /**
     * @return the curReceivePane
     */
    public ReceivePane getCurReceivePane() {
	return this.curReceivePane;
    }

    /**
     * @param curReceivePane
     *            the curReceivePane to set
     */
    public void setCurReceivePane(ReceivePane curReceivePane) {
	this.curReceivePane = curReceivePane;
    }

    /**
     * @return the curSendPane
     */
    public SendPane getCurSendPane() {
	return this.curSendPane;
    }

    /**
     * @param curSendPane
     *            the curSendPane to set
     */
    public void setCurSendPane(SendPane curSendPane) {
	this.curSendPane = curSendPane;
    }

    /**
     * @return the curSignWindow
     */
    public SignWindow getCurSignWindow() {
	return this.curSignWindow;
    }

    /**
     * @param curSignWindow
     *            the curSignWindow to set
     */
    public void setCurSignWindow(SignWindow curSignWindow) {
	this.curSignWindow = curSignWindow;
    }

    /**
     * @return the curTransactionPane
     */
    public TransactionPane getCurTransactionPane() {
	return this.curTransactionPane;
    }

    /**
     * @param curTransactionPane
     *            the curTransactionPane to set
     */
    public void setCurTransactionPane(TransactionPane curTransactionPane) {
	this.curTransactionPane = curTransactionPane;
    }

    /**
     * @return the panel_coinTab
     */
    public JPanel getPanel_coinTab() {
	return this.panel_coinTab;
    }

    /**
     * @return the coinTabs
     */
    public ArrayList<ArrayList<JComponent>> getCoinTabs() {
	return this.coinTabs;
    }
}