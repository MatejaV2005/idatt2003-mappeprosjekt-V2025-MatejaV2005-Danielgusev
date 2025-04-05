package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.ActionType;
import edu.ntnu.idi.idatt.model.playertype.Player;
import java.util.function.Consumer;

public class SpecialAction implements TileAction {
  private final Consumer<Player> action;
  private final String description;

  public SpecialAction(String description, Consumer<Player> action) {
    this.description = description;
    this.action = action;

  }

  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " " + description);
    action.accept(player);
  }

  @Override
  public ActionType getActionType() {
    return ActionType.SPECIAL;
  }

  @Override
  public int getDestinationTileId() {
    return -1; // No destination tile
  }

  @Override
  public String getDescription() {
    return description;
  }


}
