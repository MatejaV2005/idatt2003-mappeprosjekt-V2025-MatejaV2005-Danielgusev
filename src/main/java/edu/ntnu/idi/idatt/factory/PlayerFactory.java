package edu.ntnu.idi.idatt.factory;

import edu.ntnu.idi.idatt.model.Tile;
import edu.ntnu.idi.idatt.model.playertype.HumanPlayer;
import edu.ntnu.idi.idatt.model.playertype.Player;

public class PlayerFactory {

  public static Player createPlayer(String name, String pieceType, Tile currentTile) {
    Player player = new HumanPlayer(name, pieceType);
    player.setOnCurrentTile(currentTile);

    return player;
  }

  // For creating and saving players only?
  public static Player createPlayer(String name, String pieceType) {
    return new HumanPlayer(name, pieceType);
  }



}
