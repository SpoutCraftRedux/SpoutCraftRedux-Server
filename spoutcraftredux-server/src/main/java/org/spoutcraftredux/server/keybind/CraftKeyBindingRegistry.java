package org.spoutcraftredux.server.keybind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;
import org.spoutcraftredux.api.keybind.KeyBinding;
import org.spoutcraftredux.api.keybind.KeyBindingRegistry;
import org.spoutcraftredux.api.keybind.KeyModifier;
import org.spoutcraftredux.protocol.Capabilities;
import org.spoutcraftredux.protocol.message.KeyBindings;
import org.spoutcraftredux.server.network.Payloads;

public final class CraftKeyBindingRegistry implements KeyBindingRegistry {
    private record Entry(Plugin plugin, KeyBinding binding) {
    }

    private final Map<Key, Entry> bindings = new LinkedHashMap<>();

    @Override
    public void register(final Plugin plugin, final KeyBinding binding) {
        this.bindings.put(binding.id(), new Entry(plugin, binding));
        this.sendToAll();
    }

    @Override
    public void unregister(final Key id) {
        if (this.bindings.remove(id) != null) {
            this.sendToAll();
        }
    }

    @Override
    public void unregisterAll(final Plugin plugin) {
        if (this.bindings.values().removeIf(entry -> entry.plugin() == plugin)) {
            this.sendToAll();
        }
    }

    @Override
    public @Nullable KeyBinding get(final Key id) {
        this.pruneDisabled();
        final Entry entry = this.bindings.get(id);
        return entry == null ? null : entry.binding();
    }

    @Override
    public Collection<KeyBinding> all() {
        this.pruneDisabled();
        return this.bindings.values().stream().map(Entry::binding).toList();
    }

    private boolean pruneDisabled() {
        return this.bindings.values().removeIf(entry -> !entry.plugin().isEnabled());
    }

    /**
     * Sends the current list to one player, if their client supports key bindings.
     *
     * @param player the player
     */
    public void sendTo(final Player player) {
        if (!player.spoutCraftRedux().hasCapability(Capabilities.KEYBIND)) {
            return;
        }
        this.pruneDisabled();
        final List<KeyBindings.Binding> wire = new ArrayList<>();
        for (final Entry entry : this.bindings.values()) {
            final KeyBinding binding = entry.binding();
            wire.add(new KeyBindings.Binding(
                binding.id().asString(),
                Payloads.json(binding.name()),
                entry.plugin().getName(),
                binding.defaultKey() == null ? "" : binding.defaultKey(),
                modifierBits(binding)));
        }
        Payloads.send(((CraftPlayer) player).getHandle(), KeyBindings.CHANNEL, new KeyBindings(wire).encode());
    }

    private void sendToAll() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            this.sendTo(player);
        }
    }

    private static int modifierBits(final KeyBinding binding) {
        int bits = 0;
        for (final KeyModifier modifier : binding.defaultModifiers()) {
            bits |= switch (modifier) {
                case CTRL -> KeyBindings.CTRL;
                case SHIFT -> KeyBindings.SHIFT;
                case ALT -> KeyBindings.ALT;
            };
        }
        return bits;
    }
}
