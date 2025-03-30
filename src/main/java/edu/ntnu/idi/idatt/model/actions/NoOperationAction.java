package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.ActionType;
import edu.ntnu.idi.idatt.model.playertype.Player;

public class NoOperationAction implements TileAction {

    @Override
    public void perform(Player player) {
        // No operation performed
    }

    @Override
    public String getActionType() {
        return ActionType.NO_OP.name();
    }

}
