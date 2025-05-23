package edu.ntnu.idi.idatt.utils;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import java.io.InputStream;
import java.net.URL;
import javafx.scene.image.Image;

/**
 * A utility class for loading resources such as images and CSS files from the classpath.
 *
 * <p>This class provides static methods to simplify the process of accessing embedded
 * resources within the application. It handles common exceptions that can occur during
 * resource loading and wraps them in a custom {@link BoardGameResourceException}
 * for consistent error handling throughout the application.
 * </p>
 * This class is not meant to be instantiated.
 *
 * @see Image
 * @see URL
 * @see BoardGameResourceException
 */
public final class ResourceLoader {


  /**
   * Private constructor to prevent instantiation of this utility class.
   * Throws an {@link IllegalStateException} if an attempt is made to instantiate it
   * via reflection or other means.
   */
  private ResourceLoader() {
    // Prevent instantiation of this utility class
    throw new IllegalStateException("Utility class ResourceLoader should not be instantiated.");
  }

  /**
   * Loads an image resource from the classpath.
   *
   * @param path Path to the image resource
   * @return The loaded image
   * @throws BoardGameResourceException If the image cannot be loaded
   */
  public static Image loadImage(String path) throws BoardGameResourceException {
    try (InputStream is = ResourceLoader.class.getResourceAsStream(path)) {
      if (is == null) {
        throw new BoardGameResourceException("Resource not found: " + path);
      }

      Image image = new Image(is);
      if (image.isError()) {
        throw new BoardGameResourceException("Failed to load image: " + path);
      }

      return image;
    } catch (Exception e) {
      throw new BoardGameResourceException("Error loading image resource: " + path, e);
    }
  }


  /**
   * Loads a CSS resource from the classpath.
   *
   * @param path Path to the CSS resource
   * @return The URL to the CSS resource
   * @throws BoardGameResourceException If the CSS file cannot be found
   */
  public static String loadCssResource(String path) throws BoardGameResourceException {
    URL resource = ResourceLoader.class.getResource(path);
    if (resource == null) {
      throw new BoardGameResourceException("CSS resource not found: " + path);
    }
    return resource.toExternalForm();
  }
}
