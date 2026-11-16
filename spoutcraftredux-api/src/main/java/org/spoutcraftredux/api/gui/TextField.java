package org.spoutcraftredux.api.gui;

import java.util.function.BiConsumer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * A single line of text the player can type in.
 */
public final class TextField extends Widget<TextField> {
    private String text = "";
    private Component placeholder = Component.empty();
    private int maxLength = 256;
    private BiConsumer<Player, String> onChange = (player, text) -> {
    };
    private BiConsumer<Player, String> onSubmit = (player, text) -> {
    };

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return this.text;
    }

    /**
     * Sets the text.
     *
     * @param text the text
     * @return this text field
     */
    public TextField setText(final String text) {
        this.text = text.length() > this.maxLength ? text.substring(0, this.maxLength) : text;
        this.changed();
        return this;
    }

    /**
     * Gets the placeholder.
     *
     * @return the placeholder
     */
    public Component getPlaceholder() {
        return this.placeholder;
    }

    /**
     * Sets the hint shown while the field is empty.
     *
     * @param placeholder the hint
     * @return this text field
     */
    public TextField setPlaceholder(final Component placeholder) {
        this.placeholder = placeholder;
        this.changed();
        return this;
    }

    /**
     * Gets the maximum length.
     *
     * @return the maximum number of characters
     */
    public int getMaxLength() {
        return this.maxLength;
    }

    /**
     * Sets the maximum length.
     *
     * @param maxLength the maximum number of characters, 1 to 4096
     * @return this text field
     */
    public TextField setMaxLength(final int maxLength) {
        this.maxLength = Math.clamp(maxLength, 1, 4096);
        this.changed();
        return this;
    }

    /**
     * Runs on every change while the player types.
     *
     * @param onChange the callback, given the player and the text
     * @return this text field
     */
    public TextField onChange(final BiConsumer<Player, String> onChange) {
        this.onChange = onChange;
        return this;
    }

    /**
     * Runs when the player presses Enter.
     *
     * @param onSubmit the callback, given the player and the text
     * @return this text field
     */
    public TextField onSubmit(final BiConsumer<Player, String> onSubmit) {
        this.onSubmit = onSubmit;
        return this;
    }

    /**
     * Handles typing from the player.
     *
     * @param player    the player
     * @param text      the new text
     * @param submitted true if the player pressed Enter
     */
    @ApiStatus.Internal
    public void handleText(final Player player, final String text, final boolean submitted) {
        if (text.length() > this.maxLength) {
            return;
        }
        this.text = text;
        (submitted ? this.onSubmit : this.onChange).accept(player, text);
    }
}
