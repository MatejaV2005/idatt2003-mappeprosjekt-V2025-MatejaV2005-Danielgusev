package edu.ntnu.idi.idatt.converter;

import edu.ntnu.idi.idatt.DataTransfer.BoardDto;
import edu.ntnu.idi.idatt.DataTransfer.TileDto;
import edu.ntnu.idi.idatt.exceptions.InvalidBoardFormatException;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import java.util.HashMap;
import java.util.Map;



/**
 * Converts between {@link Board} domain objects and their
 * JSON‐serializable {@link BoardDto} representations.
 *
 * <p>Supports full validation of board dimensions, tile continuity,
 * and action mapping. Invalid or missing data results in
 * {@link InvalidBoardFormatException}.
 */
public class BoardConverter {

  private BoardConverter() {

  }


  /**
   * Transforms a {@link Board} into a {@link BoardDto}.
   *
   * <p>Copies all tiles via {@link TileConverter#toDto(Tile)}, and
   * includes row and column counts for later reconstruction.
   *
   * @param board the source {@link Board}; must not be null
   * @return a {@link BoardDto} containing tile DTOs and dimensions
   */
  public static BoardDto toDto(Board board) {
    Map<Integer, Tile> fromBoard = board.getTiles();
    int rows = board.getRows();
    int cols = board.getColumns();
    Map<Integer, TileDto> boardMap = new HashMap<>();
    fromBoard.forEach((id, tile) -> boardMap.put(id, TileConverter.toDto(tile)));
    return new BoardDto(boardMap, rows, cols);
  }


  /**
   * Reconstructs a {@link Board} from a {@link BoardDto}.
   *
   * <p>This method orchestrates the validation of the DTO, conversion of its constituent tiles and
   * actions, and the final assembly of the {@link Board} object. It ensures data integrity and
   * structural correctness according to defined board rules.
   *
   * @param boardDto the DTO to convert; must not be {@code null}
   * @return a fully initialized and validated {@link Board}
   * @throws InvalidBoardFormatException if any validation or conversion step fails, detailing the
   *                                     nature of the error.
   */
  public static Board fromDto(BoardDto boardDto) throws InvalidBoardFormatException {
    validateInitialBoardDto(boardDto);

    int rows = boardDto.getRows();
    int cols = boardDto.getColumns();
    int totalTiles = validateBoardDimensionsAndTileCount(boardDto, rows, cols);

    Map<Integer, Tile> tilesMap = convertTileDtosToTiles(boardDto.getTiles());
    validateAllTilesPresent(tilesMap, totalTiles);

    ActionConverter actionConverter = new ActionConverter();
    assignActionsToTiles(boardDto.getTiles(), tilesMap, actionConverter);

    Board board = new Board();
    board.setRows(rows);
    board.setColumns(cols);
    board.setTiles(tilesMap);

    try {
      board.relinkTiles();
    } catch (IllegalStateException e) {
      throw new InvalidBoardFormatException("Error relinking tiles: " + e.getMessage(), e);
    }

    return board;
  }

  private static void validateInitialBoardDto(BoardDto boardDto)
      throws InvalidBoardFormatException {
    if (boardDto == null) {
      throw new InvalidBoardFormatException("Board data (DTO) is null.");
    }
  }


  private static int validateBoardDimensionsAndTileCount(BoardDto boardDto, int rows, int cols)
      throws InvalidBoardFormatException {
    int totalTiles = rows * cols;

    if (totalTiles < 50) {
      throw new InvalidBoardFormatException(
          "Board too small. Minimum 50 tiles required, found " + totalTiles + ".");
    }
    if (totalTiles > 150) {
      throw new InvalidBoardFormatException(
          "Board too large. Maximum 150 tiles allowed, found " + totalTiles + ".");
    }

    if (boardDto.getTiles() == null || boardDto.getTiles().isEmpty()) {
      throw new InvalidBoardFormatException("Board DTO contains no tiles.");
    }
    if (boardDto.getTiles().size() != totalTiles) {
      throw new InvalidBoardFormatException(
          "Mismatch between declared rows/cols and actual number of tiles. "
              + "Expected: "
              + totalTiles
              + ", Found: "
              + boardDto.getTiles().size());
    }
    return totalTiles;
  }


  private static Map<Integer, Tile> convertTileDtosToTiles(Map<Integer, TileDto> tileDtoMap)
      throws InvalidBoardFormatException {
    Map<Integer, Tile> tilesMap = new HashMap<>();
    for (Map.Entry<Integer, TileDto> entry : tileDtoMap.entrySet()) {
      TileDto tileDto = entry.getValue();
      if (tileDto == null) {
        throw new InvalidBoardFormatException("Found null tile DTO for ID: " + entry.getKey());
      }
      try {
        Tile tile = TileConverter.fromDto(tileDto);
        if (tile == null) {
          throw new InvalidBoardFormatException(
              "Failed to convert tile DTO to Tile for ID: " + tileDto.getId());
        }
        if (tile.getTileId() != entry.getKey()) {
          throw new InvalidBoardFormatException(
              "Tile ID mismatch in DTO map. Map key: "
                  + entry.getKey()
                  + ", Tile's actual ID: "
                  + tile.getTileId());
        }
        tilesMap.put(tile.getTileId(), tile);
      } catch (IllegalArgumentException e) {
        throw new InvalidBoardFormatException(
            "Invalid data for tile ID " + tileDto.getId() + ": " + e.getMessage(), e);
      }
    }
    return tilesMap;
  }

  private static void validateAllTilesPresent(Map<Integer, Tile> tilesMap, int totalTiles)
      throws InvalidBoardFormatException {
    for (int i = 1; i <= totalTiles; i++) {
      if (!tilesMap.containsKey(i)) {
        throw new InvalidBoardFormatException(
            "Missing tile with ID: "
                + i
                + ". All tiles from 1 to "
                + totalTiles
                + " must be defined.");
      }
    }
  }


  private static void assignActionsToTiles(
      Map<Integer, TileDto> tileDtoMap,
      Map<Integer, Tile> tilesMap,
      ActionConverter actionConverter)
      throws InvalidBoardFormatException {
    for (TileDto tileDto : tileDtoMap.values()) {
      if (tileDto.getAction() != null) {
        Tile tile = tilesMap.get(tileDto.getId());
        if (tile == null) {
          throw new InvalidBoardFormatException(
              "Internal error: Tile not found for ID "
                  + tileDto.getId()
                  + " during action assignment.");
        }
        try {
          TileAction action = actionConverter.fromDto(tileDto.getAction(), tilesMap);
          tile.setLandAction(action);
        } catch (IllegalArgumentException | NullPointerException e) {
          throw new InvalidBoardFormatException(
              "Invalid action for tile ID " + tileDto.getId() + ": " + e.getMessage(), e);
        }
      }
    }
  }
}

