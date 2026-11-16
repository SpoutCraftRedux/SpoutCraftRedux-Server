package org.spoutcraftredux.testplugin;

import java.util.List;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;
import org.spoutcraftredux.api.SpoutCraftRedux;
import org.spoutcraftredux.api.SpoutCraftReduxPlayer;
import org.spoutcraftredux.api.asset.Asset;
import org.spoutcraftredux.api.event.SpoutCraftReduxClientMissingEvent;
import org.spoutcraftredux.api.event.SpoutCraftReduxClientReadyEvent;
import org.spoutcraftredux.api.event.SpoutCraftReduxKeyBindingEvent;
import org.spoutcraftredux.api.gui.Button;
import org.spoutcraftredux.api.gui.CheckBox;
import org.spoutcraftredux.api.gui.Gradient;
import org.spoutcraftredux.api.gui.ItemWidget;
import org.spoutcraftredux.api.gui.Label;
import org.spoutcraftredux.api.gui.ListWidget;
import org.spoutcraftredux.api.gui.PopupScreen;
import org.spoutcraftredux.api.gui.Slider;
import org.spoutcraftredux.api.gui.TextAlign;
import org.spoutcraftredux.api.gui.TextField;
import org.spoutcraftredux.api.gui.VanillaHudElement;
import org.spoutcraftredux.api.gui.Texture;
import org.spoutcraftredux.api.gui.WidgetAnchor;
import org.spoutcraftredux.api.keybind.KeyBinding;

public class TestPlugin extends JavaPlugin implements Listener {

    private Asset logo;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.logo = SpoutCraftRedux.assets().registerImage(this, Key.key("spoutcraftredux_test", "logo"), "images/logo.png");
        try {
            // Turns the diamond item into a red gem, for every player
            SpoutCraftRedux.assets().addResourcePack(this, "resourcepack");
        } catch (final java.io.IOException e) {
            this.getSLF4JLogger().error("Could not add the test resource pack", e);
        }
        SpoutCraftRedux.keyBindings().register(this, KeyBinding.of(
            Key.key("spoutcraftredux_test", "hello"), Component.text("Open the test screen"), "key.keyboard.h"));
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
        final Player player = event.getPlayer();
        // A small HUD in the top left: a label over a mana-style bar
        player.spoutCraftRedux().hud().attach(
            new Label(Component.text("Mana", NamedTextColor.AQUA)).setGeometry(5, 5, 0, 0),
            new Gradient(0xFF101010, 0xFF101010).setGeometry(5, 16, 82, 7),
            new Gradient(0xFF4060FF, 0xFF203090).setGeometry(6, 17, 60, 5));
        this.getServer().getScheduler().runTaskLater(this, () -> player.spoutCraftRedux().sendNotification(
            Component.text("Welcome!"), Component.text("Press H for the test"), new ItemStack(Material.DIAMOND)), 60L);
    }

    @EventHandler
    public void onKeyBinding(final SpoutCraftReduxKeyBindingEvent event) {
        if (event.isPressed()) {
            event.getPlayer().spoutCraftRedux().openScreen(this.testScreen());
        }
    }

    private PopupScreen testScreen() {
        final PopupScreen screen = new PopupScreen(Component.text("Test screen"));
        final Label status = new Label(Component.text("Nothing clicked yet")).setAnchor(WidgetAnchor.TOP_CENTER)
            .setGeometry(-100, 10, 200, 10).setAlign(TextAlign.CENTER);
        final Slider slider = new Slider(Component.text("Volume: 50%")).setAnchor(WidgetAnchor.CENTER_CENTER)
            .setGeometry(-100, 20, 200, 20).setPosition(0.5f);
        slider.onChange((player, position) -> slider.setText(Component.text("Volume: " + Math.round(position * 100) + "%")));
        screen.attach(
            new Texture(this.logo).setAnchor(WidgetAnchor.TOP_LEFT).setGeometry(5, 5, 64, 32),
            status,
            new ItemWidget(ItemType.EMERALD).setAnchor(WidgetAnchor.TOP_CENTER).setGeometry(-8, 25, 16, 16),
            new Button(Component.text("Buy an emerald")).setAnchor(WidgetAnchor.CENTER_CENTER).setGeometry(-100, -60, 200, 20)
                .onClick(player -> {
                    this.getSLF4JLogger().info("{} clicked the button", player.getName());
                    status.setText(Component.text("Bought an emerald", NamedTextColor.GREEN));
                }),
            new CheckBox(Component.text("Send me updates")).setAnchor(WidgetAnchor.CENTER_CENTER).setGeometry(-100, -35, 200, 20)
                .onChange((player, checked) -> this.getSLF4JLogger().info("{} set the checkbox to {}", player.getName(), checked)),
            new TextField().setPlaceholder(Component.text("Your name")).setAnchor(WidgetAnchor.CENTER_CENTER).setGeometry(-100, -8, 200, 20)
                .onSubmit((player, text) -> this.getSLF4JLogger().info("{} submitted '{}'", player.getName(), text)),
            slider,
            new CheckBox(Component.text("Hide the survival HUD")).setAnchor(WidgetAnchor.BOTTOM_LEFT).setGeometry(5, -50, 150, 20)
                .onChange((player, checked) -> player.spoutCraftRedux().hud().setSurvivalHudVisible(!checked)),
            new CheckBox(Component.text("Raise the hotbar")).setAnchor(WidgetAnchor.BOTTOM_LEFT).setGeometry(5, -25, 150, 20)
                .onChange((player, checked) -> player.spoutCraftRedux().hud().setVanillaOffset(VanillaHudElement.HOTBAR, 0, checked ? -40 : 0)),
            new ListWidget().setRows(List.of(
                    new ListWidget.Row(Component.text("Swords"), Component.text("Sharp things")),
                    new ListWidget.Row(Component.text("Shields"), Component.text("Blocky things")),
                    new ListWidget.Row(Component.text("Potions"), Component.text("Bubbly things"))))
                .setAnchor(WidgetAnchor.CENTER_CENTER).setGeometry(-100, 45, 200, 50)
                .onSelect((player, index) -> this.getSLF4JLogger().info("{} selected row {}", player.getName(), index)),
            new Button(Component.text("Close")).setAnchor(WidgetAnchor.BOTTOM_CENTER).setGeometry(-50, -25, 100, 20)
                .onClick(player -> player.spoutCraftRedux().closeScreen()));
        screen.onClose(player -> this.getSLF4JLogger().info("{} closed the test screen", player.getName()));
        return screen;
    }
}
