package org.spoutcraftredux.testplugin;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.spoutcraftredux.api.SpoutCraftRedux;
import org.spoutcraftredux.api.SpoutCraftReduxPlayer;
import org.spoutcraftredux.api.event.SpoutCraftReduxClientMissingEvent;
import org.spoutcraftredux.api.event.SpoutCraftReduxClientReadyEvent;
import org.spoutcraftredux.api.event.SpoutCraftReduxKeyBindingEvent;
import org.spoutcraftredux.api.keybind.KeyBinding;

public class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        SpoutCraftRedux.keyBindings().register(this, KeyBinding.of(
            Key.key("spoutcraftredux_test", "hello"), Component.text("Say hello"), "key.keyboard.h"));
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

    @EventHandler
    public void onKeyBinding(final SpoutCraftReduxKeyBindingEvent event) {
        this.getSLF4JLogger().info("{} {} {}", event.getPlayer().getName(), event.isPressed() ? "pressed" : "released", event.getKeyBinding().id().asString());
        if (event.isPressed()) {
            event.getPlayer().spoutCraftRedux().sendNotification(
                Component.text("Hello!"), Component.text("You pressed the key"), new ItemStack(Material.BELL));
        }
    }
}
