package edu.ntnu.idi.idatt.converter;

import edu.ntnu.idi.idatt.DataTransfer.ActionDto;
import edu.ntnu.idi.idatt.DataTransfer.TileDto;
import edu.ntnu.idi.idatt.factory.TileFactory;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;

public class TileConverter {
  private static final ActionConverter actionConverter = new ActionConverter(); // Still needed for toDto

  public static TileDto toDto(Tile tile) {
    TileAction action = tile.getLandAction();
    ActionDto actionDto = action != null ? actionConverter.toDto(action) : actionConverter.toDto(NoOperationAction.INSTANCE);

    return new TileDto(
        tile.getTileId(),
        tile.getRow(),
        tile.getColumn(),
        tile.getNextTileId(),
        actionDto);
  }

  public static Tile fromDto(TileDto dto) { // tileMap parameter removed
    if (dto == null) {
      System.err.println("TileDto is null in TileConverter.fromDto. Returning null.");
      return null;
    }

    TileFactory factory = new TileFactory();
    Tile tile = factory.createTile(dto.getId(), dto.getRow(), dto.getColumn());
    tile.setNextTileId(dto.getNextTileId());

    return tile;
  }
}