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
 * information without logging at this layer.
 *
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
      LOGGER.log(Level.INFO, "Saved board to {0}", filePath);
    } catch (IOException e) {
      throw new FileSaveException(
          String.format("Failed to save board to JSON file '%s'", filePath), e);
    }
  }

  /**
   * Loads a {@link Board} object from a JSON file.
   *
   * @param filePath the path of the file to load; must not be null or empty
   * @return the loaded Board object
   * @throws FileLoadException        if an error occurs during the load operation
   * @throws IllegalArgumentException if the file path is invalid
   */
  @Override
  public Board loadFromFile(String filePath) throws FileLoadException {
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    try (FileReader reader = new FileReader(filePath)) {
      BoardDto dto = gson.fromJson(reader, BoardDto.class);
      if (dto == null) {
        throw new InvalidBoardFormatException(
            String.format("Failed to parse JSON: null DTO from '%s'", filePath));
      }
      Board board = BoardFactory.createBoardFromDto(dto);
      LOGGER.log(Level.INFO, "Loaded board from {0}", filePath);
      return board;

    } catch (FileNotFoundException e) {
      throw new FileLoadException(
          String.format("Board file not found: '%s'", filePath), e);

    } catch (JsonSyntaxException e) {
      throw new InvalidBoardFormatException(
          String.format("Invalid JSON syntax in '%s': %s", filePath, e.getMessage()), e);

    } catch (IOException e) {
      throw new FileLoadException(
          String.format("Failed to read board from file '%s'", filePath), e);
    }
  }
}
