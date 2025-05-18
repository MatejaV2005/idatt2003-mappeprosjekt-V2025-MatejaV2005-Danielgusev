package edu.ntnu.idi.idatt.exceptions;

public class InvalidBoardFormatException extends FileLoadException {

  public InvalidBoardFormatException(String message) {
    super(message);
  }

  public InvalidBoardFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
