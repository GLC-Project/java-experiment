package client;

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

import core.Main;
import net.miginfocom.swing.MigLayout;

/**
 * @author Amir Eslampanah
 * 
 */
public class AboutWindow extends JDialog implements ActionListener {
    private final JButton btn_ok;
    private final JLabel lbl_about, lbl_license;
    private final JTabbedPane aboutTabs;

    /**
     * 
     */
    public AboutWindow() {
	this.aboutTabs = new JTabbedPane();
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

	this.setTitle("About GoldCoin (GLD)"); //$NON-NLS-1$

	JPanel aboutTab = new JPanel(new MigLayout());
	JPanel licensedTab = new JPanel(new MigLayout());

	aboutTab.setName("Credits"); //$NON-NLS-1$
	licensedTab.setName("Licensed"); //$NON-NLS-1$

	this.lbl_about = new JLabel(
		"<html><body style=\'width: 500px\'><p>GoldCoin (GLD)<br/>© 2014 Amir Eslampanah and GoldCoin Development Team <br/>License: MIT License<br/><br/>Copyright &copy; 2013&ndash;2014 Amir Eslampanah, GoldCoin Developers Team.\r\n<P>\r\nPermission is hereby granted, free of charge, to any person obtaining a copy\r\nof this software and associated documentation files (the \"Software\"), to deal\r\nin the Software without restriction, including without limitation the rights\r\nto use, copy, modify, merge, publish, distribute, sublicense, and/or sell\r\ncopies of the Software, and to permit persons to whom the Software is\r\nfurnished to do so, subject to the following conditions:\r\n<P>\r\nThe above copyright notice and this permission notice shall be included in\r\nall copies or substantial portions of the Software.\r\n<P>\r\nTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\r\nIMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\r\nFITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE\r\nAUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\r\nLIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\r\nOUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN\r\nTHE SOFTWARE.<br/><br/><p>ExcaliburDB<br/>© 2014 Amir Eslampanah and GoldCoin Development Team <br/>License: MIT License<br/><br/>Copyright &copy; 2013&ndash;2014 Amir Eslampanah, GoldCoin Developers Team.\r\n<P>\r\nPermission is hereby granted, free of charge, to any person obtaining a copy\r\nof this software and associated documentation files (the \"Software\"), to deal\r\nin the Software without restriction, including without limitation the rights\r\nto use, copy, modify, merge, publish, distribute, sublicense, and/or sell\r\ncopies of the Software, and to permit persons to whom the Software is\r\nfurnished to do so, subject to the following conditions:\r\n<P>\r\nThe above copyright notice and this permission notice shall be included in\r\nall copies or substantial portions of the Software.\r\n<P>\r\nTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\r\nIMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\r\nFITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE\r\nAUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\r\nLIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\r\nOUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN\r\nTHE SOFTWARE."); //$NON-NLS-1$
	this.lbl_license = new JLabel(
		"<html><body style=\'width: 500px\'><p>SwingX<br/>© SwingLabs<br/>License: Lesser General Public License<br/><br/>BouncyCastle<br/>© The Legion of the Bouncy Castle Inc.<br/>License: MIT License<br/><br/>kXML<br/>© Stefan Haustein<br/>License: Lesser General Public License Version 2<br/><br/>Xstream<br/>© Joe Walnes & XStream Committers<br/>License: BSD License<br/><br/>MigLayout<br/>© MiG InfoCom AB<br/>License: BSD License<br/><br/>Lag-plugin/Laf-Widget<br/>© Kirill Grouchnikov<br/>License: BSD License<br/><br/>Trident<br/>© Kirill Grouchnikov<br/>License: BSD License<br/><br/>Substance<br/>© Substance, Kirill Grouchnikov<br/>License: BSD License<br/><br/>ExcaliburDB<br/>© Amir Eslampanah<br/>License: MIT License<br/><br/>Brownies Collections<br/>© Thomas Mauch<br/>License: Apache License 2.0<br/><br/>Allatori Java Obfuscator<br/>© Smardec<br/>License: Free for Non-Commercial Usage<br/>Allatori Java Obsuficator is only used in binaries.<br/>It is not used in the source code.<br/>Therefore licensing restrictions related to this product only apply when using the binary we provide.<br/></p>"); //$NON-NLS-1$
	this.btn_ok = new JButton("OK"); //$NON-NLS-1$

	ImageIcon img_ok = new ImageIcon("images/ok.png"); //$NON-NLS-1$
	img_ok = new ImageIcon(img_ok.getImage().getScaledInstance(32, 32,
		java.awt.Image.SCALE_SMOOTH));

	this.btn_ok.setIcon(img_ok);

	this.btn_ok.addActionListener(this);

	aboutTab.add(this.lbl_about);
	licensedTab.add(this.lbl_license);

	this.btn_ok.setToolTipText("OK"); //$NON-NLS-1$

	this.add(this.aboutTabs, "wrap"); //$NON-NLS-1$
	this.add(this.btn_ok);

	this.aboutTabs.add(aboutTab);
	this.aboutTabs.add(licensedTab);

    }

    /**
     * 
     */
    public void showLicensePane() {
	this.aboutTabs.setSelectedIndex(1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String s = "About Window: " + e.getActionCommand(); //$NON-NLS-1$

	System.out.println(s); // $NON-NLS-1$
	Main.daemon.getCommandHandler().guiCommand(s);

    }

    /**
     * @return the btn_ok
     */
    public JButton getBtn_ok() {
	return btn_ok;
    }

    /**
     * @return the lbl_about
     */
    public JLabel getLbl_about() {
	return lbl_about;
    }

    /**
     * @return the lbl_license
     */
    public JLabel getLbl_license() {
	return lbl_license;
    }

    /**
     * @return the aboutTabs
     */
    public JTabbedPane getAboutTabs() {
	return aboutTabs;
    }
}
