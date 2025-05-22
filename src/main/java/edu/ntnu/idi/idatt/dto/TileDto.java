package edu.ntnu.idi.idatt.dto;

/**
 * Data transfer object for a single board tile.
 *
 * <p>Encapsulates the tile’s unique ID, its position (row and column),
 * the ID of the next tile it links to, and an optional landing action.
 */
public class TileDto {

  private final int id;
  private final int row;
  private final int column;
  private final int nextTileId;
  private final ActionDto action;

  /**
   * Constructs a TileDto with the specified properties.
   *
   * @param id the unique identifier of the tile
   * @param row the row index of this tile on the board (1-based)
   * @param column the column index of this tile on the board (1-based)
   * @param nextTileId the ID of the next tile in sequence, or a non-positive value if none
   * @param action the {@link ActionDto} to perform when landing on this tile; may be null
   */
  public TileDto(int id, int row, int column, int nextTileId, ActionDto action) {
    this.id = id;
    this.row = row;
    this.column = column;
    this.nextTileId = nextTileId;
    this.action = action;
  }

  /**
   * Returns the unique ID of this tile.
   *
   * @return the tile’s ID
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the row index of this tile.
   *
   * @return the row number (1-based)
   */
  public int getRow() {
    return row;
  }

  /**
   * Returns the column index of this tile.
   *
   * @return the column number (1-based)
   */
  public int getColumn() {
    return column;
  }

  /**
   * Returns the ID of the next tile in sequence.
   *
   * @return the next tile’s ID, or a non-positive value if there is no next tile
   */
  public int getNextTileId() {
    return nextTileId;
  }

  /**
   * Returns the landing action associated with this tile.
   *
   * @return the {@link ActionDto} for this tile, or null if no action is defined
   */
  public ActionDto getAction() {
    return action;
  }
}
