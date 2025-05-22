package strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.Player;

import edu.ntnu.idi.idatt.model.strategy.SnakesAndLaddersStrategy;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link SnakesAndLaddersStrategy}.
 * Verifies the game-specific logic implemented in this strategy.
 */
class SnakesAndLaddersStrategyTest {

  private Dice mockDice;
  private SnakesAndLaddersStrategy strategy;
  private Player mockPlayer;
  private Board mockBoard;
  private Tile mockCurrentTile;
  private Tile mockNextTile;
  private Tile mockLastTile;

  /**
   * Sets up mock objects and the strategy instance before each test.
   */
  @BeforeEach
  void setUp() {
    mockDice = mock(Dice.class);
    strategy = new SnakesAndLaddersStrategy(mockDice);

    mockPlayer = mock(Player.class);
    mockBoard = mock(Board.class);
    mockCurrentTile = mock(Tile.class, "CurrentTile");
    mockNextTile = mock(Tile.class, "NextTile");
    mockLastTile = mock(Tile.class, "LastTile");

    // Standard oppsett for spiller
    when(mockPlayer.getCurrentTile()).thenReturn(mockCurrentTile);
    when(mockPlayer.getName()).thenReturn("TestPlayer"); // For logging/exceptions
  }

  /**
   * Tests for the constructor of {@link SnakesAndLaddersStrategy}.
   */
  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    /**
     * Verifies that the constructor successfully creates an instance
     * when a valid Dice object is provided.
     */
    @Test
    @DisplayName("Should Construct Successfully With Valid Dice")
    void constructor_ValidDice_InitializesSuccessfully() {
      assertNotNull(strategy);
    }

    /**
     * Verifies that the constructor throws an {@link IllegalArgumentException}
     * if the provided Dice object is null.
     */
    @Test
    @DisplayName("Should Throw IllegalArgumentException For Null Dice")
    void constructor_NullDice_ThrowsIllegalArgumentException() {
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> new SnakesAndLaddersStrategy(null));
      assertEquals("Dice for SnakesAndLaddersStrategy cannot be null.", exception.getMessage());
    }
  }

  /**
   * Tests for the {@link SnakesAndLaddersStrategy#checkWinCondition(Player)} method.
   */
  @Nested
  @DisplayName("Win Condition Check")
  class WinConditionTests {

    /**
     * Verifies that {@code checkWinCondition} returns true if the player is on a tile
     * that has no next tile (indicating it's the last tile).
     */
    @Test
    @DisplayName("Should Return True If Player Is On Last Tile")
    void checkWinCondition_PlayerOnLastTile_ReturnsTrue() {
      when(mockCurrentTile.getNextTile()).thenReturn(null); // currentTile er siste felt
      assertTrue(strategy.checkWinCondition(mockPlayer));
    }

    /**
     * Verifies that {@code checkWinCondition} returns false if the player is on a tile
     * that has a next tile (not the last tile).
     */
    @Test
    @DisplayName("Should Return False If Player Is Not On Last Tile")
    void checkWinCondition_PlayerNotOnLastTile_ReturnsFalse() {
      when(mockCurrentTile.getNextTile()).thenReturn(mockNextTile); // currentTile er ikke siste
      assertFalse(strategy.checkWinCondition(mockPlayer));
    }

    /**
     * Verifies that {@code checkWinCondition} throws an {@link IllegalArgumentException}
     * if the player parameter is null.
     */
    @Test
    @DisplayName("Should Throw IllegalArgumentException For Null Player")
    void checkWinCondition_NullPlayer_ThrowsIllegalArgumentException() {
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> strategy.checkWinCondition(null));
      assertEquals("Player for checkWinCondition cannot be null.", exception.getMessage());
    }

    /**
     * Verifies that {@code checkWinCondition} throws an {@link IllegalStateException}
     * if the player's current tile is null.
     */
    @Test
    @DisplayName("Should Throw IllegalStateException If Player's CurrentTile Is Null")
    void checkWinCondition_PlayerCurrentTileNull_ThrowsIllegalStateException() {
      when(mockPlayer.getCurrentTile()).thenReturn(null);
      IllegalStateException exception =
          assertThrows(IllegalStateException.class, () -> strategy.checkWinCondition(mockPlayer));
      assertEquals("Player 'TestPlayer' must be on a tile to check win",
          exception.getMessage());
    }
  }

  /**
   * Tests for the {@link SnakesAndLaddersStrategy#executePlayerTurn(Player)} method.
   */
  @Nested
  @DisplayName("Player Turn Execution")
  class ExecutePlayerTurnTests {

    /**
     * Verifies that {@code executePlayerTurn} correctly calls dice roll,
     * player's basic move, and updates player's tile.
     */
    @Test
    @DisplayName("Should Roll Dice, Call BasicMove, And Update Player Tile")
    void executePlayerTurn_RollsDiceMovesPlayer_UpdatesTile() {
      int stepsRolled = 3;
      when(mockDice.roll()).thenReturn(stepsRolled);
      when(mockPlayer.basicMove(stepsRolled)).thenReturn(mockNextTile);

      strategy.executePlayerTurn(mockPlayer);

      verify(mockDice, times(1)).roll();
      verify(mockPlayer, times(1)).basicMove(stepsRolled);
      verify(mockCurrentTile, times(1)).leavePlayer(mockPlayer);
      verify(mockPlayer, times(1)).setOnCurrentTile(mockNextTile);
    }

    /**
     * Verifies that {@code executePlayerTurn} throws an {@link IllegalArgumentException}
     * if the player parameter is null.
     */
    @Test
    @DisplayName("Should Throw IllegalArgumentException For Null Player")
    void executePlayerTurn_NullPlayer_ThrowsIllegalArgumentException() {
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> strategy.executePlayerTurn(null));
      assertEquals("Player for executePlayerTurn cannot be null.", exception.getMessage());
    }

    /**
     * Verifies that {@code executePlayerTurn} throws an {@link IllegalStateException}
     * if the player's current tile is null before the turn.
     */
    @Test
    @DisplayName("Should Throw IllegalStateException If Player's CurrentTile Is Null")
    void executePlayerTurn_PlayerCurrentTileNull_ThrowsIllegalStateException() {
      when(mockPlayer.getCurrentTile()).thenReturn(null);
      IllegalStateException exception =
          assertThrows(IllegalStateException.class, () -> strategy.executePlayerTurn(mockPlayer));
      assertEquals("Player 'TestPlayer' must be on a tile to take a turn",
          exception.getMessage());
    }
  }

  /**
   * Tests for the {@link SnakesAndLaddersStrategy#initializeGame(Board, List)} method.
   */
  @Nested
  @DisplayName("Game Initialization")
  class InitializeGameTests {
    private Tile mockStartTile;

    /**
     * Sets up the start tile for game initialization tests.
     */
    @BeforeEach
    void setUpInitialize() {
      mockStartTile = mock(Tile.class, "StartTileForInit");
      when(mockBoard.getTileById(1)).thenReturn(mockStartTile);
    }

    /**
     * Verifies that {@code initializeGame} sets each player's current tile
     * to the starting tile (tile ID 1) obtained from the board.
     */
    @Test
    @DisplayName("Should Set All Players To Starting Tile")
    void initializeGame_SetsPlayersToStartTile() {
      Player mockPlayer2 = mock(Player.class);
      List<Player> players = List.of(mockPlayer, mockPlayer2);

      strategy.initializeGame(mockBoard, players);

      verify(mockPlayer, times(1)).setOnCurrentTile(mockStartTile);
      verify(mockPlayer2, times(1)).setOnCurrentTile(mockStartTile);
    }

    /**
     * Verifies that {@code initializeGame} throws an {@link IllegalArgumentException}
     * if the provided players list is empty, as per ExceptionHandling.requireNotEmpty.
     */
    @Test
    @DisplayName("Should Throw IllegalArgumentException For Empty Player List")
    void initializeGame_EmptyPlayerList_ThrowsIllegalArgumentException() {
      List<Player> emptyPlayers = new ArrayList<>();

      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class,
          () -> strategy.initializeGame(mockBoard, emptyPlayers),
          "initializeGame should throw IllegalArgumentException for an empty player list."
      );

      assertEquals("Players list for initializeGame cannot be null or empty.",
          exception.getMessage(),
          "Exception message should match the one from ExceptionHandling.requireNotEmpty.");

      verify(mockPlayer, never()).setOnCurrentTile(any(Tile.class));
    }

    /**
     * Verifies that {@code initializeGame} throws an {@link IllegalArgumentException}
     * if the board parameter is null.
     */
    @Test
    @DisplayName("Should Throw IllegalArgumentException For Null Board")
    void initializeGame_NullBoard_ThrowsIllegalArgumentException() {
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> strategy.initializeGame(null, List.of(mockPlayer)));
      assertEquals("Board for initializeGame cannot be null.", exception.getMessage());
    }

    /**
     * Verifies that {@code initializeGame} throws an {@link IllegalArgumentException}
     * if the players list parameter is null.
     */
    @Test
    @DisplayName("Should Throw IllegalArgumentException For Null PlayerList")
    void initializeGame_NullPlayerList_ThrowsIllegalArgumentException() {
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> strategy.initializeGame(mockBoard, null));
      assertEquals("Players list for initializeGame cannot be null or empty.", exception.getMessage());
    }

    /**
     * Verifies that {@code initializeGame} throws an {@link IllegalStateException}
     * if the starting tile (ID 1) cannot be found on the board.
     */
    @Test
    @DisplayName("Should Throw IllegalStateException If StartTile Not Found")
    void initializeGame_StartTileNotFound_ThrowsIllegalStateException() {
      when(mockBoard.getTileById(1)).thenReturn(null); // Simuler at startfelt ikke finnes
      List<Player> players = List.of(mockPlayer);

      IllegalStateException exception = assertThrows(IllegalStateException.class,
          () -> strategy.initializeGame(mockBoard, players));
      assertEquals("Starting tile (ID 1) not found on board",
          exception.getMessage());
    }
  }
}
