package edu.ntnu.idi.idatt.observer;

/**
 * Defines an {@code Observable} subject that can be observed by
 * objects implementing a matching observer interface.
 *
 * @param <T> the observer type
 */
public interface Observable<T> {

  /**
   * Subscribes the given observer to receive notifications.
   *
   * @param observer the observer to add; must not be null
   */
  void addObserver(T observer);

  /**
   * Unsubscribes the given observer from receiving notifications.
   * If the observer is not registered, this has no effect.
   *
   * @param observer the observer to remove; must not be null
   */
  void removeObserver(T observer);
}
