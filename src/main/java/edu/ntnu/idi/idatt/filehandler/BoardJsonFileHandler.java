package edu.ntnu.idi.idatt.filehandler;

import com.google.gson.Gson;
import edu.ntnu.idi.idatt.model.Board;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BoardJsonFileHandler implements FileHandler {

  Gson gson = new Gson();

  @Override
  public void saveToFile(Object object, String filePath) {
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(object, writer);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save file", e);
    }
  }

  @Override
  public Board loadFromFile(String filePath) {
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    try (FileReader reader = new FileReader(filePath)) {
      return gson.fromJson(reader, Board.class);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load file", e);
    }
  }

}
