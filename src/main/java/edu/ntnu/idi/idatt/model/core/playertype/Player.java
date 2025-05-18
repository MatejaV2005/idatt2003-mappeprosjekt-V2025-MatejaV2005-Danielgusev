package edu.ntnu.idi.idatt.model.core.playertype;

import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.utils.ExceptionHandling; // Din ExceptionHandling klasse

import java.util.Objects;

/**
 * Represents a player in the board game.
 * Maintains the player’s name, chosen piece type, current tile and skip-turn status.
 * Provides movement along linked tiles via {@link #basicMove(int)}, skip-turn management and tile assignment.
 */
public class Player {

  private final String name;
  private Tile currentTile;
  private final String pieceType;
  private boolean skipTurn;

  /**
   * Constructs a new Player.
   *
   * @param name      The name of the player. Cannot be null, empty, or blank.
   * The length of the name should also be within reasonable limits,
   * e.g., 2 to 20 characters (validation for length can be added if needed).
   * @param pieceType A string identifier for the player's game piece/token
   * (e.g., "Car", "Hat"). Cannot be null, empty, or blank.
   * @throws IllegalArgumentException if name or pieceType is null, empty, or blank.
   */
  public Player(String name, String pieceType) {
    ExceptionHandling.requireNonNullOrBlank(name, "Player name");
    ExceptionHandling.requireNonNullOrBlank(pieceType, "Player piece type");

    this.name = name.trim();
    this.pieceType = pieceType.trim();
    this.skipTurn = false;
  }

  /**
   * Gets the name of the player.
   *
   * @return The player's name (never null or blank).
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the current tile the player is on.
   *
   * @return The current {@link Tile}, or {@code null} if the player has not been placed on a tile.
   */
  public Tile getCurrentTile() {
    return currentTile;
  }

  /**
   * Sets the player's current tile.
   * This method is typically called by the game logic when a player moves or is placed on the board.
   *
   * @param newTile The new {@link Tile} the player is on. Cannot be null.
   * @throws IllegalArgumentException if newTile is null.
   */
  public void setOnCurrentTile(Tile newTile) {
    ExceptionHandling.requireNonNull(newTile, "New tile for player");
    this.currentTile = newTile;
  }

  /**
   * Gets the type or identifier of the player's game piece (e.g., "Car", "Hat").
   *
   * @return The player's piece type (never null or blank).
   */
  public String getPieceType() {
    return pieceType;
  }

  /**
   * Checks if the player should skip their next turn.
   * If this method returns {@code true}, the {@code skipTurn} flag is reset to {@code false},
   * so the player will only skip one turn per {@code setSkipTurn(true)} call.
   *
   * @return {@code true} if the player is marked to skip their turn, {@code false} otherwise.
   */
  public boolean shouldSkipTurn() {
    if (skipTurn) {
      skipTurn = false;
      return true;
    }
    return false;
  }

  /**
   * Sets whether the player should skip their next turn.
   *
   * @param skipTurn {@code true} to make the player skip their next turn,
   * {@code false} otherwise.
   */
  public void setSkipTurn(boolean skipTurn) {
    this.skipTurn = skipTurn;
  }

  /**
   * Performs a basic sequential move for the player along the linked list of tiles.
   * The player moves forward {@code steps} times from their current tile, following
   * the {@code nextTile} references. If the player reaches the end of the board
   * (a tile with no {@code nextTile}), they will stop on that last tile, even if
   * {@code steps} would have taken them further.
   * <p>
   *
   * @param steps The number of tiles to advance. Must be strictly positive.
   * @return The destination {@link Tile} after moving.
   * @throws IllegalArgumentException if steps is not strictly positive.
   * @throws IllegalStateException if the player's current tile is null (player not on board).
   */
  public Tile basicMove(int steps) {
    ExceptionHandling.requireStrictlyPositive(steps, "Number of steps for basicMove");
    ExceptionHandling.requireState(this.currentTile != null, "Player must be on a tile to perform basicMove.");

    Tile destinationTile = this.currentTile;
    for (int i = 0; i < steps; i++) {
      if (destinationTile.getNextTile() != null) {
        destinationTile = destinationTile.getNextTile();
      } else {
        break;
      }
    }
    return destinationTile;
  }

  /**
   * Compares this player to another object for equality.
   * Two players are considered equal if they have the same name (case-sensitive).
   *
   * @param o The object to compare with this player.
   * @return {@code true} if the given object is a Player and has the same name,
   * {@code false} otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Player)) {
      return false;
    }
    Player player = (Player) o;
    return Objects.equals(name, player.name);
  }

  /**
   * Returns a hash code value for the player, based on their name.
   *
   * @return A hash code value for this player.
   */
  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
