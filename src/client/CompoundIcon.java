package client;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * Created originally by: Rob Camick Modified by: Amir Eslampanah The
 * CompoundIcon will paint two, or more, Icons as a single Icon. The Icons are
 * painted in the order in which they are added.
 * 
 * The Icons are layed out on the specified axis:
 * <ul>
 * <li>X-Axis (horizontally)
 * <li>Y-Axis (vertically)
 * <li>Z-Axis (stacked)
 * </ul>
 * 
 */
public class CompoundIcon implements Icon {
    /**
     * @author A
     * 
     */
    public enum Axis {
	/**
         * 
         */
	X_AXIS,
	/**
         * 
         */
	Y_AXIS,
	/**
         * 
         */
	Z_AXIS;
    }

    /**
     * 
     */
    public final static float TOP = 0.0f;
    /**
     * 
     */
    public final static float LEFT = 0.0f;
    /**
     * 
     */
    public final static float CENTER = 0.5f;
    /**
     * 
     */
    public final static float BOTTOM = 1.0f;
    /**
     * 
     */
    public final static float RIGHT = 1.0f;

    private final Icon[] icons;

    private final Axis axis;

    private final int gap;

    private float alignmentX = CENTER;
    private float alignmentY = CENTER;

    /**
     * Convenience contructor for creating a CompoundIcon where the icons are
     * layed out on on the X-AXIS, the gap is 0 and the X/Y alignments will
     * default to CENTER.
     * 
     * @param icons1
     *            the Icons to be painted as part of the CompoundIcon
     */
    public CompoundIcon(Icon[] icons1) {
	this(Axis.X_AXIS, icons1);
    }

    /**
     * Convenience contructor for creating a CompoundIcon where the gap is 0 and
     * the X/Y alignments will default to CENTER.
     * 
     * @param axis1
     *            the axis used to lay out the icons for painting. Must be one
     *            of the Axis enums: X_AXIS, Y_AXIS, Z_Axis.
     * @param icons1
     *            the Icons to be painted as part of the CompoundIcon
     */
    public CompoundIcon(Axis axis1, Icon[] icons1) {
	this(axis1, 0, icons1);
    }

    /**
     * Convenience contructor for creating a CompoundIcon where the X/Y
     * alignments will default to CENTER.
     * 
     * @param axis1
     *            the axis used to lay out the icons for painting Must be one of
     *            the Axis enums: X_AXIS, Y_AXIS, Z_Axis.
     * @param gap1
     *            the gap between the icons
     * @param icons1
     *            the Icons to be painted as part of the CompoundIcon
     */
    public CompoundIcon(Axis axis1, int gap1, Icon[] icons1) {
	this(axis1, gap1, CENTER, CENTER, icons1);
    }

    /**
     * Create a CompoundIcon specifying all the properties.
     * 
     * @param axis1
     *            the axis used to lay out the icons for painting Must be one of
     *            the Axis enums: X_AXIS, Y_AXIS, Z_Axis.
     * @param gap1
     *            the gap between the icons
     * @param alignmentX1
     *            the X alignment of the icons. Common values are LEFT, CENTER,
     *            RIGHT. Can be any value between 0.0 and 1.0
     * @param alignmentY1
     *            the Y alignment of the icons. Common values are TOP, CENTER,
     *            BOTTOM. Can be any value between 0.0 and 1.0
     * @param icons1
     *            the Icons to be painted as part of the CompoundIcon
     */
    public CompoundIcon(Axis axis1, int gap1, float alignmentX1,
	    float alignmentY1, Icon[] icons1) {
	this.axis = axis1;
	this.gap = gap1;
	this.alignmentX = alignmentX1 > 1.0f ? 1.0f : alignmentX1 < 0.0f ? 0.0f
		: alignmentX1;
	this.alignmentY = alignmentY1 > 1.0f ? 1.0f : alignmentY1 < 0.0f ? 0.0f
		: alignmentY1;
	for (int i = 0; i < icons1.length; i++) {
	    if (icons1[i] == null) {
		String message = "Icon (" + i + ") cannot be null"; //$NON-NLS-1$ //$NON-NLS-2$
		throw new IllegalArgumentException(message);
	    }
	}

	this.icons = icons1;
    }

    /**
     * Get the Axis along which each icon is painted.
     * 
     * @return the Axis
     */
    public Axis getAxis() {
	return this.axis;
    }

    /**
     * Get the gap between each icon
     * 
     * @return the gap in pixels
     */
    public int getGap() {
	return this.gap;
    }

    /**
     * Get the alignment of the icon on the x-axis
     * 
     * @return the alignment
     */
    public float getAlignmentX() {
	return this.alignmentX;
    }

    /**
     * Get the alignment of the icon on the y-axis
     * 
     * @return the alignment
     */
    public float getAlignmentY() {
	return this.alignmentY;
    }

    /**
     * Get the number of Icons contained in this CompoundIcon.
     * 
     * @return the total number of Icons
     */
    public int getIconCount() {
	return this.icons.length;
    }

    /**
     * Get the Icon at the specified index.
     * 
     * @param index
     *            the index of the Icon to be returned
     * @return the Icon at the specifed index
     * @exception IndexOutOfBoundsException
     *                if the index is out of range
     */
    public Icon getIcon(int index) {
	return this.icons[index];
    }

    //
    // Implement the Icon Interface
    //
    /**
     * Gets the width of this icon.
     * 
     * @return the width of the icon in pixels.
     */
    // @Override
    @Override
    public int getIconWidth() {
	int width = 0;

	// Add the width of all Icons while also including the gap

	if (this.axis == Axis.X_AXIS) {
	    width += (this.icons.length - 1) * this.gap;

	    for (Icon icon : this.icons)
		width += icon.getIconWidth();
	} else // Just find the maximum width
	{
	    for (Icon icon : this.icons)
		width = Math.max(width, icon.getIconWidth());
	}

	return width;
    }

    /**
     * Gets the height of this icon.
     * 
     * @return the height of the icon in pixels.
     */
    // @Override
    @Override
    public int getIconHeight() {
	int height = 0;

	// Add the height of all Icons while also including the gap

	if (this.axis == Axis.Y_AXIS) {
	    height += (this.icons.length - 1) * this.gap;

	    for (Icon icon : this.icons)
		height += icon.getIconHeight();
	} else // Just find the maximum height
	{
	    for (Icon icon : this.icons)
		height = Math.max(height, icon.getIconHeight());
	}

	return height;
    }

    /**
     * Paint the icons of this compound icon at the specified location
     * 
     * @param c
     *            The component on which the icon is painted
     * @param g
     *            the graphics context
     * @param x
     *            the X coordinate of the icon's top-left corner
     * @param y
     *            the Y coordinate of the icon's top-left corner
     */

    @Override
    // CLEANUP
    public void paintIcon(Component c, Graphics g, int x, int y) {

	if (this.axis == Axis.X_AXIS) {
	    int height = getIconHeight();
	    for (Icon icon : this.icons) {
		int iconY = getOffset(height, icon.getIconHeight(),
			this.alignmentY);
		icon.paintIcon(c, g, x, y + iconY);
		x += icon.getIconWidth() + this.gap;
	    }
	} else if (this.axis == Axis.Y_AXIS) {
	    int width = getIconWidth();

	    for (Icon icon : this.icons) {
		int iconX = getOffset(width, icon.getIconWidth(),
			this.alignmentX);
		icon.paintIcon(c, g, x + iconX, y);
		y += icon.getIconHeight() + this.gap;
	    }
	} else// Z
	{
	    int width = getIconWidth();
	    int height = getIconHeight();

	    for (Icon icon : this.icons) {
		int iconX = getOffset(width, icon.getIconWidth(),
			this.alignmentX);
		int iconY = getOffset(height, icon.getIconHeight(),
			this.alignmentY);
		icon.paintIcon(c, g, x + iconX, y + iconY);
	    }
	}
    }

    /*
     * When the icon value is smaller than the maximum value of all icons the
     * icon needs to be aligned appropriately. Calculate the offset to be used
     * when painting the icon to achieve the proper alignment.
     */
    private static int getOffset(int maxValue, int iconValue, float alignment) {
	float offset = (maxValue - iconValue) * alignment;
	return Math.round(offset);
    }
}
