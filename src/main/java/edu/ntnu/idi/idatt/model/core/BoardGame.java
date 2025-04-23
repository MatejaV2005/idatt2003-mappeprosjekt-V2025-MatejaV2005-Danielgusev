package edu.ntnu.idi.idatt.model.core;

import edu.ntnu.idi.idatt.model.playertype.Player;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract base class for board games implementing the Template Method pattern.
 * Provides a structured game flow with customizable behavior for specific game types.
 * Acts as a facade for interacting with the Board Game system,
 * hiding the complexities of the GameEngine, Board, and Dice.
 */
public abstract class BoardGame {

  protected final GameEngine gameEngine;
  protected final List<Player> players;
  protected Player currentPlayer;
  protected Player winner;
  protected boolean gameStarted;
  protected boolean gameOver;
  protected int roundCount;

  /**
   * Creates a new Board Game.
   *
   * @param board The game board. Cannot be null.
   * @param dice The dice mechanism. Cannot be null.
   * @throws NullPointerException if board or dice is null.
   */
  public BoardGame(Board board, Dice dice, GameStrategy strategy) {
    Objects.requireNonNull(board, "Board cannot be null");
    Objects.requireNonNull(dice, "Dice cannot be null");

    this.players = new ArrayList<>();
    this.currentPlayer = null;
    this.winner = null;
    this.gameStarted = false;
    this.gameOver = false;
    this.roundCount = 0;
    this.gameEngine = createGameEngine(board, strategy);
  }

  /**
   * Factory method for creating the appropriate GameEngine.
   * Subclasses can override to provide specific GameEngine implementations.
   *
   * @param board The game board
   * @param strategy the games strategy
   * @return A configured GameEngine
   */
  protected GameEngine createGameEngine(Board board, GameStrategy strategy) {
    return new GameEngine(board, strategy);
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
      player.setOnCurrentTile(gameEngine.getStartingTile());
      players.add(player);
      if (currentPlayer == null) {
        currentPlayer = player;
      }
    }
  }

  /**
   * Starts the game and performs initialization. Requires at least one player.
   * Template method with common game setup logic.
   *
   * @throws IllegalStateException if the game has already started or no players have been added.
   */
  public final void startGame() {
    if (gameStarted) {
      throw new IllegalStateException("Game has already started.");
    }
    if (players.isEmpty()) {
      throw new IllegalStateException("Cannot start game with no players.");
    }
    this.gameStarted = true;
    this.gameOver = false;
    this.winner = null;
    this.roundCount = 1;

    // Hook for game-specific initialization
    initializeGameState();
  }

  /**
   * Hook method for game-specific initialization.
   * Subclasses can override to add custom initialization logic.
   */
  protected void initializeGameState() {
    // Default implementation does nothing
  }

  /**
   * Hook method for handling special tile actions.
   * Subclasses can override to implement game-specific tile effects.
   *
   * @param player The player who landed on the tile
   * @param tile The tile the player landed on
   */
  protected void handleSpecialTileAction(Player player, Tile tile) {
    // Default implementation does nothing
  }

  /**
   * Template method that plays a single turn for the current player.
   * Controls the flow of a turn while delegating game-specific logic to hook methods.
   *
   * @throws IllegalStateException if the game hasn't started or is already over.
   */
  public final void playNextTurn() {
    validateGameState();

    // Check if player should skip this turn
    if (currentPlayer.shouldSkipTurn()) {
      currentPlayer.setSkipTurn(false); // Reset the skip flag
      advanceToNextPlayer();
      return;
    }

    // Execute the turn logic - delegated to subclasses
    handlePlayerTurn(currentPlayer);

    // Check for win condition
    if (checkWinCondition(currentPlayer)) {
      this.gameOver = true;
      this.winner = currentPlayer;
    } else {
      advanceToNextPlayer();
    }
  }

  /**
   * Validates that the game is in a valid state to play a turn.
   * @throws IllegalStateException if game state is invalid
   */
  private void validateGameState() {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not started yet. Call startGame().");
    }
    if (gameOver) {
      throw new IllegalStateException("Game is already over.");
    }
    if (currentPlayer == null) {
      throw new IllegalStateException("No current player set. Ensure players were added and game started.");
    }
  }

  /**
   * Advances to the next player and updates round count if needed.
   */
  protected final void advanceToNextPlayer() {
    if (players.size() > 1) {
      int currentIndex = players.indexOf(currentPlayer);
      int nextIndex = (currentIndex + 1) % players.size();
      currentPlayer = players.get(nextIndex);

      // If we wrapped around back to the first player, increment round count
      if (nextIndex == 0) {
        roundCount++;
      }
    }
  }

  /**
   * Hook method for handling a player's turn.
   * Subclasses must implement with game-specific turn logic.
   *
   * @param player The player whose turn it is
   */
  protected abstract void handlePlayerTurn(Player player);

  /**
   * Hook method for checking if a player has won.
   * Subclasses must implement with game-specific win conditions.
   *
   * @param player The player to check
   * @return true if the player has won, false otherwise
   */
  protected abstract boolean checkWinCondition(Player player);



  // --- Facade Getters for Client to Query State ---

  /**
   * Gets the starting tile of the board.
   *
   * @return The starting Tile.
   */
  public Tile getStartingTile() {
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

  /**
   * Gets the game engine.
   *
   * @return The GameEngine used by this board game.
   */
  protected GameEngine getGameEngine() {
    return gameEngine;
  }
}