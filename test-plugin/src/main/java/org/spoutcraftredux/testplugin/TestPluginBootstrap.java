package org.spoutcraftredux.testplugin;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.spoutcraftredux.api.SpoutCraftRedux;
import org.spoutcraftredux.api.content.CustomItem;

public class TestPluginBootstrap implements PluginBootstrap {
    public static final Key RUBY = Key.key("spoutcraftredux_test", "ruby");
    public static final Key RUBY_WAND = Key.key("spoutcraftredux_test", "ruby_wand");
    public static final Key SNACK = Key.key("spoutcraftredux_test", "snack");
    public static final Key RUBY_SWORD = Key.key("spoutcraftredux_test", "ruby_sword");
    public static final Key RUBY_HELMET = Key.key("spoutcraftredux_test", "ruby_helmet");
    public static final Key PARTY_HAT = Key.key("spoutcraftredux_test", "party_hat");

    @Override
    public void bootstrap(final BootstrapContext context) {
        SpoutCraftRedux.content().registerItem(CustomItem.builder(RUBY)
            .name(Component.text("Ruby"))
            .rarity(ItemRarity.UNCOMMON)
            .onUse((player, hand, item) -> {
                player.sendActionBar(Component.text("The ruby glows", NamedTextColor.RED));
                return true;
            })
            .build());
        SpoutCraftRedux.content().registerItem(CustomItem.builder(RUBY_WAND)
            .name(Component.text("Ruby Wand"))
            .rarity(ItemRarity.RARE)
            .maxDamage(32)
            .fireResistant(true)
            .onUseOnBlock((player, hand, item, block, face) -> {
                block.getWorld().strikeLightningEffect(block.getRelative(face).getLocation());
                item.damage(1, player);
                return true;
            })
            .build());
        SpoutCraftRedux.content().registerItem(CustomItem.builder(SNACK)
            .name(Component.text("Snack"))
            .maxStackSize(16)
            .food(3, 0.6f, true)
            .build());
        SpoutCraftRedux.content().registerItem(CustomItem.builder(RUBY_SWORD)
            .name(Component.text("Ruby Sword"))
            .tool(CustomItem.ToolType.SWORD, CustomItem.ToolMaterial.DIAMOND, 4, -2.4f)
            .build());
        SpoutCraftRedux.content().registerItem(CustomItem.builder(RUBY_HELMET)
            .name(Component.text("Ruby Helmet"))
            .armor(CustomItem.ArmorMaterial.DIAMOND, CustomItem.ArmorPiece.HELMET, Key.key("spoutcraftredux_test", "ruby"))
            .build());
        SpoutCraftRedux.content().registerItem(CustomItem.builder(PARTY_HAT)
            .name(Component.text("Party Hat"))
            .maxStackSize(1)
            .equippable(EquipmentSlot.HEAD)
            .build());
    }
}
