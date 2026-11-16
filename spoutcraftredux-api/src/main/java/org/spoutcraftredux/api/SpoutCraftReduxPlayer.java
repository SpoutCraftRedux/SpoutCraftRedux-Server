package org.spoutcraftredux.api;

import java.util.Optional;
import java.util.Set;
import org.bukkit.entity.Player;

/**
 * SpoutCraft Redux view of a player. Available for every player, modded or not.
 * <p>
 * Client capabilities are hints for deciding what to send. Never use them to grant permissions.
 */
public interface SpoutCraftReduxPlayer {

    /**
     * Gets the Bukkit player this view belongs to.
     *
     * @return the player
     */
    Player getPlayer();

    /**
     * Checks whether the player completed the SpoutCraft Redux handshake.
     *
     * @return true if the player runs the SpoutCraft Redux client
     */
    boolean isModded();

    /**
     * Gets the protocol version agreed during the handshake.
     *
     * @return the protocol version, or 0 for vanilla clients
     */
    int protocolVersion();

    /**
     * Gets the features the client reported.
     *
     * @return the client capabilities, empty for vanilla clients
     */
    Set<String> capabilities();

    /**
     * Checks whether the client reported a capability.
     *
     * @param capability the capability id, such as {@code ui.v1}
     * @return true if the client reported it
     */
    default boolean hasCapability(final String capability) {
        return this.capabilities().contains(capability);
    }

    /**
     * Gets the version string of the client mod.
     *
     * @return the mod version, or empty for vanilla clients
     */
    Optional<String> modVersion();
}
