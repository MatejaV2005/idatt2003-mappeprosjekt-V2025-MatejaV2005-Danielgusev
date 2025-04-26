package edu.ntnu.idi.idatt.observer;

public interface GameSetupObserver {
  void onGameModeSelected(String gameMode);
  void onDifficultySelected(String difficulty);
  void onBoardPreviewUpdated(String boardImagePath);
  //void onSetupCompleted(GameConfig config);

}
