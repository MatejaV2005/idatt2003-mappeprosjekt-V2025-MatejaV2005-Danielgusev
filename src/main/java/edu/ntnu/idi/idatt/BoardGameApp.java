package edu.ntnu.idi.idatt;

import static edu.ntnu.idi.idatt.model.core.playertype.BotPlayer.LOGGER;

import edu.ntnu.idi.idatt.exceptions.BoardManagementException;
import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.model.management.BoardManager;
import edu.ntnu.idi.idatt.model.management.PlayerManager;
import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.games.SnakesAndLaddersGame;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.model.strategy.SnakesAndLaddersStrategy;
import java.util.List;
import java.util.logging.Level;

public class BoardGameApp {
  private BoardGame game;
  private GameStrategy strategy;

  public BoardGameApp() {}

  public void start() {
    try {
      init();
      runGame();
    } catch (BoardManagementException e) {
      LOGGER.log(Level.SEVERE, "Failed to initialize game: " + e.getMessage(), e);
      System.err.println("Unable to start game: " + e.getMessage());
      // You might want to add additional error handling here based on your application's needs
    }
  }

  private void runGame() {
    if (game == null) {
      LOGGER.severe("Cannot run game - game was not initialized properly");
      return;
    }

    game.startGame();

    System.out.println("Game has started!");
    System.out.println("Current round: " + game.getRoundCount());
    System.out.println("Players: ");
    game.getPlayers().forEach(p ->
        System.out.println("- " + p.getName() + " starting on tile " + p.getCurrentTile().getTileId()));

    if (game.hasGameStarted()) {
      while (!game.isGameOver()) {
        System.out.println("Round " + game.getRoundCount());
        System.out.println("Current player: " + game.getCurrentPlayer().getName());

        game.playNextTurn();

        for (Player player : game.getPlayers()) {
          System.out.println(player.getName() + " is on tile: " + player.getCurrentTile().getTileId());
        }

        System.out.println();
      }
    }

    game.getWinner().ifPresentOrElse(
        winner -> System.out.println("congrats " + winner.getName() + ", YOU WON!"),
        () -> System.out.println("no winner was determined")
    );
  }

  private void init() throws BoardManagementException {
    BoardManager boardManager = BoardManager.getInstance();
    PlayerManager playerManager = PlayerManager.getInstance();



    // Load the default board
    Board defaultBoard = boardManager.loadBoardFromFile(
        "Files/Boards/Hard_Board_20250517_180054.json");

    // Initialize dice and game components
    Dice dice = new Dice(2);
    this.strategy = new SnakesAndLaddersStrategy(defaultBoard, dice);
    this.game = new SnakesAndLaddersGame(defaultBoard, dice, strategy);

    // Load and add players (don't throw exception if player loading fails)
    try {
      List<Player> players = playerManager.loadPlayersFromFile();
      for (Player player : players) {
        game.addPlayer(player);
      }
      LOGGER.info("Added " + players.size() + " players to the game");
    } catch (Exception e) {
      // Just log the error and continue without players
      LOGGER.warning("Failed to load players: " + e.getMessage());
    }
  }
}