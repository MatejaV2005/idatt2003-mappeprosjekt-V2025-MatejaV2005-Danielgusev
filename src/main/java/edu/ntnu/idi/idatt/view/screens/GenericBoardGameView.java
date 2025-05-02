package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.controller.BoardGameController;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import edu.ntnu.idi.idatt.view.components.boardGame.BoardComponent;
import edu.ntnu.idi.idatt.view.components.boardGame.BoardInfoPanel;
import edu.ntnu.idi.idatt.view.components.boardGame.CurrentPlayerPanel;
import edu.ntnu.idi.idatt.view.components.boardGame.DicePanel;
import edu.ntnu.idi.idatt.view.renderer.BoardRenderer;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class GenericBoardGameView implements BoardGameView, BoardGameObserver {
  private final Scene scene;
  private BoardGameController controller;
  private final BorderPane root;

  // Your panel components
  private final BoardComponent boardComponent;
  private final BoardInfoPanel boardInfoPanel;
  private final CurrentPlayerPanel currentPlayerPanel;
  private final DicePanel dicePanel;

  // Constructor initializing panels
  public GenericBoardGameView(BoardRenderer renderer) {
    this.root = new BorderPane();

    // Initialize all panels
    this.boardComponent = new BoardComponent(renderer);
    this.boardInfoPanel = new BoardInfoPanel();
    this.currentPlayerPanel = new CurrentPlayerPanel();
    this.dicePanel = new DicePanel();

    // Layout panels in the root BorderPane
    root.setCenter(boardComponent);

    this.scene = new Scene(root, 1000, 700);
  }


  @Override
  public Scene getScene() {
    return this.scene;
  }

  @Override
  public void setController(BoardGameController controller) {
    this.controller = controller;
  }

  @Override
  public void onPlayerMoved(Player player, Tile from, Tile to ) {
    //boardComponent.movePlayer(player, from, to)
  }

  @Override
  public void onPlayerAdded(Player player) {
    //currentPlayerPanel.addPlayer(Player player)
    //boardComponent.addPlayerToken(player)
  }

  @Override
  public void onGameStateChanged(Player player) {
    //currentPlayerPanel.setCurrentPlayer(currentPlayer);
    //dicePanel.setDisabled(false);
  }


  @Override
  public void onGameWon(Player player) {
    //boardInfoPanel.showGameResult(player);
    //dicePanel.setDisabled(true);
  }

  @Override
  public void update(BoardGame boardGame) {
//    boardComponent.updateBoard(boardGame.getBoard());
//    boardInfoPanel.updateGameInfo(boardGame);
//    currentPlayerPanel.setCurrentPlayer(boardGame.getCurrentPlayer());
//    dicePanel.reset();

  }




}

