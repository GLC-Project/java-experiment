package client;

/**
 * @Author: http://www.javafaq.nu/java-example-code-484.html
 * @CoAuthor: Amir Eslampanah (the code has been modified heavily)
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author Amir Eslampanah
 * 
 */
public class TransparentBackground extends JComponent
	implements ComponentListener, WindowFocusListener, Runnable {
    /**
     * 
     */
    private static final long serialVersionUID = 5955514672628989854L;
    private final JFrame frame;
    protected Image background;

    /**
     * Constructor for the TransparentBackground Class.
     * 
     * @param frame1
     *            Main window frame.
     */
    public TransparentBackground(JFrame frame1) {
	this.frame = frame1;
	frame1.addComponentListener(this);
	frame1.addWindowFocusListener(this);
	new Thread(this).start();
    }

    /**
     * Updates the rectangle/background which is used to create the illusion of
     * transparency.
     */
    public void updateBackground() {
	try {
	    Robot rbt = new Robot();
	    Toolkit tk = Toolkit.getDefaultToolkit();
	    Dimension dim = tk.getScreenSize();
	    this.background = rbt.createScreenCapture(new Rectangle(0, 0,
		    (int) dim.getWidth(), (int) dim.getHeight()));
	    this.validate();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    /**
     * Actually paints the background unto the screen.
     */
    @Override
    public void paintComponent(Graphics g) {
	Point pos = this.getLocationOnScreen();
	Point offset = new Point(-pos.x, -pos.y);
	g.drawImage(this.background, offset.x, offset.y, null);
    }

    /**
     * Executes while the component is shown on the screen.
     */
    @SuppressWarnings("unused")
    @Override
    public void componentShown(ComponentEvent evt) {// Empty
    }

    /**
     * Executes while the component is being resized.
     */
    @SuppressWarnings("unused")
    @Override
    public void componentResized(ComponentEvent evt) {
	quickRefresh();
    }

    /**
     * Executes while the component is moved.
     */
    @SuppressWarnings("unused")
    @Override
    public void componentMoved(ComponentEvent evt) {
	quickRefresh();
    }

    /**
     * Executes while the component is hidden.
     */
    @SuppressWarnings("unused")
    @Override
    public void componentHidden(ComponentEvent evt) {// Empty
    }

    /**
     * Executes for as long as the window is within focus.
     */
    @SuppressWarnings("unused")
    @Override
    public void windowGainedFocus(WindowEvent evt) {// Empty
    }

    /**
     * Executes for as long as the window is out of focus.
     */
    @SuppressWarnings("unused")
    @Override
    public void windowLostFocus(WindowEvent evt) {// Empty
	if (Math.random() > 0.7) {
	    quickRefresh();
	}
    }

    /**
     * Reloads the background.
     */
    public void quickRefresh() {
	if (this.frame.isVisible()) {
	    Point location = this.frame.getLocation();
	    this.frame.setVisible(false);
	    updateBackground();
	    this.frame.repaint();
	    this.frame.validate();
	    this.frame.setVisible(true);
	    this.frame.setLocation(location);
	}
    }

    /**
     * Overrides the default run() method in JComponent.
     */
    @Override
    public void run() {// Empty
    }
}