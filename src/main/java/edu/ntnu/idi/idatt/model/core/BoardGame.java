package edu.ntnu.idi.idatt.model.core;

import edu.ntnu.idi.idatt.model.games.GameType;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import edu.ntnu.idi.idatt.observer.Observable;
import edu.ntnu.idi.idatt.utils.ExceptionHandling; // Din ExceptionHandling klasse

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger; // Importer Logger

/**
 * Abstract base class for board games, designed using the Template Method pattern.
 * This class provides a skeletal structure for game flow, allowing subclasses to define
 * specific game rules and behaviors by implementing abstract hook methods.
 * It acts as a facade for the game system, managing players, the board, dice,
 * game state (started, game over, winner), and observer notifications.
 */
public abstract class BoardGame implements Observable<BoardGameObserver> {
  protected static final Logger LOGGER_MODEL = Logger.getLogger(BoardGame.class.getName());


  protected final Dice dice;
  protected final Board board;
  protected final GameEngine gameEngine;
  protected final List<Player> players;
  protected Player currentPlayer;
  protected Player winner;
  protected boolean gameStarted;
  protected boolean gameOver;
  protected int roundCount;
  protected final List<BoardGameObserver> observers;

  /**
   * Constructs a new {@code BoardGame}.
   *
   * @param board    The game board. Cannot be null.
   * @param dice     The dice mechanism for the game. Cannot be null.
   * @param strategy The game strategy defining game-specific rules. Cannot be null.
   * @throws IllegalArgumentException if board, dice, or strategy is null.
   */
  protected BoardGame(Board board, Dice dice, GameStrategy strategy) {
    ExceptionHandling.requireNonNull(board, "Board for BoardGame constructor");
    ExceptionHandling.requireNonNull(dice, "Dice for BoardGame constructor");
    ExceptionHandling.requireNonNull(strategy, "GameStrategy for BoardGame constructor");

    this.players = new ArrayList<>();
    this.observers = new ArrayList<>();
    this.currentPlayer = null;
    this.winner = null;
    this.gameStarted = false;
    this.gameOver = false;
    this.roundCount = 0;

    this.gameEngine = createGameEngine(board, strategy);
    this.board = this.gameEngine.getBoard();
    this.dice = dice;
  }

  /**
   * Adds an observer to be notified of game events. If the observer is null or already registered,
   * the method does nothing.
   *
   * @param observer The {@link BoardGameObserver} to add.
   */
  @Override
  public void addObserver(BoardGameObserver observer) {
    ExceptionHandling.requireNonNull(observer, "Observer to add");
    if (!this.observers.contains(observer)) {
      this.observers.add(observer);
    }
  }

  /**
   * Removes an observer from the list of registered observers. If the observer is null or not
   * registered, the method does nothing.
   *
   * @param observer The {@link BoardGameObserver} to remove.
   */
  @Override
  public void removeObserver(BoardGameObserver observer) {
    if (observer != null) {
      this.observers.remove(observer);
    }
  }

  /**
   * Notifies all registered observers that a player has moved.
   *
   * @param player   The {@link Player} who moved.
   * @param fromTile The {@link Tile} the player moved from.
   * @param toTile   The {@link Tile} the player moved to.
   */
  public void notifyPlayerMoved(Player player, Tile fromTile, Tile toTile) {
    List<BoardGameObserver> observersCopy = new ArrayList<>(this.observers);
    for (BoardGameObserver observer : observersCopy) {
      observer.onPlayerMoved(player, fromTile, toTile);
    }
  }

  /**
   * Notifies all registered observers that a new player has been added to the game.
   *
   * @param player The {@link Player} who was added.
   */
  public void notifyOnPlayerAdded(Player player) {
    List<BoardGameObserver> observersCopy = new ArrayList<>(this.observers);
    for (BoardGameObserver observer : observersCopy) {
      observer.onPlayerAdded(player);
    }
  }

  /**
   * Notifies all registered observers that a player has won the game. This method also sets the
   * game state to game over and records the winner.
   *
   * @param winningPlayer The {@link Player} who won the game.
   */
  public void notifyOnGameWon(Player winningPlayer) {
    this.gameOver = true;
    this.winner = winningPlayer;
    List<BoardGameObserver> observersCopy = new ArrayList<>(this.observers);
    for (BoardGameObserver observer : observersCopy) {
      observer.onGameWon(winningPlayer);
    }
  }

  /**
   * Notifies all registered observers that a player has landed on a tile and an action effect has
   * occurred, potentially moving the player.
   *
   * @param player            The {@link Player} affected by the action.
   * @param fromActionTile    The {@link Tile} that triggered the action.
   * @param toDestinationTile The {@link Tile} the player ended up on after the action, which might
   *                          be the same as {@code fromActionTile} if the action did not cause
   *                          movement (e.g., SkipTurn).
   */
  public void notifyActionTileEffect(Player player, Tile fromActionTile, Tile toDestinationTile) {
    List<BoardGameObserver> observersCopy = new ArrayList<>(this.observers);
    for (BoardGameObserver observer : observersCopy) {
      observer.onActionTileEffect(player, fromActionTile, toDestinationTile);
    }
  }

  /**
   * Notifies all registered observers that the overall game state has been updated. This is a
   * general notification typically used after a turn is completed or when significant game state
   * changes occur that are not covered by more specific notifications.
   */
  public void notifyObserversOfStateChange() {
    List<BoardGameObserver> observersCopy = new ArrayList<>(this.observers);
    for (BoardGameObserver observer : observersCopy) {
      observer.onGameStateUpdated(this);
    }
  }

  /**
   * Factory method for creating the appropriate {@link GameEngine}. Subclasses can override this to
   * provide specific {@code GameEngine} implementations tailored to the game type.
   *
   * @param gameBoard The game board to be used by the engine.
   * @param strategy  The game strategy to be used by the engine.
   * @return A configured {@link GameEngine} instance.
   */
  protected GameEngine createGameEngine(Board gameBoard, GameStrategy strategy) {
    return new GameEngine(gameBoard, strategy);
  }

  /**
   * Adds a player to the game. Players can only be added before the game has officially started.
   * The first player added will typically become the initial {@code currentPlayer}. If the player
   * is already in the game, they are not added again. Players are placed on the game's starting
   * tile upon being added.
   *
   * @param player The {@link Player} to add. Cannot be null.
   * @throws IllegalArgumentException if player is null.
   * @throws IllegalStateException    if the game has already started.
   */
  public void addPlayer(Player player) {
    ExceptionHandling.requireNonNull(player, "Player to add");
    ExceptionHandling.requireState(!gameStarted, "Cannot add players after the game has started.");

    if (!this.players.contains(player)) {
      Tile startingTile = this.gameEngine.getStartingTile();
      ExceptionHandling.requireState(startingTile != null,
          "Starting tile cannot be null when adding player.");
      player.setOnCurrentTile(startingTile);
      this.players.add(player);
      notifyOnPlayerAdded(player);

      if (this.currentPlayer == null) {
        this.currentPlayer = player;
      }
    }
  }

  /**
   * Starts the game, performing necessary initializations. The game must have at least one player
   * added before it can be started. This method is final to ensure a consistent game start
   * sequence, relying on the hook method {@link #initializeGameState()} for game-specific setup.
   *
   * @throws IllegalStateException if the game has already started or if no players have been
   *                               added.
   */
  public final void startGame() {
    ExceptionHandling.requireState(!gameStarted, "Game has already started.");
    ExceptionHandling.requireNotEmpty(this.players, "Cannot start game with no players");

    this.gameStarted = true;
    this.gameOver = false;
    this.winner = null;
    this.roundCount = 1;

    if (this.currentPlayer == null && !this.players.isEmpty()) {
      this.currentPlayer = this.players.get(0); // Default to the first added player
    }

    Tile startTile = getStartingTile();
    if (startTile != null) {
      for (Player p : this.players) {
        if (p.getCurrentTile() == null || p.getCurrentTile().getTileId() != startTile.getTileId()) {
          p.setOnCurrentTile(startTile);
        }
      }
    }

    initializeGameState();
    notifyObserversOfStateChange();
  }

  /**
   * Hook method for game-specific initialization logic that should run when the game starts.
   * Subclasses can override this to perform setup tasks like placing special items, dealing cards,
   * etc., after players have been added and basic game state is set.
   */
  protected void initializeGameState() {
  }

  /**
   * Hook method for handling actions that occur when a player lands on a special tile. Subclasses
   * should override this to implement game-specific effects for tiles that have actions beyond
   * simple movement (e.g., ladders, snakes, chance cards).
   *
   * @param player The {@link Player} who landed on the tile.
   * @param tile   The {@link Tile} the player landed on, which may have a special action.
   */
  protected void handleSpecialTileAction(Player player, Tile tile) {
  }

  /**
   * Plays a single turn for the current player. This is a template method that defines the general
   * sequence of a turn:
   * 1. Validate game state.
   * 2. Check if the current player should skip their turn.
   * 3. If not skipping, execute the player's turn (delegated to
   * {@link #handlePlayerTurn(Player)}).
   * 4. Check for a win condition (delegated to {@link #checkWinCondition(Player)}).
   * 5. If no winner, advance to the next player.
   * 6. Notify observers of state changes.
   *
   * @throws IllegalStateException if the game hasn't started, is already over, or if
   *                               {@code currentPlayer} is null.
   */
  public final void playNextTurn() {
    validateGameState();

    if (currentPlayer.shouldSkipTurn()) {
      LOGGER_MODEL.info("Player " + currentPlayer.getName() + " is skipping a turn.");
      advanceToNextPlayer();
      notifyObserversOfStateChange();
      return;
    }

    handlePlayerTurn(currentPlayer);

    if (checkWinCondition(currentPlayer)) {
      notifyOnGameWon(currentPlayer);
    } else {
      advanceToNextPlayer();
      notifyObserversOfStateChange();
    }
  }

  /**
   * Validates that the game is in a playable state (started, not over, current player exists).
   *
   * @throws IllegalStateException if the game state is invalid for playing a turn.
   */
  private void validateGameState() {
    ExceptionHandling.requireState(gameStarted, "Game has not started yet. Call startGame().");
    ExceptionHandling.requireState(!gameOver, "Game is already over.");
    ExceptionHandling.requireState(currentPlayer != null,
        "No current player set. Ensure players were added and game started.");
  }

  /**
   * Advances play to the next player in the list. If the current player is the last in the list,
   * play moves to the first player, and the round count is incremented. If there is only one
   * player, the current player remains the same, and round count increments.
   */
  protected final void advanceToNextPlayer() {
    if (this.players.isEmpty()) {
      return;
    }
    if (this.players.size() == 1) {
      roundCount++;
      return;
    }

    int currentIndex = this.players.indexOf(this.currentPlayer);
    int nextIndex = (currentIndex + 1) % this.players.size();
    this.currentPlayer = this.players.get(nextIndex);

    if (nextIndex == 0) {
      this.roundCount++;
    }
  }

  /**
   * Hook method for game-specific logic to handle a single player's turn. This is where actions
   * like rolling dice, moving the player, and applying tile effects (before checking for special
   * actions) would typically occur.
   *
   * @param player The {@link Player} whose turn it is.
   */
  protected abstract void handlePlayerTurn(Player player);

  /**
   * Hook method for game-specific logic to determine if a player has met the win conditions.
   *
   * @param player The {@link Player} to check.
   * @return {@code true} if the player has won, {@code false} otherwise.
   */
  protected abstract boolean checkWinCondition(Player player);

  /**
   * Gets the {@link GameType} of this specific board game instance.
   *
   * @return The type of the game.
   */
  public abstract GameType getGameType();

  /**
   * Gets the starting tile of the board, as defined by the {@link GameEngine}.
   *
   * @return The starting {@link Tile}.
   */
  public Tile getStartingTile() {
    ExceptionHandling.requireNonNull(this.gameEngine, "Game engine for getStartingTile");
    return this.gameEngine.getStartingTile();
  }

  /**
   * Gets the player whose turn it currently is.
   *
   * @return The current {@link Player}, or {@code null} if the game hasn't started or if there is
   * no current player set.
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * Gets the game board associated with this game instance.
   *
   * @return The current {@link Board} object.
   */
  public Board getBoard() {
    return this.board;
  }

  /**
   * Gets the {@link Dice} object used in this game, reflecting the last roll.
   *
   * @return The {@link Dice} object.
   */
  public Dice getLastDiceRoll() {
    return this.dice;
  }

  /**
   * Gets an unmodifiable list of all players participating in the game.
   *
   * @return A {@link List} of {@link Player}s.
   */
  public List<Player> getPlayers() {
    return List.copyOf(this.players);
  }

  /**
   * Checks if the game is over.
   *
   * @return {@code true} if the game has finished, {@code false} otherwise.
   */
  public boolean isGameOver() {
    return gameOver;
  }

  /**
   * Checks if the game has started.
   *
   * @return {@code true} if {@link #startGame()} has been successfully called, {@code false}
   * otherwise.
   */
  public boolean hasGameStarted() {
    return gameStarted;
  }

  /**
   * Gets the winner of the game, if the game is over and a winner has been determined.
   *
   * @return An {@link Optional} containing the winning {@link Player} if the game is over and there
   * is a winner; otherwise, an empty {@code Optional}.
   */
  public Optional<Player> getWinner() {
    return Optional.ofNullable(this.winner);
  }

  /**
   * Gets the current round number of the game. The round count starts at 1 after the game begins.
   *
   * @return The current round number, or 0 if the game has not started.
   */
  public int getRoundCount() {
    return roundCount;
  }

  /**
   * Gets the {@link GameEngine} used by this board game. This method is protected as the engine is
   * primarily for internal use by the {@code BoardGame} and its subclasses.
   *
   * @return The {@link GameEngine} instance.
   */
  protected GameEngine getGameEngine() {
    return this.gameEngine;
  }
}