package edu.ntnu.idi.idatt.converter;


import edu.ntnu.idi.idatt.DataTransfer.ActionDto;
import edu.ntnu.idi.idatt.DataTransfer.TileDto;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.actions.NoOperationAction;
import edu.ntnu.idi.idatt.model.actions.TileAction;

public class TileConverter {
  private static final ActionConverter actionConverter = new ActionConverter();


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

  public static Tile fromDto(TileDto dto) {
    if (dto == null) {
      System.err.println("");
    }

    Tile tile = new Tile(dto.getId(), dto.getRow(), dto.getColumn());
    tile.setNextTileId(dto.getNextTileId());

    if (dto.getAction() != null) {
      TileAction action = actionConverter.fromDto(dto.getAction());
      tile.setLandAction(action);
    }
    return tile;
  }

}
