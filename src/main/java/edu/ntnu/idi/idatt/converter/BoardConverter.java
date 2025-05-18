package edu.ntnu.idi.idatt.converter;

import edu.ntnu.idi.idatt.DataTransfer.BoardDto;
import edu.ntnu.idi.idatt.DataTransfer.TileDto;
import edu.ntnu.idi.idatt.exceptions.InvalidBoardFormatException;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class BoardConverter {
  private static final Logger LOGGER = java.util.logging.Logger.getLogger(BoardConverter.class.getName());

  public static BoardDto toDto(Board board) {
    Map<Integer, Tile> fromBoard = board.getTiles();
    int rows = board.getRows();
    int cols = board.getColumns();
    Map<Integer, TileDto> boardMap = new HashMap<>();
    fromBoard.forEach((id, tile) -> boardMap.put(id, TileConverter.toDto(tile)));
    return new BoardDto(boardMap, rows, cols);
  }

  // In BoardConverter.java
  public static Board fromDto(BoardDto boardDto) throws InvalidBoardFormatException { // Kast det nye unntaket
    if (boardDto == null) {
      throw new InvalidBoardFormatException("Board data (DTO) is null.");
    }

    int rows = boardDto.getRows();
    int cols = boardDto.getColumns();
    int totalTiles = rows * cols;

    if (totalTiles < 50) {
      throw new InvalidBoardFormatException("Board too small. Minimum 50 tiles required, found " + totalTiles + ".");
    }
    if (totalTiles > 150) {
      throw new InvalidBoardFormatException("Board too large. Maximum 150 tiles allowed, found " + totalTiles + ".");
    }

    Board board = new Board();
    board.setRows(rows);
    board.setColumns(cols);

    ActionConverter actionConverter = new ActionConverter();
    Map<Integer, Tile> tilesMap = new HashMap<>();

    if (boardDto.getTiles() == null || boardDto.getTiles().isEmpty()) {
      throw new InvalidBoardFormatException("Board DTO contains no tiles.");
    }
    if (boardDto.getTiles().size() != totalTiles) {
      throw new InvalidBoardFormatException("Mismatch between declared rows/cols and actual number of tiles. Expected: " + totalTiles + ", Found: " + boardDto.getTiles().size());
    }

    for (Map.Entry<Integer, TileDto> entry : boardDto.getTiles().entrySet()) {
      TileDto tileDto = entry.getValue();
      if (tileDto == null) {
        throw new InvalidBoardFormatException("Found null tile DTO for ID: " + entry.getKey());
      }
      try {
        Tile tile = TileConverter.fromDto(tileDto);
        if (tile == null) {
          throw new InvalidBoardFormatException("Failed to convert tile DTO to Tile for ID: " + tileDto.getId());
        }

        if (tile.getTileId() != entry.getKey()) {
          throw new InvalidBoardFormatException("Tile ID mismatch in DTO. Map key: " + entry.getKey() + ", DTO ID: " + tile.getTileId());
        }
        tilesMap.put(tile.getTileId(), tile);
      } catch (IllegalArgumentException e) {
        throw new InvalidBoardFormatException("Invalid data for tile ID " + tileDto.getId() + ": " + e.getMessage(), e);
      }
    }

    for (int i = 1; i <= totalTiles; i++) {
      if (!tilesMap.containsKey(i)) {
        throw new InvalidBoardFormatException("Missing tile with ID: " + i + ". All tiles from 1 to " + totalTiles + " must be defined.");
      }
    }


    for (TileDto tileDto : boardDto.getTiles().values()) {
      Tile tile = tilesMap.get(tileDto.getId());
      if (tile != null && tileDto.getAction() != null) {
        try {
          TileAction action = actionConverter.fromDto(tileDto.getAction(), tilesMap);
          tile.setLandAction(action);
        } catch (IllegalArgumentException | NullPointerException e) {
          throw new InvalidBoardFormatException("Invalid action for tile ID " + tileDto.getId() + ": " + e.getMessage(), e);
        }
      }
    }

    board.setTiles(tilesMap);
    try {
      board.relinkTiles();
    } catch (IllegalStateException e) {
      throw new InvalidBoardFormatException("Error relinking tiles: " + e.getMessage(), e);
    }


    return board;
  }
}