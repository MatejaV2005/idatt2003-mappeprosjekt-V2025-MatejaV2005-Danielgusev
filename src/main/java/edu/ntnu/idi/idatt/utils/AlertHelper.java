package edu.ntnu.idi.idatt.utils;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Utility class for creating and displaying consistent alerts throughout the application.
 */
public final class AlertHelper {

  /**
   * Private constructor to prevent instantiation of this utility class.
   * Throws an {@link IllegalStateException} if an attempt is made to instantiate it
   * via reflection or other means.
   */
  private AlertHelper() {
    // Prevent instantiation of this utility class
    throw new IllegalStateException("Utility class AlertHelper should not be instantiated.");
  }

  /**
   * Shows an error alert with the specified title and message.
   *
   * @param title The alert title
   * @param message The alert message
   */
  public static void showErrorAlert(String title, String message) {
    showAlert(AlertType.ERROR, title, message);
  }

  /**
   * Shows a warning alert with the specified title and message.
   *
   * @param title The alert title
   * @param message The alert message
   */
  public static void showWarningAlert(String title, String message) {
    showAlert(AlertType.WARNING, title, message);
  }

  /**
   * Shows an information alert with the specified title and message.
   *
   * @param title The alert title
   * @param message The alert message
   */
  public static void showInfoAlert(String title, String message) {
    showAlert(AlertType.INFORMATION, title, message);
  }

  /**
   * Shows a confirmation alert and returns the result.
   *
   * @param title The alert title
   * @param message The alert message
   * @return true if user clicked OK, false otherwise
   */
  public static boolean showConfirmationAlert(String title, String message) {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);

    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() && result.get() == ButtonType.OK;
  }

  /**
   * Shows an alert with the specified type, title, and message.
   *
   * @param alertType The type of alert
   * @param title The alert title
   * @param message The alert message
   */
  private static void showAlert(AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}