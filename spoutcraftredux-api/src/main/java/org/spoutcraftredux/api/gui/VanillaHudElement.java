package org.spoutcraftredux.api.gui;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * A part of the vanilla HUD that {@link Hud} can hide or move.
 */
public enum VanillaHudElement {
    /** The hotbar and offhand slot. */
    HOTBAR,
    /** The hearts. */
    HEALTH,
    /** The armor points. */
    ARMOR,
    /** The hunger bar. */
    FOOD,
    /** The air bubbles. */
    AIR,
    /** The health of the mount being ridden. */
    MOUNT_HEALTH,
    /** The experience bar, also used for the jump bar and the locator bar. */
    EXPERIENCE_BAR,
    /** The experience level number. */
    EXPERIENCE_LEVEL,
    /** The crosshair. */
    CROSSHAIR,
    /** The name shown above the hotbar when the held item changes. */
    HELD_ITEM_NAME,
    /** The potion effect icons. */
    EFFECTS,
    /** Boss bars. */
    BOSS_BAR,
    /** The sidebar scoreboard. */
    SCOREBOARD,
    /** The action bar message. */
    ACTION_BAR,
    /** Titles and subtitles. */
    TITLE,
    /** Chat. */
    CHAT,
    /** The tab player list. */
    PLAYER_LIST,
    /** Sound subtitles. */
    SUBTITLES;

    /**
     * The elements of the survival HUD: health, armor, food, air and experience.
     */
    public static final Set<VanillaHudElement> SURVIVAL = Collections.unmodifiableSet(
        EnumSet.of(HEALTH, ARMOR, FOOD, AIR, EXPERIENCE_BAR, EXPERIENCE_LEVEL));
}
