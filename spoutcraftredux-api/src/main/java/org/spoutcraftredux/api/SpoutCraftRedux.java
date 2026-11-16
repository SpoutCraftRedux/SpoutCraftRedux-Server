package org.spoutcraftredux.api;

import java.util.ServiceLoader;
import org.jspecify.annotations.Nullable;
import org.spoutcraftredux.api.asset.AssetRegistry;
import org.spoutcraftredux.api.content.ContentRegistry;
import org.spoutcraftredux.api.keybind.KeyBindingRegistry;

/**
 * Server-wide entry point to the SpoutCraft Redux API. Per-player features are on
 * {@link org.bukkit.entity.Player#spoutCraftRedux()}.
 */
public final class SpoutCraftRedux {
    private static @Nullable KeyBindingRegistry keyBindings;
    private static @Nullable AssetRegistry assets;
    private static @Nullable ContentRegistry content;

    private SpoutCraftRedux() {
    }

    /**
     * Gets the server's key bindings.
     *
     * @return the key binding registry
     */
    public static synchronized KeyBindingRegistry keyBindings() {
        if (keyBindings == null) {
            keyBindings = load(KeyBindingRegistry.class);
        }
        return keyBindings;
    }

    /**
     * Gets the server's images and resource pack files.
     *
     * @return the asset registry
     */
    public static synchronized AssetRegistry assets() {
        if (assets == null) {
            assets = load(AssetRegistry.class);
        }
        return assets;
    }

    /**
     * Gets the custom content registry. Safe to call from a plugin bootstrapper.
     *
     * @return the content registry
     */
    public static synchronized ContentRegistry content() {
        if (content == null) {
            content = load(ContentRegistry.class);
        }
        return content;
    }

    private static <T> T load(final Class<T> type) {
        return ServiceLoader.load(type, SpoutCraftRedux.class.getClassLoader())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("This server is not running SpoutCraft Redux"));
    }
}
