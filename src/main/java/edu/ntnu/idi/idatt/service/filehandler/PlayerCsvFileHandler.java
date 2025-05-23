package edu.ntnu.idi.idatt.service.filehandler;

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
          () -> String.format("Successfully saved %d players to \"%s\"", players.size(), filePath)
      );
    } catch (IOException e) {
      String errorMessage = String.format(
          "Failed to save %d players to CSV file \"%s\"",
          players.size(),
          filePath
      );
      LOGGER.log(Level.SEVERE, errorMessage, e);
      throw new FileSaveException(errorMessage, e);
    }
  }


  /**
   * Attempts to create a Player object from name and piece type and adds it to the list.
   * Logs a warning if creation fails due to invalid data.
   *
   * @param playersList The list to add the player to.
   * @param name        The name of the player.
   * @param piece       The piece type of the player.
   * @param filePath    The path of the file being processed (for logging).
   * @param currentLine The current line number in the file (for logging, effectively final).
   */
  private void tryCreateAndAddPlayer(
      List<Player> playersList,
      String name,
      String piece,
      String filePath,
      int currentLine) { // Renamed to currentLine to avoid confusion with the loop variable
    try {
      playersList.add(PlayerFactory.createPlayer(name, piece));
    } catch (IllegalArgumentException iae) {
      LOGGER.log(Level.WARNING,
          String.format(
              "Skipping line %d in \"%s\": invalid player data (%s)",
              currentLine, // Use the effectively final parameter
              filePath,
              iae.getMessage()
          )
      );
    }
  }

  /**
   * Handles saving and loading {@link Player} lists to and from CSV files.
   *
   * <p>Uses OpenCSV under the hood, validates each line, and wraps I/O errors
   * in domain-specific exceptions so callers can handle them appropriately.
   * </p>
   *
   * <p>KI-assistanse (Claude 3.7) ble benyttet som sparringspartner for å:
   * - Utvikle og forbedre logikken for innlesing av spillere fra CSV-fil
   * i metoden {@code loadFromFile}, inkludert feilhåndtering og validering.
   * - Strukturere hjelpemetoden {@code tryCreateAndAddPlayer} for robust
   * opprettelse av Player-objekter basert på CSV-data.
   * Spesifikke metoder hvor KI-assistanse var sentral er kommentert deretter.
   * Dato for assistanse: 02-04-25
   * */
  @Override
  public List<Player> loadFromFile(String filePath) throws FileLoadException {
    if (filePath == null || filePath.isBlank()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    List<Player> players = new ArrayList<>();
    int lineCounter = 0;

    try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
      String[] cols;
      while ((cols = reader.readNext()) != null) {
        lineCounter++;
        final int currentLineNum = lineCounter;

        if (cols.length < EXPECTED_COLUMNS) {
          String[] finalCols = cols;
          LOGGER.log(
              Level.WARNING,
              () -> String.format(
                  "Skipping line %d in \"%s\": expected at least %d columns but found %d",
                  currentLineNum,
                  filePath,
                  EXPECTED_COLUMNS,
                  finalCols.length
              )
          );
          continue;
        }

        String name = cols[0];
        String piece = cols[1];
        if (name == null || name.isBlank() || piece == null || piece.isBlank()) {
          LOGGER.log(
              Level.WARNING,
              () -> String.format(
                  "Skipping line %d in \"%s\": name or pieceType is blank or null",
                  currentLineNum,
                  filePath
              )
          );
          continue;
        }

        tryCreateAndAddPlayer(players, name, piece, filePath, currentLineNum);
      }

      LOGGER.log(
          Level.INFO,
          () -> String.format("Successfully loaded %d players from \"%s\"",
              players.size(),
              filePath)
      );
      return players;

    } catch (FileNotFoundException e) {
      LOGGER.log(
          Level.INFO,
          () -> String.format("Player data file not found: \"%s\". Returning empty list.", filePath)
      );
      return players;

    } catch (CsvValidationException e) {
      String context = String.format(
          "CSV validation failed at or near line %d in \"%s\"",
          lineCounter,
          filePath
      );
      String errorMessage = String.format(
          "Failed to load players due to CSV validation error: %s: %s",
          context,
          e.getMessage()
      );
      LOGGER.log(Level.WARNING, errorMessage, e);
      throw new FileLoadException(errorMessage, e);

    } catch (IOException e) {
      String context;
      if (lineCounter > 0) {
        context = String.format(
            "I/O error at or near line %d in \"%s\"",
            lineCounter,
            filePath
        );
      } else {
        context = String.format(
            "I/O error opening or reading \"%s\"",
            filePath
        );
      }
      String errorMessage = String.format(
          "Failed to load players from CSV file: %s: %s",
          context,
          e.getMessage()
      );
      LOGGER.log(Level.WARNING, errorMessage, e);
      throw new FileLoadException(errorMessage, e);
    }
  }
}
