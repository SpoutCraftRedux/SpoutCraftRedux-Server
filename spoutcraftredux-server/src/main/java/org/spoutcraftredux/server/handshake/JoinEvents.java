package org.spoutcraftredux.server.handshake;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.spoutcraftredux.api.SpoutCraftRedux;
import org.spoutcraftredux.api.event.SpoutCraftReduxClientMissingEvent;
import org.spoutcraftredux.api.event.SpoutCraftReduxClientReadyEvent;
import org.spoutcraftredux.server.SpoutCraftReduxClientInfo;
import org.spoutcraftredux.server.SpoutCraftReduxConfig;
import org.spoutcraftredux.server.asset.CraftAssetRegistry;
import org.spoutcraftredux.server.keybind.CraftKeyBindingRegistry;

public final class JoinEvents {
    private JoinEvents() {
    }

    /**
     * Fires the ready or missing event. Called right before PlayerJoinEvent.
     *
     * @param player the joining player
     */
    public static void fire(final CraftPlayer player) {
        ((CraftAssetRegistry) SpoutCraftRedux.assets()).sendPack(player);
        if (SpoutCraftReduxClientInfo.of(player.getHandle().connection.connection).modded()) {
            ((CraftKeyBindingRegistry) SpoutCraftRedux.keyBindings()).sendTo(player);
            new SpoutCraftReduxClientReadyEvent(player).callEvent();
        } else {
            final String template = SpoutCraftReduxConfig.missingClientMessage;
            final SpoutCraftReduxClientMissingEvent event = new SpoutCraftReduxClientMissingEvent(player,
                template.isBlank() ? null : MiniMessage.miniMessage().deserialize(template));
            event.callEvent();
            if (event.getMessage() != null) {
                player.sendMessage(event.getMessage());
            }
        }
    }
}
