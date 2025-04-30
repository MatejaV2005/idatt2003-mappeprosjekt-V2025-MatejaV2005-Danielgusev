package edu.ntnu.idi.idatt.filehandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.idi.idatt.DataTransfer.BoardDto;
import edu.ntnu.idi.idatt.converter.BoardConverter;
import edu.ntnu.idi.idatt.exceptions.FileLoadException;
import edu.ntnu.idi.idatt.exceptions.FileSaveException;
import edu.ntnu.idi.idatt.exceptions.JsonFileException;
import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.model.core.Board;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoardJsonFileHandler implements FileHandler<Board> {
  private static final Logger logger = Logger.getLogger(BoardJsonFileHandler.class.getName());
  private final Gson gson;

  public BoardJsonFileHandler() {
    this.gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();
    logger.setLevel(Level.INFO);
  }

  @Override
  public void saveToFile(Board board, String filePath) throws FileSaveException {
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    BoardDto dto = BoardConverter.toDto(board);
    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(dto, writer);
      logger.info("Successfully saved board to file: " + filePath);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to save board to file: " + filePath, e);
      throw new FileSaveException("Failed to save board to file: " + filePath, e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unexpected error saving board to file: " + filePath, e);
      throw new JsonFileException("Unexpected error saving board to JSON file: " + filePath, e);
    }
  }

  @Override
  public Board loadFromFile(String filePath) throws FileLoadException {
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    try (FileReader reader = new FileReader(filePath)) {
      BoardFactory factory = new BoardFactory();
      BoardDto dto = gson.fromJson(reader, BoardDto.class);

      logger.info("Successfully loaded and relinked board from file: " + filePath);
      return factory.createBoardFromDto(dto);

    } catch (FileNotFoundException e) {
      logger.log(Level.SEVERE, "Board file not found: " + filePath, e);
      throw new FileLoadException("Board file not found: " + filePath, e);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to load board from file: " + filePath, e);
      throw new FileLoadException("Failed to read board from file: " + filePath, e);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Unexpected error loading board from file: " + filePath, e);
      throw new JsonFileException("Unexpected error loading board from JSON file: " + filePath, e);
    }
  }
}