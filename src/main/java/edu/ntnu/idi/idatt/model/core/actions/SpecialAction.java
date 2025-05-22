package edu.ntnu.idi.idatt.model.core.actions;

import edu.ntnu.idi.idatt.model.core.ActionType;
import edu.ntnu.idi.idatt.model.core.Player;
import java.util.function.Consumer;

public class SpecialAction implements TileAction {
  private final Consumer<Player> action;
  private final String description;
  private final ActionType specificType;


  public SpecialAction(String description, Consumer<Player> action, ActionType specificType) {
    this.description = description;
    this.action = action;
    this.specificType = specificType;

  }

  @Override
  public void perform(Player player) {
    System.out.println(player.getName() + " " + description);
    action.accept(player);
  }

  @Override
  public ActionType getActionType() {
    return this.specificType;
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
