package org.spoutcraftredux.api.gui;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Widgets drawn over the game for one player, from {@link org.spoutcraftredux.api.SpoutCraftReduxPlayer#hud()}. They
 * take no input. The HUD can also hide and move parts of the vanilla HUD. Players without the SpoutCraft Redux client
 * see none of this.
 */
public final class Hud extends WidgetContainer<Hud> {
    private final Set<VanillaHudElement> hidden = EnumSet.noneOf(VanillaHudElement.class);
    private final Map<VanillaHudElement, int[]> offsets = new EnumMap<>(VanillaHudElement.class);

    private void vanillaChanged() {
        final Sink sink = this.sink();
        if (sink != null) {
            sink.vanillaChanged();
        }
    }

    /**
     * Gets whether a vanilla HUD element is drawn.
     *
     * @param element the element
     * @return true unless hidden
     */
    public boolean isVanillaVisible(final VanillaHudElement element) {
        return !this.hidden.contains(element);
    }

    /**
     * Shows or hides a vanilla HUD element.
     *
     * @param element the element
     * @param visible whether to draw it
     * @return this HUD
     */
    public Hud setVanillaVisible(final VanillaHudElement element, final boolean visible) {
        if (visible ? this.hidden.remove(element) : this.hidden.add(element)) {
            this.vanillaChanged();
        }
        return this;
    }

    /**
     * Shows or hides the survival HUD: health, armor, food, air and experience.
     *
     * @param visible whether to draw it
     * @return this HUD
     */
    public Hud setSurvivalHudVisible(final boolean visible) {
        if (visible) {
            this.hidden.removeAll(VanillaHudElement.SURVIVAL);
        } else {
            this.hidden.addAll(VanillaHudElement.SURVIVAL);
        }
        this.vanillaChanged();
        return this;
    }

    /**
     * Gets how far a vanilla HUD element is moved sideways.
     *
     * @param element the element
     * @return the offset in GUI pixels, right is positive
     */
    public int getVanillaOffsetX(final VanillaHudElement element) {
        final int[] offset = this.offsets.get(element);
        return offset == null ? 0 : offset[0];
    }

    /**
     * Gets how far a vanilla HUD element is moved up or down.
     *
     * @param element the element
     * @return the offset in GUI pixels, down is positive
     */
    public int getVanillaOffsetY(final VanillaHudElement element) {
        final int[] offset = this.offsets.get(element);
        return offset == null ? 0 : offset[1];
    }

    /**
     * Moves a vanilla HUD element from where it is normally drawn.
     *
     * @param element the element
     * @param x       the sideways offset in GUI pixels, right is positive
     * @param y       the vertical offset in GUI pixels, down is positive
     * @return this HUD
     */
    public Hud setVanillaOffset(final VanillaHudElement element, final int x, final int y) {
        if (x == 0 && y == 0) {
            this.offsets.remove(element);
        } else {
            this.offsets.put(element, new int[] {x, y});
        }
        this.vanillaChanged();
        return this;
    }

    /**
     * Shows every vanilla HUD element in its normal place again.
     *
     * @return this HUD
     */
    public Hud resetVanilla() {
        this.hidden.clear();
        this.offsets.clear();
        this.vanillaChanged();
        return this;
    }
}
