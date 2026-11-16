package org.spoutcraftredux.api.gui;

import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * A screen of widgets shown to one player, opened with
 * {@link org.spoutcraftredux.api.SpoutCraftReduxPlayer#openScreen(PopupScreen)}. The game keeps running behind it.
 */
public final class PopupScreen extends WidgetContainer<PopupScreen> {
    private Component title;
    private Consumer<Player> onClose = player -> {
    };

    /**
     * Creates a screen.
     *
     * @param title the title, read out by the narrator
     */
    public PopupScreen(final Component title) {
        this.title = title;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public Component getTitle() {
        return this.title;
    }

    /**
     * Sets the title. Takes effect the next time the screen opens.
     *
     * @param title the title
     * @return this screen
     */
    public PopupScreen setTitle(final Component title) {
        this.title = title;
        return this;
    }

    /**
     * Runs when the player closes the screen themselves, for example with Escape.
     *
     * @param onClose the callback
     * @return this screen
     */
    public PopupScreen onClose(final Consumer<Player> onClose) {
        this.onClose = onClose;
        return this;
    }

    /**
     * Closes the screen if it is open.
     */
    public void close() {
        final Sink sink = this.sink();
        if (sink != null) {
            sink.close();
        }
    }

    /**
     * Calls the close callback. Used by the server when the player closes the screen.
     *
     * @param player the player
     */
    @ApiStatus.Internal
    public void handleClose(final Player player) {
        this.onClose.accept(player);
    }
}
