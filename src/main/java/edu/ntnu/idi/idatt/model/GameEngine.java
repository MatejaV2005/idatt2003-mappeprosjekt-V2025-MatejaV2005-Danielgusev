package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.player_type.Player;

public class GameEngine {
  private Board board;
  private Dice dice;

  public GameEngine(int numOfTiles, int numOfDice) {
    this.board = new Board(numOfTiles);
    this.dice = new Dice(numOfDice);
  }

  public void playTurn(Player currentPlayer) {

  }

  public boolean isWinner(Player winner) {
    return false;
  }

  private void landPlayer(Player player, Tile landTile) {

  }

  private void LeavePlayer(Player player, Tile leaveTile) {

  }





}
