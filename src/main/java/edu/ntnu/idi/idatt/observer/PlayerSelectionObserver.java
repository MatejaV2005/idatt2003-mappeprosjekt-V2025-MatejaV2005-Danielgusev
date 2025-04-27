package edu.ntnu.idi.idatt.observer;

import edu.ntnu.idi.idatt.model.player_type.Player;
import java.util.List;

public interface PlayerSelectionObserver {
  void onPlayerAdded(String playerName, String tokenType);
  void onPlayerRemoved(String playerName);
  void onPlayerListCompleted(List<Player> players);

}
