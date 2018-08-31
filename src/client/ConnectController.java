/**
 * ConnectController.java
 */
package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the connect scene, to control the various GUI elements.
 * @author [ec00727]
 */
public class ConnectController implements Initializable {
    /** The TextField with the chat server's IP */
    @FXML private TextField serverIP;
    /** The TextField with the chat server's Port */
    @FXML private Spinner<Integer> serverPort;

    /**
     * Method acting essentially as the constructor for the class, it's called when the GUI is loaded.
     * Initialises all fields and GUI elements properly.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuring and initialising the Spinner
        SpinnerValueFactory<Integer> portValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,65535,7890);
        this.serverPort.setValueFactory(portValueFactory);
    }

    /**
     * Method to attempt to connect to a chat server and change the GUI scene.
     */
    public void connectToServer(ActionEvent event) throws IOException {
        // Loading the new layout
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("chat_scene.fxml"));
        Parent newSceneParent = loader.load();
        Scene newScene = new Scene(newSceneParent);

        // Getting the target window / stage from the event
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Attempting to connect to the server via the controller of the new layout
        MainController controller = loader.getController();
        controller.connectToServer(this.serverIP.getText(), this.serverPort.getValue());

        // Loading the new scene onto the window and showing it
        window.setScene(newScene);
        window.show();
    }

}
