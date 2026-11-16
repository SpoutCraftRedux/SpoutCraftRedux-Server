package org.spoutcraftredux.api.gui;

import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/**
 * Base for every widget. Changing a widget that is already on a player's screen updates it there straight away.
 *
 * @param <T> the widget's own type, so setters can be chained
 */
public abstract class Widget<T extends Widget<T>> {
    private static final AtomicInteger NEXT_ID = new AtomicInteger();

    private final int id = NEXT_ID.incrementAndGet();
    private int x;
    private int y;
    private int width;
    private int height;
    private WidgetAnchor anchor = WidgetAnchor.TOP_LEFT;
    private boolean visible = true;
    private boolean enabled = true;
    private @Nullable Component tooltip;
    private @Nullable WidgetContainer<?> container;

    /**
     * Returns this widget as its own type.
     *
     * @return this widget
     */
    @SuppressWarnings("unchecked")
    protected final T self() {
        return (T) this;
    }

    /**
     * Sends this widget's new state to the player, if it is on screen.
     */
    protected final void changed() {
        if (this.container != null) {
            this.container.changed(this);
        }
    }

    /**
     * Gets the id that identifies this widget on the player's screen.
     *
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets position and size together.
     *
     * @param x      x offset from the anchor point
     * @param y      y offset from the anchor point
     * @param width  width in GUI pixels, or 0 for the widget's natural width
     * @param height height in GUI pixels, or 0 for the widget's natural height
     * @return this widget
     */
    public T setGeometry(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.changed();
        return this.self();
    }

    /**
     * Sets the x offset from the anchor point.
     *
     * @param x the offset
     * @return this widget
     */
    public T setX(final int x) {
        this.x = x;
        this.changed();
        return this.self();
    }

    /**
     * Sets the y offset from the anchor point.
     *
     * @param y the offset
     * @return this widget
     */
    public T setY(final int y) {
        this.y = y;
        this.changed();
        return this.self();
    }

    /**
     * Sets the width.
     *
     * @param width width in GUI pixels, or 0 for the natural width
     * @return this widget
     */
    public T setWidth(final int width) {
        this.width = width;
        this.changed();
        return this.self();
    }

    /**
     * Sets the height.
     *
     * @param height height in GUI pixels, or 0 for the natural height
     * @return this widget
     */
    public T setHeight(final int height) {
        this.height = height;
        this.changed();
        return this.self();
    }

    /**
     * Sets the point x and y are measured from.
     *
     * @param anchor the anchor
     * @return this widget
     */
    public T setAnchor(final WidgetAnchor anchor) {
        this.anchor = anchor;
        this.changed();
        return this.self();
    }

    /**
     * Shows or hides the widget.
     *
     * @param visible whether it is drawn
     * @return this widget
     */
    public T setVisible(final boolean visible) {
        this.visible = visible;
        this.changed();
        return this.self();
    }

    /**
     * Enables or disables the widget. Disabled widgets are greyed out and ignore input.
     *
     * @param enabled whether it reacts to input
     * @return this widget
     */
    public T setEnabled(final boolean enabled) {
        this.enabled = enabled;
        this.changed();
        return this.self();
    }

    /**
     * Sets the text shown when the mouse is over the widget.
     *
     * @param tooltip the tooltip, or null for none
     * @return this widget
     */
    public T setTooltip(final @Nullable Component tooltip) {
        this.tooltip = tooltip;
        this.changed();
        return this.self();
    }

    /**
     * Gets the x offset.
     *
     * @return the x offset
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the y offset.
     *
     * @return the y offset
     */
    public int getY() {
        return this.y;
    }

    /**
     * Gets the width.
     *
     * @return the width, or 0 for the natural width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Gets the height.
     *
     * @return the height, or 0 for the natural height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Gets the anchor.
     *
     * @return the anchor
     */
    public WidgetAnchor getAnchor() {
        return this.anchor;
    }

    /**
     * Checks whether the widget is drawn.
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Checks whether the widget reacts to input.
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Gets the tooltip.
     *
     * @return the tooltip, or null for none
     */
    public @Nullable Component getTooltip() {
        return this.tooltip;
    }

    /**
     * Gets the screen or HUD this widget is attached to.
     *
     * @return the container, or null if not attached
     */
    public @Nullable WidgetContainer<?> getContainer() {
        return this.container;
    }

    @ApiStatus.Internal
    final void setContainer(final @Nullable WidgetContainer<?> container) {
        if (container != null && this.container != null && this.container != container) {
            throw new IllegalStateException("A widget can only be attached to one screen or HUD");
        }
        this.container = container;
    }
}
