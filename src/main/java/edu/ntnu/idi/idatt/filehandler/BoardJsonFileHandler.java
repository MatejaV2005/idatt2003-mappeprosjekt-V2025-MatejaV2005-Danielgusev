package edu.ntnu.idi.idatt.filehandler;

import com.google.gson.Gson;
import edu.ntnu.idi.idatt.model.Board;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoardJsonFileHandler implements FileHandler {

  private static final Logger logger = Logger.getLogger(BoardJsonFileHandler.class.getName());
  private final Gson gson = new Gson();

  @Override
  public void saveToFile(Object object, String filePath) {
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(object, writer);
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
      Board board = gson.fromJson(reader, Board.class);
      logger.info("Successfully loaded board from file: " + filePath);
      return board;
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to load board from file: " + filePath, e);
      throw new RuntimeException("Failed to load file", e);
    }
  }
}
