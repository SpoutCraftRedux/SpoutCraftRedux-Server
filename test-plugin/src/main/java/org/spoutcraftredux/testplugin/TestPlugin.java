package org.spoutcraftredux.testplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.spoutcraftredux.api.SpoutCraftReduxPlayer;
import org.spoutcraftredux.api.event.SpoutCraftReduxClientMissingEvent;
import org.spoutcraftredux.api.event.SpoutCraftReduxClientReadyEvent;

public class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onReady(final SpoutCraftReduxClientReadyEvent event) {
        final SpoutCraftReduxPlayer spoutCraftRedux = event.getSpoutCraftReduxPlayer();
        this.getSLF4JLogger().info("{} joined with SpoutCraft Redux {} (protocol {}, capabilities {})",
            event.getPlayer().getName(), spoutCraftRedux.modVersion().orElse("?"), spoutCraftRedux.protocolVersion(), spoutCraftRedux.capabilities());
    }

    @EventHandler
    public void onMissing(final SpoutCraftReduxClientMissingEvent event) {
        this.getSLF4JLogger().info("{} joined without the SpoutCraft Redux client", event.getPlayer().getName());
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        // Wait a moment so the notification shows once the world is on screen
        this.getServer().getScheduler().runTaskLater(this, () -> event.getPlayer().spoutCraftRedux().sendNotification(
            Component.text("Welcome!"), Component.text("SpoutCraft Redux"), new ItemStack(Material.DIAMOND)), 60L);
    }
}
