package org.spoutcraftredux.api.gui;

import org.bukkit.inventory.ItemType;

/**
 * An item icon, 16 by 16 unless sized otherwise.
 */
public final class ItemWidget extends Widget<ItemWidget> {
    private ItemType item;

    /**
     * Creates an item icon.
     *
     * @param item the item to draw
     */
    public ItemWidget(final ItemType item) {
        this.item = item;
    }

    /**
     * Gets the item.
     *
     * @return the item
     */
    public ItemType getItem() {
        return this.item;
    }

    /**
     * Sets the item.
     *
     * @param item the item to draw
     * @return this widget
     */
    public ItemWidget setItem(final ItemType item) {
        this.item = item;
        this.changed();
        return this;
    }
}
