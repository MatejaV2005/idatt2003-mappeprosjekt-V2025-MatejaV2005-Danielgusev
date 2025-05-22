package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Player;

/**
 * A stateless, singleton action that does nothing when performed.
 *
 * <p>Used as a default or fallback when no other action applies.
 *
 * @see TileAction
 * @since 1.0
 */
@SuppressWarnings("checkstyle:Indentation")
public class NoOperationAction implements TileAction {
    public static final NoOperationAction INSTANCE = new NoOperationAction();

    private NoOperationAction() {
    }

    /**
     * Does nothing.
     *
     * @param player the player (ignored)
     */
    @Override
    public void perform(Player player) {
        // intentionally no-op
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link ActionType#NO_OP}
     */
    @Override
    public ActionType getActionType() {
        return ActionType.NO_OP;
    }

    /**
     * {@inheritDoc}
     *
     * @return always -1, since there is no destination
     */
    @Override
    public int getDestinationTileId() {
        return -1;
    }

    /**
     * {@inheritDoc}
     *
     * @return a fixed "No operation performed" message
     */
    @Override
    public String getDescription() {
        return "No operation performed";
    }
}
