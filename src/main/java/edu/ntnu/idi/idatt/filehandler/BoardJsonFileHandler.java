package edu.ntnu.idi.idatt.filehandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import edu.ntnu.idi.idatt.DataTransfer.BoardDto;
import edu.ntnu.idi.idatt.converter.BoardConverter;
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
 * Handles saving and loading Board objects to/from JSON files.
 * Uses Gson for JSON serialization/deserialization.
 */
public class BoardJsonFileHandler implements FileHandler<Board> {
  private static final Logger LOGGER = Logger.getLogger(BoardJsonFileHandler.class.getName());
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
   * Saves a Board object to a JSON file.
   *
   * @param board the Board object to save
   * @param filePath the path where the file should be saved
   * @throws FileSaveException if an error occurs during the save operation
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
      LOGGER.info("Successfully saved board to file: " + filePath);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to save board to file: " + filePath, e);
      throw new FileSaveException("Failed to save board to JSON file: " + filePath, e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error saving board to file: " + filePath, e);
      throw new FileSaveException("Unexpected error saving board to JSON file: " + filePath, e);
    }
  }

  /**
   * Loads a Board object from a JSON file.
   *
   * @param filePath the path of the file to load
   * @return the loaded Board object
   * @throws FileLoadException if an error occurs during the load operation
   * @throws IllegalArgumentException if the file path is invalid
   */
  @Override
  public Board loadFromFile(String filePath) throws FileLoadException { // Deklarer begge
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    try (FileReader reader = new FileReader(filePath)) {
      BoardDto dto = gson.fromJson(reader, BoardDto.class);

      if (dto == null) {
        throw new InvalidBoardFormatException("Failed to parse JSON: resulted in null DTO from file: " + filePath);
      }
      // BoardConverter.fromDto kaster InvalidBoardFormatException ved valideringsfeil
      Board board = BoardConverter.fromDto(dto);
      LOGGER.info("Successfully loaded and validated board from file: " + filePath);
      return board;
    } catch (FileNotFoundException e) {
      LOGGER.log(Level.SEVERE, "Board file not found: " + filePath, e);
      throw new FileLoadException("Board file not found: " + filePath, e);
    } catch (JsonSyntaxException e) {
      LOGGER.log(Level.SEVERE, "Invalid JSON syntax in file: " + filePath, e);
      throw new InvalidBoardFormatException("Invalid JSON syntax: " + e.getMessage(), e);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to read board from file: " + filePath, e);
      throw new FileLoadException("Failed to read board from file: " + filePath, e);
    } catch (InvalidBoardFormatException e) { // Fanger og kaster videre det spesifikke unntaket
      LOGGER.log(Level.WARNING, "Invalid board format in file: " + filePath + ". " + e.getMessage());
      throw e;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error loading board from file: " + filePath, e);
      // Pakk inn i FileLoadException hvis det ikke allerede er en av de mer spesifikke
      if (e instanceof FileLoadException) throw (FileLoadException) e;
      if (e instanceof InvalidBoardFormatException) throw (InvalidBoardFormatException) e;
      throw new FileLoadException("Unexpected error loading board from JSON file: " + filePath, e);
    }
  }
}