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
 * @param id            the item id; any namespace except {@code minecraft}
 * @param name          the default display name
 * @param model         the item model id
 * @param maxStackSize  1 to 99
 * @param maxDamage     durability, or 0 for none; damageable items stack to 1
 * @param rarity        the rarity, which colours the name
 * @param fireResistant whether the dropped item survives fire and lava
 * @param food          food values, or null if it can't be eaten
 * @param tool          tool or weapon settings, or null
 * @param armor         armor settings, or null
 * @param equipSlot     a slot the item can be worn in without being armor, such as a hat; null for none
 * @param onUse         called when a player uses the item in the air
 * @param onUseOnBlock  called when a player uses the item on a block
 */
public record CustomItem(Key id, Component name, Key model, int maxStackSize, int maxDamage, ItemRarity rarity,
                         boolean fireResistant, @Nullable Food food, @Nullable Tool tool, @Nullable Armor armor,
                         @Nullable EquipmentSlot equipSlot, @Nullable UseHandler onUse,
                         @Nullable UseOnBlockHandler onUseOnBlock) {

    /**
     * Checks the definition.
     *
     * @throws IllegalArgumentException if a value is out of range or the settings conflict
     */
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
        if (tool != null && armor != null) {
            throw new IllegalArgumentException("An item can't be both a tool and armor");
        }
        if ((tool != null || armor != null) && maxStackSize != 1) {
            throw new IllegalArgumentException("Tools and armor stack to 1");
        }
        if (armor != null && equipSlot != null) {
            throw new IllegalArgumentException("Armor already has a slot");
        }
        if (equipSlot == EquipmentSlot.HAND || equipSlot == EquipmentSlot.OFF_HAND) {
            throw new IllegalArgumentException("Items can't be worn in a hand slot");
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
        /**
         * Checks the values.
         *
         * @throws IllegalArgumentException if nutrition is negative
         */
        public Food {
            if (nutrition < 0) {
                throw new IllegalArgumentException("Nutrition can't be negative");
            }
        }
    }

    /** Kinds of tool, each with the vanilla behaviour of that tool. */
    public enum ToolType {
        /** Mines cobwebs and sword-efficient blocks. */
        SWORD,
        /** Mines stone and ores. */
        PICKAXE,
        /** Mines wood, strips logs and disables shields. */
        AXE,
        /** Mines dirt and sand and makes paths. */
        SHOVEL,
        /** Mines leaves and crops and tills soil. */
        HOE
    }

    /** Vanilla tool tiers: durability, mining speed and level, enchantability and repair items. */
    public enum ToolMaterial {
        /** Like vanilla wooden tools. */
        WOOD,
        /** Like vanilla stone tools. */
        STONE,
        /** Like vanilla copper tools. */
        COPPER,
        /** Like vanilla iron tools. */
        IRON,
        /** Like vanilla diamond tools. */
        DIAMOND,
        /** Like vanilla golden tools. */
        GOLD,
        /** Like vanilla netherite tools. */
        NETHERITE
    }

    /** Vanilla armor materials: durability, defense, toughness, enchantability, sound, repair items and look. */
    public enum ArmorMaterial {
        /** Like vanilla leather armor. */
        LEATHER,
        /** Like vanilla copper armor. */
        COPPER,
        /** Like vanilla chainmail armor. */
        CHAINMAIL,
        /** Like vanilla iron armor. */
        IRON,
        /** Like vanilla golden armor. */
        GOLD,
        /** Like vanilla diamond armor. */
        DIAMOND,
        /** Like vanilla turtle shell armor. */
        TURTLE_SCUTE,
        /** Like vanilla netherite armor. */
        NETHERITE
    }

    /** Armor pieces. */
    public enum ArmorPiece {
        /** Worn on the head. */
        HELMET,
        /** Worn on the chest. */
        CHESTPLATE,
        /** Worn on the legs. */
        LEGGINGS,
        /** Worn on the feet. */
        BOOTS
    }

    /**
     * Tool or weapon settings. The attack values are the same baselines vanilla uses for its own tools; a diamond
     * sword is 3 and -2.4, a diamond pickaxe 1 and -2.8.
     *
     * @param type         the kind of tool
     * @param material     the tier
     * @param attackDamage attack damage added to the tier's bonus
     * @param attackSpeed  attack speed modifier
     */
    public record Tool(ToolType type, ToolMaterial material, float attackDamage, float attackSpeed) {
    }

    /**
     * Armor settings.
     *
     * @param material       the vanilla material for its stats
     * @param piece          the piece
     * @param equipmentAsset the worn look from the resource pack ({@code assets/<namespace>/equipment/<path>.json}),
     *                       or null to look like the material
     */
    public record Armor(ArmorMaterial material, ArmorPiece piece, @Nullable Key equipmentAsset) {
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
        private @Nullable Tool tool;
        private @Nullable Armor armor;
        private @Nullable EquipmentSlot equipSlot;
        private @Nullable UseHandler onUse;
        private @Nullable UseOnBlockHandler onUseOnBlock;

        private Builder(final Key id) {
            this.id = id;
        }

        /**
         * Sets the default name; defaults to the id's path.
         *
         * @param name the name
         * @return this builder
         */
        public Builder name(final Component name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the item model; defaults to the item id.
         *
         * @param model the model id
         * @return this builder
         */
        public Builder model(final Key model) {
            this.model = model;
            return this;
        }

        /**
         * Sets the stack size; defaults to 64.
         *
         * @param maxStackSize 1 to 99
         * @return this builder
         */
        public Builder maxStackSize(final int maxStackSize) {
            this.maxStackSize = maxStackSize;
            return this;
        }

        /**
         * Makes the item damageable, which also makes it stack to 1. Overrides the durability of a tool or armor
         * material.
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

        /**
         * Sets the rarity, which colours the name.
         *
         * @param rarity the rarity
         * @return this builder
         */
        public Builder rarity(final ItemRarity rarity) {
            this.rarity = rarity;
            return this;
        }

        /**
         * Sets whether the dropped item survives fire and lava.
         *
         * @param fireResistant true to survive
         * @return this builder
         */
        public Builder fireResistant(final boolean fireResistant) {
            this.fireResistant = fireResistant;
            return this;
        }

        /**
         * Makes the item edible.
         *
         * @param nutrition    hunger points restored
         * @param saturation   saturation modifier, as in vanilla
         * @param canAlwaysEat whether it can be eaten on a full hunger bar
         * @return this builder
         */
        public Builder food(final int nutrition, final float saturation, final boolean canAlwaysEat) {
            this.food = new Food(nutrition, saturation, canAlwaysEat);
            return this;
        }

        /**
         * Makes the item a tool or weapon, which also makes it stack to 1.
         *
         * @param type         the kind of tool
         * @param material     the tier
         * @param attackDamage attack damage baseline, as in {@link Tool}
         * @param attackSpeed  attack speed baseline, as in {@link Tool}
         * @return this builder
         */
        public Builder tool(final ToolType type, final ToolMaterial material, final float attackDamage, final float attackSpeed) {
            this.tool = new Tool(type, material, attackDamage, attackSpeed);
            this.maxStackSize = 1;
            return this;
        }

        /**
         * Makes the item armor that looks like its material when worn. It stacks to 1.
         *
         * @param material the material
         * @param piece    the piece
         * @return this builder
         */
        public Builder armor(final ArmorMaterial material, final ArmorPiece piece) {
            return this.armor(material, piece, null);
        }

        /**
         * Makes the item armor with its own worn look. It stacks to 1.
         *
         * @param material       the material for its stats
         * @param piece          the piece
         * @param equipmentAsset the worn look, as in {@link Armor}
         * @return this builder
         */
        public Builder armor(final ArmorMaterial material, final ArmorPiece piece, final @Nullable Key equipmentAsset) {
            this.armor = new Armor(material, piece, equipmentAsset);
            this.maxStackSize = 1;
            return this;
        }

        /**
         * Lets players wear the item in a slot, like a carved pumpkin on the head.
         *
         * @param slot the slot
         * @return this builder
         */
        public Builder equippable(final EquipmentSlot slot) {
            this.equipSlot = slot;
            return this;
        }

        /**
         * Sets what happens when a player uses the item in the air.
         *
         * @param onUse the handler
         * @return this builder
         */
        public Builder onUse(final UseHandler onUse) {
            this.onUse = onUse;
            return this;
        }

        /**
         * Sets what happens when a player uses the item on a block.
         *
         * @param onUseOnBlock the handler
         * @return this builder
         */
        public Builder onUseOnBlock(final UseOnBlockHandler onUseOnBlock) {
            this.onUseOnBlock = onUseOnBlock;
            return this;
        }

        /**
         * Builds the definition.
         *
         * @return the definition
         * @throws IllegalArgumentException if the settings conflict
         */
        public CustomItem build() {
            return new CustomItem(this.id, this.name != null ? this.name : Component.text(this.id.value()),
                this.model != null ? this.model : this.id, this.maxStackSize, this.maxDamage, this.rarity,
                this.fireResistant, this.food, this.tool, this.armor, this.equipSlot, this.onUse, this.onUseOnBlock);
        }
    }
}
