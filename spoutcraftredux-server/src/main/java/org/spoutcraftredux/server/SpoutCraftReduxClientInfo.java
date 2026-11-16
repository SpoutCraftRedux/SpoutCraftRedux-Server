package org.spoutcraftredux.server;

import io.netty.util.AttributeKey;
import java.util.Set;
import net.minecraft.network.Connection;
import org.jspecify.annotations.Nullable;

/**
 * What the handshake learned about a connection. Stored on the Netty channel so it survives phase changes.
 *
 * @param protocol     the agreed protocol version, or 0 for vanilla clients
 * @param capabilities the capabilities the client reported
 * @param modVersion   the client mod version, or null for vanilla clients
 */
public record SpoutCraftReduxClientInfo(int protocol, Set<String> capabilities, @Nullable String modVersion) {
    public static final SpoutCraftReduxClientInfo VANILLA = new SpoutCraftReduxClientInfo(0, Set.of(), null);
    private static final AttributeKey<SpoutCraftReduxClientInfo> KEY = AttributeKey.valueOf("spoutcraftredux:client");

    public SpoutCraftReduxClientInfo {
        capabilities = Set.copyOf(capabilities);
    }

    public boolean modded() {
        return this.protocol > 0;
    }

    public static SpoutCraftReduxClientInfo of(final Connection connection) {
        if (connection.channel == null) {
            return VANILLA;
        }
        final SpoutCraftReduxClientInfo info = connection.channel.attr(KEY).get();
        return info != null ? info : VANILLA;
    }

    public static void set(final Connection connection, final SpoutCraftReduxClientInfo info) {
        connection.channel.attr(KEY).set(info);
    }
}
