package edu.ntnu.idi.idatt.observer;

import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;

public interface BoardGameObserver {
  void onPlayerMoved(Player player, Tile from, Tile to);
  void onPlayerAdded(Player player);
  void onGameWon(Player player);
  void onActionTileEffect(Player player, Tile fromActionTile, Tile toDestinationTile);
}
