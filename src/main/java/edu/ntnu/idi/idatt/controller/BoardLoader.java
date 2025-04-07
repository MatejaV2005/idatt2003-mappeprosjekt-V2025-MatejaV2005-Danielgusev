package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.filehandler.BoardJsonFileHandler;
import edu.ntnu.idi.idatt.model.Board;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class BoardLoader {
  private final BoardJsonFileHandler fileHandler;
  private static final Logger logger = Logger.getLogger(BoardLoader.class.getName());


  public BoardLoader() {
    this.fileHandler = new BoardJsonFileHandler();
  }

  public Board loadBoardFromFile(String filePath) {
    return fileHandler.loadFromFile(filePath);
  }

  public void saveBoardToFile(Board board, String filePath) {
    fileHandler.saveToFile(board, filePath);
  }

  public void saveBoardWithGeneratedName(Board board, String boardType) {
    String folderPath = "Files/Boards";

    File directory = new File(folderPath);

    if(!directory.exists()) {
      directory.mkdirs();
    }

    String fileName = boardType + "_Board_" + LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json";

    String fullPath = folderPath + File.separator + fileName;

    new BoardJsonFileHandler().saveToFile(board, fullPath);

    logger.info("Board saved successfully as: " + fileName + " in folder: " + folderPath);
  }
}
