package org.spoutcraftredux.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.spoutcraftredux.api.keybind.KeyBinding;

/**
 * Fired when a player with the SpoutCraft Redux client presses or releases one of the server's key bindings. Bindings
 * only fire in game, never while a screen is open.
 */
public final class SpoutCraftReduxKeyBindingEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final KeyBinding keyBinding;
    private final boolean pressed;

    /**
     * Creates the event.
     *
     * @param player     the player
     * @param keyBinding the binding
     * @param pressed    true when pressed, false when released
     */
    public SpoutCraftReduxKeyBindingEvent(final Player player, final KeyBinding keyBinding, final boolean pressed) {
        super(player);
        this.keyBinding = keyBinding;
        this.pressed = pressed;
    }

    /**
     * Gets the binding.
     *
     * @return the binding
     */
    public KeyBinding getKeyBinding() {
        return this.keyBinding;
    }

    /**
     * Checks whether the key went down or up.
     *
     * @return true when pressed, false when released
     */
    public boolean isPressed() {
        return this.pressed;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
