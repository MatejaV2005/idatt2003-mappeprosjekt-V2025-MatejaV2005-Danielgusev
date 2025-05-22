package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.Player;

/**
 * Represents a tile action that performs no operation.
 * This class is a singleton, as a no-operation action is stateless.
 */
public class NoOperationAction implements TileAction {
    public static final NoOperationAction INSTANCE = new NoOperationAction();

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private NoOperationAction() {
    }

    /**
     * Performs no action on the player.
     *
     * @param player The player on whom the action is (not) performed.
     */
    @Override
    public void perform(Player player) {
        // No operation is performed.
    }

    /**
     * Returns the type of this action.
     *
     * @return {@link ActionType#NO_OP}.
     */
    @Override
    public ActionType getActionType() {
        return ActionType.NO_OP;
    }

    /**
     * Returns the destination tile ID.
     * For a no-operation action, there is no destination.
     *
     * @return Always -1.
     */
    @Override
    public int getDestinationTileId() {
        return -1;
    }

    /**
     * Returns a description of this action.
     *
     * @return A string indicating no operation is performed.
     */
    @Override
    public String getDescription() {
        return "No operation performed";
    }
}
