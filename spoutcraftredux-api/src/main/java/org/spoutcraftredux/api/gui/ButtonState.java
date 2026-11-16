package org.spoutcraftredux.api.gui;

/**
 * The states a {@link Button} can be styled for.
 */
public enum ButtonState {
    /** Enabled and not under the mouse. */
    NORMAL,
    /** Enabled and under the mouse. Unstyled parts use {@link #NORMAL}. */
    HOVER,
    /** Disabled. Unstyled parts use {@link #NORMAL}. */
    DISABLED
}
