package boardgames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idi.idatt.model.core.Board;
import edu.ntnu.idi.idatt.model.core.BoardGame;
import edu.ntnu.idi.idatt.model.core.Dice;
import edu.ntnu.idi.idatt.model.core.GameEngine;
import edu.ntnu.idi.idatt.model.games.GameType;
import edu.ntnu.idi.idatt.model.core.Tile;
import edu.ntnu.idi.idatt.model.core.Player;
import edu.ntnu.idi.idatt.model.strategy.GameStrategy;
import edu.ntnu.idi.idatt.view.screens.BoardGameObserver;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BoardGameTest {

  private Board mockBoard;
  private Dice mockDice;
  private GameStrategy mockStrategy;
  private GameEngine mockGameEngine;
  private TestableBoardGame game;
  private Player mockPlayer1;
  private Player mockPlayer2;
  private BoardGameObserver mockObserver;

  private static GameEngine staticMockGameEngineInstance;

  private static class TestableBoardGame extends BoardGame {
    public boolean handlePlayerTurnCalled = false;
    public boolean checkWinConditionCalled = false;
    public boolean initializeGameStateCalled = false;
    public Player lastPlayerInHandleTurn = null;
    public Player lastPlayerInCheckWin = null;

    public TestableBoardGame(Board board, Dice dice, GameStrategy strategy) {
      super(board, dice, strategy);
    }

    @Override
    protected void handlePlayerTurn(Player player) {
      handlePlayerTurnCalled = true;
      lastPlayerInHandleTurn = player;
      LOGGER_MODEL.info("TestableBoardGame: handlePlayerTurn for " + player.getName());
    }

    @Override
    protected boolean checkWinCondition(Player player) {
      checkWinConditionCalled = true;
      lastPlayerInCheckWin = player;
      LOGGER_MODEL.info("TestableBoardGame: checkWinCondition for " + player.getName());
      return "Winner".equals(player.getName());
    }

    @Override
    public GameType getGameType() {
      return GameType.SNAKES_AND_LADDERS;
    }

    @Override
    protected void initializeGameState() {
      initializeGameStateCalled = true;
      LOGGER_MODEL.info("TestableBoardGame: initializeGameState called.");
    }

    @Override
    protected GameEngine createGameEngine(Board board, GameStrategy strategy) {
      return BoardGameTest.staticMockGameEngineInstance != null ?
          BoardGameTest.staticMockGameEngineInstance :
          super.createGameEngine(board, strategy);
    }

    public void callProtectedAdvanceToNextPlayer() {
      super.advanceToNextPlayer();
    }

    public void callProtectedNotifyObserversOfStateChange() {
      super.notifyObserversOfStateChange();
    }

    public int getObserverCountForTest() {
      return this.observers.size();
    }
  }


  @BeforeEach
  void setUp() {
    mockBoard = mock(Board.class);
    mockDice = mock(Dice.class);
    mockStrategy = mock(GameStrategy.class);
    mockGameEngine = mock(GameEngine.class);
    staticMockGameEngineInstance = mockGameEngine;

    Tile mockStartTile = mock(Tile.class);
    when(mockStartTile.getTileId()).thenReturn(1);
    when(mockGameEngine.getStartingTile()).thenReturn(mockStartTile);
    when(mockGameEngine.getBoard()).thenReturn(mockBoard);
    when(mockBoard.getTileById(1)).thenReturn(mockStartTile);

    game = new TestableBoardGame(mockBoard, mockDice, mockStrategy);

    mockPlayer1 = mock(Player.class);
    when(mockPlayer1.getName()).thenReturn("Player1");
    mockPlayer2 = mock(Player.class);
    when(mockPlayer2.getName()).thenReturn("Player2");

    mockObserver = mock(BoardGameObserver.class);
    game.addObserver(mockObserver);
  }

  @Nested
  @DisplayName("Constructor and Initialization")
  class ConstructorTests {
    @Test
    void constructorThrowsExceptionForNullBoard() {
      assertThrows(IllegalArgumentException.class, () -> new TestableBoardGame(null, mockDice, mockStrategy));
    }

    @Test
    void constructorThrowsExceptionForNullDice() {
      assertThrows(IllegalArgumentException.class, () -> new TestableBoardGame(mockBoard, null, mockStrategy));
    }

    @Test
    void constructorThrowsExceptionForNullStrategy() {
      assertThrows(IllegalArgumentException.class, () -> new TestableBoardGame(mockBoard, mockDice, null));
    }

    @Test
    void constructorInitializesFieldsCorrectly() {
      assertNotNull(game.getBoard());
      assertNotNull(game.getLastDiceRoll());
      assertNotNull(game.getPlayers());
      assertTrue(game.getPlayers().isEmpty());
      assertFalse(game.hasGameStarted());
      assertFalse(game.isGameOver());
      assertEquals(0, game.getRoundCount());
      assertNull(game.getCurrentPlayer());
      assertFalse(game.getWinner().isPresent());
    }
  }

  @Nested
  @DisplayName("Player Management")
  class PlayerManagementTests {
    @Test
    void addPlayerSuccessfullyAddsPlayerBeforeGameStarts() {
      game.addPlayer(mockPlayer1);
      assertEquals(1, game.getPlayers().size());
      assertTrue(game.getPlayers().contains(mockPlayer1));
      assertEquals(mockPlayer1, game.getCurrentPlayer());
      verify(mockObserver, times(1)).onPlayerAdded(mockPlayer1);
      verify(mockPlayer1, times(1)).setOnCurrentTile(mockGameEngine.getStartingTile());
    }

    @Test
    void addPlayerThrowsExceptionIfGameHasStarted() {
      game.addPlayer(mockPlayer1);
      game.startGame();
      assertThrows(IllegalStateException.class, () -> game.addPlayer(mockPlayer2));
    }

    @Test
    void addPlayerThrowsExceptionForNullPlayer() {
      assertThrows(IllegalArgumentException.class, () -> game.addPlayer(null));
    }

    @Test
    void addPlayerDoesNotAddDuplicatePlayer() {
      game.addPlayer(mockPlayer1);
      game.addPlayer(mockPlayer1);
      assertEquals(1, game.getPlayers().size());
      verify(mockObserver, times(1)).onPlayerAdded(mockPlayer1);
    }
  }

  @Nested
  @DisplayName("Game Start Logic")
  class GameStartTests {
    @Test
    void startGameSuccessfullyStartsWhenPlayersExist() {
      game.addPlayer(mockPlayer1);
      game.startGame();
      assertTrue(game.hasGameStarted());
      assertFalse(game.isGameOver());
      assertEquals(1, game.getRoundCount());
      assertEquals(mockPlayer1, game.getCurrentPlayer());
      assertTrue(game.initializeGameStateCalled);
      verify(mockObserver, times(1)).onGameStateUpdated(game);
    }

    @Test
    void startGameThrowsExceptionIfNoPlayers() {
      assertThrows(IllegalArgumentException.class, () -> game.startGame());
    }

    @Test
    void startGameThrowsExceptionIfAlreadyStarted() {
      game.addPlayer(mockPlayer1);
      game.startGame();
      assertThrows(IllegalStateException.class, () -> game.startGame());
    }
  }

  @Nested
  @DisplayName("Turn Logic (playNextTurn)")
  class TurnLogicTests {
    @BeforeEach
    void setupTurnTests() {
      game.addPlayer(mockPlayer1);
      game.addPlayer(mockPlayer2);
      game.startGame();
      game.handlePlayerTurnCalled = false; // Nullstill flagg fra startGame
      game.checkWinConditionCalled = false; // Nullstill flagg fra startGame
    }

    @Test
    void playNextTurnAdvancesPlayerAndNotifiesObserversIfNoWinner() {
      when(mockPlayer1.shouldSkipTurn()).thenReturn(false);
      // Sørg for at checkWinCondition returnerer false for den første spilleren
      when(mockPlayer1.getName()).thenReturn("Player1"); // Ikke "Winner"

      game.playNextTurn(); // Player1's turn

      assertTrue(game.handlePlayerTurnCalled);
      assertEquals(mockPlayer1, game.lastPlayerInHandleTurn);
      assertTrue(game.checkWinConditionCalled);
      assertEquals(mockPlayer1, game.lastPlayerInCheckWin);
      assertEquals(mockPlayer2, game.getCurrentPlayer());
      // startGame kaller notify en gang, playNextTurn kaller den en gang til.
      verify(mockObserver, times(2)).onGameStateUpdated(game);
    }

    @Test
    void playNextTurnHandlesPlayerSkippingTurn() {
      when(mockPlayer1.shouldSkipTurn()).thenReturn(true);
      game.playNextTurn(); // Player1 skips

      assertFalse(game.handlePlayerTurnCalled, "handlePlayerTurn should not be called if player skips.");
      assertEquals(mockPlayer2, game.getCurrentPlayer(), "Should advance to Player2 if Player1 skips.");
      verify(mockPlayer1, times(1)).shouldSkipTurn();
      verify(mockObserver, times(2)).onGameStateUpdated(game); // startGame + skip turn
    }

    @Test
    void playNextTurnDeclaresWinnerIfWinConditionMet() {
      when(mockPlayer1.shouldSkipTurn()).thenReturn(false);
      when(mockPlayer1.getName()).thenReturn("Winner"); // Gjør at checkWinCondition i TestableBoardGame returnerer true

      game.playNextTurn(); // Player1 spiller og vinner

      assertTrue(game.handlePlayerTurnCalled);
      assertTrue(game.checkWinConditionCalled);
      assertTrue(game.isGameOver());
      assertEquals(mockPlayer1, game.getWinner().orElse(null));
      verify(mockObserver, times(1)).onGameWon(mockPlayer1);
      // onGameStateUpdated kalles ikke for å bytte spiller hvis noen vinner
      verify(mockObserver, times(1)).onGameStateUpdated(game); // Kun fra startGame
    }

    @Test
    void playNextTurnThrowsExceptionIfNotStarted() {
      BoardGame newGame = new TestableBoardGame(mockBoard, mockDice, mockStrategy);
      newGame.addPlayer(mockPlayer1);
      // Game not started
      assertThrows(IllegalStateException.class, () -> newGame.playNextTurn());
    }

    @Test
    void playNextTurnThrowsExceptionIfGameOver() {
      when(mockPlayer1.getName()).thenReturn("Winner");
      game.playNextTurn(); // Spillet er nå over
      assertTrue(game.isGameOver());
      assertThrows(IllegalStateException.class, () -> game.playNextTurn());
    }

    @Test
    void advanceToNextPlayerCorrectlyCyclesPlayersAndIncrementsRound() {
      assertEquals(mockPlayer1, game.getCurrentPlayer());
      assertEquals(1, game.getRoundCount());

      game.callProtectedAdvanceToNextPlayer(); // Bruk wrapper
      assertEquals(mockPlayer2, game.getCurrentPlayer());
      assertEquals(1, game.getRoundCount());

      game.callProtectedAdvanceToNextPlayer(); // Bruk wrapper
      assertEquals(mockPlayer1, game.getCurrentPlayer());
      assertEquals(2, game.getRoundCount());
    }

    @Test
    void advanceToNextPlayerHandlesSinglePlayerCorrectly() {
      TestableBoardGame singlePlayerGame = new TestableBoardGame(mockBoard, mockDice, mockStrategy);
      singlePlayerGame.addPlayer(mockPlayer1);
      singlePlayerGame.startGame();

      assertEquals(mockPlayer1, singlePlayerGame.getCurrentPlayer());
      assertEquals(1, singlePlayerGame.getRoundCount());

      singlePlayerGame.callProtectedAdvanceToNextPlayer(); // Bruk wrapper
      assertEquals(mockPlayer1, singlePlayerGame.getCurrentPlayer());
      assertEquals(2, singlePlayerGame.getRoundCount(), "Round should increment for single player.");
    }
  }

  @Nested
  @DisplayName("Observer Pattern")
  class ObserverTests {
    @Test
    void addObserverAddsSuccessfullyAndIsNotified() {
      BoardGameObserver newObserver = mock(BoardGameObserver.class);
      game.addObserver(newObserver);

      game.callProtectedNotifyObserversOfStateChange(); // Bruk wrapper

      verify(newObserver, times(1)).onGameStateUpdated(game);
      verify(mockObserver, times(1)).onGameStateUpdated(game); // Den som ble lagt til i setUp
    }

    @Test
    void removeObserverStopsNotifications() {
      BoardGameObserver observerToRemove = mock(BoardGameObserver.class);
      game.addObserver(observerToRemove); // Legg til
      game.removeObserver(observerToRemove); // Fjern

      game.callProtectedNotifyObserversOfStateChange(); // Prøv å varsle

      verify(observerToRemove, never()).onGameStateUpdated(game);
    }

    @Test
    void addObserverThrowsExceptionForNull() {
      assertThrows(IllegalArgumentException.class, () -> game.addObserver(null));
    }

    @Test
    void addObserverDoesNotAddDuplicatesAndNotifiesOncePerEvent() {
      // mockObserver er allerede lagt til i global setUp()
      int initialObserverCount = game.getObserverCountForTest();

      game.addObserver(mockObserver); // Prøv å legge til samme instans igjen
      assertEquals(initialObserverCount, game.getObserverCountForTest(), "Duplicate observer should not increase count.");

      game.callProtectedNotifyObserversOfStateChange();

      // mockObserver skal kun bli varslet én gang for dette ENE kallet til notify,
      // selv om addObserver ble kalt to ganger med samme instans.
      verify(mockObserver, times(1)).onGameStateUpdated(game);
    }
  }

  @Nested
  @DisplayName("Game Reset Logic")
  class GameResetTests {

    private List<Player> originalPlayers;

    @BeforeEach
    void setupResetTests() {
      game.addPlayer(mockPlayer1);
      game.addPlayer(mockPlayer2);
      game.startGame();
      // Simuler litt spill for å endre tilstand
      when(mockPlayer1.shouldSkipTurn()).thenReturn(false);
      when(mockPlayer2.shouldSkipTurn()).thenReturn(false);
      when(mockPlayer1.getName()).thenReturn("Player1"); // Ikke vinner
      when(mockPlayer2.getName()).thenReturn("Player2"); // Ikke vinner

      game.playNextTurn(); // P1 sin tur
      game.playNextTurn(); // P2 sin tur, runde 1 ferdig, P1 er neste i runde 2

      originalPlayers = new ArrayList<>(game.getPlayers());
    }
  }
}
