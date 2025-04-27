package edu.ntnu.idi.idatt.observer;

public interface Observer<T> {
  void addObserver(T observer);
  void removeObserver(T observer);
}
