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
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.spoutcraftredux.api.content.CustomItem;

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
        final Properties properties = new Properties().setId(key)
            .stacksTo(definition.maxStackSize())
            .rarity(Rarity.valueOf(definition.rarity().name()));
        if (definition.maxDamage() > 0) {
            properties.durability(definition.maxDamage());
        }
        if (definition.fireResistant()) {
            properties.fireResistant();
        }
        final CustomItem.Food food = definition.food();
        if (food != null) {
            final FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(food.nutrition()).saturationModifier(food.saturation());
            properties.food((food.canAlwaysEat() ? builder.alwaysEdible() : builder).build());
        }
        final SpoutCraftReduxItem item = new SpoutCraftReduxItem(definition, properties);
        // Runs after the defaults Item adds, so the name and model win
        final net.minecraft.network.chat.Component name = PaperAdventure.asVanilla(definition.name());
        final Identifier model = Identifier.parse(definition.model().asString());
        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.add(key, (components, context, k) ->
            components.set(DataComponents.ITEM_NAME, name).set(DataComponents.ITEM_MODEL, model));
        return item;
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
