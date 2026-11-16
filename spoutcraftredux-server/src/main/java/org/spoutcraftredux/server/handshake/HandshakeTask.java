package org.spoutcraftredux.server.handshake;

import java.util.OptionalInt;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.spoutcraftredux.protocol.ProtocolException;
import org.spoutcraftredux.protocol.SpoutCraftReduxProtocol;
import org.spoutcraftredux.protocol.message.Hello;
import org.spoutcraftredux.protocol.message.HelloAck;
import org.spoutcraftredux.server.SpoutCraftReduxClientInfo;
import org.spoutcraftredux.server.SpoutCraftReduxConfig;
import org.spoutcraftredux.server.SpoutCraftReduxServer;

/** Configuration task that offers the SpoutCraft Redux protocol and waits briefly for the client mod to answer. */
public final class HandshakeTask implements ConfigurationTask {
    public static final Type TYPE = new Type("spoutcraftredux:handshake");
    private static final Identifier HELLO = Identifier.parse(Hello.CHANNEL);
    private static final Identifier HELLO_ACK = Identifier.parse(HelloAck.CHANNEL);
    private static final String VANILLA_BRAND = "vanilla";

    private final ServerConfigurationPacketListenerImpl listener;
    private int ticks;

    public HandshakeTask(final ServerConfigurationPacketListenerImpl listener) {
        this.listener = listener;
    }

    @Override
    public void start(final Consumer<Packet<?>> connection) {
        SpoutCraftReduxClientInfo.set(this.listener.connection, SpoutCraftReduxClientInfo.VANILLA);
        final Hello hello = new Hello(SpoutCraftReduxProtocol.MIN_SUPPORTED_VERSION, SpoutCraftReduxProtocol.VERSION, SpoutCraftReduxServer.SERVER_CAPABILITIES);
        connection.accept(new ClientboundCustomPayloadPacket(new DiscardedPayload(HELLO, hello.encode())));
    }

    @Override
    public boolean tick() {
        if (SpoutCraftReduxConfig.skipHandshakeForVanillaBrand && VANILLA_BRAND.equals(this.listener.clientBrand)) {
            return true;
        }
        return ++this.ticks >= SpoutCraftReduxConfig.handshakeTimeoutTicks;
    }

    @Override
    public Type type() {
        return TYPE;
    }

    /**
     * Handles a SpoutCraft Redux payload from the client.
     *
     * @return true if the payload was a SpoutCraft Redux one and is consumed
     */
    public static boolean handlePayload(final ServerConfigurationPacketListenerImpl listener, final DiscardedPayload payload) {
        if (!HELLO_ACK.equals(payload.id())) {
            return false;
        }
        if (!(listener.currentTask instanceof HandshakeTask)) {
            return true;
        }

        try {
            final HelloAck ack = HelloAck.decode(payload.data());
            final OptionalInt version = SpoutCraftReduxProtocol.negotiate(SpoutCraftReduxProtocol.MIN_SUPPORTED_VERSION, SpoutCraftReduxProtocol.VERSION, ack.protocol(), ack.protocol());
            if (version.isPresent()) {
                SpoutCraftReduxClientInfo.set(listener.connection, new SpoutCraftReduxClientInfo(version.getAsInt(), ack.capabilities(), ack.modVersion()));
            } else {
                SpoutCraftReduxServer.LOGGER.warn("{} sent unsupported SpoutCraft Redux protocol {}", listener.getOwner().name(), ack.protocol());
            }
        } catch (ProtocolException e) {
            SpoutCraftReduxServer.LOGGER.warn("{} sent a malformed SpoutCraft Redux handshake: {}", listener.getOwner().name(), e.getMessage());
        }
        listener.finishCurrentTask(TYPE);
        return true;
    }
}
