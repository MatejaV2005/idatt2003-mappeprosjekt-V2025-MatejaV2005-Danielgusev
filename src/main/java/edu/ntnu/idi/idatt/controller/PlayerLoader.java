package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.filehandler.PlayerCsvFileHandler;
import edu.ntnu.idi.idatt.model.playertype.Player;
import edu.ntnu.idi.idatt.utils.ExceptionHandling;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class PlayerLoader {
  private static final Logger LOGGER = Logger.getLogger(PlayerLoader.class.getName());
  private static final String PLAYERS_FILE_PATH = "Files/Players/players.CSV";
  private final PlayerCsvFileHandler fileHandler;

  public PlayerLoader() {
    this.fileHandler = new PlayerCsvFileHandler();
  }

  public List<Player> loadPlayers() {
    try {
      return fileHandler.loadFromFile(PLAYERS_FILE_PATH);
    } catch (Exception e) {
      LOGGER.warning("Could not load players from defualt filePath");
      return List.of();
    }
  }

  public Optional<Player> loadPlayer(String playerName) {
    ExceptionHandling.requireNonNull("name cant be null or empty", playerName);

    List<Player> players = loadPlayers();
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
}
