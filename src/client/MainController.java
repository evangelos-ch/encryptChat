/**
 * MainController.java
 */
package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import requests.Request;
import requests.RequestCode;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the chat scene, to control the various GUI elements
 * @author [ec00727]
 */
public class MainController implements Initializable {
    /** Constant that holds the maximum character length of a possible message to be sent */
    private static final int MAX_MESSAGE_LENGTH = 2000;
    /** The TextField where the user can input messages */
    @FXML private TextField messageInputBox;
    /** The ListView where the past chat messages will appear in */
    @FXML private ListView<String> messageHistoryBox;
    /** List that will be displayed in real time on the ListView above */
    private ObservableList<String> messageHistory;
    /** The Label that will display the chat's connection status */
    @FXML private Label statusLabel;
    /** The Button that when pressed initialises a key exchange */
    @FXML private Button keyExchangeButton;
    /** The ProgressIndicator that will show the current progress of the key exchange */
    @FXML private ProgressIndicator keyExchangeProgress;
    /** The chat client's connection to the chat server via the ServerConnect class */
    private ServerConnect connection = null;
    /** The IP address for the chat server */
    private String server = null;
    /** The port for the chat server */
    private int port = 0;

    /**
     * Method that essentially acts as a constructor for the class, it's called when the GUI is laded.
     * Initialises all fields and GUI elements properly.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialising handlers
        this.messageHistory = FXCollections.observableArrayList();
        this.messageHistoryBox.setItems(this.messageHistory);
        // Setting up the ListView to automatically scroll to the bottom
        this.messageHistoryBox.getItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                messageHistoryBox.scrollTo(c.getList().size() - 1);
            }
        });
        // Initialising the key exchange button in a turned off state
        this.disableKeyExchangeButton(true);
        // Initialising the progress indicator in a turned off state
        this.showProgressIndicator(false);
    }

    /**
     * Method that initialises a new ServerConnect instance to connect to a chat server.
     * @param server
     *      the chat server's IP address
     * @param port
     *      the chat server's port
     */
    public void connectToServer(String server, int port) {
        // Storing the connection details
        this.server = server;
        this.port = port;
        this.connection = new ServerConnect(server, port, this); // creating a new ServerConnect object
        this.connection.start(); // Start the thread for it
    }

    /**
     * Method that attempts to reconnect to the chat server that this scene was originally meant for.
     */
    public void reconnectToServer() {
        this.connection = new ServerConnect(server, port, this); // creating a new ServerConnect to overwrite the old one
        this.connection.start(); // Starting the thread for it
    }

    /**
     * Method that disconnects the client from the server, and changes the GUI back to the connection scene
     * @throws IOException
     *      when the layout file is not found
     */
    public void disconnect() throws IOException {
        // Disconnect from the server
        this.connection.closeObjectStreams();

        // Load the new layout
        Parent newSceneParent = FXMLLoader.load(getClass().getResource("connect_scene.fxml"));
        Scene newScene = new Scene(newSceneParent);

        /* Getting the target window / stage from a random element, messageInputBox, since it can't be used on the
         * menu item itself */
        Stage window = (Stage) this.messageInputBox.getScene().getWindow();

        // Loading the new scene onto the window and showing it
        window.setScene(newScene);
        window.show();
    }

    /**
     * Method that sends a request to the server to initiate a key exchange
     */
    public void startKeyExchange() {
        if(this.connection.isAnotherClientConnected()){ // only send the request if there's another client connected
            this.connection.sendRequest(new Request(RequestCode.INIT_KEY_EXCHANGE, null));
        } else {
            this.displayMessage("INFO: No other client connected");
        }
    }

    /**
     * Method that sends a message to the chat server (to be relayed to the other client)
     */
    public void sendMessage() {
        String message = this.messageInputBox.getText(); // Getting the text from the TextField

        if(message.length() > MainController.MAX_MESSAGE_LENGTH) { // checking if the message is within the limit
            this.displayMessage("Message is above maximum character limit " + MainController.MAX_MESSAGE_LENGTH + "!" +
                    " Try to send it again after shortening it.");
        } else { // if it is, proceed in attempting to send it
            if(this.connection.isSecureConnected()){ // if the client is securely connected, as in it has generated all the encryption details needed
                // Sending a request containing the string
                try {
                    this.connection.sendRequest(new Request(RequestCode.MESSAGE, this.connection.getCryptoHandler().encrypt(message)));
                    this.displayMessage("CLIENT - " + message); // displaying the message in the interface
                } catch (Exception e) {
                    // if there was an exception, let the user know the message was not sent
                    this.displayMessage("ERROR: Message could not be sent.");
                    e.printStackTrace();
                }
            } else {
                this.displayMessage("ERROR: Not securely connected with other client");
            }

            this.messageInputBox.setText(""); // resetting the text on the TextField
        }
    }

    /**
     * Method that displays a message onto the user interface
     * @param message
     *      the message to display
     */
    public void displayMessage(String message) {
        // using Platform.runLater() because this will be called through the ServerConnect thread too
        Platform.runLater(() -> this.messageHistory.add(message)); // add the message to the message history
    }

    /**
     * Method that enables or disables Message input
     * @param f
     *      flag on whether or not to allow input
     */
    public void allowInput(boolean f) {
        Platform.runLater(() -> {
            this.messageInputBox.setEditable(f);
            this.messageInputBox.setDisable(!f);
        });
    }

    /**
     * Method that disables or enables the key exchange button
     * @param f
     *      flag on whether or not to disable the button
     */
    public void disableKeyExchangeButton(boolean f) {
        Platform.runLater(() -> this.keyExchangeButton.setDisable(f));
    }

    /**
     * Method to update the status label
     * @param text
     *      text to update the status label with
     */
    public void updateStatus(String text) {
        Platform.runLater(() -> this.statusLabel.setText(text));
    }

    /**
     * Method to show the progress indicator
     * @param f
     * 		flag on whether or not to show the progress indicator
     */
    public void showProgressIndicator(boolean f) {
        Platform.runLater(() -> {
            this.keyExchangeProgress.setVisible(f);
            this.keyExchangeProgress.setManaged(f);
        });
    }

    /**
     * Method to update the progress on the progress indicator
     * @param value
     *      the value to update the progress to. Between 0 and 1
     * @throws IllegalArgumentException
     *      when the value parameter has an invalid value
     */
    public void updateProgressIndicator(double value) throws IllegalArgumentException{
        Platform.runLater(() -> {
            if(value >= 0 && value <= 1) {
                this.keyExchangeProgress.setProgress(value);
            } else {
                throw new IllegalArgumentException("Value can only be between 0 and 1.");
            }
        });
    }
}
