package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.playertype.Player;

public class GameEngine {
  private Board board;
  private Dice dice;

  public GameEngine(Board board, Dice dice) {
    this.board = board;
    this.dice = dice;
  }

  public Tile getStartingTile() {
    return board.getTileById(1);
  }

  public void playTurn(Player currentPlayer) {
    dice.roll();
    int steps = dice.getTotalDiceValue();

    Tile oldTile = currentPlayer.getCurrentTile();
    Tile newTile = currentPlayer.basicMove(steps);

    updatePlayerPosition(currentPlayer, oldTile, newTile);
  }

  private void updatePlayerPosition(Player currentPlayer, Tile oldTile, Tile newTile) {
    System.out.println(currentPlayer.getName() + " moves to tile " + newTile.getTileId());
    oldTile.leavePlayer(currentPlayer);
    currentPlayer.setOnCurrentTile(newTile);
    newTile.landPlayer(currentPlayer);
  }

  public boolean isWinner(Player winner) {
    Tile currentTile = winner.getCurrentTile();
    if (currentTile.getNextTile() == null) {
      return true;
    }

    return false;
  }







}
