package edu.ntnu.idi.idatt.service;

import edu.ntnu.idi.idatt.exceptions.FileLoadException;
import edu.ntnu.idi.idatt.exceptions.FileSaveException;
import edu.ntnu.idi.idatt.exceptions.PlayerManagementException;
import edu.ntnu.idi.idatt.service.filehandler.PlayerCsvFileHandler;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Singleton manager for {@link Player} objects.
 *
 * <p>Maintains an in‐memory list of current players and provides methods to
 * load/save players from/to CSV via {@link PlayerCsvFileHandler}.
 * </p>
 */
public class PlayerManager {

  private static final Logger LOGGER =
      Logger.getLogger(PlayerManager.class.getName());
  private static final String PLAYERS_FILE_PATH =
      "Files/Players/players.csv"; // Consider making this configurable or using Path

  private static PlayerManager instance;
  private final PlayerCsvFileHandler fileHandler;
  private final List<Player> currentPlayers;

  /**
   * Private constructor to enforce Singleton pattern.
   * Initializes the file handler and the list of current players.
   */
  private PlayerManager() {
    this.fileHandler = new PlayerCsvFileHandler();
    this.currentPlayers = new ArrayList<>();
  }

  /**
   * Returns the single {@link PlayerManager} instance.
   * This implementation uses lazy initialization with synchronization.
   *
   * @return the shared PlayerManager instance.
   */
  public static synchronized PlayerManager getInstance() {
    if (instance == null) {
      instance = new PlayerManager();
    }
    return instance;
  }

  /**
   * Adds a new player to the current session.
   *
   * @param player the Player to add; must not be null, and its name must not be null or blank.
   * @throws PlayerManagementException if player is null, name is invalid, or name already exists.
   */
  public void addPlayer(Player player) throws PlayerManagementException {
    ExceptionHandling.requireNonNull(player, "player");
    ExceptionHandling.requireNonNullOrBlank(player.getName(), "Player name");


    boolean exists = currentPlayers.stream()
        .anyMatch(p -> p.getName().equalsIgnoreCase(player.getName()));
    if (exists) {
      String errMsg = String.format(
          "Player with name '%s' already exists in the current session.", player.getName());
      throw new PlayerManagementException(errMsg);
    }

    currentPlayers.add(player);
    LOGGER.log(Level.INFO, () -> String.format(
        "Added player to current session: %s",
        player.getName()));
  }

  /**
   * Removes a player from the current session.
   * Does nothing if the player is null or not found.
   *
   * @param player the Player to remove; may be null.
   */
  public void removePlayer(Player player) {
    if (player == null) {
      LOGGER.log(Level.FINE, "Attempted to remove a null player. Operation skipped.");
      return;
    }
    boolean removed = currentPlayers.remove(player);
    if (removed) {
      LOGGER.log(Level.INFO, () -> String.format(
          "Removed player from current session: %s",
          player.getName()));
    } else {
      LOGGER.log(Level.FINE, () -> String.format(
          "Attempted to remove player '%s', but they were not in the current session.",
          player.getName()));
    }
  }

  /**
   * Gets an unmodifiable view of the list of current in‐session players.
   *
   * @return an unmodifiable list of current players.
   */
  public List<Player> getPlayers() {
    return Collections.unmodifiableList(currentPlayers);
  }

  /**
   * Loads all saved players from the configured CSV file.
   * If loading fails, an error is logged, and an empty list is returned.
   *
   * @return a list of saved players; empty if load fails or no players are saved.
   */
  public List<Player> loadPlayersFromFile() {
    try {
      List<Player> savedPlayers = fileHandler.loadFromFile(PLAYERS_FILE_PATH);
      LOGGER.log(Level.INFO, () -> String.format(
          "Loaded %d players from file: %s",
          savedPlayers.size(), PLAYERS_FILE_PATH));
      return savedPlayers;
    } catch (FileLoadException e) {
      String msg = String.format(
          "Could not load players from file '%s': %s",
          PLAYERS_FILE_PATH,
          e.getMessage()
      );
      LOGGER.log(Level.WARNING, msg, e);
      return new ArrayList<>();
    }
  }

  /**
   * Searches the saved players (from file) for one with the given name (case-insensitive).
   *
   * @param playerName the name to search for; must not be null or blank.
   * @return an {@link Optional} containing the found player, or empty if not found.
   * @throws IllegalArgumentException if playerName is null or blank.
   */
  @SuppressWarnings("unused")
  public Optional<Player> loadPlayerByName(String playerName) {
    if (playerName == null || playerName.isBlank()) {
      throw new IllegalArgumentException("Player name for search cannot be null or blank.");
    }
    List<Player> savedPlayers = loadPlayersFromFile();
    Optional<Player> foundPlayer = savedPlayers.stream()
        .filter(p -> p.getName().equalsIgnoreCase(playerName))
        .findFirst();

    foundPlayer.ifPresent(p ->
        LOGGER.log(Level.INFO, () -> String.format(
            "Found saved player by name '%s': %s",
            playerName, p.getName()))
    );
    if (foundPlayer.isEmpty()) {
      LOGGER.log(Level.INFO, () -> String.format(
          "No saved player found with name '%s'",
          playerName));
    }
    return foundPlayer;
  }

  /**
   * Saves the given list of players to the configured CSV file.
   *
   * @param playersToSave the list of players to persist; must not be null.
   * @throws PlayerManagementException if the list is null or if saving fails.
   */
  @SuppressWarnings("unused")
  public void savePlayers(List<Player> playersToSave)
      throws PlayerManagementException {
    ExceptionHandling.requireNonNull(playersToSave, "playersToSave list");

    try {
      fileHandler.saveToFile(playersToSave, PLAYERS_FILE_PATH);
      LOGGER.log(Level.INFO, () -> String.format(
          "Saved %d players to file: %s",
          playersToSave.size(),
          PLAYERS_FILE_PATH));
    } catch (FileSaveException e) {
      String msg = String.format(
          "Could not save players to file '%s': %s",
          PLAYERS_FILE_PATH,
          e.getMessage()
      );
      LOGGER.log(Level.SEVERE, msg, e);
      throw new PlayerManagementException(msg, e);
    }
  }

  /**
   * Adds a single player to the list of saved players in the CSV file,
   * if a player with the same name (case-insensitive) is not already present.
   *
   * @param player the player to save; must not be null.
   *
   * @return true if the player was newly saved, false if a player with the same name already existed.
   *
   * @throws PlayerManagementException if the player is null or if saving fails.
   *
   */
  public boolean savePlayer(Player player)
      throws PlayerManagementException {
    ExceptionHandling.requireNonNull(player, "player to save");
    ExceptionHandling.requireNonNullOrBlank(player.getName(), "Player name for saving");


    List<Player> savedPlayers = loadPlayersFromFile();
    boolean exists = savedPlayers.stream()
        .anyMatch(p -> p.getName().equalsIgnoreCase(player.getName()));

    if (exists) {
      LOGGER.log(Level.INFO,
          () -> String.format(
              "Player '%s' already exists in the saved players file. Not saving again.",
              player.getName()));
      return false;
    }

    savedPlayers.add(player);
    savePlayers(savedPlayers);
    LOGGER.log(Level.INFO, () ->
        String.format("Successfully saved new player '%s' to file.", player.getName()));
    return true;
  }

  /**
   * Clears all players from the current in-memory session.
   * This does not affect players saved in the file.
   */
  public void clearCurrentPlayers() {
    if (!currentPlayers.isEmpty()) {
      currentPlayers.clear();
      LOGGER.info("Cleared all players from the current session.");
    } else {
      LOGGER.info("Attempted to clear current players, but the list was already empty.");
    }
  }
}
