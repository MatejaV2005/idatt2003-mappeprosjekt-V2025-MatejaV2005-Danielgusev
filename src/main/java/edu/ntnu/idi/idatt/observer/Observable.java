package edu.ntnu.idi.idatt.observer;

public interface Observable<T> {
  void addObserver(T observer);
  void removeObserver(T observer);
}
