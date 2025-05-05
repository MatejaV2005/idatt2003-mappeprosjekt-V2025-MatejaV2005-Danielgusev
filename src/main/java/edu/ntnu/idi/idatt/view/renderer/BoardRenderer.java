package edu.ntnu.idi.idatt.view.renderer;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public interface BoardRenderer {

  void renderInitialBoard(Pane boardPane, Board board);

  Node createPlayerTokenNode(Player player);

  void updatePlayerTokenPosition(Node playerTokenNode, Tile targetTile, Pane boardPane);

}
