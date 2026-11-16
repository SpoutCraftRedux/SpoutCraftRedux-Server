package org.spoutcraftredux.api.event;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.Nullable;

/**
 * Fired just before {@link org.bukkit.event.player.PlayerJoinEvent} for players without the SpoutCraft Redux client.
 * After the event, the player is sent a chat message asking them to download the client, set by
 * {@code missing-client.message} in spoutcraftredux.yml. Change or remove it with {@link #setMessage(Component)}.
 */
public final class SpoutCraftReduxClientMissingEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private @Nullable Component message;

    /**
     * Creates the event.
     *
     * @param player  the joining player
     * @param message the chat message they will get, or null for none
     */
    public SpoutCraftReduxClientMissingEvent(final Player player, final @Nullable Component message) {
        super(player);
        this.message = message;
    }

    /**
     * Gets the chat message the player will get.
     *
     * @return the message, or null for none
     */
    public @Nullable Component getMessage() {
        return this.message;
    }

    /**
     * Sets the chat message the player will get.
     *
     * @param message the message, or null to send nothing
     */
    public void setMessage(final @Nullable Component message) {
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
