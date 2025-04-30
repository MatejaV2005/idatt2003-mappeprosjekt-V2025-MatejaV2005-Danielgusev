package edu.ntnu.idi.idatt.model.management;

import edu.ntnu.idi.idatt.exceptions.BoardManagementException;
import edu.ntnu.idi.idatt.exceptions.FileLoadException;
import edu.ntnu.idi.idatt.exceptions.FileSaveException;
import edu.ntnu.idi.idatt.filehandler.BoardJsonFileHandler;
import edu.ntnu.idi.idatt.model.core.Board;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager class for Board entities, handling loading and saving of board configurations.
 * Follows the Singleton pattern to ensure a single point of access to board operations.
 */
public class BoardManager {
  private static final String BOARDS_DIRECTORY = "Files/Boards";
  private static final Logger LOGGER = Logger.getLogger(BoardManager.class.getName());

  private static BoardManager instance = null;
  private final BoardJsonFileHandler fileHandler;

  /**
   * Private constructor to enforce Singleton pattern.
   * Initializes the file handler.
   */
  private BoardManager() {
    this.fileHandler = new BoardJsonFileHandler();
  }

  /**
   * Gets the singleton instance of BoardManager.
   *
   * @return The BoardManager instance
   */
  public static synchronized BoardManager getInstance() {
    if (instance == null) {
      instance = new BoardManager();
    }
    return instance;
  }

  /**
   * Loads a board from a file at the specified path.
   *
   * @param filePath The path of the file to load
   * @return The loaded Board object
   * @throws BoardManagementException if loading fails
   */
  public Board loadBoardFromFile(String filePath) throws BoardManagementException {
    if (filePath == null || filePath.isBlank()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    try {
      LOGGER.info("Loading board from file: " + filePath);
      return fileHandler.loadFromFile(filePath);
    } catch (FileLoadException e) {
      LOGGER.log(Level.SEVERE, "Failed to load board from file: " + filePath, e);
      throw new BoardManagementException("Failed to load board: " + e.getMessage(), e);
    }
  }

  /**
   * Saves a board to a file at the specified path.
   *
   * @param board The board to save
   * @param filePath The path where the file should be saved
   * @throws BoardManagementException if saving fails
   */
  public void saveBoardToFile(Board board, String filePath) throws BoardManagementException {
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null");
    }
    if (filePath == null || filePath.isBlank()) {
      throw new IllegalArgumentException("File path cannot be null or empty");
    }

    try {
      LOGGER.info("Saving board to file: " + filePath);
      fileHandler.saveToFile(board, filePath);
    } catch (FileSaveException e) {
      LOGGER.log(Level.SEVERE, "Failed to save board to file: " + filePath, e);
      throw new BoardManagementException("Failed to save board: " + e.getMessage(), e);
    }
  }

  /**
   * Saves a board with an automatically generated filename based on type and timestamp.
   *
   * @param board The board to save
   * @param boardType The type of board (used in filename)
   * @throws BoardManagementException if saving fails or directory creation fails
   */
  public void saveBoardWithGeneratedName(Board board, String boardType) throws BoardManagementException {
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null");
    }
    if (boardType == null || boardType.isBlank()) {
      throw new IllegalArgumentException("Board type cannot be null or empty");
    }

    try {
      // Create boards directory if it doesn't exist
      File directory = new File(BOARDS_DIRECTORY);
      if (!directory.exists()) {
        if (!directory.mkdirs()) {
          throw new BoardManagementException("Failed to create boards directory: " + BOARDS_DIRECTORY);
        }
      }

      // Generate filename with timestamp
      String fileName = boardType + "_Board_" + LocalDateTime.now()
          .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json";

      String fullPath = BOARDS_DIRECTORY + File.separator + fileName;

      // Save the board
      fileHandler.saveToFile(board, fullPath);

      LOGGER.info("Board saved successfully as: " + fileName + " in folder: " + BOARDS_DIRECTORY);
    } catch (FileSaveException e) {
      LOGGER.log(Level.SEVERE, "Failed to save board with generated name", e);
      throw new BoardManagementException("Failed to save board with generated name: " + e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unexpected error saving board with generated name", e);
      throw new BoardManagementException("Unexpected error saving board: " + e.getMessage(), e);
    }
  }

  /**
   * Gets the default boards directory path.
   *
   * @return The path to the boards directory
   */
  public String getBoardsDirectory() {
    return BOARDS_DIRECTORY;
  }
}