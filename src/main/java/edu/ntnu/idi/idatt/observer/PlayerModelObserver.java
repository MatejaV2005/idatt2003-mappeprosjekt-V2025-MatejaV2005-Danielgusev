package edu.ntnu.idi.idatt.observer;

import edu.ntnu.idi.idatt.model.playertype.Player;
import java.util.List;

public interface PlayerModelObserver {
  void onPlayerAdded(Player player);
  void onPlayerRemoved(Player player);
  void onPlayersLoaded(List<Player> players);

}
