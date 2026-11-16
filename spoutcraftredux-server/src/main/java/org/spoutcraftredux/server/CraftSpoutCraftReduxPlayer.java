package org.spoutcraftredux.server;

import com.google.common.base.Preconditions;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spoutcraftredux.api.SpoutCraftReduxPlayer;
import org.spoutcraftredux.protocol.Capabilities;
import org.spoutcraftredux.protocol.message.Notification;
import org.spoutcraftredux.server.network.Payloads;

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

    @Override
    public void sendNotification(final Component title, final Component message, final ItemStack icon) {
        this.notify(title, message, icon, 0);
    }

    @Override
    public void sendNotification(final Component title, final Component message, final ItemStack icon, final Duration time) {
        final long millis = time.toMillis();
        Preconditions.checkArgument(millis > 0 && millis <= Notification.MAX_DISPLAY_MILLIS,
            "Notification time must be between 1 ms and %s ms", Notification.MAX_DISPLAY_MILLIS);
        this.notify(title, message, icon, (int) millis);
    }

    private void notify(final Component title, final Component message, final ItemStack icon, final int millis) {
        // Longer lines run past the popup
        Preconditions.checkArgument(!icon.isEmpty(), "The icon may not be air");
        Preconditions.checkArgument(visibleLength(title) <= 26, "Notification titles can not be longer than 26 characters");
        Preconditions.checkArgument(visibleLength(message) <= 26, "Notification messages can not be longer than 26 characters");

        if (this.hasCapability(Capabilities.NOTIFICATION)) {
            final Notification notification = new Notification(
                Payloads.json(title), Payloads.json(message), icon.getType().getKey().asString(), millis);
            Payloads.send(this.player.getHandle(), Notification.CHANNEL, notification.encode());
        } else {
            this.player.sendActionBar(Component.textOfChildren(title, Component.space(), message));
        }
    }

    private static int visibleLength(final Component text) {
        return PlainTextComponentSerializer.plainText().serialize(text).length();
    }
}
