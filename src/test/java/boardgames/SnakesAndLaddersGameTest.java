package boardgames;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.games.GameType;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.games.SnakesAndLaddersGame;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;
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
class SnakesAndLaddersGameTest {

  @Mock
  Board mockBoard;
  @Mock
  Dice mockDice;
  @Mock
  GameStrategy mockGameStrategy;
  @Mock
  Player mockPlayer;
  @Mock
  Tile mockOldTile, mockNewTile, mockActionTile, mockDestinationTile, mockStartTile;
  @Mock
  TileAction mockTileAction;
  @Mock
  BoardGameObserver mockObserver;

  TestableSnakesAndLaddersGame gameSpy;

  static class TestableSnakesAndLaddersGame extends SnakesAndLaddersGame {
    public TestableSnakesAndLaddersGame(Board board, Dice dice, GameStrategy strategy) {
      super(board, dice, strategy);
    }

    @Override
    public void handlePlayerTurn(Player player) {
      super.handlePlayerTurn(player);
    }

    @Override
    public void handleSpecialTileAction(Player player, Tile actionTile) {
      super.handleSpecialTileAction(player, actionTile);
    }

    @Override
    public boolean checkWinCondition(Player player) {
      return super.checkWinCondition(player);
    }

    @Override
    public void initializeGameState() {
      super.initializeGameState();
    }
  }

  @BeforeEach
  void setUp() {
    lenient().when(mockBoard.getTileById(1)).thenReturn(mockStartTile);

    gameSpy = spy(new TestableSnakesAndLaddersGame(mockBoard, mockDice, mockGameStrategy));
    gameSpy.addObserver(mockObserver);
    gameSpy.addPlayer(mockPlayer);

    lenient().when(mockPlayer.getCurrentTile()).thenReturn(mockStartTile);
    lenient().doNothing().when(mockObserver).onPlayerAdded(any(Player.class));
    Mockito.clearInvocations(mockObserver, mockPlayer, gameSpy);
  }

  @Test
  void testHandlePlayerTurn_PlayerMoves_NoActionTile() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockOldTile);

    doAnswer(invocation -> {
      Player p = invocation.getArgument(0);
      when(p.getCurrentTile()).thenReturn(mockNewTile);
      return null;
    }).when(mockGameStrategy).executePlayerTurn(mockPlayer);

    when(mockNewTile.isActionTile()).thenReturn(false);
    Mockito.clearInvocations(mockPlayer, mockObserver, gameSpy);

    gameSpy.handlePlayerTurn(mockPlayer);

    verify(mockGameStrategy).executePlayerTurn(mockPlayer);
    verify(mockObserver).onPlayerMoved(mockPlayer, mockOldTile, mockNewTile);
    verify(mockNewTile).isActionTile();
    verify(gameSpy, never()).handleSpecialTileAction(eq(mockPlayer), any(Tile.class));
    verify(mockPlayer, never()).setOnCurrentTile(any(Tile.class));
  }

  @Test
  void testHandlePlayerTurn_PlayerLandsOnActionTile() {
    when(mockPlayer.getCurrentTile()).thenReturn(mockOldTile);

    doAnswer(invocation -> {
      Player p = invocation.getArgument(0);
      when(p.getCurrentTile()).thenReturn(mockNewTile);
      return null;
    }).when(mockGameStrategy).executePlayerTurn(mockPlayer);

    when(mockNewTile.isActionTile()).thenReturn(true);
    when(mockNewTile.getLandAction()).thenReturn(null);
    Mockito.clearInvocations(mockPlayer, mockObserver, gameSpy);

    gameSpy.handlePlayerTurn(mockPlayer);

    verify(mockGameStrategy).executePlayerTurn(mockPlayer);
    verify(mockObserver).onPlayerMoved(mockPlayer, mockOldTile, mockNewTile);
    verify(mockNewTile).isActionTile();
    verify(gameSpy).handleSpecialTileAction(mockPlayer, mockNewTile);
  }

  @Test
  void testHandleSpecialTileAction_NoActualActionOnTile() {
    when(mockActionTile.getLandAction()).thenReturn(null);
    Mockito.clearInvocations(mockPlayer, mockObserver);

    gameSpy.handleSpecialTileAction(mockPlayer, mockActionTile);

    verify(mockActionTile).getLandAction();
    verify(mockObserver, never()).onActionTileEffect(any(Player.class), any(Tile.class), any(Tile.class));
    verify(mockPlayer, never()).setOnCurrentTile(any(Tile.class));
  }

  @Test
  void testHandleSpecialTileAction_MovesToDestinationTile_SnakeOrLadder() {
    int destinationId = 5;
    when(mockActionTile.getLandAction()).thenReturn(mockTileAction);
    when(mockTileAction.getDestinationTileId()).thenReturn(destinationId);
    when(mockBoard.getTileById(destinationId)).thenReturn(mockDestinationTile);
    Mockito.clearInvocations(mockPlayer, mockObserver);

    gameSpy.handleSpecialTileAction(mockPlayer, mockActionTile);

    verify(mockObserver).onActionTileEffect(mockPlayer, mockActionTile, mockDestinationTile);
    verify(mockActionTile).leavePlayer(mockPlayer);
    verify(mockPlayer).setOnCurrentTile(mockDestinationTile);
    verify(mockDestinationTile).landPlayer(mockPlayer);
  }

  @Test
  void testHandleSpecialTileAction_MovesToDestinationTile_DestinationIsNull() {
    int destinationId = 5;
    when(mockActionTile.getLandAction()).thenReturn(mockTileAction);
    when(mockTileAction.getDestinationTileId()).thenReturn(destinationId);
    when(mockBoard.getTileById(destinationId)).thenReturn(null);
    Mockito.clearInvocations(mockPlayer, mockObserver);

    gameSpy.handleSpecialTileAction(mockPlayer, mockActionTile);

    verify(mockObserver, never()).onActionTileEffect(any(Player.class), any(Tile.class), any(Tile.class));
    verify(mockActionTile, never()).leavePlayer(mockPlayer);
    verify(mockPlayer, never()).setOnCurrentTile(any(Tile.class));
  }

  @Test
  void testHandleSpecialTileAction_MovesToDestinationTile_DestinationIsSameTile() {
    int destinationId = 5;
    when(mockActionTile.getLandAction()).thenReturn(mockTileAction);
    when(mockTileAction.getDestinationTileId()).thenReturn(destinationId);
    when(mockBoard.getTileById(destinationId)).thenReturn(mockActionTile);
    Mockito.clearInvocations(mockPlayer, mockObserver);

    gameSpy.handleSpecialTileAction(mockPlayer, mockActionTile);

    verify(mockObserver, never()).onActionTileEffect(any(Player.class), any(Tile.class), any(Tile.class));
    verify(mockActionTile, never()).leavePlayer(mockPlayer);
    verify(mockPlayer, never()).setOnCurrentTile(any(Tile.class));
  }


  @Test
  void testHandleSpecialTileAction_PerformsAction_PlayerMovesAsResult() {
    Tile mockTileAfterAction = mock(Tile.class, "TileAfterAction");
    when(mockActionTile.getLandAction()).thenReturn(mockTileAction);
    when(mockTileAction.getDestinationTileId()).thenReturn(0);

    when(mockPlayer.getCurrentTile())
        .thenReturn(mockActionTile)
        .thenReturn(mockTileAfterAction);
    Mockito.clearInvocations(mockPlayer, mockObserver);

    gameSpy.handleSpecialTileAction(mockPlayer, mockActionTile);

    verify(mockTileAction).perform(mockPlayer);
    verify(mockObserver).onActionTileEffect(mockPlayer, mockActionTile, mockTileAfterAction);
  }

  @Test
  void testHandleSpecialTileAction_PerformsAction_PlayerDoesNotMove() {
    when(mockActionTile.getLandAction()).thenReturn(mockTileAction);
    when(mockTileAction.getDestinationTileId()).thenReturn(0);

    when(mockPlayer.getCurrentTile()).thenReturn(mockActionTile);
    Mockito.clearInvocations(mockPlayer, mockObserver);

    gameSpy.handleSpecialTileAction(mockPlayer, mockActionTile);

    verify(mockTileAction).perform(mockPlayer);
    verify(mockPlayer, times(2)).getCurrentTile();
    verify(mockObserver).onActionTileEffect(mockPlayer, mockActionTile, mockActionTile);
    verify(mockPlayer, never()).setOnCurrentTile(any(Tile.class));
  }

  @Test
  void testCheckWinCondition_PlayerIsWinner() {
    when(mockGameStrategy.checkWinCondition(mockPlayer)).thenReturn(true);
    Mockito.clearInvocations(mockObserver);

    boolean isWinner = gameSpy.checkWinCondition(mockPlayer);

    assertTrue(isWinner);
    verify(mockGameStrategy).checkWinCondition(mockPlayer);
    verify(mockObserver).onGameWon(mockPlayer);
  }

  @Test
  void testCheckWinCondition_PlayerIsNotWinner() {
    when(mockGameStrategy.checkWinCondition(mockPlayer)).thenReturn(false);
    Mockito.clearInvocations(mockObserver);

    boolean isWinner = gameSpy.checkWinCondition(mockPlayer);

    assertFalse(isWinner);
    verify(mockGameStrategy).checkWinCondition(mockPlayer);
    verify(mockObserver, never()).onGameWon(any(Player.class));
  }

  @Test
  void testInitializeGameState() {
    List<Player> expectedPlayersList = Collections.singletonList(mockPlayer);
    Mockito.clearInvocations(mockGameStrategy);

    gameSpy.initializeGameState();

    verify(mockGameStrategy).initializeGame(mockBoard, expectedPlayersList);
  }

  @Test
  void testInitializeGameState_NoPlayers() {
    TestableSnakesAndLaddersGame gameWithoutPlayers = spy(new TestableSnakesAndLaddersGame(mockBoard, mockDice, mockGameStrategy));
    Mockito.clearInvocations(mockGameStrategy);

    gameWithoutPlayers.initializeGameState();

    verify(mockGameStrategy, never()).initializeGame(any(Board.class), anyList());
  }

  @Test
  void testGetGameType() {
    GameType gameType = gameSpy.getGameType();
    assertEquals(GameType.SNAKES_AND_LADDERS, gameType);
  }
}