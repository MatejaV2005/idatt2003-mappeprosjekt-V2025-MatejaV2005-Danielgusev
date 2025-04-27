package edu.ntnu.idi.idatt.observer;

import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.playertype.Player;

public interface BoardGameObserver {
  void onPlayerMoved(Player player, Tile from, Tile to);
  void onGameWon(Player player);
  void onGameStateChanged(Player player);
}
