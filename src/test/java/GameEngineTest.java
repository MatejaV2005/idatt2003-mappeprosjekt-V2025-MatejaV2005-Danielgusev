import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.GameEngine;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link GameEngine}.
 * Verifies the core functionalities of the GameEngine class.
 */
class GameEngineTest {

  private Board mockBoard;
  private GameStrategy mockStrategy;
  private GameEngine gameEngine;
  private Player mockPlayer;
  private Tile mockStartTile;


  @BeforeEach
  void setUp() {
    mockBoard = mock(Board.class);
    mockStrategy = mock(GameStrategy.class);
    gameEngine = new GameEngine(mockBoard, mockStrategy);

    mockPlayer = mock(Player.class);
    mockStartTile = mock(Tile.class);

    when(mockBoard.getTileById(1)).thenReturn(mockStartTile);
  }


  @Test
  void constructor_WithValidArguments_InitializesFields() {
    assertNotNull(gameEngine.getBoard(), "Board should be initialized.");
    assertEquals(mockBoard, gameEngine.getBoard());
  }


  @Test
  void constructor_NullBoard_ThrowsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> new GameEngine(null, mockStrategy));
    assertEquals("Board for GameEngine constructor cannot be null.", exception.getMessage());
  }


  @Test
  void constructor_NullStrategy_ThrowsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> new GameEngine(mockBoard, null));
    assertEquals("GameStrategy for GameEngine constructor cannot be null.", exception.getMessage());
  }


  @Test
  void getStartingTile_ReturnsTileWithId1() {
    assertEquals(mockStartTile, gameEngine.getStartingTile());
    verify(mockBoard, times(1)).getTileById(1);
  }


  @Test
  void getStartingTile_PropagatesExceptionFromBoard() {
    when(mockBoard.getTileById(1)).thenThrow(new IllegalArgumentException("Tile 1 not found"));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> gameEngine.getStartingTile());
    assertEquals("Tile 1 not found", exception.getMessage());
  }


  @Test
  void getBoard_ReturnsCorrectBoard() {
    assertEquals(mockBoard, gameEngine.getBoard());
  }


  @Test
  void playTurn_DelegatesToStrategy() {
    gameEngine.playTurn(mockPlayer);
    verify(mockStrategy, times(1)).executePlayerTurn(mockPlayer);
  }


  @Test
  void playTurn_NullPlayer_ThrowsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> gameEngine.playTurn(null));
    assertEquals("Current player for playTurn cannot be null.", exception.getMessage());
  }


  @Test
  void isWinner_ReturnsTrue_WhenStrategyReturnsTrue() {
    when(mockStrategy.checkWinCondition(mockPlayer)).thenReturn(true);

    assertTrue(gameEngine.isWinner(mockPlayer));
    verify(mockStrategy, times(1)).checkWinCondition(mockPlayer);
  }


  @Test
  void isWinner_ReturnsFalse_WhenStrategyReturnsFalse() {
    when(mockStrategy.checkWinCondition(mockPlayer)).thenReturn(false);

    assertFalse(gameEngine.isWinner(mockPlayer));
    verify(mockStrategy, times(1)).checkWinCondition(mockPlayer);
  }


  @Test
  void isWinner_NullPlayer_ThrowsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> gameEngine.isWinner(null));
    assertEquals("Player for isWinner check cannot be null.", exception.getMessage());
  }


  @Test
  void initializeGame_DelegatesToStrategy() {
    List<Player> playerList = new ArrayList<>();
    playerList.add(mockPlayer);

    gameEngine.initializeGame(playerList);
    verify(mockStrategy, times(1)).initializeGame(eq(mockBoard), eq(playerList));
  }

  @Test
  void initializeGame_NullPlayerList_ThrowsException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> gameEngine.initializeGame(null));
    assertEquals("List of players for initialization cannot be null or empty.", exception.getMessage());
  }


  @Test
  void initializeGame_EmptyPlayerList_ThrowsException() {
    List<Player> emptyList = new ArrayList<>();

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> gameEngine.initializeGame(emptyList));
    assertEquals("List of players for initialization cannot be null or empty.", exception.getMessage());
  }
}