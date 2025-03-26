package edu.ntnu.idi.idatt.filehandler;

public interface FileHandler<T> {
  void saveToFile(T object, String filePath);

  T loadFromFile(String filePath);
}
