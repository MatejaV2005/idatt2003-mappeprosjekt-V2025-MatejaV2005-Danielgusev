package edu.ntnu.idi.idatt.view.utils;

import edu.ntnu.idi.idatt.model.core.playertype.Player;

public class PlayerTokenData {
  private final Player player;
  private final int currentTileId;

  public PlayerTokenData(Player player, int currentTileId) {
    this.player = player;
    this.currentTileId = currentTileId;
  }

  public Player getPlayer() {
    return player;
  }

  public int getCurrentTileId() {
    return currentTileId;
  }
}
