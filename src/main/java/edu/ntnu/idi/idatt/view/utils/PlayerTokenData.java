package edu.ntnu.idi.idatt.view.utils;

import edu.ntnu.idi.idatt.model.core.Player;

public class PlayerTokenData {
  private final Player player;
  private  int currentTileId;

  public PlayerTokenData(Player player, int currentTileId) {
    this.player = player;
    setCurrentTileId(currentTileId);
  }

  public Player getPlayer() {
    return player;
  }

  public int getCurrentTileId() {
    return currentTileId;
  }

  public int setCurrentTileId(int tileId) {
    this.currentTileId = tileId;
    return currentTileId;
  }
}
