package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.controller.BoardLoader;
import edu.ntnu.idi.idatt.factory.BoardFactory;
import edu.ntnu.idi.idatt.model.Board;
import edu.ntnu.idi.idatt.model.BoardGame;
import edu.ntnu.idi.idatt.model.Dice;
import edu.ntnu.idi.idatt.model.playertype.HumanPlayer;
import edu.ntnu.idi.idatt.model.playertype.Player;
import java.util.List;

public class BoardGameApp {
  private BoardGame game;

  public BoardGameApp() {

  }

  public void start() {
    init();
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

        System.out.println(); // visual separation between rounds
      }
    }

    game.getWinner().ifPresentOrElse(
        winner -> System.out.println("congrats " + winner.getName() + ", YOU WON!"),
        () -> System.out.println("no winner was determined")
        );
  }

  private void init() {
    BoardLoader boardLoader = new BoardLoader();
    BoardFactory factory = new BoardFactory();
    BoardLoader loader = new BoardLoader();

//    Board easyBoard = factory.createEasyBoard();
//    Board defaultBoard = factory.createNormalBoard();
//    Board hardBoard = factory.createHardBoard();
//
//    loader.saveBoardWithGeneratedName(easyBoard, "Easy");
//    loader.saveBoardWithGeneratedName(defaultBoard, "Defualt");
//    loader.saveBoardWithGeneratedName(hardBoard, "Hard");

    Board defaultBoard = loader.loadBoardFromFile("Files/Boards/Defualt_Board_20250407_202351.json");

    Dice dice = new Dice(2);

    this.game = new BoardGame(defaultBoard, dice);



    List<Player> players = List.of(
        new HumanPlayer("player 1", game.getStartingTile()),
        new HumanPlayer("player 2", game.getStartingTile()),
        new HumanPlayer("player 3", game.getStartingTile())
    );

    for (Player player : players) {
      game.addPlayer(player);
    }
  }
}
