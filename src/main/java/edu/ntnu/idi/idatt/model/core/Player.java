package edu.ntnu.idi.idatt.model.core;

import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.Objects;

/**
 * Models a participant in the board game.
 *
 * <p>Tracks the player’s name, chosen token type, current position on the board,
 * completed laps, and whether they must skip their next turn. Provides core
 * operations such as moving along linked tiles, lap counting, and skip-turn logic.
 *
 * @see Tile
 */
public class Player {

  private int lapsCompleted = 0;
  private final String name;
  private Tile currentTile;
  private final String pieceType;
  private boolean skipTurn;

  /**
   * Creates a new player with the given name and piece type.
   *
   * @param name      the player’s name; must not be null, empty, or blank
   * @param pieceType the identifier for the player’s token; must not be null, empty, or blank
   * @throws IllegalArgumentException if {@code name} or {@code pieceType} is null, empty, or blank
   */
  public Player(String name, String pieceType) {
    ExceptionHandling.requireNonNullOrBlank(name, "name");
    ExceptionHandling.requireNonNullOrBlank(pieceType, "pieceType");
    this.name = name.trim();
    this.pieceType = pieceType.trim();
    this.skipTurn = false;
  }

  /**
   * Returns how many laps around the board this player has completed.
   *
   * @return the lap count, initially 0
   */
  public int getLapsCompleted() {
    return lapsCompleted;
  }

  /**
   * Resets the lap counter to zero.
   *
   * <p>Typically used when starting or restarting a game.</p>
   */
  public void resetLapsCompleted() {
    lapsCompleted = 0;
  }

  /**
   * Increments the lap counter by one.
   *
   * <p>Called when the player completes a full circuit of the board.</p>
   */
  public void incrementLapsCompleted() {
    lapsCompleted++;
  }

  /**
   * Returns this player’s name.
   *
   * @return the non-null, non-blank name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the tile on which this player is currently placed.
   *
   * @return the current {@link Tile}, or {@code null} if not yet placed
   */
  public Tile getCurrentTile() {
    return currentTile;
  }

  /**
   * Places the player on the given tile.
   *
   * @param newTile the tile to place the player on; must not be null
   * @throws IllegalArgumentException if {@code newTile} is null
   */
  public void setOnCurrentTile(Tile newTile) {
    ExceptionHandling.requireNonNull(newTile, "newTile");
    this.currentTile = newTile;
  }

  /**
   * Returns this player’s piece type (token identifier).
   *
   * @return the non-null, non-blank piece type
   */
  public String getPieceType() {
    return pieceType;
  }

  /**
   * Checks and resets the skip-turn flag.
   *
   * <p>If true, the player will skip their next turn and the flag is cleared.
   * Otherwise returns false.</p>
   *
   * @return {@code true} if the player must skip a turn, {@code false} otherwise
   */
  public boolean shouldSkipTurn() {
    if (skipTurn) {
      skipTurn = false;
      return true;
    }
    return false;
  }

  /**
   * Marks whether this player should skip their next turn.
   *
   * @param skipTurn {@code true} to skip the next turn; {@code false} otherwise
   */
  public void setSkipTurn(boolean skipTurn) {
    this.skipTurn = skipTurn;
  }

  /**
   * Advances this player forward along linked tiles by the specified number of steps.
   *
   * <p>Follows each tile’s {@code nextTile} reference. If the end of the board
   * is reached before exhausting steps, movement stops there.</p>
   *
   * @param steps the number of tiles to move; must be strictly positive
   * @return the destination {@link Tile}
   * @throws IllegalArgumentException if {@code steps} is not strictly positive
   * @throws IllegalStateException    if the player's current tile is null
   */
  public Tile basicMove(int steps) {
    ExceptionHandling.requireStrictlyPositive(steps, "steps");
    ExceptionHandling.requireState(currentTile != null, "currentTile");
    Tile destination = currentTile;
    for (int i = 0; i < steps; i++) {
      if (destination.getNextTile() != null) {
        destination = destination.getNextTile();
      } else {
        break;
      }
    }
    return destination;
  }

  /**
   * Two players are equal if they share the same name.
   *
   * @param o the object to compare
   * @return {@code true} if {@code o} is a Player with the same name
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Player that)) {
      return false;
    }
    return Objects.equals(name, that.name);
  }

  /**
   * Computes a hash code based on the player's name.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
