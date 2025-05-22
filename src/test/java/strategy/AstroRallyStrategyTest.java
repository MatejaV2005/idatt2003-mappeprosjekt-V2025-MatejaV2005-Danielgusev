package strategy;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.strategy.AstroRallyStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AstroRallyStrategyTest {

  @Mock
  private Board mockBoard;
  @Mock
  private Dice mockDice;
  @Mock
  private Player mockPlayer;
  @Mock
  private Tile mockStartTile;
  @Mock
  private Tile mockTile2;
  @Mock
  private Tile mockTile3;
  @Mock
  private Tile mockLastTile;

  private AstroRallyStrategy astroRallyStrategy;

  @BeforeEach
  void setUp() {
    lenient().when(mockBoard.getTileById(1)).thenReturn(mockStartTile);
    lenient().when(mockStartTile.getTileId()).thenReturn(1);

    lenient().when(mockBoard.getTileById(2)).thenReturn(mockTile2);
    lenient().when(mockTile2.getTileId()).thenReturn(2);

    int BOARD_SIZE = 3;
    lenient().when(mockBoard.getTileById(BOARD_SIZE)).thenReturn(mockLastTile);
    lenient().when(mockLastTile.getTileId()).thenReturn(BOARD_SIZE);

    lenient().when(mockBoard.getBoardSize()).thenReturn(BOARD_SIZE);

    lenient().when(mockStartTile.getNextTile()).thenReturn(mockTile2);
    lenient().when(mockTile2.getNextTile()).thenReturn(mockLastTile);
    lenient().when(mockLastTile.getNextTile()).thenReturn(null);

    lenient().when(mockDice.getNumberOfDice()).thenReturn(2);

    astroRallyStrategy = new AstroRallyStrategy(mockBoard, mockDice);
  }

  @Test
  void constructor_validInputs_initializesSuccessfully() {
    assertNotNull(astroRallyStrategy);
    verify(mockDice, atLeastOnce()).getNumberOfDice();
  }

  @Test
  void constructor_diceHasZeroDice_initializesDiceWithTwo() {
    when(mockDice.getNumberOfDice()).thenReturn(0);
    AstroRallyStrategy strategyWithZeroDice = new AstroRallyStrategy(mockBoard, mockDice);
    assertNotNull(strategyWithZeroDice);
    verify(mockDice).initializeDice(2);
  }

  @Test
  void constructor_nullBoard_throwsIllegalArgumentException() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> new AstroRallyStrategy(null, mockDice));
    assertTrue(exception.getMessage().contains("board"));
  }

  @Test
  void constructor_nullDice_throwsIllegalArgumentException() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> new AstroRallyStrategy(mockBoard, null));
    assertTrue(exception.getMessage().contains("dice"));
  }

  @Test
  void executePlayerTurn_nullPlayer_throwsIllegalArgumentException() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> astroRallyStrategy.executePlayerTurn(null));
    assertTrue(exception.getMessage().contains("player"));
  }

  @Test
  void executePlayerTurn_playerNotOnTile_throwsIllegalStateException() {
    when(mockPlayer.getCurrentTile()).thenReturn(null);

    Exception exception = assertThrows(IllegalStateException.class,
        () -> astroRallyStrategy.executePlayerTurn(mockPlayer));
    assertTrue(exception.getMessage().toLowerCase().contains("not on a tile"));
  }

  @Test
  void executePlayerTurn_diceRollZero_playerDoesNotMove() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockStartTile);
    when(mockDice.roll()).thenReturn(0);

    astroRallyStrategy.executePlayerTurn(mockPlayer);

    verify(mockPlayer, never()).setOnCurrentTile(any(Tile.class));
    verify(mockPlayer, never()).incrementLapsCompleted();
  }

  @Test
  void executePlayerTurn_validMoveOneStep_movesToNextTile() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockStartTile);
    when(mockDice.roll()).thenReturn(1);
    when(mockPlayer.getLapsCompleted()).thenReturn(0);

    astroRallyStrategy.executePlayerTurn(mockPlayer);

    verify(mockPlayer).setOnCurrentTile(mockTile2);
    verify(mockPlayer, never()).incrementLapsCompleted();
  }

  @Test
  void executePlayerTurn_moveFromTile2ToStart_completesLap() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockTile2);
    when(mockDice.roll()).thenReturn(2);
    when(mockPlayer.getLapsCompleted()).thenReturn(0);

    astroRallyStrategy.executePlayerTurn(mockPlayer);

    verify(mockPlayer).setOnCurrentTile(mockStartTile);
    verify(mockPlayer).incrementLapsCompleted();
  }

  @Test
  void executePlayerTurn_moveOnFinalLap_landsOnTile1AndStops() {
    when(mockPlayer.getLapsCompleted()).thenReturn(AstroRallyStrategy.TOTAL_LAPS_TO_WIN - 1);
    when(mockPlayer.getCurrentTile()).thenReturn(mockTile2);
    when(mockDice.roll()).thenReturn(2);

    astroRallyStrategy.executePlayerTurn(mockPlayer);

    verify(mockPlayer).setOnCurrentTile(mockStartTile);
    verify(mockPlayer).incrementLapsCompleted();
  }

  @Test
  void executePlayerTurn_moveOnFinalLapFromLastTile_stopsAtTile1() {
    when(mockPlayer.getLapsCompleted()).thenReturn(AstroRallyStrategy.TOTAL_LAPS_TO_WIN - 1);
    when(mockPlayer.getCurrentTile()).thenReturn(mockLastTile);
    when(mockDice.roll()).thenReturn(1);

    astroRallyStrategy.executePlayerTurn(mockPlayer);

    verify(mockPlayer).setOnCurrentTile(mockStartTile);
    verify(mockPlayer).incrementLapsCompleted();
  }

  @Test
  void executePlayerTurn_startTileIdZero_throwsIllegalArgumentException() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockStartTile);
    when(mockStartTile.getTileId()).thenReturn(0);
    when(mockDice.roll()).thenReturn(1);

    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> astroRallyStrategy.executePlayerTurn(mockPlayer));
    assertTrue(exception.getMessage().toLowerCase().contains("strictly positive"));
  }

  @Test
  void executePlayerTurn_tileNotFoundDuringMovement_throwsIllegalStateException() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockStartTile);
    when(mockDice.roll()).thenReturn(1);
    when(mockStartTile.getNextTile()).thenReturn(mockTile2);
    when(mockTile2.getTileId()).thenReturn(2);
    when(mockBoard.getTileById(2)).thenReturn(null);

    Exception exception = assertThrows(IllegalStateException.class,
        () -> astroRallyStrategy.executePlayerTurn(mockPlayer));
    assertTrue(exception.getMessage().contains("not found"));
  }

  @Test
  void checkWinCondition_hasEnoughLapsAndOnFinishTile_returnsTrue() {
    when(mockPlayer.getLapsCompleted()).thenReturn(AstroRallyStrategy.TOTAL_LAPS_TO_WIN);
    when(mockPlayer.getCurrentTile()).thenReturn(mockStartTile);

    assertTrue(astroRallyStrategy.checkWinCondition(mockPlayer));
  }

  @Test
  void checkWinCondition_notEnoughLaps_returnsFalse() {
    when(mockPlayer.getLapsCompleted()).thenReturn(AstroRallyStrategy.TOTAL_LAPS_TO_WIN - 1);
    when(mockPlayer.getCurrentTile()).thenReturn(mockStartTile);

    assertFalse(astroRallyStrategy.checkWinCondition(mockPlayer));
  }

  @Test
  void checkWinCondition_enoughLapsNotOnFinishTile_returnsFalse() {
    when(mockPlayer.getLapsCompleted()).thenReturn(AstroRallyStrategy.TOTAL_LAPS_TO_WIN);
    when(mockPlayer.getCurrentTile()).thenReturn(mockTile2);

    assertFalse(astroRallyStrategy.checkWinCondition(mockPlayer));
  }

  @Test
  void checkWinCondition_playerNotOnTile_returnsFalse() {
    when(mockPlayer.getCurrentTile()).thenReturn(null);

    assertFalse(astroRallyStrategy.checkWinCondition(mockPlayer));
  }

  @Test
  void checkWinCondition_nullPlayer_throwsIllegalArgumentException() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> astroRallyStrategy.checkWinCondition(null));
    assertTrue(exception.getMessage().contains("player"));
  }

  @Test
  void initializeGame_validInputs_setsPlayersToStartTileAndResetsState() {
    List<Player> players = Collections.singletonList(mockPlayer);

    astroRallyStrategy.initializeGame(mockBoard, players);

    verify(mockPlayer).setOnCurrentTile(mockStartTile);
    verify(mockPlayer).resetLapsCompleted();
    verify(mockPlayer).setSkipTurn(false);
  }

  @Test
  void initializeGame_nullBoard_throwsIllegalArgumentException() {
    List<Player> players = Collections.singletonList(mockPlayer);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> astroRallyStrategy.initializeGame(null, players));
    assertTrue(exception.getMessage().contains("board"));
  }

  @Test
  void initializeGame_nullPlayersList_throwsIllegalArgumentException() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> astroRallyStrategy.initializeGame(mockBoard, null));
    assertTrue(exception.getMessage().toLowerCase().contains("players"));
  }

  @Test
  void initializeGame_emptyPlayersList_throwsIllegalArgumentException() {
    List<Player> emptyPlayers = new ArrayList<>();
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> astroRallyStrategy.initializeGame(mockBoard, emptyPlayers));
    assertTrue(exception.getMessage().toLowerCase().contains("empty") ||
        exception.getMessage().toLowerCase().contains("players"));
  }

  @Test
  void initializeGame_listContainsNullPlayer_throwsIllegalArgumentException() {
    List<Player> playersWithNull = new ArrayList<>();
    playersWithNull.add(null);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> astroRallyStrategy.initializeGame(mockBoard, playersWithNull));
    assertTrue(exception.getMessage().contains("player"));
  }

  @Test
  void initializeGame_startingTileMissing_throwsIllegalStateException() {
    when(mockBoard.getTileById(1)).thenReturn(null);
    List<Player> players = Collections.singletonList(mockPlayer);

    Exception exception = assertThrows(IllegalStateException.class,
        () -> astroRallyStrategy.initializeGame(mockBoard, players));
    assertTrue(exception.getMessage().contains("start") ||
        exception.getMessage().contains("tile"));
  }

  @Test
  void executePlayerTurn_negativeSteps_handledCorrectly() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockStartTile);
    when(mockDice.roll()).thenReturn(-1);

    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> astroRallyStrategy.executePlayerTurn(mockPlayer));
    assertTrue(exception.getMessage().toLowerCase().contains("negative") ||
        exception.getMessage().toLowerCase().contains("non-negative"));
  }

  @Test
  void executePlayerTurn_playerStaysOnSameTile_noMovementCall() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockStartTile);
    when(mockDice.roll()).thenReturn(0);

    astroRallyStrategy.executePlayerTurn(mockPlayer);

    verify(mockPlayer, never()).setOnCurrentTile(any());
  }

  @Test
  void executePlayerTurn_multipleStepsAroundBoard_handlesCorrectly() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockStartTile);
    when(mockDice.roll()).thenReturn(4);
    when(mockPlayer.getLapsCompleted()).thenReturn(0);

    astroRallyStrategy.executePlayerTurn(mockPlayer);

    verify(mockPlayer).setOnCurrentTile(mockTile2);
    verify(mockPlayer).incrementLapsCompleted();
  }
}