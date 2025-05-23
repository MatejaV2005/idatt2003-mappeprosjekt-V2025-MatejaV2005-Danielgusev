package edu.ntnu.idi.idatt.view.components.gameselection;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.utils.ResourceLoader;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A popup window for creating a new player.
 *
 * <p>This dialog allows users to enter a player name and select an icon from a predefined list.
 * It includes input validation for the player's name and provides a visual preview
 * of the selected game piece icon.
 * </p>
 */
public class CreatePlayerPopup {

  private static final Logger LOGGER = Logger.getLogger(CreatePlayerPopup.class.getName());
  private static final String DEFAULT_ICON_PATH_PREFIX =
      "/edu/ntnu/idi/idatt/view/resources/icons/";
  private static final String DEFAULT_ICON_EXTENSION = ".png";
  private static final String CSS_PATH =
      "/edu/ntnu/idi/idatt/view/resources/GameSetup/PlayerPopup.css";
  private static final String TOP_LOGO_PATH =
      "/edu/ntnu/idi/idatt/view/resources/TitleScreen/logo.png";


  private static final int PREF_FIELD_WIDTH = 220; // Justert litt
  private static final int ICON_PREVIEW_SIZE = 70; // Justert litt
  private static final int MIN_NAME_LENGTH = 2;
  private static final int MAX_NAME_LENGTH = 15;
  private static final double POPUP_WIDTH = 350; // Justert litt
  private static final double POPUP_HEIGHT = 520; // Justert litt for logo og mer luft
  private static final double PADDING_INSETS = 25; // Økt padding
  private static final double SPACING_TEN = 10;
  private static final double SPACING_FIFTEEN = 15;


  private final Stage popupStage;
  private final TextField nameField;
  private final ComboBox<String> iconComboBox;
  private final ImageView iconPreview;
  private final Label errorLabel;
  private Player createdPlayer;

  /**
   * Constructs a new {@code CreatePlayerPopup} dialog.
   *
   * <p>Initializes all UI components, sets up the layout, and configures event handlers
   * for player creation and icon preview updates.
   * </p>
   */
  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  public CreatePlayerPopup() {
    popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setTitle("Character Creation");
    popupStage.setResizable(false);


    ImageView topLogoView = createTopLogo();

    Label titleLabel = new Label("Character Creation");
    titleLabel.getStyleClass().add("popup-title");

    Label nameLabel = new Label("Player name:");
    nameLabel.getStyleClass().add("popup-label");
    nameField = createNameField();

    Label iconSelectLabel = new Label("Select Icon:");
    iconSelectLabel.getStyleClass().add("popup-label");
    iconComboBox = createIconComboBox();

    iconPreview = createIconPreview();
    HBox iconPreviewBox = new HBox(iconPreview);
    iconPreviewBox.getStyleClass().add("popup-icon-preview-box");


    errorLabel = createErrorLabel();

    Button createButton = createStyledButton("Create", true);
    createButton.getStyleClass().add("popup-button-create");
    createButton.setOnAction(e -> handleCreatePlayer());

    Button cancelButton = createStyledButton("Cancel", false);
    cancelButton.getStyleClass().add("popup-button-cancel");
    cancelButton.setCancelButton(true);
    cancelButton.setOnAction(e -> popupStage.close());

    HBox buttonBox = new HBox(SPACING_FIFTEEN, createButton, cancelButton);
    buttonBox.setAlignment(Pos.CENTER);

    VBox layout = new VBox(SPACING_TEN);
    layout.setPadding(new Insets(PADDING_INSETS));
    layout.setAlignment(Pos.TOP_CENTER); // Endret til TOP_CENTER for logo
    layout.getStyleClass().add("popup-root");

    // Legger til elementer i VBox
    if (topLogoView != null) {
      layout.getChildren().add(topLogoView);
      VBox.setMargin(topLogoView, new Insets(0, 0, 5, 0)); // Litt mindre bunnmarging for logo
    }
    layout.getChildren().addAll(
        titleLabel, // Tittel under logo
        nameLabel, nameField,
        iconSelectLabel, iconComboBox,
        iconPreviewBox, // Bruker HBox for bedre kontroll over preview
        errorLabel,
        buttonBox
    );
    VBox.setMargin(buttonBox, new Insets(SPACING_FIFTEEN, 0, 0, 0)); // Margin over knapper

    iconComboBox.setOnAction(e -> updateIconPreview());

    Scene scene = new Scene(layout, POPUP_WIDTH, POPUP_HEIGHT);
    applyStylesheets(scene); // Laster inn CSS
    popupStage.setScene(scene);
  }

  private ImageView createTopLogo() {
    try {
      Image logo = ResourceLoader.loadImage(TOP_LOGO_PATH);
      ImageView logoView = new ImageView(logo);
      logoView.setFitHeight(50);
      logoView.setPreserveRatio(true);
      logoView.getStyleClass().add("popup-header-image");
      return logoView;
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.WARNING, "Could not load top logo image: " + TOP_LOGO_PATH, e);
      return null;
    }
  }


  private TextField createNameField() {
    TextField field = new TextField();
    field.setPromptText("Character Name"); // Endret prompt
    field.setPrefWidth(PREF_FIELD_WIDTH);
    field.getStyleClass().add("popup-text-field");
    return field;
  }


  private ComboBox<String> createIconComboBox() {
    ComboBox<String> comboBox = new ComboBox<>();
    comboBox.getItems().addAll("Hat", "Car", "Dog", "Dragon", "Battleship", "Default");
    comboBox.setPromptText("Choose an icon");
    comboBox.setPrefWidth(PREF_FIELD_WIDTH);
    comboBox.getStyleClass().add("popup-combo-box");
    return comboBox;
  }


  private ImageView createIconPreview() {
    ImageView preview = new ImageView();
    preview.setFitHeight(ICON_PREVIEW_SIZE);
    preview.setFitWidth(ICON_PREVIEW_SIZE);
    preview.setPreserveRatio(true);
    return preview;
  }


  private Label createErrorLabel() {
    Label label = new Label();
    label.getStyleClass().add("popup-error-label");
    label.setWrapText(true);
    label.setMaxWidth(PREF_FIELD_WIDTH + SPACING_TEN * 2); // Sikrer at den ikke blir for bred
    return label;
  }


  private Button createStyledButton(String text, boolean isDefaultButton) {
    Button button = new Button(text);
    if (isDefaultButton) {
      button.setDefaultButton(true);
    }
    // Generell knappestil kan legges til her hvis ønskelig,
    // men spesifikke stiler legges til direkte (popup-button-create/cancel)
    return button;
  }

  private void applyStylesheets(Scene scene) {
    try {
      String cssUrl = ResourceLoader.loadCssResource(CSS_PATH);
      if (cssUrl != null) {
        scene.getStylesheets().add(cssUrl);
        LOGGER.info("Successfully loaded CSS for CreatePlayerPopup: " + CSS_PATH);
      } else {
        LOGGER.log(Level.WARNING, "CreatePlayerPopup CSS resource not found: " + CSS_PATH);
      }
    } catch (BoardGameResourceException e) {
      LOGGER.log(Level.SEVERE, "Failed to load CreatePlayerPopup CSS: " + e.getMessage(), e);
    }
  }


  private void updateIconPreview() {
    String selectedIconName = iconComboBox.getValue();
    if (selectedIconName != null && !selectedIconName.isEmpty()) {
      String iconPath = DEFAULT_ICON_PATH_PREFIX
          + selectedIconName.toLowerCase().replace(" ", "") // Fjerner mellomrom for filnavn
          + DEFAULT_ICON_EXTENSION;
      try {
        Image image = ResourceLoader.loadImage(iconPath);
        iconPreview.setImage(image);
      } catch (IllegalArgumentException | BoardGameResourceException e) {
        LOGGER.log(Level.WARNING, "Could not load icon: " + iconPath, e);
        iconPreview.setImage(null); // Tømmer preview hvis ikonet ikke lastes
      }
    } else {
      iconPreview.setImage(null);
    }
  }

  private void handleCreatePlayer() {
    String name = nameField.getText().trim();
    String iconName = iconComboBox.getValue();

    if (!isNameValid(name)) {
      return;
    }
    if (iconName == null || iconName.isEmpty()) {
      errorLabel.setText("Please select an icon.");
      return;
    }

    createdPlayer = new Player(name, iconName);
    popupStage.close();
  }


  private boolean isNameValid(String name) {
    if (name.isEmpty()) {
      errorLabel.setText("Player name cannot be empty.");
      return false;
    }
    if (name.length() < MIN_NAME_LENGTH) {
      errorLabel.setText("Name must be at least " + MIN_NAME_LENGTH + " characters long.");
      return false;
    }
    if (name.length() > MAX_NAME_LENGTH) {
      errorLabel.setText("Name must be at most " + MAX_NAME_LENGTH + " characters long.");
      return false;
    }
    errorLabel.setText(""); // Tøm feilmelding hvis navnet er gyldig
    return true;
  }

  /**
   * Shows the player creation popup dialog and waits for user input.
   *
   * <p>The dialog is modal, meaning it blocks interaction with other parts of the application
   * until it is closed. Input fields are cleared before showing.
   * </p>
   *
   * @return An {@link Optional} containing the created {@link Player} if the user
   *                             successfully created a player; otherwise, an empty {@link Optional}
   *                             if the dialog was cancelled or closed without creating a player.
   */
  public Optional<Player> show() {
    nameField.clear();
    iconComboBox.setValue(null);
    iconPreview.setImage(null);
    errorLabel.setText("");
    createdPlayer = null;

    popupStage.showAndWait();
    return Optional.ofNullable(createdPlayer);
  }
}
