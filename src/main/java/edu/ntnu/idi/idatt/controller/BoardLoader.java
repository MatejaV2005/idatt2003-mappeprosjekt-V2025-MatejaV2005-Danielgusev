package edu.ntnu.idi.idatt.controller;

import edu.ntnu.idi.idatt.filehandler.BoardJsonFileHandler;
import edu.ntnu.idi.idatt.model.Board;

public class BoardLoader {
  private final BoardJsonFileHandler fileHandler;

  public BoardLoader() {
    this.fileHandler = new BoardJsonFileHandler();
  }

  public void loadBoardFromFile(String filePath) {
    fileHandler.loadFromFile(filePath);
  }

  public void saveBoardToFile(Board board, String filePath) {
    fileHandler.saveToFile(board, filePath);
  }
}
