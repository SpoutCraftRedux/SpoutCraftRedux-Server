package org.spoutcraftredux.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.spoutcraftredux.api.SpoutCraftReduxPlayer;

/**
 * Fired just before {@link org.bukkit.event.player.PlayerJoinEvent} for players running the SpoutCraft Redux client.
 */
public final class SpoutCraftReduxClientReadyEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Creates the event.
     *
     * @param player the joining player
     */
    public SpoutCraftReduxClientReadyEvent(final Player player) {
        super(player);
    }

    /**
     * Gets the SpoutCraft Redux view of the joining player.
     *
     * @return the player's SpoutCraft Redux view
     */
    public SpoutCraftReduxPlayer getSpoutCraftReduxPlayer() {
        return this.getPlayer().spoutCraftRedux();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
