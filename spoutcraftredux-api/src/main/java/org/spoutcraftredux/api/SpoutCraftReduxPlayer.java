package org.spoutcraftredux.api;

import java.util.Optional;
import java.util.Set;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

/**
 * SpoutCraft Redux view of a player. Available for every player, modded or not.
 * <p>
 * Client capabilities are hints for deciding what to send. Never use them to grant permissions.
 */
@NullMarked
public interface SpoutCraftReduxPlayer {
    Player getPlayer();

    /** Whether the player completed the SpoutCraft Redux handshake. */
    boolean isModded();

    /** The negotiated protocol version, or 0 for vanilla clients. */
    int protocolVersion();

    /** Features the client reported. Empty for vanilla clients. */
    Set<String> capabilities();

    default boolean hasCapability(final String capability) {
        return this.capabilities().contains(capability);
    }

    /** The client mod's version string, if modded. */
    Optional<String> modVersion();
}
