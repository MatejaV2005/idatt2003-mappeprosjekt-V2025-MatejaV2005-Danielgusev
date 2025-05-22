package edu.ntnu.idi.idatt.service;

import edu.ntnu.idi.idatt.exceptions.BoardManagementException;
import edu.ntnu.idi.idatt.exceptions.FileLoadException;
import edu.ntnu.idi.idatt.exceptions.FileSaveException;
import edu.ntnu.idi.idatt.exceptions.InvalidBoardFormatException;
import edu.ntnu.idi.idatt.filehandler.BoardJsonFileHandler;
import edu.ntnu.idi.idatt.model.core.Board;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton service for loading and saving {@link Board} instances.
 * Delegates to {@link BoardJsonFileHandler} and wraps all exceptions
 * in contextual, formatted messages.
 */
public class BoardManager {

  private static final String BOARDS_DIRECTORY = "Files/Boards";
  private static final Logger LOGGER = Logger.getLogger(BoardManager.class.getName());

  private static BoardManager instance;
  private final BoardJsonFileHandler fileHandler;

  private BoardManager() {
    this.fileHandler = new BoardJsonFileHandler();
  }

  /**
   * Returns the singleton instance of BoardManager.
   *
   * @return the singleton instance.
   */
  public static synchronized BoardManager getInstance() {
    if (instance == null) {
      instance = new BoardManager();
    }
    return instance;
  }

  /**
   * Loads a {@link Board} from disk.
   *
   * @param filePath JSON file to read; must not be null/blank.
   * @return the deserialized Board
   * @throws IllegalArgumentException    if filePath invalid
   * @throws InvalidBoardFormatException if JSON structure is invalid
   * @throws BoardManagementException    for any I/O failure
   */
  public Board loadBoardFromFile(String filePath)
      throws InvalidBoardFormatException, BoardManagementException {
    if (filePath == null || filePath.isBlank()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }
    LOGGER.log(Level.INFO, () -> String.format("Loading board from: %s", filePath));

    try {
      return fileHandler.loadFromFile(filePath);

    } catch (InvalidBoardFormatException e) {
      String msg = String.format(
          "Invalid board format in '%s': %s",
          filePath,
          e.getMessage()
      );
      LOGGER.log(Level.WARNING, msg, e);
      throw new InvalidBoardFormatException(msg, e);

    } catch (FileLoadException e) {
      String msg = String.format(
          "I/O error loading board from '%s': %s",
          filePath,
          e.getMessage()
      );
      LOGGER.log(Level.SEVERE, msg, e);
      throw new BoardManagementException(msg, e);
    }
  }

  /**
   * Saves a {@link Board} to disk.
   *
   * @param board    the board to save; must not be null
   * @param filePath JSON file to write; must not be null/blank
   * @throws IllegalArgumentException if args invalid
   * @throws BoardManagementException on any I/O failure
   */
  @SuppressWarnings("unused")
  public void saveBoardToFile(Board board, String filePath) throws BoardManagementException {
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null");
    }
    if (filePath == null || filePath.isBlank()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }
    LOGGER.log(Level.INFO, () -> String.format("Saving board to: %s", filePath));

    try {
      fileHandler.saveToFile(board, filePath);
    } catch (FileSaveException e) {
      String msg = String.format(
          "Failed to save board to '%s': %s",
          filePath,
          e.getMessage()
      );
      LOGGER.log(Level.SEVERE, msg, e);
      throw new BoardManagementException(msg, e);
    }
  }

  /**
   * Saves a board with a timestamped filename under the default directory.
   *
   * @param board     the board to save; must not be null
   * @param boardType prefix for the filename; must not be null/blank
   * @throws IllegalArgumentException if args invalid
   * @throws BoardManagementException on any I/O failure or if directory cannot be created
   */
  @SuppressWarnings("unused")
  public void saveBoardWithGeneratedName(Board board, String boardType)
      throws BoardManagementException {
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null");
    }
    if (boardType == null || boardType.isBlank()) {
      throw new IllegalArgumentException("Board type cannot be null or empty");
    }

    File dir = new File(BOARDS_DIRECTORY);
    if (!dir.exists()) {
      boolean dirCreated = dir.mkdirs();
      if (!dirCreated) {
        String msg = String.format("Could not create directory '%s'", BOARDS_DIRECTORY);
        LOGGER.severe(msg);
        throw new BoardManagementException(msg);
      }
    }

    String timestamp = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String fileName = String.format("%s_Board_%s.json", boardType, timestamp);
    String fullPath = BOARDS_DIRECTORY + File.separator + fileName;

    LOGGER.log(Level.INFO, () -> String.format("Saving board as: %s in directory %s",
        fileName, BOARDS_DIRECTORY));

    try {
      fileHandler.saveToFile(board, fullPath);
    } catch (FileSaveException e) {
      String msg = String.format(
          "Failed to save board with generated name '%s': %s",
          fileName,
          e.getMessage()
      );
      LOGGER.log(Level.SEVERE, msg, e);
      throw new BoardManagementException(msg, e);
    }
  }

  /**
   * Returns the directory under which boards are saved.
   *
   * <p>This method is provided for future use; callers may safely ignore it.
   * </p>
   *
   * @return The path to the board's directory.
   */
  @SuppressWarnings("unused")
  public String getBoardsDirectory() {
    return BOARDS_DIRECTORY;
  }
}
