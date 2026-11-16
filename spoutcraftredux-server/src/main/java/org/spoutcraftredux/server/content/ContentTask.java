package org.spoutcraftredux.server.content;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.connection.DisconnectionReason;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.spoutcraftredux.protocol.Capabilities;
import org.spoutcraftredux.protocol.ProtocolException;
import org.spoutcraftredux.protocol.message.Content;
import org.spoutcraftredux.protocol.message.ContentAck;
import org.spoutcraftredux.server.SpoutCraftReduxClientInfo;
import org.spoutcraftredux.server.SpoutCraftReduxConfig;
import org.spoutcraftredux.server.SpoutCraftReduxServer;

/**
 * Configuration task that sends the custom content and waits for the client to register it. Players without the
 * client can't join a server with custom content.
 */
public final class ContentTask implements ConfigurationTask {
    public static final Type TYPE = new Type("spoutcraftredux:content");
    private static final Identifier CONTENT = Identifier.parse(Content.CHANNEL);
    private static final Identifier CONTENT_ACK = Identifier.parse(ContentAck.CHANNEL);
    private static final int TIMEOUT_TICKS = 20 * 30;

    private final ServerConfigurationPacketListenerImpl listener;
    private boolean done;
    private int ticks;

    public ContentTask(final ServerConfigurationPacketListenerImpl listener) {
        this.listener = listener;
    }

    @Override
    public void start(final Consumer<Packet<?>> connection) {
        final CraftContentRegistry registry = CraftContentRegistry.get();
        if (!registry.hasContent()) {
            this.done = true;
            return;
        }
        final SpoutCraftReduxClientInfo info = SpoutCraftReduxClientInfo.of(this.listener.connection);
        if (!info.modded() || !info.capabilities().contains(Capabilities.CONTENT)) {
            disconnect(this.listener, missingClientMessage());
            return;
        }
        connection.accept(new ClientboundCustomPayloadPacket(new DiscardedPayload(CONTENT, registry.content().encode())));
    }

    @Override
    public boolean tick() {
        if (this.done) {
            return true;
        }
        if (++this.ticks >= TIMEOUT_TICKS) {
            disconnect(this.listener, Component.text("Timed out loading the server's custom content"));
        }
        return false;
    }

    @Override
    public Type type() {
        return TYPE;
    }

    private static void disconnect(final ServerConfigurationPacketListenerImpl listener, final Component reason) {
        listener.disconnect(PaperAdventure.asVanilla(reason), DisconnectionReason.UNKNOWN);
    }

    private static Component missingClientMessage() {
        final String template = SpoutCraftReduxConfig.missingClientKickMessage;
        return template.isBlank()
            ? Component.text("This server needs the SpoutCraft Redux client.")
            : MiniMessage.miniMessage().deserialize(template);
    }

    /**
     * Handles the client's answer.
     *
     * @param listener the connection's configuration listener
     * @param payload  the custom payload the client sent
     * @return true if the payload was the content answer and is consumed
     */
    public static boolean handlePayload(final ServerConfigurationPacketListenerImpl listener, final DiscardedPayload payload) {
        if (!CONTENT_ACK.equals(payload.id())) {
            return false;
        }
        if (!(listener.currentTask instanceof ContentTask task)) {
            return true;
        }
        try {
            final ContentAck ack = ContentAck.decode(payload.data());
            if (!ack.ok()) {
                SpoutCraftReduxServer.LOGGER.warn("{} could not load the custom content: {}", listener.getOwner().name(), ack.error());
                disconnect(listener, Component.text("Your client could not load this server's custom content: " + ack.error()));
                return true;
            }
        } catch (ProtocolException _) {
            disconnect(listener, Component.text("Bad custom content answer"));
            return true;
        }
        task.done = true;
        return true;
    }
}
