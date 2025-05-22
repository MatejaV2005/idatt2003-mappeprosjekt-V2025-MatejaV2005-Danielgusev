package edu.ntnu.idi.idatt.filehandler;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
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
 * Handles saving and loading {@link Player} lists to and from CSV files.
 *
 * <p>Uses OpenCSV under the hood, validates each line, and wraps I/O errors
 * in domain‐specific exceptions so callers can handle them appropriately.
 *
 * @since 1.0
 */
public class PlayerCsvFileHandler implements FileHandler<List<Player>> {

  private static final Logger LOGGER =
      Logger.getLogger(PlayerCsvFileHandler.class.getName());

  private static final int EXPECTED_COLUMNS = 2;

  /**
   * Saves a list of players to a CSV file.
   *
   * @param players  the list of players to save; must not be null
   * @param filePath the file path; must not be null or blank
   * @throws FileSaveException        on I/O errors during save
   * @throws IllegalArgumentException if players or filePath are invalid
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
      for (Player p : players) {
        writer.writeNext(new String[]{p.getName(), p.getPieceType()});
      }
      LOGGER.log(
          Level.INFO,
          "Successfully saved {0} players to \"{1}\"",
          new Object[]{players.size(), filePath});
    } catch (IOException e) {
      String errorMessage = String.format(
          "Failed to save %d players to CSV file \"%s\"",
          players.size(),
          filePath);
      throw new FileSaveException(errorMessage, e);
    }
  }


  /**
   * Attempts to create a Player object from name and piece type and adds it to the list.
   * Logs a warning if creation fails due to invalid data.
   *
   * @param playersList The list to add the player to.
   * @param name The name of the player.
   * @param piece The piece type of the player.
   * @param filePath The path of the file being processed (for logging).
   * @param lineNum The current line number in the file (for logging).
   */
  private void tryCreateAndAddPlayer(List<Player> playersList,
      String name,
      String piece,
      String filePath,
      int lineNum) {
    try {
      playersList.add(PlayerFactory.createPlayer(name, piece));
    } catch (IllegalArgumentException iae) {
      LOGGER.log(Level.WARNING,
          "Skipping line {0} in \"{1}\": invalid player data ({2})",
          new Object[]{lineNum, filePath, iae.getMessage()});
    }
  }

  /**
   * Loads a list of players from a CSV file.
   *
   * @param filePath the path of the file to load; must not be null or blank
   * @return the list of successfully parsed players (empty if file not found)
   * @throws FileLoadException        on CSV validation or I/O errors
   * @throws IllegalArgumentException if filePath is invalid
   */
  @Override
  public List<Player> loadFromFile(String filePath) throws FileLoadException {
    if (filePath == null || filePath.isBlank()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    List<Player> players = new ArrayList<>();
    int lineNum = 0;

    try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
      String[] cols;
      while ((cols = reader.readNext()) != null) {
        lineNum++;
        if (cols.length < EXPECTED_COLUMNS) {
          LOGGER.log(
              Level.WARNING,
              "Skipping line {0} in \"{1}\": expected at least {2} columns but found {3}",
              new Object[]{lineNum, filePath, EXPECTED_COLUMNS, cols.length});
          continue;
        }

        String name = cols[0];
        String piece = cols[1];
        if (name == null || name.isBlank() || piece == null || piece.isBlank()) {
          LOGGER.log(
              Level.WARNING,
              "Skipping line {0} in \"{1}\": name or pieceType is blank or null",
              new Object[]{lineNum, filePath});
          continue;
        }

        tryCreateAndAddPlayer(players, name, piece, filePath, lineNum);
      }

      LOGGER.log(
          Level.INFO,
          "Successfully loaded {0} players from \"{1}\"",
          new Object[]{players.size(), filePath});
      return players;

    } catch (FileNotFoundException e) {
      LOGGER.log(
          Level.INFO,
          "Player data file not found: \"{0}\". Returning empty list.",
          filePath);
      return players;

    } catch (CsvValidationException e) {
      String context = String.format(
          "CSV validation failed at line %d in \"%s\": %s",
          lineNum, filePath, e.getMessage());
      String errorMessage = "Failed to load players due to CSV validation error: " + context;
      throw new FileLoadException(errorMessage, e);

    } catch (IOException e) {
      String context;
      if (lineNum > 0) {
        context = String.format(
            "I/O error at line %d in \"%s\": %s",
            lineNum, filePath, e.getMessage());
      } else {
        context = String.format(
            "I/O error opening or reading \"%s\": %s",
            filePath, e.getMessage());
      }
      String errorMessage = "Failed to load players from CSV file: " + context;
      throw new FileLoadException(errorMessage, e);
    }
  }
}
