package edu.ntnu.idi.idatt.model.core;

import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;

import java.util.List;
import java.util.logging.Logger; // Importer Logger

/**
 * The {@code GameEngine} class orchestrates core game mechanics by delegating
 * game-specific logic to a {@link GameStrategy}. It serves as an intermediary,
 * providing a consistent interface for operations such as retrieving the starting tile,
 * playing a turn, checking win conditions, and initializing the game state
 * based on the provided strategy and board.
 */
public class GameEngine {


  private static final Logger LOGGER = Logger.getLogger(GameEngine.class.getName());
  private final GameStrategy strategy;
  private final Board board;

  /**
   * Constructs a new {@code GameEngine} with a specific game board and strategy.
   * The provided strategy dictates the rules and flow of the game played on the board.
   *
   * @param board    The {@link Board} on which the game will be played. Cannot be null.
   * @param strategy The {@link GameStrategy} that defines the game's rules and logic.
   * Cannot be null.
   * @throws IllegalArgumentException if board or strategy is null.
   */
  public GameEngine(Board board, GameStrategy strategy) {
    ExceptionHandling.requireNonNull(board, "Board for GameEngine constructor");
    ExceptionHandling.requireNonNull(strategy, "GameStrategy for GameEngine constructor");
    this.board = board;
    this.strategy = strategy;
  }

  /**
   * Gets the starting tile of the game board.
   * By convention, this is usually the tile with ID 1.
   *
   * @return The starting {@link Tile}.
   * @throws IllegalArgumentException if the board is not properly initialized or
   * tile ID 1 does not exist (as per
   * {@link Board#getTileById(int)}'s contract).
   */
  public Tile getStartingTile() {
    return board.getTileById(1);
  }

  /**
   * Gets the game board associated with this game engine.
   *
   * @return The {@link Board} instance (never null after successful construction).
   */
  public Board getBoard() {
    return this.board;
  }

  /**
   * Executes a single turn for the given player according to the current game strategy.
   * This typically involves operations like dice rolling, player movement,
   * and handling any actions on the tiles landed upon.
   *
   * @param currentPlayer The {@link Player} whose turn it is. Cannot be null.
   * @throws IllegalArgumentException if currentPlayer is null.
   */
  public void playTurn(Player currentPlayer) {
    ExceptionHandling.requireNonNull(currentPlayer, "Current player for playTurn");
    LOGGER.finer("GameEngine: Executing turn for player " + currentPlayer.getName()
        + " using strategy " + strategy.getClass().getSimpleName());
    strategy.executePlayerTurn(currentPlayer);
  }

  /**
   * Checks if the given player has met the win conditions according to the current game strategy.
   *
   * @param player The {@link Player} to check. Cannot be null.
   * @return {@code true} if the player has won according to the strategy, {@code false} otherwise.
   * @throws IllegalArgumentException if player is null.
   */
  public boolean isWinner(Player player) {
    ExceptionHandling.requireNonNull(player, "Player for isWinner check");
    return strategy.checkWinCondition(player);
  }


  /**
   * Initializes the game state for the given list of players using the current game strategy.
   * This typically involves placing players on their starting positions on the board
   * and any other strategy-specific setup required before the game begins.
   *
   * @param players The list of {@link Player}s to initialize for the game.
   * Cannot be null or empty.
   *
   * @throws IllegalArgumentException if the players list is null or empty.
   */
  public void initializeGame(List<Player> players) {
    ExceptionHandling.requireNotEmpty(players, "List of players for initialization");
    strategy.initializeGame(board, players);
  }
}