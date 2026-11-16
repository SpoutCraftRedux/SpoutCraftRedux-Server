package org.spoutcraftredux.api.gui;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import org.spoutcraftredux.api.asset.Asset;

/**
 * A button. 200 by 20 unless sized otherwise. It looks like a vanilla button until styled: give it server images
 * with {@link #setTexture(ButtonState, Asset)}, flat colours with {@link #setBackground(ButtonState, Integer)}, and
 * text colours with {@link #setTextColor(ButtonState, Integer)}. An image takes priority over a background colour.
 */
public final class Button extends Widget<Button> {
    private Component text;
    private TextAlign align = TextAlign.CENTER;
    private final Map<ButtonState, Asset> textures = new EnumMap<>(ButtonState.class);
    private final Map<ButtonState, Integer> backgrounds = new EnumMap<>(ButtonState.class);
    private final Map<ButtonState, Integer> textColors = new EnumMap<>(ButtonState.class);
    private int textureBorder;
    private boolean textShadow = true;
    private Consumer<Player> onClick = player -> {
    };

    /**
     * Creates a button.
     *
     * @param text the label
     */
    public Button(final Component text) {
        this.text = text;
    }

    /**
     * Gets the label.
     *
     * @return the label
     */
    public Component getText() {
        return this.text;
    }

    /**
     * Sets the label.
     *
     * @param text the label
     * @return this button
     */
    public Button setText(final Component text) {
        this.text = text;
        this.changed();
        return this;
    }

    /**
     * Gets the label alignment.
     *
     * @return the alignment
     */
    public TextAlign getAlign() {
        return this.align;
    }

    /**
     * Sets the label alignment.
     *
     * @param align the alignment
     * @return this button
     */
    public Button setAlign(final TextAlign align) {
        this.align = align;
        this.changed();
        return this;
    }

    private static <V> void put(final Map<ButtonState, V> map, final ButtonState state, final @Nullable V value) {
        if (value == null) {
            map.remove(state);
        } else {
            map.put(state, value);
        }
    }

    /**
     * Gets the image drawn for a state.
     *
     * @param state the state
     * @return the image, or null for none
     */
    public @Nullable Asset getTexture(final ButtonState state) {
        return this.textures.get(state);
    }

    /**
     * Draws a server image as the button for a state, stretched to the button's size.
     *
     * @param state   the state
     * @param texture an image from {@link org.spoutcraftredux.api.asset.AssetRegistry}, or null for none
     * @return this button
     */
    public Button setTexture(final ButtonState state, final @Nullable Asset texture) {
        put(this.textures, state, texture);
        this.changed();
        return this;
    }

    /**
     * Draws a server image as the button in every state.
     *
     * @param texture an image from {@link org.spoutcraftredux.api.asset.AssetRegistry}, or null for none
     * @return this button
     */
    public Button setTexture(final @Nullable Asset texture) {
        return this.setTexture(ButtonState.NORMAL, texture);
    }

    /**
     * Gets how many image pixels at each edge are kept unstretched.
     *
     * @return the border, 0 when the whole image is stretched
     */
    public int getTextureBorder() {
        return this.textureBorder;
    }

    /**
     * Keeps the edges of the images sharp: this many pixels at each edge are drawn at their own size and only the
     * middle is stretched.
     *
     * @param border the border in image pixels, 0 to stretch the whole image
     * @return this button
     */
    public Button setTextureBorder(final int border) {
        this.textureBorder = Math.max(0, border);
        this.changed();
        return this;
    }

    /**
     * Gets the flat background colour for a state.
     *
     * @param state the state
     * @return the colour as ARGB, or null for none
     */
    public @Nullable Integer getBackground(final ButtonState state) {
        return this.backgrounds.get(state);
    }

    /**
     * Fills the button with a flat colour for a state instead of the vanilla texture.
     *
     * @param state the state
     * @param argb  the colour as ARGB, or null for none
     * @return this button
     */
    public Button setBackground(final ButtonState state, final @Nullable Integer argb) {
        put(this.backgrounds, state, argb);
        this.changed();
        return this;
    }

    /**
     * Gets the text colour for a state.
     *
     * @param state the state
     * @return the colour as ARGB, or null for the vanilla colour
     */
    public @Nullable Integer getTextColor(final ButtonState state) {
        return this.textColors.get(state);
    }

    /**
     * Sets the text colour for a state. Colours in the label itself still win.
     *
     * @param state the state
     * @param argb  the colour as ARGB, or null for the vanilla colour
     * @return this button
     */
    public Button setTextColor(final ButtonState state, final @Nullable Integer argb) {
        put(this.textColors, state, argb);
        this.changed();
        return this;
    }

    /**
     * Gets whether the text has a shadow.
     *
     * @return true for a shadow
     */
    public boolean hasTextShadow() {
        return this.textShadow;
    }

    /**
     * Sets whether the text has a shadow.
     *
     * @param shadow true for a shadow
     * @return this button
     */
    public Button setTextShadow(final boolean shadow) {
        this.textShadow = shadow;
        this.changed();
        return this;
    }

    /**
     * Runs when the player clicks the button.
     *
     * @param onClick the callback
     * @return this button
     */
    public Button onClick(final Consumer<Player> onClick) {
        this.onClick = onClick;
        return this;
    }

    /**
     * Handles a click from the player.
     *
     * @param player the player
     */
    @ApiStatus.Internal
    public void handleClick(final Player player) {
        this.onClick.accept(player);
    }
}
