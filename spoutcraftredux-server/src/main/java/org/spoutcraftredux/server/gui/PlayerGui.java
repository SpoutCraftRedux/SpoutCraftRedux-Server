package org.spoutcraftredux.server.gui;

import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jspecify.annotations.Nullable;
import org.spoutcraftredux.api.gui.Button;
import org.spoutcraftredux.api.gui.CheckBox;
import org.spoutcraftredux.api.gui.Hud;
import org.spoutcraftredux.api.gui.ListWidget;
import org.spoutcraftredux.api.gui.PopupScreen;
import org.spoutcraftredux.api.gui.Slider;
import org.spoutcraftredux.api.gui.TextField;
import org.spoutcraftredux.api.gui.Widget;
import org.spoutcraftredux.api.gui.WidgetContainer;
import org.spoutcraftredux.protocol.Capabilities;
import org.spoutcraftredux.protocol.message.ControlEvent;
import org.spoutcraftredux.protocol.message.ScreenClose;
import org.spoutcraftredux.protocol.message.ScreenOpen;
import org.spoutcraftredux.protocol.message.WidgetRemove;
import org.spoutcraftredux.protocol.message.WidgetUpdate;
import org.spoutcraftredux.server.network.Payloads;

/** One player's HUD and popup screen, kept in step with their client. */
public final class PlayerGui {
    private final CraftPlayer player;
    private final Hud hud = new Hud();
    private @Nullable PopupScreen screen;
    private int screenId;
    private int nextScreenId;

    /**
     * Creates the GUI state for a player.
     *
     * @param player the player
     */
    public PlayerGui(final CraftPlayer player) {
        this.player = player;
        this.hud.bind(this.sink(WidgetUpdate.HUD, this.hud));
    }

    private ServerPlayer handle() {
        return this.player.getHandle();
    }

    private boolean supports(final String capability) {
        return this.player.spoutCraftRedux().hasCapability(capability);
    }

    private WidgetContainer.Sink sink(final int id, final WidgetContainer<?> container) {
        return new WidgetContainer.Sink() {
            @Override
            public void update(final Widget<?> widget) {
                if (PlayerGui.this.supports(id == WidgetUpdate.HUD ? Capabilities.HUD : Capabilities.UI)) {
                    Payloads.send(PlayerGui.this.handle(), WidgetUpdate.CHANNEL, new WidgetUpdate(id, List.of(WidgetSpecs.of(widget))).encode());
                }
            }

            @Override
            public void remove(final Widget<?> widget) {
                if (PlayerGui.this.supports(id == WidgetUpdate.HUD ? Capabilities.HUD : Capabilities.UI)) {
                    Payloads.send(PlayerGui.this.handle(), WidgetRemove.CHANNEL, new WidgetRemove(id, List.of(widget.getId())).encode());
                }
            }

            @Override
            public void close() {
                if (PlayerGui.this.screen == container) {
                    PlayerGui.this.closeScreen();
                }
            }
        };
    }

    /**
     * Gets the HUD.
     *
     * @return the HUD
     */
    public Hud hud() {
        return this.hud;
    }

    /**
     * Sends the whole HUD, for when the player has just joined.
     */
    public void sendHud() {
        if (this.supports(Capabilities.HUD) && !this.hud.getWidgets().isEmpty()) {
            Payloads.send(this.handle(), WidgetUpdate.CHANNEL,
                new WidgetUpdate(WidgetUpdate.HUD, this.hud.getWidgets().stream().map(WidgetSpecs::of).toList()).encode());
        }
    }

    /**
     * Opens a popup screen.
     *
     * @param screen the screen
     */
    public void openScreen(final PopupScreen screen) {
        if (!this.supports(Capabilities.UI)) {
            return;
        }
        if (this.screen != null) {
            this.screen.bind(null);
        }
        this.screen = screen;
        this.screenId = ++this.nextScreenId;
        screen.bind(this.sink(this.screenId, screen));
        Payloads.send(this.handle(), ScreenOpen.CHANNEL, new ScreenOpen(this.screenId, Payloads.json(screen.getTitle()),
            screen.getWidgets().stream().map(WidgetSpecs::of).toList()).encode());
    }

    /**
     * Gets the open popup.
     *
     * @return the screen, or null
     */
    public @Nullable PopupScreen openScreen() {
        return this.screen;
    }

    /**
     * Closes the open popup.
     */
    public void closeScreen() {
        if (this.screen == null) {
            return;
        }
        this.screen.bind(null);
        this.screen = null;
        Payloads.send(this.handle(), ScreenClose.CLIENTBOUND, new ScreenClose(this.screenId).encode());
    }

    /**
     * Handles the player closing the popup themselves.
     *
     * @param message the message
     */
    public void playerClosed(final ScreenClose message) {
        final PopupScreen open = this.screen;
        if (open != null && message.screenId() == this.screenId) {
            open.bind(null);
            this.screen = null;
            open.handleClose(this.player);
        }
    }

    /**
     * Handles the player using a widget on the open popup. Events for other screens or the wrong widget type are
     * dropped.
     *
     * @param event the event
     */
    public void control(final ControlEvent event) {
        final PopupScreen open = this.screen;
        if (open == null || event.screenId() != this.screenId) {
            return;
        }
        final Widget<?> widget = open.find(event.widgetId());
        if (widget == null || !widget.isEnabled() || !widget.isVisible()) {
            return;
        }
        switch (widget) {
            case Button button when event.action() == ControlEvent.CLICK -> button.handleClick(this.player);
            case CheckBox box when event.action() == ControlEvent.CHECK -> box.handleChange(this.player, event.flag());
            case TextField field when event.action() == ControlEvent.TEXT -> field.handleText(this.player, event.text(), false);
            case TextField field when event.action() == ControlEvent.SUBMIT -> field.handleText(this.player, event.text(), true);
            case Slider slider when event.action() == ControlEvent.SLIDE -> slider.handleSlide(this.player, event.number());
            case ListWidget list when event.action() == ControlEvent.SELECT -> list.handleSelect(this.player, event.index());
            default -> {
                // Wrong action for this widget: ignore
            }
        }
    }
}
