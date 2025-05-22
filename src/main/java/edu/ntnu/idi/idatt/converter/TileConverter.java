package edu.ntnu.idi.idatt.converter;

import edu.ntnu.idi.idatt.DataTransfer.ActionDto;
import edu.ntnu.idi.idatt.DataTransfer.TileDto;
import edu.ntnu.idi.idatt.factory.TileFactory;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import java.util.logging.Logger;

/**
 * Converts between {@link Tile} domain objects and their
 * {@link TileDto} data‐transfer representations.
 *
 * <p>Handles mapping of tile identity, position (row/column), linkage
 * (next tile ID), and landing actions to and from DTOs.
 */
public class TileConverter {

  private static final ActionConverter actionConverter = new ActionConverter();
  private static final Logger LOGGER =
      Logger.getLogger(TileConverter.class.getName());


  private TileConverter() {

  }


  /**
   * Converts a {@link Tile} into a {@link TileDto}.
   *
   * <p>Includes the tile’s ID, row, column, next‐tile linkage, and
   * a serialized {@link ActionDto} for the tile’s landing action.
   *
   * @param tile the domain tile to convert; must not be null
   * @return a {@link TileDto} representing the given tile
   */
  public static TileDto toDto(Tile tile) {
    TileAction action = tile.getLandAction();
    ActionDto actionDto = (action != null)
        ? actionConverter.toDto(action)
        : actionConverter.toDto(NoOperationAction.INSTANCE);

    return new TileDto(
        tile.getTileId(),
        tile.getRow(),
        tile.getColumn(),
        tile.getNextTileId(),
        actionDto);
  }

  /**
   * Converts a {@link TileDto} into a {@link Tile}.
   *
   * <p>Creates a new {@link Tile} via {@link TileFactory}, sets its
   * next‐tile linkage, but does not assign any landing action.
   * If the input DTO is null, logs an error and returns null.
   *
   * @param dto the {@link TileDto} to convert
   * @return a new {@link Tile} instance, or null if {@code dto} is null
   */
  public static Tile fromDto(TileDto dto) {
    if (dto == null) {
      LOGGER.warning("TileDto is null in TileConverter.fromDto; returning null.");
      return null;
    }

    TileFactory factory = new TileFactory();
    Tile tile = factory.createTile(dto.getId(), dto.getRow(), dto.getColumn());
    tile.setNextTileId(dto.getNextTileId());
    return tile;
  }
}
