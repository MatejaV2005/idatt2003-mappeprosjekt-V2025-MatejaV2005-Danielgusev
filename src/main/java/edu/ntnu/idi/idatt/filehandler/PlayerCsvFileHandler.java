package edu.ntnu.idi.idatt.filehandler;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import edu.ntnu.idi.idatt.factory.PlayerFactory;
import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.playertype.Player;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerCsvFileHandler implements FileHandler<List<Player>> {
  private static final Logger LOGGER = Logger.getLogger(PlayerCsvFileHandler.class.getName());
  
  @Override
  public void saveToFile(List<Player> players, String filePath) {
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
      LOGGER.info("Succesfully saved " + players.size() + " players to: " + filePath);

    } catch (IOException e) {
      e.printStackTrace();
      LOGGER.log(Level.SEVERE, "Error saving players to file: " + filePath, e);
      //  throw new CsvFileException("Failed to save players to file: " + filePath, e);
    }
  }

  @Override
  public List<Player> loadFromFile(String filePath) {
    List<Player> players = new ArrayList<>();
    int lineNumber = 0;

    try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
      String[] line;
      lineNumber++;


      while ((line = reader.readNext()) != null) {

        if (line.length < 2) {
          LOGGER.warning("skipping line: " + lineNumber + ": line length less than 2");
          continue;
        }

        String name = line[0];
        String pieceType = line[1];

        if (name.isBlank() || pieceType.isBlank()) {
          LOGGER.warning("Warning at line: " + lineNumber + " skipping, player name and/or pieceType is blank");
          continue;
        }

        try {
          players.add(PlayerFactory.createPlayer(name, pieceType));

        } catch (IllegalArgumentException e) {
          LOGGER.warning("skipping line: " + lineNumber + ": " + e.getMessage());
        }
      }

      LOGGER.info("Successfully loaded " + players.size() + " players from " + filePath);
    } catch (CsvValidationException e) {
      LOGGER.log(Level.SEVERE, "Error loading players from file: " + filePath, e);
      throw new RuntimeException(e);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error loading players from file: " + filePath, e);
      throw new RuntimeException(e);
    }
    return players;
  }
}
