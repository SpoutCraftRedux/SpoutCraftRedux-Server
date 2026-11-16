package org.spoutcraftredux.api;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    /**
     * Shows a notification popup for 7.5 seconds.
     * <p>
     * Players without the SpoutCraft Redux client see the title and message in the action bar instead.
     *
     * @param title   the title, at most 26 visible characters
     * @param message the message, at most 26 visible characters
     * @param icon    the item drawn in the popup, not air
     * @throws IllegalArgumentException if a line is too long or the icon is air
     * @see #sendNotification(Component, Component, ItemStack, Duration)
     */
    void sendNotification(Component title, Component message, ItemStack icon);

    /**
     * Shows a notification popup.
     * <p>
     * Players without the SpoutCraft Redux client see the title and message in the action bar instead.
     *
     * @param title   the title, at most 26 visible characters
     * @param message the message, at most 26 visible characters
     * @param icon    the item drawn in the popup, not air
     * @param time    how long it stays up, at most 60 seconds
     * @throws IllegalArgumentException if a line is too long, the icon is air or the time is out of range
     */
    void sendNotification(Component title, Component message, ItemStack icon, Duration time);
}
