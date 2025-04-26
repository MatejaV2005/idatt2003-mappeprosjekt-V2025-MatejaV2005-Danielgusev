package edu.ntnu.idi.idatt.view.screens;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.factory.ButtonFactory;
import edu.ntnu.idi.idatt.view.components.gameSelection.GameInfoPanel;
import edu.ntnu.idi.idatt.view.components.gameSelection.DifficultySelectionPanel;
import edu.ntnu.idi.idatt.view.components.gameSelection.PlayerManagementPanel;
import edu.ntnu.idi.idatt.view.utils.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class GameSetupView {

  private final Scene scene;
  private final BorderPane root;
  private static final String CSS_PATH = "edu/ntnu/idi/idatt/view/resources/GameSetup/gameSetupStyle.css";


  private final DifficultySelectionPanel difficultySelectionPanel;
  private final PlayerManagementPanel playerManagementPanel;
  private final GameInfoPanel gameInfoPanel;

  private final Button startGameButton;
  private final Button backButton;
  private final HBox bottomActionBar;

  public GameSetupView() {
    root = new BorderPane();
    root.setPadding(new Insets(20));

    ButtonFactory buttonFactory = new ButtonFactory();
    difficultySelectionPanel = new DifficultySelectionPanel();
    playerManagementPanel = new PlayerManagementPanel(buttonFactory);
    gameInfoPanel = new GameInfoPanel();

    startGameButton = buttonFactory.createStandardButton("Start Game");
    backButton = buttonFactory.createStandardButton("Back");

    root.setLeft(difficultySelectionPanel);
    BorderPane.setAlignment(difficultySelectionPanel, Pos.TOP_CENTER);
    BorderPane.setMargin(difficultySelectionPanel, new Insets(0, 20, 0, 0));

    root.setCenter(playerManagementPanel);
    BorderPane.setMargin(playerManagementPanel, new Insets(0, 20, 0, 0));

    root.setRight(gameInfoPanel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    bottomActionBar = new HBox(20, backButton, spacer, startGameButton);
    bottomActionBar.setAlignment(Pos.CENTER);
    bottomActionBar.setPadding(new Insets(20, 0, 0, 0));
    root.setBottom(bottomActionBar);

    scene = new Scene(root, 800, 600);
    // Add stylesheets if needed
    applyStylesheets();
  }


  /**
   * Applies CSS stylesheets to the scene.
   */
  private void applyStylesheets() {
    try {
      scene.getStylesheets().add(ResourceLoader.loadCssResource(CSS_PATH));
    } catch (BoardGameResourceException e) {
      System.err.println("Failed to load CSS: " + e.getMessage());
    }
  }

  // --- Getters for Controller Access ---

  public Scene getScene() {
    return scene;
  }

  public DifficultySelectionPanel getDifficultySelectionPanel() {
    return difficultySelectionPanel;
  }

  public PlayerManagementPanel getPlayerManagementPanel() {
    return playerManagementPanel;
  }

  public GameInfoPanel getBoardPreviewPanel() {
    return gameInfoPanel;
  }

  public Button getStartGameButton() {
    return startGameButton;
  }

  public Button getBackButton() {
    return backButton;
  }


}