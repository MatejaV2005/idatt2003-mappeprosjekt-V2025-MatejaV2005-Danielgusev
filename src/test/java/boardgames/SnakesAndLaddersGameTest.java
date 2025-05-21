package boardgames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.GameType;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.ActionType;
import edu.ntnu.idi.idatt.model.core.actions.TileAction;
import edu.ntnu.idi.idatt.model.core.playertype.Player;
import edu.ntnu.idi.idatt.model.games.SnakesAndLaddersGame;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.model.strategy.SnakesAndLaddersStrategy;
import edu.ntnu.idi.idatt.observer.BoardGameObserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

class SnakesAndLaddersGameTest {

  private Board mockBoard;
  private Dice mockDice;
  private GameStrategy mockStrategy;
  private SnakesAndLaddersGame game;
  private Player mockPlayer;
  private BoardGameObserver mockObserver;

  @BeforeEach
  void setUp() {
    mockBoard = mock(Board.class);
    mockDice = mock(Dice.class);
    mockStrategy = mock(SnakesAndLaddersStrategy.class, Answers.RETURNS_SMART_NULLS);

    game = spy(new SnakesAndLaddersGame(mockBoard, mockDice, mockStrategy));

    mockPlayer = mock(Player.class);
    when(mockPlayer.getName()).thenReturn("TestPlayer");

    Tile mockStartTile = mock(Tile.class, "StartTile");
    when(mockStartTile.getTileId()).thenReturn(1);
    when(mockBoard.getTileById(1)).thenReturn(mockStartTile);

    game.addPlayer(mockPlayer);

    mockObserver = mock(BoardGameObserver.class);
    game.addObserver(mockObserver);
  }

  @Test
  @DisplayName("getGameType should return SNAKES_AND_LADDERS")
  void getGameTypeReturnsCorrectType() {
    assertEquals(GameType.SNAKES_AND_LADDERS, game.getGameType());
  }

  @Nested
  @DisplayName("initializeGameState Tests")
  class InitializeGameStateTests {

    @Nested
    @DisplayName("handlePlayerTurn Tests")
    class HandlePlayerTurnTests {

      private Tile mockOldTile;
      private Tile mockNewTileNoAction;
      private Tile mockNewTileWithAction;
      private TileAction mockSpecificActionInstance;

      @BeforeEach
      void setUpHandleTurn() {
        mockOldTile = mock(Tile.class, "OldTile");
        mockNewTileNoAction = mock(Tile.class, "NewTileNoAction");
        mockNewTileWithAction = mock(Tile.class, "NewTileWithAction");
        mockSpecificActionInstance = mock(TileAction.class, "SpecificActionInstance");

        when(mockPlayer.getCurrentTile()).thenReturn(mockOldTile);
        when(mockNewTileNoAction.isActionTile()).thenReturn(false);
        when(mockNewTileWithAction.isActionTile()).thenReturn(true);
        when(mockNewTileWithAction.getLandAction()).thenReturn(mockSpecificActionInstance);

        game.startGame(); // Nødvendig for at playNextTurn/handlePlayerTurn skal kunne kalles
        // og for at currentPlayer skal være satt.
      }

      @Test
      @DisplayName("handlePlayerTurn executes turn, notifies move, and no special action if not action tile")
      void handlePlayerTurnStandardMoveNoAction() {
        doAnswer(invocation -> {
          Player p = invocation.getArgument(0);
          p.setOnCurrentTile(mockNewTileNoAction);
          return null;
        }).when(mockStrategy).executePlayerTurn(mockPlayer);

        game.handlePlayerTurn(mockPlayer);

        verify(mockStrategy, times(1)).executePlayerTurn(mockPlayer);
        verify(game, times(1)).notifyPlayerMoved(mockPlayer, mockOldTile, mockNewTileNoAction);
        verify(game, never()).handleSpecialTileAction(mockPlayer, mockNewTileNoAction);
      }

      @Test
      @DisplayName("handlePlayerTurn calls handleSpecialTileAction if player lands on action tile")
      void handlePlayerTurnCallsSpecialActionOnActionTile() {
        doAnswer(invocation -> {
          Player p = invocation.getArgument(0);
          p.setOnCurrentTile(mockNewTileWithAction);
          return null;
        }).when(mockStrategy).executePlayerTurn(mockPlayer);

        game.handlePlayerTurn(mockPlayer);

        verify(mockStrategy, times(1)).executePlayerTurn(mockPlayer);
        verify(game, times(1)).notifyPlayerMoved(mockPlayer, mockOldTile, mockNewTileWithAction);
        verify(game, times(1)).handleSpecialTileAction(mockPlayer, mockNewTileWithAction);
      }

      @Test
      @DisplayName("handlePlayerTurn throws for null player")
      void handlePlayerTurnThrowsForNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> game.handlePlayerTurn(null));
      }
    }

    @Nested
    @DisplayName("handleSpecialTileAction Tests")
    class HandleSpecialTileActionTests {

      private Tile mockActionTile;
      private Tile mockDestinationTile;
      private Tile mockStartTile;

      @BeforeEach
      void setUpSpecialAction() {
        mockActionTile = mock(Tile.class, "ActionTile");
        mockDestinationTile = mock(Tile.class, "DestinationTile");
        mockStartTile = mock(Tile.class, "StartTile");

        when(mockPlayer.getCurrentTile()).thenReturn(mockActionTile);
        when(mockStartTile.getTileId()).thenReturn(1);
        game.startGame();
      }

      @Test
      @DisplayName("handleSpecialTileAction for LADDER moves player and notifies")
      void handleSpecialTileActionForLadder() {
        TileAction ladderAction = mock(TileAction.class);
        when(ladderAction.getActionType()).thenReturn(ActionType.LADDER);
        when(ladderAction.getDestinationTileId()).thenReturn(10);
        when(mockActionTile.getLandAction()).thenReturn(ladderAction);
        when(mockBoard.getTileById(10)).thenReturn(mockDestinationTile);
        when(mockActionTile.getTileId()).thenReturn(2); // For ulikhetssjekk
        when(mockDestinationTile.getTileId()).thenReturn(10);

        game.handleSpecialTileAction(mockPlayer, mockActionTile);

        verify(mockActionTile, times(1)).leavePlayer(mockPlayer);
        verify(mockPlayer, times(1)).setOnCurrentTile(mockDestinationTile);
        verify(mockDestinationTile, times(1)).landPlayer(mockPlayer);
        verify(game, times(1)).notifyActionTileEffect(mockPlayer, mockActionTile,
            mockDestinationTile);
      }

      @Test
      @DisplayName("handleSpecialTileAction for SKIP_TURN calls perform and notifies same tile")
      void handleSpecialTileActionForSkipTurn() {
        TileAction skipAction = mock(TileAction.class);
        when(skipAction.getActionType()).thenReturn(ActionType.SKIP_TURN);
        when(skipAction.getDestinationTileId()).thenReturn(-1);
        when(mockActionTile.getLandAction()).thenReturn(skipAction);
        // Spilleren blir på samme felt, så getCurrentTile vil fortsatt være mockActionTile etter perform
        when(mockPlayer.getCurrentTile()).thenReturn(mockActionTile);

        game.handleSpecialTileAction(mockPlayer, mockActionTile);

        verify(skipAction, times(1)).perform(mockPlayer);
        verify(game, times(1)).notifyActionTileEffect(mockPlayer, mockActionTile, mockActionTile);
      }

      @Test
      @DisplayName("handleSpecialTileAction for RETURN_TO_START calls perform, moves player to start, and notifies")
      void handleSpecialTileActionForReturnToStart() {
        TileAction returnAction = mock(TileAction.class);
        when(returnAction.getActionType()).thenReturn(ActionType.RETURN_TO_START);
        when(returnAction.getDestinationTileId()).thenReturn(-1);
        when(mockActionTile.getLandAction()).thenReturn(returnAction);

        // Simuler at perform-metoden flytter spilleren til startTile
        doAnswer(invocation -> {
          Player p = invocation.getArgument(0);
          p.setOnCurrentTile(mockStartTile); // Anta at SpecialAction.perform() gjør dette
          return null;
        }).when(returnAction).perform(mockPlayer);

        // Før perform: getCurrentTile() == mockActionTile (satt i @BeforeEach)
        // Etter perform (simulert av doAnswer): getCurrentTile() == mockStartTile
        // For å teste dette korrekt, må vi mocke getCurrentTile til å endre seg ETTER perform.
        // Den enkleste måten er å la mockPlayer.getCurrentTile() returnere mockActionTile før kallet,
        // og så sjekke at notifyActionTileEffect kalles med mockStartTile som destinasjon.
        // Selve endringen av currentTile skjer inne i perform-kallet som simuleres.

        game.handleSpecialTileAction(mockPlayer, mockActionTile);

        verify(returnAction, times(1)).perform(mockPlayer);
        verify(game, times(1)).notifyActionTileEffect(mockPlayer, mockActionTile, mockStartTile);
      }

      @Test
      @DisplayName("handleSpecialTileAction for action with destinationId > 0 but same as actionTile notifies same tile")
      void handleSpecialTileActionForActionToSameTile() {
        TileAction sameTileAction = mock(TileAction.class);
        when(sameTileAction.getActionType()).thenReturn(ActionType.SPECIAL);
        when(sameTileAction.getDestinationTileId()).thenReturn(5); // Anta actionTile har ID 5
        when(mockActionTile.getLandAction()).thenReturn(sameTileAction);
        when(mockActionTile.getTileId()).thenReturn(5);
        when(mockBoard.getTileById(5)).thenReturn(mockActionTile); // destinationTile er actionTile

        game.handleSpecialTileAction(mockPlayer, mockActionTile);

        // Ingen leavePlayer, setOnCurrentTile, eller landPlayer på et annet felt
        verify(mockActionTile, never()).leavePlayer(mockPlayer);
        verify(mockPlayer, never()).setOnCurrentTile(
            any(Tile.class)); // Aldri kalt med et annet felt
        verify(mockActionTile, never()).landPlayer(
            mockPlayer); // Ikke land på nytt på samme felt via denne logikken
        verify(game, times(1)).notifyActionTileEffect(mockPlayer, mockActionTile, mockActionTile);
      }
    }

    @Nested
    @DisplayName("checkWinCondition Tests")
    class CheckWinConditionTests {

      @BeforeEach
      void setupWinCondition() {
        game.startGame();
      }

      @Test
      @DisplayName("checkWinCondition returns true when gameEngine indicates winner")
      void checkWinConditionReturnsTrueForWinner() {
        when(mockStrategy.checkWinCondition(mockPlayer)).thenReturn(true);

        boolean isWinner = game.checkWinCondition(mockPlayer);

        assertTrue(isWinner);
        verify(mockStrategy, times(1)).checkWinCondition(mockPlayer);
        // notifyOnGameWon kalles av BoardGame.playNextTurn, ikke direkte her.
      }

      @Test
      @DisplayName("checkWinCondition returns false when gameEngine indicates not winner")
      void checkWinConditionReturnsFalseForNonWinner() {
        when(mockStrategy.checkWinCondition(mockPlayer)).thenReturn(false);

        boolean isWinner = game.checkWinCondition(mockPlayer);

        assertFalse(isWinner);
        verify(mockStrategy, times(1)).checkWinCondition(mockPlayer);
      }

      @Test
      @DisplayName("checkWinCondition throws for null player")
      void checkWinConditionThrowsForNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> game.checkWinCondition(null));
      }
    }
  }
}
