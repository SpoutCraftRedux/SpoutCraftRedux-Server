package org.spoutcraftredux.api.keybind;

import java.util.Set;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

/**
 * A key binding declared by a plugin. Players with the SpoutCraft Redux client see it under Custom Bindings in their
 * controls screen and can change its key. Presses arrive as {@link org.spoutcraftredux.api.event.SpoutCraftReduxKeyBindingEvent}.
 *
 * @param id               unique id, such as {@code myplugin:open_menu}
 * @param name             the name shown in the controls screen
 * @param defaultKey       Minecraft's name for the default key, such as {@code key.keyboard.m} or {@code key.mouse.middle},
 *                         or null for no default key
 * @param defaultModifiers modifiers that must be held with the default key
 */
public record KeyBinding(Key id, Component name, @Nullable String defaultKey, Set<KeyModifier> defaultModifiers) {

    /**
     * Validates and copies the modifiers.
     *
     * @param id               unique id
     * @param name             the name shown in the controls screen
     * @param defaultKey       the default key, or null
     * @param defaultModifiers modifiers needed with the default key
     */
    public KeyBinding {
        if (defaultKey != null && !defaultKey.matches("key\\.(keyboard|mouse)\\.[a-z0-9._]+")) {
            throw new IllegalArgumentException("Default key must look like key.keyboard.m or key.mouse.left, got " + defaultKey);
        }
        defaultModifiers = Set.copyOf(defaultModifiers);
    }

    /**
     * Creates a binding.
     *
     * @param id         unique id
     * @param name       the name shown in the controls screen
     * @param defaultKey the default key, such as {@code key.keyboard.m}, or null for none
     * @param modifiers  modifiers needed with the default key
     * @return the binding
     */
    public static KeyBinding of(final Key id, final Component name, final @Nullable String defaultKey, final KeyModifier... modifiers) {
        return new KeyBinding(id, name, defaultKey, Set.of(modifiers));
    }
}
