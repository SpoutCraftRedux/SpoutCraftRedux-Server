package org.spoutcraftredux.server;

import java.util.Optional;
import java.util.Set;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spoutcraftredux.api.SpoutCraftReduxPlayer;

public final class CraftSpoutCraftReduxPlayer implements SpoutCraftReduxPlayer {
    private final CraftPlayer player;

    public CraftSpoutCraftReduxPlayer(final CraftPlayer player) {
        this.player = player;
    }

    private SpoutCraftReduxClientInfo info() {
        final var connection = this.player.getHandle().connection;
        return connection == null ? SpoutCraftReduxClientInfo.VANILLA : SpoutCraftReduxClientInfo.of(connection.connection);
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean isModded() {
        return this.info().modded();
    }

    @Override
    public int protocolVersion() {
        return this.info().protocol();
    }

    @Override
    public Set<String> capabilities() {
        return this.info().capabilities();
    }

    @Override
    public Optional<String> modVersion() {
        return Optional.ofNullable(this.info().modVersion());
    }
}
