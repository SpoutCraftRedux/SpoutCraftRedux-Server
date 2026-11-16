package org.spoutcraftredux.api.gui;

import net.kyori.adventure.text.Component;

/**
 * Text. Each line break starts a new line, 10 pixels apart.
 */
public final class Label extends Widget<Label> {
    private Component text;
    private int color = 0xFFFFFFFF;
    private boolean shadow = true;
    private boolean wrap;
    private TextAlign align = TextAlign.LEFT;

    /**
     * Creates a label.
     *
     * @param text the text
     */
    public Label(final Component text) {
        this.text = text;
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public Component getText() {
        return this.text;
    }

    /**
     * Sets the text.
     *
     * @param text the text
     * @return this label
     */
    public Label setText(final Component text) {
        this.text = text;
        this.changed();
        return this;
    }

    /**
     * Gets the default text color.
     *
     * @return the color as ARGB
     */
    public int getColor() {
        return this.color;
    }

    /**
     * Sets the color used where the text has no color of its own.
     *
     * @param argb the color as ARGB, such as {@code 0xFFFFFFFF} for white
     * @return this label
     */
    public Label setColor(final int argb) {
        this.color = argb;
        this.changed();
        return this;
    }

    /**
     * Checks whether the text has a shadow.
     *
     * @return true if shadowed
     */
    public boolean hasShadow() {
        return this.shadow;
    }

    /**
     * Draws the text with or without a shadow.
     *
     * @param shadow true for a shadow
     * @return this label
     */
    public Label setShadow(final boolean shadow) {
        this.shadow = shadow;
        this.changed();
        return this;
    }

    /**
     * Checks whether long lines wrap at the label's width.
     *
     * @return true if wrapping
     */
    public boolean isWrap() {
        return this.wrap;
    }

    /**
     * Wraps long lines at the label's width.
     *
     * @param wrap true to wrap
     * @return this label
     */
    public Label setWrap(final boolean wrap) {
        this.wrap = wrap;
        this.changed();
        return this;
    }

    /**
     * Gets the alignment.
     *
     * @return the alignment
     */
    public TextAlign getAlign() {
        return this.align;
    }

    /**
     * Sets the alignment within the label's width.
     *
     * @param align the alignment
     * @return this label
     */
    public Label setAlign(final TextAlign align) {
        this.align = align;
        this.changed();
        return this;
    }
}
