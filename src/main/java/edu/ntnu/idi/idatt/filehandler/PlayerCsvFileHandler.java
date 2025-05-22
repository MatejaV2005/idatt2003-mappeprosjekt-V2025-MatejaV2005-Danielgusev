package edu.ntnu.idi.idatt.filehandler;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import edu.ntnu.idi.idatt.exceptions.FileLoadException;
import edu.ntnu.idi.idatt.exceptions.FileSaveException;
import edu.ntnu.idi.idatt.factory.PlayerFactory;
import edu.ntnu.idi.idatt.model.core.Player;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles saving and loading Player objects to/from CSV files.
 * Uses OpenCSV for CSV file operations.
 */
public class PlayerCsvFileHandler implements FileHandler<List<Player>> {
  private static final Logger LOGGER = Logger.getLogger(PlayerCsvFileHandler.class.getName());

  /**
   * Saves a list of Player objects to a CSV file.
   *
   * @param players the list of Player objects to save
   * @param filePath the path where the file should be saved
   * @throws FileSaveException if an error occurs during the save operation
   * @throws IllegalArgumentException if the players list or file path is invalid
   */
  @Override
  public void saveToFile(List<Player> players, String filePath) throws FileSaveException {
    if (players == null) {
      throw new IllegalArgumentException("Players list cannot be null");
    }
    if (filePath == null || filePath.isBlank()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
      for (Player player : players) {
        String[] playerData = {player.getName(), player.getPieceType()};
        writer.writeNext(playerData);
      }
      LOGGER.info("Successfully saved " + players.size() + " players to: " + filePath);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error saving players to file: " + filePath, e);
      throw new FileSaveException("Failed to save players to CSV file: " + filePath, e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error saving players to file: " + filePath, e);
      throw new FileSaveException("Unexpected error saving players to CSV file: " + filePath, e);
    }
  }

  /**
   * Loads a list of Player objects from a CSV file.
   *
   * @param filePath the path of the file to load
   * @return the loaded list of Player objects
   * @throws FileLoadException if an error occurs during the load operation
   * @throws IllegalArgumentException if the file path is invalid
   */
  @Override
  public List<Player> loadFromFile(String filePath) throws FileLoadException {
    if (filePath == null || filePath.isBlank()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    List<Player> players = new ArrayList<>();
    int lineNumber = 0;

    try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
      String[] line;

      while ((line = reader.readNext()) != null) {
        lineNumber++;

        if (line.length < 2) {
          LOGGER.warning("Skipping line " + lineNumber + ": insufficient columns (expected 2, got " + line.length + ")");
          continue;
        }

        String name = line[0];
        String pieceType = line[1];

        if (name.isBlank() || pieceType.isBlank()) {
          LOGGER.warning("Skipping line " + lineNumber + ": player name and/or pieceType is blank");
          continue;
        }

        try {
          players.add(PlayerFactory.createPlayer(name, pieceType));
        } catch (IllegalArgumentException e) {
          LOGGER.warning("Skipping line " + lineNumber + ": " + e.getMessage());
        }
      }

      LOGGER.info("Successfully loaded " + players.size() + " players from " + filePath);
      return players;
    } catch (FileNotFoundException e) {
      LOGGER.log(Level.SEVERE, "Players file not found: " + filePath, e);
      return new ArrayList<>();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error reading players from file: " + filePath, e);
      throw new FileLoadException("Failed to read players from CSV file: " + filePath, e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error loading players from file: " + filePath, e);
      throw new FileLoadException("Unexpected error loading players from CSV file: " + filePath, e);
    }
  }
}