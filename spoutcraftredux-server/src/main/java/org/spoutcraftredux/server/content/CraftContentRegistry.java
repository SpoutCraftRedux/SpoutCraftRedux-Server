package org.spoutcraftredux.server.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spoutcraftredux.api.SpoutCraftRedux;
import org.spoutcraftredux.api.content.ContentRegistry;
import org.spoutcraftredux.api.content.CustomItem;
import org.spoutcraftredux.protocol.content.ContentKeys;
import org.spoutcraftredux.protocol.message.Content;
import org.spoutcraftredux.server.SpoutCraftReduxServer;
import org.spoutcraftredux.server.network.Payloads;

/** Holds custom content from plugin bootstrappers and adds it to the built-in registries before they freeze. */
public final class CraftContentRegistry implements ContentRegistry {
    private final Map<Key, CustomItem> items = new LinkedHashMap<>();
    private final Map<Key, SpoutCraftReduxItem> registeredItems = new LinkedHashMap<>();
    private boolean frozen;
    private @Nullable Content content;

    /**
     * Gets the registry.
     *
     * @return the registry
     */
    public static CraftContentRegistry get() {
        return (CraftContentRegistry) SpoutCraftRedux.content();
    }

    @Override
    public synchronized void registerItem(final CustomItem item) {
        if (this.frozen) {
            throw new IllegalStateException("Custom items must be registered from a plugin bootstrapper");
        }
        if (this.items.putIfAbsent(item.id(), item) != null) {
            throw new IllegalStateException("A custom item with id " + item.id() + " is already registered");
        }
    }

    /**
     * Adds every custom entry to the built-in registries. Called once, right before they freeze.
     */
    public synchronized void registerAll() {
        this.frozen = true;
        for (final CustomItem definition : this.items.values()) {
            final SpoutCraftReduxItem item = SpoutCraftReduxItem.create(definition);
            Registry.register(BuiltInRegistries.ITEM, Identifier.parse(definition.id().asString()), item);
            this.registeredItems.put(definition.id(), item);
        }
        if (!this.items.isEmpty()) {
            SpoutCraftReduxServer.LOGGER.info("Registered {} custom items", this.items.size());
        }
    }

    /**
     * Whether any custom content is registered, which makes the client required.
     *
     * @return true if there is custom content
     */
    public boolean hasContent() {
        return !this.registeredItems.isEmpty();
    }

    /**
     * Gets the content message for clients, built once.
     *
     * @return the message
     */
    public synchronized Content content() {
        if (this.content == null) {
            final List<Content.Entry> entries = new ArrayList<>();
            for (final SpoutCraftReduxItem item : this.registeredItems.values()) {
                final CustomItem definition = item.definition();
                final Map<String, Object> properties = new LinkedHashMap<>(SpoutCraftReduxItem.definition(definition));
                properties.put(ContentKeys.NAME, Payloads.json(definition.name()));
                entries.add(new Content.Entry(ContentKeys.ITEM, definition.id().asString(), BuiltInRegistries.ITEM.getId(item), properties));
            }
            this.content = new Content(entries);
        }
        return this.content;
    }

    @Override
    public @Nullable CustomItem item(final Key id) {
        return this.items.get(id);
    }

    @Override
    public Collection<CustomItem> items() {
        return Collections.unmodifiableCollection(this.items.values());
    }

    @Override
    public @Nullable CustomItem item(final ItemStack stack) {
        return CraftItemStack.unwrap(stack).getItem() instanceof SpoutCraftReduxItem item ? item.definition() : null;
    }

    @Override
    public ItemStack createStack(final CustomItem item, final int amount) {
        final SpoutCraftReduxItem registered = this.registeredItems.get(item.id());
        if (registered == null) {
            throw new IllegalArgumentException("Custom item " + item.id() + " is not registered");
        }
        return CraftItemStack.asBukkitMirror(new net.minecraft.world.item.ItemStack(registered, amount));
    }
}
