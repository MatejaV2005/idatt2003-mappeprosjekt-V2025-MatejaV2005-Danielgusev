package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.player_type.Player;
import java.util.ArrayList;
import java.util.List;

public class BoardGame {
    private Board board;
    private Dice dice;
    private Player currentPlayer;
    private List<Player> players;
    private boolean gameOver;



  public BoardGame(int numOfTiles, int numOfDice) {
    //TODO: add exception Handling

    this.players = new ArrayList<>();
    this.currentPlayer = null;
    createBoard(numOfTiles);
    createDice(numOfDice);

  }

  public void addPlayer(Player player) {
      players.add(player);

      if (currentPlayer == null) {
        currentPlayer = player;
      }
  }

  public Board getBoard() {
    return board;
  }

  public void createBoard(int numOfTiles) {
    board = new Board(numOfTiles);
  }

  public void createDice(int numOfDice) {
    dice = new Dice(2);
  }

  // Play should only handle the flow of the game, calling upon the game rules from the game engine
  public void play() {
    if (players.isEmpty()) {
      System.out.println("no players in the game");
    }
  }


  public void announceWinner() {
    System.out.println("Congratulations! player: " + currentPlayer + " You won!");
  }


  public void nextPlayer() {
    int currentIndex = players.indexOf(currentPlayer);
    int nextIndex = currentIndex + 1;
    currentPlayer = players.get(nextIndex);
  }

}
