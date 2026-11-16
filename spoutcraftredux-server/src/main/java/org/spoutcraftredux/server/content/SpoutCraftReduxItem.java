package org.spoutcraftredux.server.content;

import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.spoutcraftredux.api.content.CustomItem;
import org.spoutcraftredux.protocol.content.ContentKeys;

/** A real item backed by a {@link CustomItem} definition. */
public final class SpoutCraftReduxItem extends Item {
    private final CustomItem definition;

    private SpoutCraftReduxItem(final CustomItem definition, final Properties properties) {
        super(properties);
        this.definition = definition;
    }

    /**
     * Creates the item and its component defaults. The registry must still be open.
     *
     * @param definition the definition
     * @return the item, not yet registered
     */
    static SpoutCraftReduxItem create(final CustomItem definition) {
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.parse(definition.id().asString()));
        final SpoutCraftReduxItem item = new SpoutCraftReduxItem(definition, properties(key, definition(definition)));
        // Runs after the defaults Item adds, so the name and model win
        final net.minecraft.network.chat.Component name = PaperAdventure.asVanilla(definition.name());
        final Identifier model = Identifier.parse(definition.model().asString());
        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.add(key, (components, context, k) ->
            components.set(DataComponents.ITEM_NAME, name).set(DataComponents.ITEM_MODEL, model));
        return item;
    }

    /**
     * Describes an item the way the client receives it. The name is added separately as JSON.
     *
     * @param definition the definition
     * @return the content properties
     */
    static Map<String, Object> definition(final CustomItem definition) {
        final Map<String, Object> properties = new LinkedHashMap<>();
        properties.put(ContentKeys.MODEL, definition.model().asString());
        properties.put(ContentKeys.MAX_STACK_SIZE, definition.maxStackSize());
        properties.put(ContentKeys.MAX_DAMAGE, definition.maxDamage());
        properties.put(ContentKeys.RARITY, lower(definition.rarity()));
        properties.put(ContentKeys.FIRE_RESISTANT, definition.fireResistant());
        final CustomItem.Food food = definition.food();
        if (food != null) {
            properties.put(ContentKeys.NUTRITION, food.nutrition());
            properties.put(ContentKeys.SATURATION, food.saturation());
            properties.put(ContentKeys.CAN_ALWAYS_EAT, food.canAlwaysEat());
        }
        final CustomItem.Tool tool = definition.tool();
        if (tool != null) {
            properties.put(ContentKeys.TOOL, lower(tool.type()));
            properties.put(ContentKeys.TOOL_MATERIAL, lower(tool.material()));
            properties.put(ContentKeys.ATTACK_DAMAGE, tool.attackDamage());
            properties.put(ContentKeys.ATTACK_SPEED, tool.attackSpeed());
        }
        final CustomItem.Armor armor = definition.armor();
        if (armor != null) {
            properties.put(ContentKeys.ARMOR_MATERIAL, lower(armor.material()));
            properties.put(ContentKeys.ARMOR_PIECE, lower(armor.piece()));
            if (armor.equipmentAsset() != null) {
                properties.put(ContentKeys.EQUIPMENT_ASSET, armor.equipmentAsset().asString());
            }
        }
        if (definition.equipSlot() != null) {
            properties.put(ContentKeys.EQUIP_SLOT, CraftEquipmentSlot.getNMS(definition.equipSlot()).getSerializedName());
        }
        return properties;
    }

    private static String lower(final Enum<?> value) {
        return value.name().toLowerCase(Locale.ROOT);
    }

    // The client's ClientContent#properties must stay the same as this
    private static Properties properties(final ResourceKey<Item> key, final Map<String, Object> definition) {
        final Properties properties = new Properties().setId(key)
            .stacksTo((Integer) definition.get(ContentKeys.MAX_STACK_SIZE))
            .rarity(Rarity.valueOf(((String) definition.get(ContentKeys.RARITY)).toUpperCase(Locale.ROOT)));
        if (definition.get(ContentKeys.TOOL) instanceof String tool) {
            final ToolMaterial material = toolMaterial((String) definition.get(ContentKeys.TOOL_MATERIAL));
            final float damage = (Float) definition.get(ContentKeys.ATTACK_DAMAGE);
            final float speed = (Float) definition.get(ContentKeys.ATTACK_SPEED);
            switch (tool) {
                case "sword" -> properties.sword(material, damage, speed);
                case "pickaxe" -> properties.pickaxe(material, damage, speed);
                case "axe" -> properties.axe(material, damage, speed);
                case "shovel" -> properties.shovel(material, damage, speed);
                case "hoe" -> properties.hoe(material, damage, speed);
                default -> throw new IllegalArgumentException("Unknown tool " + tool);
            }
        }
        if (definition.get(ContentKeys.ARMOR_MATERIAL) instanceof String armor) {
            ArmorMaterial material = armorMaterial(armor);
            if (definition.get(ContentKeys.EQUIPMENT_ASSET) instanceof String asset) {
                material = new ArmorMaterial(material.durability(), material.defense(), material.enchantmentValue(), material.equipSound(),
                    material.toughness(), material.knockbackResistance(), material.repairIngredient(),
                    ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.parse(asset)));
            }
            properties.humanoidArmor(material, ArmorType.valueOf(((String) definition.get(ContentKeys.ARMOR_PIECE)).toUpperCase(Locale.ROOT)));
        }
        if (definition.get(ContentKeys.EQUIP_SLOT) instanceof String slot) {
            properties.equippable(EquipmentSlot.byName(slot));
        }
        final int maxDamage = (Integer) definition.get(ContentKeys.MAX_DAMAGE);
        if (maxDamage > 0) {
            properties.durability(maxDamage);
        }
        if (Boolean.TRUE.equals(definition.get(ContentKeys.FIRE_RESISTANT))) {
            properties.fireResistant();
        }
        if (definition.get(ContentKeys.NUTRITION) instanceof Integer nutrition) {
            final FoodProperties.Builder food = new FoodProperties.Builder().nutrition(nutrition)
                .saturationModifier((Float) definition.get(ContentKeys.SATURATION));
            properties.food((Boolean.TRUE.equals(definition.get(ContentKeys.CAN_ALWAYS_EAT)) ? food.alwaysEdible() : food).build());
        }
        return properties;
    }

    private static ToolMaterial toolMaterial(final String name) {
        return switch (name) {
            case "wood" -> ToolMaterial.WOOD;
            case "stone" -> ToolMaterial.STONE;
            case "copper" -> ToolMaterial.COPPER;
            case "iron" -> ToolMaterial.IRON;
            case "diamond" -> ToolMaterial.DIAMOND;
            case "gold" -> ToolMaterial.GOLD;
            case "netherite" -> ToolMaterial.NETHERITE;
            default -> throw new IllegalArgumentException("Unknown tool material " + name);
        };
    }

    private static ArmorMaterial armorMaterial(final String name) {
        return switch (name) {
            case "leather" -> ArmorMaterials.LEATHER;
            case "copper" -> ArmorMaterials.COPPER;
            case "chainmail" -> ArmorMaterials.CHAINMAIL;
            case "iron" -> ArmorMaterials.IRON;
            case "gold" -> ArmorMaterials.GOLD;
            case "diamond" -> ArmorMaterials.DIAMOND;
            case "turtle_scute" -> ArmorMaterials.TURTLE_SCUTE;
            case "netherite" -> ArmorMaterials.NETHERITE;
            default -> throw new IllegalArgumentException("Unknown armor material " + name);
        };
    }

    /**
     * Gets the definition.
     *
     * @return the definition
     */
    public CustomItem definition() {
        return this.definition;
    }

    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        final CustomItem.UseHandler handler = this.definition.onUse();
        if (handler != null && !level.isClientSide()
            && handler.use((org.bukkit.entity.Player) player.getBukkitEntity(), CraftEquipmentSlot.getHand(hand), CraftItemStack.asBukkitMirror(player.getItemInHand(hand)))) {
            return InteractionResult.SUCCESS;
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final CustomItem.UseOnBlockHandler handler = this.definition.onUseOnBlock();
        final Player player = context.getPlayer();
        if (handler != null && player != null && !context.getLevel().isClientSide()
            && handler.useOn((org.bukkit.entity.Player) player.getBukkitEntity(), CraftEquipmentSlot.getHand(context.getHand()),
                CraftItemStack.asBukkitMirror(context.getItemInHand()), CraftBlock.at(context.getLevel(), context.getClickedPos()),
                CraftBlock.notchToBlockFace(context.getClickedFace()))) {
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
