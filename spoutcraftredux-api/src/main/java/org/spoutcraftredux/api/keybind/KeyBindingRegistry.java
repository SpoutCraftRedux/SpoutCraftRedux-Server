package org.spoutcraftredux.api.keybind;

import java.util.Collection;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

/**
 * The server's key bindings. Changes are sent straight to every player with the SpoutCraft Redux client. Bindings of
 * a disabled plugin are dropped automatically.
 */
public interface KeyBindingRegistry {

    /**
     * Registers a binding, replacing one with the same id.
     *
     * @param plugin  the plugin that owns the binding
     * @param binding the binding
     */
    void register(Plugin plugin, KeyBinding binding);

    /**
     * Removes a binding.
     *
     * @param id the binding's id
     */
    void unregister(Key id);

    /**
     * Removes every binding a plugin registered.
     *
     * @param plugin the plugin
     */
    void unregisterAll(Plugin plugin);

    /**
     * Gets a binding.
     *
     * @param id the binding's id
     * @return the binding, or null if none is registered
     */
    @Nullable KeyBinding get(Key id);

    /**
     * Gets every registered binding.
     *
     * @return the bindings, in registration order
     */
    Collection<KeyBinding> all();
}
