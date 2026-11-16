package org.spoutcraftredux.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;
import org.spoutcraftredux.api.SpoutCraftReduxPlayer;

/** Fired just before {@link org.bukkit.event.player.PlayerJoinEvent} for players running the SpoutCraft Redux client. */
@NullMarked
public final class SpoutCraftReduxClientReadyEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public SpoutCraftReduxClientReadyEvent(final Player player) {
        super(player);
    }

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
