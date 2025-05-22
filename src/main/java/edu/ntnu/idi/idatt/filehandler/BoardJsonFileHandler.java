package edu.ntnu.idi.idatt.filehandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import edu.ntnu.idi.idatt.converter.BoardConverter;
import edu.ntnu.idi.idatt.dto.BoardDto;
import edu.ntnu.idi.idatt.exceptions.FileLoadException;
import edu.ntnu.idi.idatt.exceptions.FileSaveException;
import edu.ntnu.idi.idatt.exceptions.InvalidBoardFormatException;
import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.model.core.Board;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles saving and loading {@link Board} objects to/from JSON files.
 * Uses Gson for JSON serialization/deserialization and delegates validation
 * to {@link BoardFactory}. Exceptions are wrapped to provide contextual
 * information.
 */
public class BoardJsonFileHandler implements FileHandler<Board> {
  private static final Logger LOGGER =
      Logger.getLogger(BoardJsonFileHandler.class.getName());
  private final Gson gson;

  /**
   * Constructs a BoardJsonFileHandler with a configured Gson instance.
   */
  public BoardJsonFileHandler() {
    this.gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();
  }

  /**
   * Saves a {@link Board} object to a JSON file.
   *
   * @param board    the Board object to save; must not be null
   * @param filePath the path where the file should be saved; must not be null or empty
   * @throws FileSaveException        if an error occurs during the save operation
   * @throws IllegalArgumentException if the board is null or file path is invalid
   */
  @Override
  public void saveToFile(Board board, String filePath) throws FileSaveException {
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null");
    }
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    BoardDto dto = BoardConverter.toDto(board);
    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(dto, writer);
      LOGGER.log(Level.INFO, () -> String.format("Saved board to %s", filePath));
    } catch (IOException e) {
      String msg = String.format(
          "Failed to save board to JSON file '%s'",
          filePath
      );
      LOGGER.log(Level.SEVERE, msg, e);
      throw new FileSaveException(msg, e);
    }
  }

  /**
   * Loads a {@link Board} object from a JSON file.
   *
   * @param filePath the path of the file to load; must not be null or empty
   * @return the loaded Board object
   *
   * @throws FileLoadException if an error occurs during the load operation
   *                           (e.g., file not found, I/O error)
   *
   * @throws InvalidBoardFormatException if the JSON syntax is invalid or
   *                                     content cannot be parsed to a board
   *
   * @throws IllegalArgumentException    if the file path is invalid
   */
  @Override
  public Board loadFromFile(String filePath) throws FileLoadException {
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    try (FileReader reader = new FileReader(filePath)) {
      BoardDto dto = gson.fromJson(reader, BoardDto.class);
      if (dto == null) {
        String msg = String.format("Failed to parse JSON: null DTO from '%s'", filePath);
        LOGGER.log(Level.WARNING, msg);
        throw new InvalidBoardFormatException(msg);
      }
      Board board = BoardFactory.createBoardFromDto(dto);
      LOGGER.log(Level.INFO, () -> String.format("Loaded board from %s", filePath));
      return board;

    } catch (FileNotFoundException e) {
      String msg = String.format(
          "Board file not found: '%s'",
          filePath
      );
      LOGGER.log(Level.WARNING, msg, e);
      throw new FileLoadException(msg, e);

    } catch (JsonSyntaxException e) {
      String msg = String.format(
          "Invalid JSON syntax in '%s': %s",
          filePath,
          e.getMessage()
      );
      LOGGER.log(Level.WARNING, msg, e);
      throw new InvalidBoardFormatException(msg, e);

    } catch (IOException e) {
      String msg = String.format(
          "Failed to read board from file '%s'",
          filePath
      );
      LOGGER.log(Level.WARNING, msg, e);
      throw new FileLoadException(msg, e);
    }
  }
}
