package org.spoutcraftredux.testplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
}
