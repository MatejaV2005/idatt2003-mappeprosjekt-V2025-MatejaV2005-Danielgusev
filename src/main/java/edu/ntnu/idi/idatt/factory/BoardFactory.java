package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.Board;

  public class BoardFactory {

    // Normal difficulty
    public Board createDefaultBoard() {
      return new Board(90);
    }

    // Easy difficulty
    public Board createSmallBoard() {
        return new Board(50);
    }

    // Hard difficulty
    public Board createBigBoard() {
      return new Board(120);
    }
}
