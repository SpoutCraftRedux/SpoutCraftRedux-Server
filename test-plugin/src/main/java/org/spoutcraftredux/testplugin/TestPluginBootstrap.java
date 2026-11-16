package org.spoutcraftredux.testplugin;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemRarity;
import org.spoutcraftredux.api.SpoutCraftRedux;
import org.spoutcraftredux.api.content.CustomItem;

public class TestPluginBootstrap implements PluginBootstrap {
    public static final Key RUBY = Key.key("spoutcraftredux_test", "ruby");
    public static final Key RUBY_WAND = Key.key("spoutcraftredux_test", "ruby_wand");
    public static final Key SNACK = Key.key("spoutcraftredux_test", "snack");

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
    }
}
