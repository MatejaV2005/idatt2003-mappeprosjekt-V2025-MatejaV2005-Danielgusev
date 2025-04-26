package edu.ntnu.idi.idatt.observer;

public interface BoardGameObserver {
  void onPlayerMoved(String playerName, int tileId);
  void onGameWon(String winnerName, int turns);
  void onGameStateChanged(String message);
}
