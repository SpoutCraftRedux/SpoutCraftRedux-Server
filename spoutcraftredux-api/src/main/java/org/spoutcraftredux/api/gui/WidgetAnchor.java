package org.spoutcraftredux.api.gui;

/**
 * The point on the screen a widget's x and y are measured from. With {@link #SCALE}, position and size are on a 427
 * by 240 layout that stretches to fit the screen.
 */
public enum WidgetAnchor {
    /** The top left corner. */
    TOP_LEFT,
    /** The middle of the top edge. */
    TOP_CENTER,
    /** The top right corner. */
    TOP_RIGHT,
    /** The middle of the left edge. */
    CENTER_LEFT,
    /** The center of the screen. */
    CENTER_CENTER,
    /** The middle of the right edge. */
    CENTER_RIGHT,
    /** The bottom left corner. */
    BOTTOM_LEFT,
    /** The middle of the bottom edge. */
    BOTTOM_CENTER,
    /** The bottom right corner. */
    BOTTOM_RIGHT,
    /** A 427 by 240 layout scaled to the screen. */
    SCALE
}
