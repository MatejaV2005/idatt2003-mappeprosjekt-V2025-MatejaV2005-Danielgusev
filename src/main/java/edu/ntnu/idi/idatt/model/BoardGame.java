package edu.ntnu.idi.idatt.model;

import edu.ntnu.idi.idatt.model.playertype.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Facade for interacting with the Board Game system.
 * Provides a simplified interface to manage and play the game,
 * hiding the complexities of the GameEngine, Board, and Dice.
 * The client is responsible for presentation (e.g., printing to console or updating UI).
 */
public class BoardGame {

  private final GameEngine gameEngine;
  private final List<Player> players;
  private Player currentPlayer;
  private Player winner; // Store the winner explicitly
  private boolean gameStarted;
  private boolean gameOver;
  private int roundCount; // Keep track of rounds if needed


  /**
   * Creates a new Board Game facade.
   *
   * @param board The game board. Cannot be null.
   * @param dice The dice mechanism. Cannot be null.
   * @throws NullPointerException if board or dice is null.
   */
  public BoardGame(Board board, Dice dice) {
    // Exception Handling added
    Objects.requireNonNull(board, "Board cannot be null");
    Objects.requireNonNull(dice, "Dice cannot be null");

    this.players = new ArrayList<>();
    this.currentPlayer = null;
    this.winner = null;
    this.gameStarted = false;
    this.gameOver = false;
    this.roundCount = 0;
    // GameEngine setup is internal detail handled by the facade
    this.gameEngine = new GameEngine(board, dice);
  }

  /**
   * Adds a player to the game. Players can only be added before the game starts.
   * The first player added becomes the starting player.
   *
   * @param player The player to add. Cannot be null.
   * @throws NullPointerException if player is null.
   * @throws IllegalStateException if the game has already started.
   */
  public void addPlayer(Player player) {
    Objects.requireNonNull(player, "Player cannot be null");
    if (gameStarted) {
      throw new IllegalStateException("Cannot add players after the game has started.");
    }
    if (!players.contains(player)) {
      players.add(player);
      if (currentPlayer == null) {
        currentPlayer = player;
      }
    }
  }

  /**
   * Starts the game. Requires at least one player.
   *
   * @throws IllegalStateException if the game has already started or no players have been added.
   */
  public void startGame() {
    if (gameStarted) {
      throw new IllegalStateException("Game has already started.");
    }
    if (players.isEmpty()) {
      throw new IllegalStateException("Cannot start game with no players.");
    }
    this.gameStarted = true;
    this.gameOver = false;
    this.winner = null;
    this.roundCount = 1; // Start at round 1
    // gameEngine.initializePlayerPositions(players);
  }


  /**
   * Plays a single turn for the current player.
   * Advances to the next player if the game is not over.
   * Updates the game over status and winner if the current player wins.
   *
   * @throws IllegalStateException if the game hasn't started or is already over.
   */
  public void playNextTurn() {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not started yet. Call startGame().");
    }
    if (gameOver) {
      throw new IllegalStateException("Game is already over.");
    }
    if (currentPlayer == null) {
      throw new IllegalStateException("No current player set. Ensure players were added and game started.");
    }


    // Delegate the turn logic to the engine
    gameEngine.playTurn(currentPlayer);

    // Check for win condition
    if (gameEngine.isWinner(currentPlayer)) {
      this.gameOver = true;
      this.winner = currentPlayer;
      // Don't advance player if game is over
    } else {
      // Advance to the next player
      nextPlayer();
      // If we wrapped around back to the first player, increment round count
      if (players.indexOf(currentPlayer) == 0) {
        roundCount++;
      }
    }
  }

  /**
   * Advances the turn to the next player in sequence.
   */
  private void nextPlayer() {
    if (players.size() > 1) {
      int currentIndex = players.indexOf(currentPlayer);
      // Calculate next index, wrapping around using modulo
      int nextIndex = (currentIndex + 1) % players.size();
      currentPlayer = players.get(nextIndex);
    }
    // If only one player, they remain the current player
  }

  // --- Getters for Client to Query State ---

  /**
   * Gets the starting tile of the board.
   *
   * @return The starting Tile.
   */
  public Tile getStartingTile() {
    // This might still be useful for client setup (e.g., drawing the initial state)
    return gameEngine.getStartingTile();
  }

  /**
   * Gets the player whose turn it currently is.
   *
   * @return The current Player, or null if the game hasn't started.
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * Gets an unmodifiable list of players in the game.
   *
   * @return An unmodifiable list of players.
   */
  public List<Player> getPlayers() {
    return Collections.unmodifiableList(players);
  }

  /**
   * Checks if the game is over.
   *
   * @return true if the game has finished, false otherwise.
   */
  public boolean isGameOver() {
    return gameOver;
  }

  /**
   * Checks if the game has started.
   *
   * @return true if startGame() has been called successfully, false otherwise.
   */
  public boolean hasGameStarted() {
    return gameStarted;
  }

  /**
   * Gets the winner of the game, if it is over.
   *
   * @return An Optional containing the winning Player if the game is over and there's a winner,
   * otherwise an empty Optional.
   */
  public Optional<Player> getWinner() {
    return Optional.ofNullable(winner);
  }

  /**
   * Gets the current round number.
   * Returns 0 if the game hasn't started.
   *
   * @return The current round number.
   */
  public int getRoundCount() {
    return roundCount;
  }
}