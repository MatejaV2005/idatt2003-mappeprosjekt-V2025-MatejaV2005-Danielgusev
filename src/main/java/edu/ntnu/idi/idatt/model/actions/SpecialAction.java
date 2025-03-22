package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.player_type.Player;
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

}
