package edu.ntnu.idi.idatt.model.actions;

import edu.ntnu.idi.idatt.model.playertype.Player;

public interface TileAction {

  // A static final variable providing a default "noaction" TileAction
  static final TileAction NO_OP = player -> {};


  void perform(Player player);

}
