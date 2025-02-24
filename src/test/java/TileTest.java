import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.actions.LadderAction;
import edu.ntnu.idi.idatt.model.actions.TileAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class TileTest {
  private Tile tile1;
  private Tile tile2;
  private TileAction action;


  @BeforeEach
  void setUp() {
    tile1 = new Tile(1);
    tile2 = new Tile(2);
    action = new LadderAction(3, "You moved to tile 3!");
  }

  @Test
  void testTileInitialization() {
    assertEquals(1  , tile1.getTileId() );
  }

  @Test
  void testSetNextTile() {
    tile1.setNextTile(tile2);
    assertEquals(tile2, tile1.getNextTile());
  }

  @Test
  void testSetLandAction() {
    tile1.setLandAction(action);
    assertEquals(action, tile1.getLandaction());

  }

  @Test
  @Disabled("to be implemented later")
  void testLandingPlayer() {
      //TODO: fill out once player is fully implemented with BoardGame
  }

  @Test
  @Disabled("to be implemented later")
  void testLeavingPlayer() {
    //TODO: fill out once player is fully implemented with BoardGame
  }


  // NEAGTIVE TESTS

  @Test
  void testInvalidIntInConstructor() {


    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      new Tile(-1);
    });

    IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
      new Tile(0);
    });

    assertEquals("tileid must be positive", exception.getMessage()); // Validate the error message
    assertEquals("tileid must be positive", exception2.getMessage()); // Validate the error message
  }

  @Test
  void testSetLandActionThrowsExceptionForNull() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      tile1.setLandAction(null);
    });

    assertEquals("TileAction cannot be null or doesnt exist", exception.getMessage());
  }

  @Test
  void testSetNextTileThrowsExceptionForNull() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      tile1.setNextTile(null);
    });

    assertEquals("Tile cannot be null or doesnt exist", exception.getMessage());
  }

  @Test
  @Disabled("To be implemented later")
  void testLandPlayerThrowsExceptionForNull() {}

  @Test
  @Disabled("To be implemented later")
  void testLeavePlayerThrowsExceptionForNull() {}

}
