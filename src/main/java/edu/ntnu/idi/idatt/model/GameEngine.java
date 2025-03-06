package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.player_type.Player;

public class GameEngine {
  private Board board;
  private Dice dice;

  public GameEngine(int numOfTiles, int numOfDice) {
    this.board = new Board(numOfTiles);
    this.dice = new Dice(numOfDice);
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
    System.out.println(currentPlayer.getName() + " is at tile " + newTile.getTileId());
  }

  private void updatePlayerPosition(Player currentPlayer, Tile oldTile, Tile newTile) {
    oldTile.leavePlayer(currentPlayer);
    currentPlayer.placeOnTile(newTile);
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
