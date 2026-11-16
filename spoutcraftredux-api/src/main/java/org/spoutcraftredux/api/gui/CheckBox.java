package org.spoutcraftredux.api.gui;

import java.util.function.BiConsumer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * A box the player can tick, with a label to its right.
 */
public final class CheckBox extends Widget<CheckBox> {
    private Component text;
    private boolean checked;
    private BiConsumer<Player, Boolean> onChange = (player, checked) -> {
    };

    /**
     * Creates a checkbox.
     *
     * @param text the label
     */
    public CheckBox(final Component text) {
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
     * @return this checkbox
     */
    public CheckBox setText(final Component text) {
        this.text = text;
        this.changed();
        return this;
    }

    /**
     * Checks whether it is ticked.
     *
     * @return true if ticked
     */
    public boolean isChecked() {
        return this.checked;
    }

    /**
     * Ticks or clears the box.
     *
     * @param checked true to tick
     * @return this checkbox
     */
    public CheckBox setChecked(final boolean checked) {
        this.checked = checked;
        this.changed();
        return this;
    }

    /**
     * Runs when the player ticks or clears the box.
     *
     * @param onChange the callback, given the player and the new state
     * @return this checkbox
     */
    public CheckBox onChange(final BiConsumer<Player, Boolean> onChange) {
        this.onChange = onChange;
        return this;
    }

    /**
     * Handles a change from the player.
     *
     * @param player  the player
     * @param checked the new state
     */
    @ApiStatus.Internal
    public void handleChange(final Player player, final boolean checked) {
        this.checked = checked;
        this.onChange.accept(player, checked);
    }
}
