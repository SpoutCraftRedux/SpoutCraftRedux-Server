package org.spoutcraftredux.api.gui;

/**
 * A rectangle fading from a top color to a bottom color. Use the same color twice for a solid box.
 */
public final class Gradient extends Widget<Gradient> {
    private int topColor;
    private int bottomColor;

    /**
     * Creates a gradient.
     *
     * @param topColor    the top color as ARGB
     * @param bottomColor the bottom color as ARGB
     */
    public Gradient(final int topColor, final int bottomColor) {
        this.topColor = topColor;
        this.bottomColor = bottomColor;
    }

    /**
     * Gets the top color.
     *
     * @return the color as ARGB
     */
    public int getTopColor() {
        return this.topColor;
    }

    /**
     * Gets the bottom color.
     *
     * @return the color as ARGB
     */
    public int getBottomColor() {
        return this.bottomColor;
    }

    /**
     * Sets both colors.
     *
     * @param topColor    the top color as ARGB
     * @param bottomColor the bottom color as ARGB
     * @return this gradient
     */
    public Gradient setColors(final int topColor, final int bottomColor) {
        this.topColor = topColor;
        this.bottomColor = bottomColor;
        this.changed();
        return this;
    }
}
