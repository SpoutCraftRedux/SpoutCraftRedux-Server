package org.spoutcraftredux.api.gui;

import java.util.function.BiConsumer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * A slider with a label on it. The position runs from 0 to 1.
 */
public final class Slider extends Widget<Slider> {
    private Component text;
    private float position;
    private BiConsumer<Player, Float> onChange = (player, position) -> {
    };

    /**
     * Creates a slider.
     *
     * @param text the label
     */
    public Slider(final Component text) {
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
     * Sets the label, for example to show the current value.
     *
     * @param text the label
     * @return this slider
     */
    public Slider setText(final Component text) {
        this.text = text;
        this.changed();
        return this;
    }

    /**
     * Gets the position.
     *
     * @return the position, 0 to 1
     */
    public float getPosition() {
        return this.position;
    }

    /**
     * Sets the position.
     *
     * @param position the position, clamped to 0 to 1
     * @return this slider
     */
    public Slider setPosition(final float position) {
        this.position = Math.clamp(position, 0.0f, 1.0f);
        this.changed();
        return this;
    }

    /**
     * Runs while the player drags the slider.
     *
     * @param onChange the callback, given the player and the new position
     * @return this slider
     */
    public Slider onChange(final BiConsumer<Player, Float> onChange) {
        this.onChange = onChange;
        return this;
    }

    /**
     * Handles a move from the player.
     *
     * @param player   the player
     * @param position the new position
     */
    @ApiStatus.Internal
    public void handleSlide(final Player player, final float position) {
        if (Float.isNaN(position)) {
            return;
        }
        this.position = Math.clamp(position, 0.0f, 1.0f);
        this.onChange.accept(player, this.position);
    }
}
