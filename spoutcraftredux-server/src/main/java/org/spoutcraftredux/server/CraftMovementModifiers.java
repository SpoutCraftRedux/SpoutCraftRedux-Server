package org.spoutcraftredux.server;

import com.google.common.base.Preconditions;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jspecify.annotations.Nullable;
import org.spoutcraftredux.api.MovementModifiers;

/**
 * Stores each multiplier as a transient attribute modifier, so it never ends up in the player's saved data.
 */
public final class CraftMovementModifiers implements MovementModifiers {
    private static final NamespacedKey GRAVITY = new NamespacedKey("spoutcraftredux", "gravity_multiplier");
    private static final NamespacedKey WALKING = new NamespacedKey("spoutcraftredux", "walking_multiplier");
    private static final NamespacedKey JUMPING = new NamespacedKey("spoutcraftredux", "jumping_multiplier");

    private final CraftPlayer player;

    public CraftMovementModifiers(final CraftPlayer player) {
        this.player = player;
    }

    @Override
    public double getGravityMultiplier() {
        return this.get(Attribute.GRAVITY, GRAVITY);
    }

    @Override
    public void setGravityMultiplier(final double multiplier) {
        this.set(Attribute.GRAVITY, GRAVITY, multiplier);
    }

    @Override
    public double getWalkingMultiplier() {
        return this.get(Attribute.MOVEMENT_SPEED, WALKING);
    }

    @Override
    public void setWalkingMultiplier(final double multiplier) {
        this.set(Attribute.MOVEMENT_SPEED, WALKING, multiplier);
    }

    @Override
    public double getJumpingMultiplier() {
        return this.get(Attribute.JUMP_STRENGTH, JUMPING);
    }

    @Override
    public void setJumpingMultiplier(final double multiplier) {
        this.set(Attribute.JUMP_STRENGTH, JUMPING, multiplier);
    }

    @Override
    public void reset() {
        this.set(Attribute.GRAVITY, GRAVITY, 1.0);
        this.set(Attribute.MOVEMENT_SPEED, WALKING, 1.0);
        this.set(Attribute.JUMP_STRENGTH, JUMPING, 1.0);
    }

    private double get(final Attribute attribute, final NamespacedKey key) {
        final @Nullable AttributeInstance instance = this.player.getAttribute(attribute);
        final @Nullable AttributeModifier modifier = instance == null ? null : instance.getModifier(key);
        return modifier == null ? 1.0 : modifier.getAmount() + 1.0;
    }

    private void set(final Attribute attribute, final NamespacedKey key, final double multiplier) {
        Preconditions.checkArgument(Double.isFinite(multiplier) && multiplier >= 0, "Multiplier must be 0 or more, got %s", multiplier);
        final @Nullable AttributeInstance instance = this.player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        instance.removeModifier(key);
        if (multiplier != 1.0) {
            // MULTIPLY_SCALAR_1 scales the final value by (1 + amount)
            instance.addTransientModifier(new AttributeModifier(key, multiplier - 1.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        }
    }
}
