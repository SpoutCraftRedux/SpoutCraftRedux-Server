package org.spoutcraftredux.server.network;

import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.spoutcraftredux.api.SpoutCraftRedux;
import org.spoutcraftredux.api.event.SpoutCraftReduxKeyBindingEvent;
import org.spoutcraftredux.api.keybind.KeyBinding;
import org.spoutcraftredux.protocol.ProtocolException;
import org.spoutcraftredux.protocol.message.AssetRequest;
import org.spoutcraftredux.protocol.message.ControlEvent;
import org.spoutcraftredux.protocol.message.KeyBindingPressed;
import org.spoutcraftredux.protocol.message.ScreenClose;
import org.spoutcraftredux.server.CraftSpoutCraftReduxPlayer;
import org.spoutcraftredux.server.asset.CraftAssetRegistry;
import org.spoutcraftredux.server.gui.PlayerGui;

/** Handles SpoutCraft Redux payloads that players send while in game. */
public final class GamePayloads {
    private static final Identifier KEY_BINDING_PRESSED = Identifier.parse(KeyBindingPressed.CHANNEL);
    private static final Identifier CONTROL_EVENT = Identifier.parse(ControlEvent.CHANNEL);
    private static final Identifier SCREEN_CLOSED = Identifier.parse(ScreenClose.SERVERBOUND);
    private static final Identifier ASSET_REQUEST = Identifier.parse(AssetRequest.CHANNEL);

    private GamePayloads() {
    }

    /**
     * Handles a payload from a player in game.
     *
     * @param player the sender
     * @param id     the payload channel
     * @param data   the payload bytes
     * @return true if it was a SpoutCraft Redux payload and is consumed
     */
    public static boolean handle(final ServerPlayer player, final Identifier id, final byte[] data) {
        if (ASSET_REQUEST.equals(id)) {
            try {
                ((CraftAssetRegistry) SpoutCraftRedux.assets()).sendImage(player, AssetRequest.decode(data).sha256());
            } catch (ProtocolException _) {
                // A malformed request from a modified client is dropped
            }
            return true;
        }
        if (CONTROL_EVENT.equals(id) || SCREEN_CLOSED.equals(id)) {
            final PlayerGui gui = ((CraftSpoutCraftReduxPlayer) player.getBukkitEntity().spoutCraftRedux()).gui();
            try {
                if (CONTROL_EVENT.equals(id)) {
                    gui.control(ControlEvent.decode(data));
                } else {
                    gui.playerClosed(ScreenClose.decode(data));
                }
            } catch (ProtocolException _) {
                // A malformed message from a modified client is dropped
            }
            return true;
        }
        if (!KEY_BINDING_PRESSED.equals(id)) {
            return false;
        }
        try {
            final KeyBindingPressed press = KeyBindingPressed.decode(data);
            final KeyBinding binding = SpoutCraftRedux.keyBindings().get(Key.key(press.id()));
            if (binding != null) {
                new SpoutCraftReduxKeyBindingEvent(player.getBukkitEntity(), binding, press.pressed()).callEvent();
            }
        } catch (ProtocolException | InvalidKeyException _) {
            // A malformed press from a modified client is dropped
        }
        return true;
    }
}
