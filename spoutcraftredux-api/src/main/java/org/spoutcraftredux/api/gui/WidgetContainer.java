package org.spoutcraftredux.api.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/**
 * Holds widgets: a {@link PopupScreen} or a player's {@link Hud}.
 *
 * @param <C> the container's own type, so calls can be chained
 */
public abstract class WidgetContainer<C extends WidgetContainer<C>> {
    private final List<Widget<?>> widgets = new ArrayList<>();
    private @Nullable Sink sink;

    /**
     * Where a shown container sends its changes. Implemented by the server.
     */
    @ApiStatus.Internal
    public interface Sink {
        /**
         * Sends a widget that was added or changed.
         *
         * @param widget the widget
         */
        void update(Widget<?> widget);

        /**
         * Sends a widget that was removed.
         *
         * @param widget the widget
         */
        void remove(Widget<?> widget);

        /**
         * Closes the container, for popups.
         */
        default void close() {
        }
    }

    /**
     * Returns this container as its own type.
     *
     * @return this container
     */
    @SuppressWarnings("unchecked")
    protected final C self() {
        return (C) this;
    }

    /**
     * Adds widgets. They are drawn in the order they were attached.
     *
     * @param widgets the widgets
     * @return this container
     */
    public C attach(final Widget<?>... widgets) {
        for (final Widget<?> widget : widgets) {
            widget.setContainer(this);
            this.widgets.add(widget);
            if (this.sink != null) {
                this.sink.update(widget);
            }
        }
        return this.self();
    }

    /**
     * Removes a widget.
     *
     * @param widget the widget
     * @return this container
     */
    public C detach(final Widget<?> widget) {
        if (this.widgets.remove(widget)) {
            widget.setContainer(null);
            if (this.sink != null) {
                this.sink.remove(widget);
            }
        }
        return this.self();
    }

    /**
     * Gets the attached widgets.
     *
     * @return the widgets, in drawing order
     */
    public List<Widget<?>> getWidgets() {
        return Collections.unmodifiableList(this.widgets);
    }

    final void changed(final Widget<?> widget) {
        if (this.sink != null && this.widgets.contains(widget)) {
            this.sink.update(widget);
        }
    }

    /**
     * Gets the sink this container sends changes to.
     *
     * @return the sink, or null while not shown
     */
    protected final @Nullable Sink sink() {
        return this.sink;
    }

    /**
     * Connects or disconnects this container from a player's screen.
     *
     * @param sink where to send changes, or null when no longer shown
     */
    @ApiStatus.Internal
    public void bind(final @Nullable Sink sink) {
        this.sink = sink;
    }

    /**
     * Finds an attached widget by id.
     *
     * @param id the widget id
     * @return the widget, or null if none has that id
     */
    @ApiStatus.Internal
    public @Nullable Widget<?> find(final int id) {
        for (final Widget<?> widget : this.widgets) {
            if (widget.getId() == id) {
                return widget;
            }
        }
        return null;
    }
}
