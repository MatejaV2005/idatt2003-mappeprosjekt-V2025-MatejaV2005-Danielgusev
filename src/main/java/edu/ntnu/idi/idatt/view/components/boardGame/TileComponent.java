package edu.ntnu.idi.idatt.view.components.boardGame;

import edu.ntnu.idi.idatt.model.core.Tile;
import javafx.scene.layout.StackPane;

public class TileComponent extends StackPane {
  private final Tile tile;

  public TileComponent(Tile tile) {
    this.tile = tile;
  }

}
