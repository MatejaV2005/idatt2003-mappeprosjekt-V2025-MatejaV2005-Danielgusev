package edu.ntnu.idi.idatt.view.utils;

import edu.ntnu.idi.idatt.exceptions.BoardGameResourceException;
import java.io.InputStream;
import java.net.URL;
import javafx.scene.image.Image;

public class ResourceLoader {


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
