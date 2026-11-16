package org.spoutcraftredux.api;

/**
 * Multipliers for how a player moves. They work for every player, modded or not, because they are applied as
 * attribute modifiers that vanilla clients already understand.
 * <p>
 * A multiplier of 1 means normal movement. Changes last until the player logs out or {@link #reset()} is called.
 */
public interface MovementModifiers {

    /**
     * Gets the gravity multiplier.
     *
     * @return the multiplier, 1 when unchanged
     */
    double getGravityMultiplier();

    /**
     * Sets how strongly gravity pulls the player down.
     *
     * @param multiplier the multiplier, 0 or more; 0 means no gravity
     * @throws IllegalArgumentException if the multiplier is negative or not finite
     */
    void setGravityMultiplier(double multiplier);

    /**
     * Gets the walking speed multiplier.
     *
     * @return the multiplier, 1 when unchanged
     */
    double getWalkingMultiplier();

    /**
     * Sets how fast the player walks and runs.
     *
     * @param multiplier the multiplier, 0 or more
     * @throws IllegalArgumentException if the multiplier is negative or not finite
     */
    void setWalkingMultiplier(double multiplier);

    /**
     * Gets the jumping multiplier.
     *
     * @return the multiplier, 1 when unchanged
     */
    double getJumpingMultiplier();

    /**
     * Sets how strongly the player jumps.
     *
     * @param multiplier the multiplier, 0 or more
     * @throws IllegalArgumentException if the multiplier is negative or not finite
     */
    void setJumpingMultiplier(double multiplier);

    /**
     * Sets every multiplier back to 1.
     */
    void reset();
}
