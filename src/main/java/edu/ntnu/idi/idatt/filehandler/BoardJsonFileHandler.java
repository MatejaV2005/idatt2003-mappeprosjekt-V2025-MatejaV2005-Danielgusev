package edu.ntnu.idi.idatt.filehandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.idi.idatt.DataTransfer.BoardDto;
import edu.ntnu.idi.idatt.converter.BoardConverter;
import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.model.Board;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoardJsonFileHandler implements FileHandler {
  private static final Logger logger = Logger.getLogger(BoardJsonFileHandler.class.getName());
  private final Gson gson;

  public BoardJsonFileHandler() {
    // Configure Gson to handle complex objects
    this.gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();
    logger.setLevel(Level.INFO);
  }

  @Override
  public void saveToFile(Object object, String filePath) {
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    if (!(object instanceof Board board)) {
      throw new IllegalArgumentException("Object must be of type Board");
    }

    BoardDto dto = BoardConverter.toDto(board);
    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(dto, writer);
      logger.info("Successfully saved board to file: " + filePath);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to save board to file: " + filePath, e);
      throw new RuntimeException("Failed to save file", e);
    }
  }

  @Override
  public Board loadFromFile(String filePath) {
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    try (FileReader reader = new FileReader(filePath)) {
      BoardFactory factory = new BoardFactory();
      BoardDto dto = gson.fromJson(reader, BoardDto.class);


      logger.info("Successfully loaded and relinked board from file: " + filePath);
      return factory.createBoardFromDto(dto);

    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to load board from file: " + filePath, e);
      throw new RuntimeException("Failed to load file", e);
    }
  }
}