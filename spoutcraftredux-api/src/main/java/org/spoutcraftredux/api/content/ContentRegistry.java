package org.spoutcraftredux.api.content;

import java.util.Collection;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * The server's custom content. Content is registered from a plugin bootstrapper, before the server's registries
 * freeze; registering later throws. Once any content is registered, players without the SpoutCraft Redux client are
 * disconnected while joining with the {@code missing-client.kick-message}.
 */
public interface ContentRegistry {

    /**
     * Registers a custom item. Call it from {@code PluginBootstrap#bootstrap}.
     *
     * @param item the definition
     * @throws IllegalStateException if the registries are already frozen or the id is taken
     */
    void registerItem(CustomItem item);

    /**
     * Gets a custom item.
     *
     * @param id the item id
     * @return the definition, or null if none is registered
     */
    @Nullable CustomItem item(Key id);

    /**
     * Gets every custom item.
     *
     * @return the definitions, in registration order
     */
    Collection<CustomItem> items();

    /**
     * Gets the custom item a stack is.
     *
     * @param stack the stack
     * @return the definition, or null if it's not a custom item
     */
    @Nullable CustomItem item(ItemStack stack);

    /**
     * Creates a stack of a custom item. Only works once the server has started.
     *
     * @param item   the item
     * @param amount the stack size
     * @return the stack
     */
    ItemStack createStack(CustomItem item, int amount);
}
