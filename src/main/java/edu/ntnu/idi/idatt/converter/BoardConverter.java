package edu.ntnu.idi.idatt.converter;

import edu.ntnu.idi.idatt.DataTransfer.BoardDto;
import edu.ntnu.idi.idatt.DataTransfer.TileDto;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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
    board.setRows(board.getRows());
    board.setColumns(board.getColumns());

    //Convert tileDtos to Tiles and put in map
    Map<Integer, Tile> tilesMap = new HashMap<>();
    boardDto.getTiles().forEach((id, tileDto) -> {
      Tile tile = TileConverter.fromDto(tileDto);
      tilesMap.put(id, tile);
    });

    board.setTiles(tilesMap);
    board.relink();

    return board;
  }
}
