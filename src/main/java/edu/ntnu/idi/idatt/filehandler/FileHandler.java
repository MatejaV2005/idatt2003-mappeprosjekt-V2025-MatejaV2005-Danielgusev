package edu.ntnu.idi.idatt.filehandler;

import edu.ntnu.idi.idatt.exceptions.FileLoadException;
import edu.ntnu.idi.idatt.exceptions.FileSaveException;

public interface FileHandler<T> {

  /**
   * Saves an object to a file at the specified path.
   *
   * @param object the object to save
   * @param filePath the path where the file should be saved
   * @throws FileSaveException if an error occurs during the save operation
   * @throws IllegalArgumentException if the object or file path is invalid
   */
  void saveToFile(T object, String filePath) throws FileSaveException;

  /**
   * Loads an object from a file at the specified path.
   *
   * @param filePath the path of the file to load
   * @return the loaded object
   * @throws FileLoadException if an error occurs during the load operation
   * @throws IllegalArgumentException if the file path is invalid
   */
  T loadFromFile(String filePath) throws FileLoadException;
}
