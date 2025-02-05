package edu.ntnu.idi.idatt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class fx extends Application{
  //AI-GENERERT KODE, BRUKES BARE FOR Å TESTE OM JAVAFX FUNKER
  @Override
  public void start(Stage primaryStage) {
    // Opprett en etikett og en knapp
    Label label = new Label("Velkommen til JavaFX!");
    Button button = new Button("Klikk meg");

    // Legg til en handling på knappen
    button.setOnAction(event -> label.setText("Knappen ble klikket!"));

    // Organiser kontroller i en VBox (vertikal layout)
    VBox vbox = new VBox(10, label, button); // 10px avstand mellom elementene
    vbox.setStyle("-fx-padding: 20; -fx-alignment: center;"); // Padding og sentrering

    // Lag en scene med VBox
    Scene scene = new Scene(vbox, 300, 200);

    // Konfigurer hovedvinduet (stage)
    primaryStage.setTitle("JavaFX Test");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args); // Start JavaFX-applikasjonen
  }
}
