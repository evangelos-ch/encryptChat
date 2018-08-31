/**
 * Main.java
 */
package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main class for the chat client. Sets up the user interface layouts and launches the application.
 * @author [ec00727]
 */
public class Main extends Application {

    /**
     * Method that initialises the window.
     * @param primaryStage
     *      the GUI Stage to display onto.
     * @throws IOException
     *      when the .fxml layout file to load is not found.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("connect_scene.fxml")); // load the layout
        primaryStage.setTitle("COM1028_EncryptChat"); // set the title of the window
        primaryStage.setScene(new Scene(root, 650, 500)); // set a window size
        primaryStage.show(); // show the stage
    }

    /**
     * Method to launch the application.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
