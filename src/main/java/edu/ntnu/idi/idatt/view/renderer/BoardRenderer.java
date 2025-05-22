package edu.ntnu.idi.idatt.view.renderer;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.core.Tile;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * Defines the contract for rendering a board game’s visual components
 * within a JavaFX application. Implementations are responsible for:
 * <ul>
 *   <li>Drawing the static board layout and tiles.</li>
 *   <li>Creating visual tokens for players.</li>
 *   <li>Animating token movements based on game logic.</li>
 *   <li>Placing tokens at specific tiles without animation.</li>
 * </ul>
 *
 * <p>All UI updates should be performed on the JavaFX Application Thread.</p>
 *
 * @see javafx.scene.layout.Pane
 * @see javafx.scene.Node
 */
public interface BoardRenderer {

  /**
   * Renders the initial game board into the specified pane.
   *
   * <p>This method should clear any existing children in the pane and draw
   * all tiles (e.g., squares, circles) in their correct positions based
   * on the provided {@link Board} model.
   * </p>
   *
   * @param boardPane the JavaFX Pane that will contain the board visuals; must not be null
   * @param board the data model representing tile layout and properties; must not be null
   */
  void renderInitialBoard(Pane boardPane, Board board);

  /**
   * Creates a JavaFX Node that visually represents the specified player’s token.
   *
   * <p>The returned node should be styled or sized appropriately (e.g., a colored circle)
   * and carry sufficient user data to allow later identification and positioning.
   * </p>
   *
   * @param player the player for whom to create the token; must not be null
   * @return a JavaFX Node representing the player’s token
   */
  Node createPlayerTokenNode(Player player);

  /**
   * Animates a player token from its current location to the center of a target tile.
   *
   * <p>Implementations should retrieve stored tile center coordinates, perform any
   * walk or jump animations, update the token’s user data to the new tile ID,
   * and invoke the provided callback when the animation sequence completes.
   * </p>
   *
   * @param playerTokenNode the token Node to animate; must not be null
   * @param targetTile the destination {@link Tile} after dice roll or action; must not be null
   * @param boardPane the Pane containing both board and token; must not be null
   * @param onAnimationComplete callback to run when all animations finish; may be null
   */
  void updatePlayerTokenPosition(
      Node playerTokenNode,
      Tile targetTile,
      Pane boardPane,
      Runnable onAnimationComplete
  );

  /**
   * Immediately places a player token node at the center of the specified tile,
   * without any animation.
   *
   * <p>The token’s layoutX/layoutY and user data (current tile ID) should be updated
   * to reflect its new position.
   * </p>
   *
   * @param playerTokenNode the token Node to place; must not be null
   * @param tile the {@link Tile} on which to place the token; must not be null
   * @param boardPane the Pane containing the board; may be used to compute tile centers
   */
  void placePlayerTokenAtTile(Node playerTokenNode, Tile tile, Pane boardPane);

}

