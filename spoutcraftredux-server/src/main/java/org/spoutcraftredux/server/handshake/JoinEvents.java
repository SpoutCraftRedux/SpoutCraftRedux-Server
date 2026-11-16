package org.spoutcraftredux.server.handshake;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.spoutcraftredux.api.event.SpoutCraftReduxClientMissingEvent;
import org.spoutcraftredux.api.event.SpoutCraftReduxClientReadyEvent;
import org.spoutcraftredux.server.SpoutCraftReduxClientInfo;

public final class JoinEvents {
    private JoinEvents() {
    }

    /**
     * Fires the ready or missing event. Called right before PlayerJoinEvent.
     *
     * @param player the joining player
     */
    public static void fire(final CraftPlayer player) {
        if (SpoutCraftReduxClientInfo.of(player.getHandle().connection.connection).modded()) {
            new SpoutCraftReduxClientReadyEvent(player).callEvent();
        } else {
            new SpoutCraftReduxClientMissingEvent(player).callEvent();
        }
    }
}
