package org.spoutcraftredux.api.content;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * A custom item definition. Register it with {@link ContentRegistry#registerItem} from a plugin bootstrapper; the
 * server then has a real item with this id, and players with the client see it with its own model from the server's
 * resource pack ({@code assets/<namespace>/items/<path>.json}).
 *
 * @param id           the item id; any namespace except {@code minecraft}
 * @param name         the default display name
 * @param model        the item model id
 * @param maxStackSize 1 to 99
 * @param maxDamage    durability, or 0 for none; damageable items stack to 1
 * @param rarity       the rarity, which colours the name
 * @param fireResistant whether the dropped item survives fire and lava
 * @param food         food values, or null if it can't be eaten
 * @param onUse        called when a player uses the item in the air
 * @param onUseOnBlock called when a player uses the item on a block
 */
public record CustomItem(Key id, Component name, Key model, int maxStackSize, int maxDamage, ItemRarity rarity,
                         boolean fireResistant, @Nullable Food food, @Nullable UseHandler onUse,
                         @Nullable UseOnBlockHandler onUseOnBlock) {

    public CustomItem {
        Objects.requireNonNull(id, "id");
        if (id.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
            throw new IllegalArgumentException("Custom items can't use the minecraft namespace: " + id);
        }
        if (maxStackSize < 1 || maxStackSize > 99) {
            throw new IllegalArgumentException("Max stack size must be 1 to 99");
        }
        if (maxDamage < 0) {
            throw new IllegalArgumentException("Max damage can't be negative");
        }
        if (maxDamage > 0 && maxStackSize != 1) {
            throw new IllegalArgumentException("Damageable items stack to 1");
        }
    }

    /**
     * Starts a definition.
     *
     * @param id the item id
     * @return a builder
     */
    public static Builder builder(final Key id) {
        return new Builder(id);
    }

    /**
     * Food values.
     *
     * @param nutrition    hunger points restored
     * @param saturation   saturation modifier
     * @param canAlwaysEat whether it can be eaten on a full hunger bar
     */
    public record Food(int nutrition, float saturation, boolean canAlwaysEat) {
        public Food {
            if (nutrition < 0) {
                throw new IllegalArgumentException("Nutrition can't be negative");
            }
        }
    }

    /** Handles a player using the item in the air. */
    @FunctionalInterface
    public interface UseHandler {
        /**
         * Handles the use.
         *
         * @param player the player
         * @param hand   the hand holding the item
         * @param item   the item
         * @return true if the use did something, which swings the arm and stops vanilla handling
         */
        boolean use(Player player, EquipmentSlot hand, ItemStack item);
    }

    /** Handles a player using the item on a block. */
    @FunctionalInterface
    public interface UseOnBlockHandler {
        /**
         * Handles the use.
         *
         * @param player the player
         * @param hand   the hand holding the item
         * @param item   the item
         * @param block  the clicked block
         * @param face   the clicked face
         * @return true if the use did something
         */
        boolean useOn(Player player, EquipmentSlot hand, ItemStack item, Block block, BlockFace face);
    }

    /** Builds a {@link CustomItem}. */
    public static final class Builder {
        private final Key id;
        private @Nullable Component name;
        private @Nullable Key model;
        private int maxStackSize = 64;
        private int maxDamage;
        private ItemRarity rarity = ItemRarity.COMMON;
        private boolean fireResistant;
        private @Nullable Food food;
        private @Nullable UseHandler onUse;
        private @Nullable UseOnBlockHandler onUseOnBlock;

        private Builder(final Key id) {
            this.id = id;
        }

        public Builder name(final Component name) {
            this.name = name;
            return this;
        }

        public Builder model(final Key model) {
            this.model = model;
            return this;
        }

        public Builder maxStackSize(final int maxStackSize) {
            this.maxStackSize = maxStackSize;
            return this;
        }

        /**
         * Makes the item damageable, which also makes it stack to 1.
         *
         * @param maxDamage the durability
         * @return this builder
         */
        public Builder maxDamage(final int maxDamage) {
            this.maxDamage = maxDamage;
            if (maxDamage > 0) {
                this.maxStackSize = 1;
            }
            return this;
        }

        public Builder rarity(final ItemRarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder fireResistant(final boolean fireResistant) {
            this.fireResistant = fireResistant;
            return this;
        }

        public Builder food(final int nutrition, final float saturation, final boolean canAlwaysEat) {
            this.food = new Food(nutrition, saturation, canAlwaysEat);
            return this;
        }

        public Builder onUse(final UseHandler onUse) {
            this.onUse = onUse;
            return this;
        }

        public Builder onUseOnBlock(final UseOnBlockHandler onUseOnBlock) {
            this.onUseOnBlock = onUseOnBlock;
            return this;
        }

        public CustomItem build() {
            return new CustomItem(this.id, this.name != null ? this.name : Component.text(this.id.value()),
                this.model != null ? this.model : this.id, this.maxStackSize, this.maxDamage, this.rarity,
                this.fireResistant, this.food, this.onUse, this.onUseOnBlock);
        }
    }
}
