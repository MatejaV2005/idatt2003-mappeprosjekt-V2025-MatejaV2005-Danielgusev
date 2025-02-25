package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.player_type.HumanPlayer;
import edu.ntnu.idi.idatt.model.player_type.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

import java.util.ArrayList;
import java.util.List;

public class BoardGame {
  private final Board board;
  private Player currentPlayer;
  private final List<Player> players;
  private Dice dice;

  /**
   * Constructs a BoardGame with the provided board.
   *
   * @param board the board to use; must not be null
   * @throws IllegalArgumentException if board is null
   */
  public BoardGame(Board board) {
    ExceptionHandling.requireNonNull(board, "Board must not be null.");
    this.board = board;
    this.players = new ArrayList<>();
    createDice();
  }

  /**
   * Adds a player to the game.
   *
   * @param player the player to add; must not be null
   */
  public void addPlayer(Player player) {
    ExceptionHandling.requireNonNull(player, "Player cannot be null.");
    players.add(player);
    if (currentPlayer == null) {
      currentPlayer = player; // Set first player as current player
    }
  }

  /**
   * Initializes the dice for the game.
   */
  private void createDice() {
    this.dice = new Dice(2); // Creates a pair of dice
  }

  /**
   * Starts and runs the game.
   */
  public void play() {
    System.out.println("Starting the Board Game!");
    int i = 0;
    boolean gameOver = false;
    while (!gameOver) {
      i++;
      System.out.println("Round " + i);
      for (Player player : players) {
        currentPlayer = player;
        int roll = dice.roll();
        System.out.println(currentPlayer.getName() + " rolls a " + roll);

        currentPlayer.move(roll); // Player moves based on its own logic

        Tile newTile = currentPlayer.getCurrentTile();
        System.out.println(currentPlayer.getName() + " moves to tile " + newTile.getTileId());
        System.out.println();

        // Check if the new tile has an action (if you later re-enable TileAction)
        // if (newTile.getLandaction() != null) {
        //     newTile.getLandaction().perform(currentPlayer);
        // }

        // Check if the player has won
        if (newTile.getTileId() >= board.getBoardSize()) {
          System.out.println(currentPlayer.getName() + " has won the game!");
          gameOver = true;
        }
      }
    }
  }

  /**
   * Returns the winner of the game, if there is one.
   *
   * @return the winning player, or null if no winner yet
   */
  public Player getWinner() {
    for (Player player : players) {
      if (player.getCurrentTile().getTileId() >= board.getBoardSize()) {
        return player;
      }
    }
    return null; // No winner yet
  }

  /**
   * Returns the board associated with this game.
   *
   * @return the board
   */
  public Board getBoard() {
    return board;
  }
}
