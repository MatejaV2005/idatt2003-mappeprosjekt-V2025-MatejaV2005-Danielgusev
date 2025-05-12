package edu.ntnu.idi.idatt.converter;

import edu.ntnu.idi.idatt.DataTransfer.BoardDto;
import edu.ntnu.idi.idatt.DataTransfer.TileDto;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import java.util.HashMap;
import java.util.Map;

public class BoardConverter {

  public static BoardDto toDto(Board board) {
    Map<Integer, Tile> fromBoard = board.getTiles();

    int rows = board.getRows();
    int cols = board.getColumns();

    Map<Integer, TileDto> boardMap = new HashMap<>();

    fromBoard.forEach((id, tile) -> boardMap.put(id, TileConverter.toDto(tile)));

    BoardDto boardDto = new BoardDto(boardMap, rows, cols);

    return boardDto;
  }

  public static Board fromDto(BoardDto boardDto) {
    Board board = new Board();
    board.setRows(boardDto.getRows());
    board.setColumns(boardDto.getColumns());

    ActionConverter actionConverter = new ActionConverter();
    Map<Integer, Tile> tilesMap = new HashMap<>();

    boardDto.getTiles().forEach((id, dto) -> {
      Tile tile = TileConverter.fromDto(dto, tilesMap);
      tile.setNextTileId(dto.getNextTileId());
      tilesMap.put(id, tile);
    });

    boardDto.getTiles().forEach((id, dto) -> {
      Tile tile = tilesMap.get(id);
      TileAction action = actionConverter.fromDto(dto.getAction(), tilesMap);
      tile.setLandAction(action);

    });

    board.setTiles(tilesMap);
    board.relinkTiles();

    return board;
  }
}
