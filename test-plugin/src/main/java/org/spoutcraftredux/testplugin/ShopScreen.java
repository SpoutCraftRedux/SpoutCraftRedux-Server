package org.spoutcraftredux.testplugin;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.spoutcraftredux.api.asset.Asset;
import org.spoutcraftredux.api.gui.Button;
import org.spoutcraftredux.api.gui.ButtonState;
import org.spoutcraftredux.api.gui.Gradient;
import org.spoutcraftredux.api.gui.ItemWidget;
import org.spoutcraftredux.api.gui.Label;
import org.spoutcraftredux.api.gui.PopupScreen;
import org.spoutcraftredux.api.gui.TextAlign;
import org.spoutcraftredux.api.gui.Widget;
import org.spoutcraftredux.api.gui.WidgetAnchor;

/** A demo shop that sells items for emeralds from the player's inventory. */
final class ShopScreen {
    private record Offer(ItemType item, int price, String description) {
    }

    private static final List<Offer> OFFERS = List.of(
        new Offer(ItemType.DIAMOND, 3, "Shiny, hard and always in demand."),
        new Offer(ItemType.GOLDEN_APPLE, 5, "Regeneration and a little extra health."),
        new Offer(ItemType.ENDER_PEARL, 2, "Throw it to teleport where it lands."),
        new Offer(ItemType.IRON_SWORD, 4, "A trusty blade for any adventurer."),
        new Offer(ItemType.BREAD, 1, "Fresh from the village bakery."),
        new Offer(ItemType.EXPERIENCE_BOTTLE, 2, "Bottled experience. Throw to collect."),
        new Offer(ItemType.TOTEM_OF_UNDYING, 20, "Cheats death once, then crumbles."),
        new Offer(ItemType.ELYTRA, 40, "Wings for gliding through the sky."),
        new Offer(ItemType.FIREWORK_ROCKET, 1, "Boosts flight, or just looks pretty."));

    private static final int TILE = 40;
    private static final int GAP = 4;
    private static final int GRID_X = -144;
    private static final int GRID_Y = -68;

    private final PopupScreen screen = new PopupScreen(Component.text("Shop"));
    private final Label balance = centered(new Label(Component.empty()).setAlign(TextAlign.LEFT));
    private final Gradient selection = new Gradient(0xFFFFD86B, 0xFFB07A10);
    private final ItemWidget bigIcon = new ItemWidget(ItemType.DIAMOND);
    private final Label name = new Label(Component.empty()).setAlign(TextAlign.CENTER);
    private final Label description = new Label(Component.empty()).setAlign(TextAlign.CENTER).setWrap(true);
    private final Label price = new Label(Component.empty()).setAlign(TextAlign.CENTER);
    private final Label status = new Label(Component.text("Pick something nice.", NamedTextColor.GRAY));
    private final Button buyOne;
    private final Button buyEight;
    private int selected;
    private int emeralds;

    /** Gold images for the buy buttons. */
    record GoldButton(Asset normal, Asset hover, Asset disabled) {
        Button style(final Button button) {
            return button.setTexture(ButtonState.NORMAL, this.normal).setTexture(ButtonState.HOVER, this.hover)
                .setTexture(ButtonState.DISABLED, this.disabled).setTextureBorder(3).setTextShadow(false)
                .setTextColor(ButtonState.NORMAL, 0xFF3A2508).setTextColor(ButtonState.HOVER, 0xFF000000)
                .setTextColor(ButtonState.DISABLED, 0xFFB0B0B0);
        }
    }

    private static Button flat(final Button button, final int normal, final int hover) {
        return button.setBackground(ButtonState.NORMAL, normal).setBackground(ButtonState.HOVER, hover);
    }

    private static <T extends Widget<T>> T centered(final T widget) {
        return widget.setAnchor(WidgetAnchor.CENTER_CENTER);
    }

    private static int tileX(final int index) {
        return GRID_X + (index % 3) * (TILE + GAP);
    }

    private static int tileY(final int index) {
        return GRID_Y + (index / 3) * (TILE + GAP);
    }

    private static int emeralds(final Player player) {
        int count = 0;
        for (final ItemStack stack : player.getInventory().getStorageContents()) {
            if (stack != null && stack.getType().asItemType() == ItemType.EMERALD) {
                count += stack.getAmount();
            }
        }
        return count;
    }

    ShopScreen(final Player player, final GoldButton gold) {
        // Gold frame, dark body and a header strip
        this.screen.attach(
            centered(new Gradient(0xFFE8C060, 0xFF8A6418)).setGeometry(-152, -97, 304, 194),
            centered(new Gradient(0xF2241830, 0xF20E0814)).setGeometry(-150, -95, 300, 190),
            centered(new Gradient(0xFF4A2A6A, 0xFF2A1840)).setGeometry(-150, -95, 300, 22),
            centered(new Label(Component.text("✦ Emerald Emporium ✦", NamedTextColor.GOLD, TextDecoration.BOLD)))
                .setAlign(TextAlign.CENTER).setGeometry(-150, -88, 300, 9),
            centered(new ItemWidget(ItemType.EMERALD)).setGeometry(112, -91, 12, 12),
            this.balance.setGeometry(126, -88, 24, 9),
            centered(this.selection));

        for (int i = 0; i < OFFERS.size(); i++) {
            final int index = i;
            final Offer offer = OFFERS.get(i);
            final int x = tileX(i);
            final int y = tileY(i);
            this.screen.attach(
                flat(centered(new Button(Component.empty())), 0xFF2E1E40, 0xFF4E3470).setGeometry(x, y, TILE, TILE)
                    .setTooltip(Component.translatable(offer.item().translationKey()))
                    .onClick(clicker -> this.select(index)),
                centered(new ItemWidget(offer.item())).setGeometry(x + 10, y + 5, 20, 20),
                centered(new Label(Component.text(offer.price(), NamedTextColor.GREEN))).setAlign(TextAlign.CENTER)
                    .setGeometry(x, y + 28, TILE, 9));
        }

        // Details on the right
        this.screen.attach(
            centered(new Gradient(0x60000000, 0x30000000)).setGeometry(-2, -68, 146, 132),
            centered(this.bigIcon).setGeometry(47, -62, 48, 48),
            centered(this.name).setGeometry(-2, -10, 146, 9),
            centered(this.description).setGeometry(8, 2, 126, 20),
            centered(this.price).setGeometry(-2, 26, 146, 9),
            this.buyOne = gold.style(centered(new Button(Component.text("Buy 1")))).setGeometry(6, 40, 64, 20)
                .onClick(buyer -> this.buy(buyer, 1)),
            this.buyEight = gold.style(centered(new Button(Component.text("Buy 8")))).setGeometry(74, 40, 64, 20)
                .onClick(buyer -> this.buy(buyer, 8)),
            centered(this.status).setGeometry(-144, 76, 220, 9),
            flat(centered(new Button(Component.text("Close"))), 0xFF6A2020, 0xFF9A3030).setGeometry(90, 70, 54, 20)
                .onClick(closer -> closer.spoutCraftRedux().closeScreen()));

        this.select(0);
        this.updateBalance(player);
    }

    PopupScreen screen() {
        return this.screen;
    }

    private void select(final int index) {
        this.selected = index;
        final Offer offer = OFFERS.get(index);
        this.selection.setX(tileX(index) - 2).setY(tileY(index) - 2).setWidth(TILE + 4).setHeight(TILE + 4);
        this.bigIcon.setItem(offer.item());
        this.name.setText(Component.translatable(offer.item().translationKey(), NamedTextColor.YELLOW));
        this.description.setText(Component.text(offer.description(), NamedTextColor.GRAY));
        this.price.setText(Component.text("Price: ", NamedTextColor.WHITE)
            .append(Component.text(offer.price() + (offer.price() == 1 ? " emerald" : " emeralds"), NamedTextColor.GREEN)));
        if (this.buyOne != null) {
            this.updateButtons();
        }
    }

    private void updateBalance(final Player player) {
        this.emeralds = emeralds(player);
        this.balance.setText(Component.text(this.emeralds, NamedTextColor.GREEN));
        this.updateButtons();
    }

    private void updateButtons() {
        final int price = OFFERS.get(this.selected).price();
        this.buyOne.setEnabled(this.emeralds >= price);
        this.buyEight.setEnabled(this.emeralds >= price * 8);
    }

    private void buy(final Player player, final int amount) {
        final Offer offer = OFFERS.get(this.selected);
        final int cost = offer.price() * amount;
        final int have = emeralds(player);
        if (have < cost) {
            this.status.setText(Component.text("You need " + (cost - have) + " more emeralds.", NamedTextColor.RED));
            return;
        }
        player.getInventory().removeItem(ItemType.EMERALD.createItemStack(cost));
        int left = amount;
        final int max = offer.item().getMaxStackSize();
        while (left > 0) {
            final int count = Math.min(left, max);
            player.getInventory().addItem(offer.item().createItemStack(count)).values()
                .forEach(overflow -> player.getWorld().dropItem(player.getLocation(), overflow));
            left -= count;
        }
        this.updateBalance(player);
        this.status.setText(Component.text("Bought " + amount + " ", NamedTextColor.GREEN)
            .append(Component.translatable(offer.item().translationKey())).append(Component.text("!")));
    }
}
