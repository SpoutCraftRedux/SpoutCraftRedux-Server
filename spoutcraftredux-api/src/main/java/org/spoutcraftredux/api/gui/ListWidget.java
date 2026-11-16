package org.spoutcraftredux.api.gui;

import java.util.List;
import java.util.function.BiConsumer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * A scrolling list of rows, each with a title and a grey line of text. The player can select one row.
 */
public final class ListWidget extends Widget<ListWidget> {
    private List<Row> rows = List.of();
    private int selected = -1;
    private BiConsumer<Player, Integer> onSelect = (player, index) -> {
    };

    /**
     * One row of the list.
     *
     * @param title the first line
     * @param text  the second line, in grey
     */
    public record Row(Component title, Component text) {
    }

    /**
     * Gets the rows.
     *
     * @return the rows
     */
    public List<Row> getRows() {
        return this.rows;
    }

    /**
     * Sets the rows. Clears the selection if it no longer exists.
     *
     * @param rows the rows
     * @return this list
     */
    public ListWidget setRows(final List<Row> rows) {
        this.rows = List.copyOf(rows);
        if (this.selected >= this.rows.size()) {
            this.selected = -1;
        }
        this.changed();
        return this;
    }

    /**
     * Gets the selected row.
     *
     * @return the index, or -1 for none
     */
    public int getSelected() {
        return this.selected;
    }

    /**
     * Selects a row.
     *
     * @param index the index, or -1 for none
     * @return this list
     */
    public ListWidget setSelected(final int index) {
        this.selected = index >= 0 && index < this.rows.size() ? index : -1;
        this.changed();
        return this;
    }

    /**
     * Runs when the player selects a row.
     *
     * @param onSelect the callback, given the player and the row index
     * @return this list
     */
    public ListWidget onSelect(final BiConsumer<Player, Integer> onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    /**
     * Handles a selection from the player.
     *
     * @param player the player
     * @param index  the row index
     */
    @ApiStatus.Internal
    public void handleSelect(final Player player, final int index) {
        if (index < 0 || index >= this.rows.size()) {
            return;
        }
        this.selected = index;
        this.onSelect.accept(player, index);
    }
}
