package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.playertype.Player;
import java.util.ArrayList;
import java.util.List;

public class BoardGame {

  private Player currentPlayer;
  private GameEngine gameEngine;
  private List<Player> players;
  private boolean gameOver;


  public BoardGame(Board board, Dice dice) {
    //TODO: add exception Handling

    this.players = new ArrayList<>();
    this.currentPlayer = null;
    this.gameOver = false;
    this.gameEngine = new GameEngine(board, dice);

  }

  public Tile getStartingTile() {
    return gameEngine.getStartingTile();
  }

  public void addPlayer(Player player) {
    players.add(player);

    if (currentPlayer == null) {
      currentPlayer = player;
    }
  }

  // Play should only handle the flow of the game, calling upon the game rules from the game engine
  public void play() {
    int round = 1;

    System.out.println("these are the following players: \n");
    for (Player player : players) {
      System.out.println("name: " + player.getName());
    }

    while (!gameOver) {
      System.out.println("\nRound " + round);

      for (Player player : players) {
        gameEngine.playTurn(player);

        if (gameEngine.isWinner(player)) {
          gameOver = true;
          break; //TODO: find a way to remove break statement
        } else {
          nextPlayer();
        }
      }

      if (gameOver) {
        announceWinner();
      }

      round++;
      System.out.println();
    }
  }

  public void announceWinner() {
    System.out.println("Congratulations! Player: " + currentPlayer.getName() + " won!");
  }

  public void nextPlayer() {
    int currentIndex = players.indexOf(currentPlayer);
    int nextIndex = (currentIndex + 1) % players.size();
    currentPlayer = players.get(nextIndex);
  }

}
