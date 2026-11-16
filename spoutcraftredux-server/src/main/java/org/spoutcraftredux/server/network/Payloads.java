package org.spoutcraftredux.server.network;

import com.mojang.serialization.JsonOps;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/** Sends protocol messages as raw custom payloads. */
public final class Payloads {
    private Payloads() {
    }

    /**
     * Sends an encoded message to a player in the play phase.
     *
     * @param player  the receiving player
     * @param channel the payload channel, such as {@code spoutcraftredux:notification}
     * @param data    the encoded message
     */
    public static void send(final ServerPlayer player, final String channel, final byte[] data) {
        player.connection.send(new ClientboundCustomPayloadPacket(new DiscardedPayload(Identifier.parse(channel), data)));
    }

    /**
     * Encodes an Adventure component as the JSON the client parses with vanilla's codec.
     *
     * @param component the text to encode
     * @return the JSON string
     */
    public static String json(final Component component) {
        final var ops = MinecraftServer.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        return ComponentSerialization.CODEC.encodeStart(ops, PaperAdventure.asVanilla(component)).getOrThrow().toString();
    }
}
