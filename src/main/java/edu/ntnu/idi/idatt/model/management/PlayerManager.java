package edu.ntnu.idi.idatt.model.management;

import edu.ntnu.idi.idatt.exceptions.FileLoadException;
import edu.ntnu.idi.idatt.exceptions.FileSaveException;
import edu.ntnu.idi.idatt.filehandler.PlayerCsvFileHandler;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.exceptions.PlayerManagementException;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager class for Player entities, handling both in-memory player storage
 * and persistent storage operations. Follows the Singleton pattern to ensure
 * a single point of access to player data throughout the application.
 */
public class PlayerManager {

  private static final Logger LOGGER = Logger.getLogger(PlayerManager.class.getName());
  private static final String PLAYERS_FILE_PATH = "Files/Players/players.CSV";

  private static PlayerManager instance;
  private final PlayerCsvFileHandler fileHandler;
  private final List<Player> currentPlayers;

  /**
   * Private constructor to enforce Singleton pattern.
   * Initializes the file handler and player list.
   */
  private PlayerManager() {
    this.fileHandler = new PlayerCsvFileHandler();
    this.currentPlayers = new ArrayList<>();
  }

  /**
   * Gets the singleton instance of PlayerManager.
   *
   * @return The PlayerManager instance
   */
  public static synchronized PlayerManager getInstance() {
    if (instance == null) {
      instance = new PlayerManager();
    }
    return instance;
  }

  /**
   * Adds a player to the current game session.
   *
   * @param player The player to add
   * @throws PlayerManagementException if the player is null or already exists
   */
  public void addPlayer(Player player) throws PlayerManagementException {
    ExceptionHandling.requireNonNull(player, "player");
    ExceptionHandling.requireNonNullOrBlank("Player name cannot be null", player.getName());

    if (currentPlayers.stream().anyMatch(p -> p.getName().equals(player.getName()))) {
      throw new PlayerManagementException("Player with name '" + player.getName() + "' already exists");
    }

    currentPlayers.add(player);
    LOGGER.log(Level.INFO, "Added player: {0}", player.getName());
  }

  /**
   * Removes a player from the current game session.
   *
   * @param player The player to remove
   * @return true if player was successfully removed, false otherwise
   */
  public boolean removePlayer(Player player) {
    boolean removed = currentPlayers.remove(player);
    if (removed) {
      LOGGER.log(Level.INFO, "Removed player: {0}", player.getName());
    }
    return removed;
  }

  /**
   * Gets a defensive copy of the current players list.
   *
   * @return Unmodifiable list of players in the current game session
   */
  public List<Player> getPlayers() {
    return Collections.unmodifiableList(new ArrayList<>(currentPlayers));
  }

  /**
   * Loads all players from the saved players file.
   *
   * @return List of saved players, empty list if loading fails
   */
  public List<Player> loadPlayersFromFile() {
    try {
      List<Player> loadedPlayers = fileHandler.loadFromFile(PLAYERS_FILE_PATH);
      LOGGER.log(Level.INFO, "Loaded {0} players from file", loadedPlayers.size());
      return loadedPlayers;

    } catch (FileLoadException e) {
      LOGGER.log(Level.WARNING, "Could not load players from file: {0}", e.getMessage());
      return new ArrayList<>();
    }
  }

  /**
   * Loads a specific player by name from the saved players file.
   *
   * @param playerName The name of the player to load
   * @return Optional containing the player if found, empty otherwise
   * @throws IllegalArgumentException if playerName is null or empty
   */
  public Optional<Player> loadPlayerByName(String playerName) {
    if (playerName == null || playerName.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty");
    }

    try {
      List<Player> savedPlayers = loadPlayersFromFile();
      Optional<Player> foundPlayer = savedPlayers.stream()
          .filter(p -> p.getName().equals(playerName))
          .findFirst();

      foundPlayer.ifPresent(player ->
          LOGGER.log(Level.INFO, "Found player: {0}", player.getName()));

      return foundPlayer;
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error searching for player '{0}': {1}",
          new Object[]{playerName, e.getMessage()});
      return Optional.empty();
    }
  }

  /**
   * Saves a list of players to the persistent storage.
   *
   * @param playersToSave The list of players to save
   * @throws PlayerManagementException if saving fails
   */
  public void savePlayers(List<Player> playersToSave) throws PlayerManagementException {
    try {
      fileHandler.saveToFile(playersToSave, PLAYERS_FILE_PATH);
      LOGGER.log(Level.INFO, "Saved {0} players to file", playersToSave.size());
    } catch (FileSaveException e) {
      LOGGER.log(Level.SEVERE, "Failed to save players: {0}", e.getMessage());
      throw new PlayerManagementException("Could not save players: " + e.getMessage(), e);
    }
  }

  /**
   * Saves a single player to the persistent storage by adding to existing saved players.
   *
   * @param player The player to save
   * @return true if player was successfully saved, false if player already exists
   * @throws PlayerManagementException if saving fails
   */
  public boolean savePlayer(Player player) throws PlayerManagementException {
    ExceptionHandling.requireNonNull(player, "Player");

    List<Player> savedPlayers = loadPlayersFromFile();

    // Check if player already exists
    if (savedPlayers.stream().anyMatch(p -> p.getName().equals(player.getName()))) {
      LOGGER.log(Level.INFO, "Player {0} already exists in saved players", player.getName());
      return false;
    }

    savedPlayers.add(player);
    savePlayers(savedPlayers);
    return true;
  }

  /**
   * Clears all players from the current game session.
   */
  public void clearCurrentPlayers() {
    currentPlayers.clear();
    LOGGER.log(Level.INFO, "Cleared all current players");
  }
}