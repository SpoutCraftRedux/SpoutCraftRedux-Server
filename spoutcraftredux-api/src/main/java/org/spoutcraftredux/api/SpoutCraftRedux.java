package org.spoutcraftredux.api;

import java.util.ServiceLoader;
import org.jspecify.annotations.Nullable;
import org.spoutcraftredux.api.keybind.KeyBindingRegistry;

/**
 * Server-wide entry point to the SpoutCraft Redux API. Per-player features are on
 * {@link org.bukkit.entity.Player#spoutCraftRedux()}.
 */
public final class SpoutCraftRedux {
    private static @Nullable KeyBindingRegistry keyBindings;

    private SpoutCraftRedux() {
    }

    /**
     * Gets the server's key bindings.
     *
     * @return the key binding registry
     */
    public static synchronized KeyBindingRegistry keyBindings() {
        if (keyBindings == null) {
            keyBindings = ServiceLoader.load(KeyBindingRegistry.class, SpoutCraftRedux.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("This server is not running SpoutCraft Redux"));
        }
        return keyBindings;
    }
}
