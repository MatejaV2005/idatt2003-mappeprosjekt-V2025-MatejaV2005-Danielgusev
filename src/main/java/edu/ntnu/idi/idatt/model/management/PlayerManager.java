package edu.ntnu.idi.idatt.model.management;

import edu.ntnu.idi.idatt.filehandler.PlayerCsvFileHandler;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class PlayerManager {

  private static PlayerManager instance = null;

  private static final Logger LOGGER = Logger.getLogger(PlayerManager.class.getName());
  private static final String PLAYERS_FILE_PATH = "Files/Players/players.CSV";
  private final PlayerCsvFileHandler fileHandler;
  private final List<Player> players;

  private PlayerManager() {
    this.fileHandler = new PlayerCsvFileHandler();
    this.players = new ArrayList<>();
  }


  // get-method for single-Instance access
  public static PlayerManager getInstance() {
    if (instance == null) {
      instance = new PlayerManager();
    }

    return instance;
  }

  // In-memory management methods
  public void addPlayer(Player player) {
    ExceptionHandling.requireNonNull("player cannot be null", player.getName());
    players.add(player);
  }

  public void removePlayer(Player player) {
    players.remove(player);
  }

  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  public List<Player> loadPlayersFromFile() {
    try {
      return fileHandler.loadFromFile(PLAYERS_FILE_PATH);
    } catch (Exception e) {
      LOGGER.warning("Could not load players from defualt filePath");
      return List.of();
    }
  }

  // Will use if there is a search functionality for saved players
  public Optional<Player> loadPlayer(String playerName) {
    ExceptionHandling.requireNonNull("name cant be null or empty", playerName);

    List<Player> players = loadPlayersFromFile();
    try {
    return players.stream()
        .filter(p -> p.getName().equals(playerName))
        .findFirst();
    } catch (Exception e) {
      LOGGER.warning("Could not load player '" + playerName + "': " + e.getMessage());
      return Optional.empty();
    }
  }

  public void savePlayers(List<Player> players) {
    try {
      fileHandler.saveToFile(players, PLAYERS_FILE_PATH);
    } catch (Exception e) {
      LOGGER.warning("Could not save players to defualt filePath");
      e.printStackTrace();
    }
  }


  // FIX METHOD LATER
  public void savePlayerToFile(Player player) {

  }
}
