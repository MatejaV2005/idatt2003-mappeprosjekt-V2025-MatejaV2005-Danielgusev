package boardgames; // Assuming this is your test package structure

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.games.GameType;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.games.AstroRallyGame;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
import edu.ntnu.idi.idatt.model.strategy.AstroRallyStrategy;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AstroRallyGameTest {

  @Mock
  private Board mockBoard;
  @Mock
  private Dice mockDice;
  @Mock
  private AstroRallyStrategy mockAstroRallyStrategy;
  @Mock
  private GameStrategy mockWrongStrategy;
  @Mock
  private Player mockPlayer;
  @Mock
  private Tile mockStartTile, mockOldTile, mockNewTile, mockActionTile, mockDestinationTile;
  @Mock
  private TileAction mockTileAction;
  @Mock
  private BoardGameObserver mockObserver;

  private TestableAstroRallyGame gameSpy;

  static class TestableAstroRallyGame extends AstroRallyGame {
    public TestableAstroRallyGame(Board board, Dice dice, GameStrategy strategy) {
      super(board, dice, strategy);
    }

    @Override
    public void initializeGameState() {
      super.initializeGameState();
    }

    @Override
    public void handlePlayerTurn(Player player) {
      super.handlePlayerTurn(player);
    }

    @Override
    public void handleSpecialTileAction(Player player, Tile triggerActionTile) {
      super.handleSpecialTileAction(player, triggerActionTile);
    }

    @Override
    public boolean checkWinCondition(Player player) {
      return super.checkWinCondition(player);
    }
  }


  @BeforeEach
  void setUp() {
    lenient().when(mockBoard.getTileById(1)).thenReturn(mockStartTile);
    lenient().when(mockStartTile.getTileId()).thenReturn(1);
    lenient().when(mockOldTile.getTileId()).thenReturn(1);
    lenient().when(mockNewTile.getTileId()).thenReturn(2);
    lenient().when(mockActionTile.getTileId()).thenReturn(3);
    lenient().when(mockDestinationTile.getTileId()).thenReturn(4);


    gameSpy = spy(new TestableAstroRallyGame(mockBoard, mockDice, mockAstroRallyStrategy));
    gameSpy.addObserver(mockObserver);
    gameSpy.addPlayer(mockPlayer);

    lenient().when(mockPlayer.getCurrentTile()).thenReturn(mockStartTile);

    Mockito.clearInvocations(mockObserver, mockPlayer, mockAstroRallyStrategy);
  }

  @Test
  void constructor_withCorrectStrategy_initializesSuccessfully() {
    AstroRallyGame game = new AstroRallyGame(mockBoard, mockDice, mockAstroRallyStrategy);
    assertNotNull(game);
    assertEquals(GameType.ASTRO_RALLY, game.getGameType());
  }

  @Test
  void constructor_withWrongStrategyType_throwsIllegalArgumentException() {
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> new AstroRallyGame(mockBoard, mockDice, mockWrongStrategy));
    assertEquals("AstroRallyGame requires an AstroRallyStrategy instance.", exception.getMessage());
  }

  @Test
  void handlePlayerTurn_playerMoves_noActionTile_noLapCompletion() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockOldTile);
    doAnswer(invocation -> {
      Player p = invocation.getArgument(0);
      when(p.getCurrentTile()).thenReturn(mockNewTile);
      return null;
    }).when(mockAstroRallyStrategy).executePlayerTurn(mockPlayer);

    when(mockNewTile.isActionTile()).thenReturn(false);
    when(mockBoard.getBoardSize()).thenReturn(10);

    gameSpy.handlePlayerTurn(mockPlayer);

    verify(mockAstroRallyStrategy).executePlayerTurn(mockPlayer);
    verify(mockOldTile).leavePlayer(mockPlayer);
    verify(mockNewTile).landPlayer(mockPlayer);
    verify(mockObserver).onPlayerMoved(mockPlayer, mockOldTile, mockNewTile);
    verify(mockPlayer, never()).incrementLapsCompleted();
    verify(gameSpy, never()).handleSpecialTileAction(any(Player.class), any(Tile.class));
  }

  @Test
  void handlePlayerTurn_playerMoves_landsOnActionTile() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockOldTile);
    doAnswer(invocation -> {
      Player p = invocation.getArgument(0);
      when(p.getCurrentTile()).thenReturn(mockNewTile);
      return null;
    }).when(mockAstroRallyStrategy).executePlayerTurn(mockPlayer);

    when(mockNewTile.isActionTile()).thenReturn(true);
    when(mockNewTile.getLandAction()).thenReturn(mockTileAction);
    when(mockTileAction.getActionType()).thenReturn(ActionType.NO_OP);

    gameSpy.handlePlayerTurn(mockPlayer);

    verify(mockAstroRallyStrategy).executePlayerTurn(mockPlayer);
    verify(mockOldTile).leavePlayer(mockPlayer);
    verify(mockNewTile).landPlayer(mockPlayer);
    verify(mockObserver).onPlayerMoved(mockPlayer, mockOldTile, mockNewTile);
    verify(gameSpy).handleSpecialTileAction(mockPlayer, mockNewTile);
  }

  @Test
  void handlePlayerTurn_lapCompletionLogic_crossedMidpoint() {
    when(mockBoard.getBoardSize()).thenReturn(10);
    when(mockOldTile.getTileId()).thenReturn(8);
    when(mockNewTile.getTileId()).thenReturn(2);

    when(mockPlayer.getCurrentTile()).thenReturn(mockOldTile);
    doAnswer(invocation -> {
      Player p = invocation.getArgument(0);
      when(p.getCurrentTile()).thenReturn(mockNewTile);
      return null;
    }).when(mockAstroRallyStrategy).executePlayerTurn(mockPlayer);
    when(mockNewTile.isActionTile()).thenReturn(false);

    gameSpy.handlePlayerTurn(mockPlayer);

    verify(mockPlayer).incrementLapsCompleted();
    verify(mockObserver).onPlayerMoved(mockPlayer, mockOldTile, mockNewTile);
  }

  @Test
  void handlePlayerTurn_lapCompletionLogic_landsOnTile1FromHigherId() {
    when(mockBoard.getBoardSize()).thenReturn(10);
    when(mockOldTile.getTileId()).thenReturn(10);
    when(mockNewTile.getTileId()).thenReturn(1);

    when(mockPlayer.getCurrentTile()).thenReturn(mockOldTile);
    doAnswer(invocation -> {
      Player p = invocation.getArgument(0);
      when(p.getCurrentTile()).thenReturn(mockNewTile);
      return null;
    }).when(mockAstroRallyStrategy).executePlayerTurn(mockPlayer);
    when(mockNewTile.isActionTile()).thenReturn(false);

    gameSpy.handlePlayerTurn(mockPlayer);
    verify(mockPlayer).incrementLapsCompleted();
  }


  @Test
  void handlePlayerTurn_playerDoesNotPhysicallyMoveAfterEngineTurn() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockOldTile);
    doAnswer(invocation -> null).when(mockAstroRallyStrategy).executePlayerTurn(mockPlayer);

    when(mockOldTile.isActionTile()).thenReturn(false);

    gameSpy.handlePlayerTurn(mockPlayer);

    verify(mockAstroRallyStrategy).executePlayerTurn(mockPlayer);
    verify(mockOldTile, never()).leavePlayer(mockPlayer);
    verify(mockObserver).onPlayerMoved(mockPlayer, mockOldTile, mockOldTile);
    verify(mockPlayer, never()).incrementLapsCompleted();
    verify(gameSpy, never()).handleSpecialTileAction(any(Player.class), any(Tile.class));
  }


  @Test
  void handleSpecialTileAction_noLandAction_notifiesEffectOnSameTile() {
    when(mockActionTile.getLandAction()).thenReturn(null);

    gameSpy.handleSpecialTileAction(mockPlayer, mockActionTile);

    verify(mockObserver).onActionTileEffect(mockPlayer, mockActionTile, mockActionTile);
    verify(mockPlayer, never()).setOnCurrentTile(any(Tile.class));
  }

  @Test
  void handleSpecialTileAction_noOpAction_notifiesEffectOnSameTile() {
    when(mockActionTile.getLandAction()).thenReturn(mockTileAction);
    when(mockTileAction.getActionType()).thenReturn(ActionType.NO_OP);

    gameSpy.handleSpecialTileAction(mockPlayer, mockActionTile);

    verify(mockObserver).onActionTileEffect(mockPlayer, mockActionTile, mockActionTile);
    verify(mockPlayer, never()).setOnCurrentTile(any(Tile.class));
  }

  @Test
  void handleSpecialTileAction_actionBoostPadTile() {
    when(mockActionTile.getLandAction()).thenReturn(mockTileAction);
    when(mockTileAction.getActionType()).thenReturn(ActionType.BOOST_PAD);
    when(mockPlayer.getCurrentTile()).thenReturn(mockActionTile);

    doAnswer(invocation -> {
      Player p = invocation.getArgument(0);
      when(p.getCurrentTile()).thenReturn(mockDestinationTile);
      return null;
    }).when(mockTileAction).perform(mockPlayer);


    gameSpy.handleSpecialTileAction(mockPlayer, mockActionTile);

    verify(mockTileAction).perform(mockPlayer);
    verify(mockActionTile).leavePlayer(mockPlayer);
    verify(mockDestinationTile).landPlayer(mockPlayer);
    verify(mockObserver).onPlayerMoved(mockPlayer, mockActionTile, mockDestinationTile);
    verify(mockObserver).onActionTileEffect(mockPlayer, mockActionTile, mockDestinationTile);
  }

  @Test
  void handleSpecialTileAction_actionAsteroidFieldTile() {
    when(mockActionTile.getLandAction()).thenReturn(mockTileAction);
    when(mockTileAction.getActionType()).thenReturn(ActionType.ASTEROID_FIELD);
    when(mockPlayer.getCurrentTile()).thenReturn(mockActionTile);

    doAnswer(invocation -> null).when(mockTileAction).perform(mockPlayer);

    gameSpy.handleSpecialTileAction(mockPlayer, mockActionTile);

    verify(mockTileAction).perform(mockPlayer);
    verify(mockActionTile, never()).leavePlayer(mockPlayer);
    verify(mockObserver, never()).onPlayerMoved(any(Player.class), any(Tile.class), any(Tile.class));
    verify(mockObserver).onActionTileEffect(mockPlayer, mockActionTile, mockActionTile);
  }

  @Test
  void checkWinCondition_playerIsWinner_notifiesAndReturnsTrue() {
    when(mockAstroRallyStrategy.checkWinCondition(mockPlayer)).thenReturn(true);

    boolean isWinner = gameSpy.checkWinCondition(mockPlayer);

    assertTrue(isWinner);
    verify(mockAstroRallyStrategy).checkWinCondition(mockPlayer);
    verify(mockObserver).onGameWon(mockPlayer);
  }

  @Test
  void checkWinCondition_playerIsNotWinner_returnsFalseNoNotification() {
    when(mockAstroRallyStrategy.checkWinCondition(mockPlayer)).thenReturn(false);

    boolean isWinner = gameSpy.checkWinCondition(mockPlayer);

    assertFalse(isWinner);
    verify(mockAstroRallyStrategy).checkWinCondition(mockPlayer);
    verify(mockObserver, never()).onGameWon(any(Player.class));
  }

  @Test
  void initializeGameState_validState_callsGameEngineInitialize() {
    List<Player> expectedPlayers = Collections.singletonList(mockPlayer);

    gameSpy.initializeGameState();

    verify(mockAstroRallyStrategy).initializeGame(mockBoard, expectedPlayers);
  }

  @Test
  void initializeGameState_noPlayers_doesNotCallGameEngineInitialize() {
    TestableAstroRallyGame gameWithoutPlayers = new TestableAstroRallyGame(mockBoard, mockDice, mockAstroRallyStrategy);

    gameWithoutPlayers.initializeGameState();

    verify(mockAstroRallyStrategy, never()).initializeGame(any(Board.class), anyList());
  }

  @Test
  void getGameType_returnsAstroRally() {
    assertEquals(GameType.ASTRO_RALLY, gameSpy.getGameType());
  }
}
