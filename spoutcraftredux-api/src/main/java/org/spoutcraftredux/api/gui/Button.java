package org.spoutcraftredux.api.gui;

import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * A button. 200 by 20 unless sized otherwise.
 */
public final class Button extends Widget<Button> {
    private Component text;
    private TextAlign align = TextAlign.CENTER;
    private Consumer<Player> onClick = player -> {
    };

    /**
     * Creates a button.
     *
     * @param text the label
     */
    public Button(final Component text) {
        this.text = text;
    }

    /**
     * Gets the label.
     *
     * @return the label
     */
    public Component getText() {
        return this.text;
    }

    /**
     * Sets the label.
     *
     * @param text the label
     * @return this button
     */
    public Button setText(final Component text) {
        this.text = text;
        this.changed();
        return this;
    }

    /**
     * Gets the label alignment.
     *
     * @return the alignment
     */
    public TextAlign getAlign() {
        return this.align;
    }

    /**
     * Sets the label alignment.
     *
     * @param align the alignment
     * @return this button
     */
    public Button setAlign(final TextAlign align) {
        this.align = align;
        this.changed();
        return this;
    }

    /**
     * Runs when the player clicks the button.
     *
     * @param onClick the callback
     * @return this button
     */
    public Button onClick(final Consumer<Player> onClick) {
        this.onClick = onClick;
        return this;
    }

    /**
     * Handles a click from the player.
     *
     * @param player the player
     */
    @ApiStatus.Internal
    public void handleClick(final Player player) {
        this.onClick.accept(player);
    }
}
