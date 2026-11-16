package org.spoutcraftredux.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/** Fired just before {@link org.bukkit.event.player.PlayerJoinEvent} for players without the SpoutCraft Redux client. */
@NullMarked
public final class SpoutCraftReduxClientMissingEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public SpoutCraftReduxClientMissingEvent(final Player player) {
        super(player);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
