package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.ActionType;
import edu.ntnu.idi.idatt.model.playertype.Player;

public class NoOperationAction implements TileAction {
    public static final NoOperationAction INSTANCE = new NoOperationAction();

    @Override
    public void perform(Player player) {
        // No operation performed
    }

    @Override
    public ActionType getActionType() {
        return ActionType.NO_OP;
    }

    @Override
    public int getDestinationTileId() {
        return -1; // No destination tile
    }

    @Override
    public String getDescription() {
        return "No operation performed"; // No description
    }

}
