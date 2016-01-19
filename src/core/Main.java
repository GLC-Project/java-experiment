/**
 * 
 */
package core;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel;

import client.GUI;

/**
 * @author Amir Eslampanah
 * 
 */
public class Main {
    /**
     * 
     */
    public static Daemon daemon;

    /**
     * 
     */
    public static GUI gui;

    /**
     * @param args
     */
    public static void main(String[] args) {
	// If args[0] is the client then we load up the GUI interface
	// otherwise we just go ahead and load up the daemon
	// if (args.length > 0)
	// if (args[0].toLowerCase().compareTo("client") == 0) { //$NON-NLS-1$
	JFrame.setDefaultLookAndFeelDecorated(true);
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		try {
		    UIManager.setLookAndFeel(
			    new SubstanceBusinessBlackSteelLookAndFeel());
		} catch (Exception e) {
		    System.out
			    .println("Substance Graphite failed to initialize"); //$NON-NLS-1$
		}
		Main.gui = new GUI();
		gui.setVisible(true);
	    }
	});
	// }

	// The daemon is loaded in both cases...
	// The GUI functions by sending commands on the localhost machine to the
	// daemon..
	daemon = new Daemon();
	daemon.start();

    }

    /**
     * 
     */
    public static void restartDaemon() {
	daemon.end();
	daemon.start();
    }

    /**
     * 
     */
    public static void startDaemon() {
	daemon.start();
    }

    /**
     * 
     */
    public static void stopDaemon() {
	daemon.end();
    }

    /**
     * 
     */
    public static void reloadDaemon() {
	daemon.reload();
    }

    /**
     * @return the daemon
     */
    public static Daemon getDaemon() {
	return daemon;
    }

    /**
     * @param daemon1
     *            the daemon to set
     */
    public static void setDaemon(Daemon daemon1) {
	Main.daemon = daemon1;
    }

    /**
     * @return the gui
     */
    public static GUI getGui() {
	return gui;
    }

    /**
     * @param gui1
     *            the gui to set
     */
    public static void setGui(GUI gui1) {
	Main.gui = gui1;
    }
}
