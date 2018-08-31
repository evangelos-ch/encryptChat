/**
 * ServerConnect.java
 */
package client;

import requests.Request;
import requests.RequestCode;

import java.io.*;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

/**
 * Class to handle the client's connection to the server. Responsible for sending and receiving requests
 * @author [ec00727]
 */
public class ServerConnect extends Thread {
    /** The chat server's IP address */
    private String server = null;
    /** The chat server's port */
    private int port = 0;
    /** The controller class for the client, so this class can access methods to alter UI elements */
    private MainController client = null;
    /** Input Stream to receive requests */
    private ObjectInputStream requestIn = null;
    /** Output Stream to send requests */
    private ObjectOutputStream requestOut = null;
    /** The socket to the chat server with */
    private Socket connection = null;
    /** Boolean to track whether or not the client is connected to a server */
    private boolean connected = false;
    /** Boolean to track whether or not another client is connected to the server */
    private boolean anotherClientConnected = false;
    /** Boolean to track whether or not a secure connection is ongoing with another client */
    private boolean secureConnected = false;
    /** Large integer to be used in the encryption process. Received from the server */
    private BigInteger publicMod = null;
    /** The Math instance for this session to handle mathematical functions */
    private Math mathHandler = null;
    /** The Cryptographer instance for this session to handle cryptographic functions */
    private Cryptographer cryptoHandler = null;

    /**
     * Parameterized constructor for the ServerConnect class.
     * @param server
     *      the IP of the chat server
     * @param port
     *      the port of the chat server
     * @param client
     *      the controller class that created the ServerConnect
     * @throws IllegalArgumentException
     *      when any of the arguments are null or invalid
     */
    public ServerConnect(String server, int port, MainController client) throws IllegalArgumentException {
        if(server != null) {
            this.server = server;
        } else {
            throw new IllegalArgumentException("Main address can't be null");
        }
        if(port > 0) {
            this.port = port;
        } else {
            throw new IllegalArgumentException("Main port can't be less or equal to 0");
        }
        if(client != null) {
            this.client = client;
        } else {
            throw new IllegalArgumentException("Client can't be null");
        }
        this.mathHandler = Math.getInstance();
        this.cryptoHandler = Cryptographer.getInstance();
    }

    /**
     * Accessor for the anotherClientConnected field.
     * @return whether or not another client is connected to the server
     */
    public boolean isAnotherClientConnected() {
        return this.anotherClientConnected;
    }

    /**
     * Accessor for the secureConnected field.
     * @return whether or not the client is connected to another one securely
     */
    public boolean isSecureConnected() {
        return this.secureConnected;
    }

    /**
     * Accessor for the cryptoHandler field.
     * @return returns the Cryptographer instance
     */
    public Cryptographer getCryptoHandler() {
        return cryptoHandler;
    }

    /**
     * Overriding the run() method of the Thread superclass in order to add custom functionality.
     * Attempts to connect to the server, set up object streams, and then waits for requests indefinitely.
     */
    @Override
    public void run() {
        try {
            this.client.allowInput(false); // disable input when first connecting to the server
            this.connect();
            if(this.connected) { // if the client managed to successfully connect
                this.setupObjectStreams(); // set up the object streams
                this.requestLoop(); // start listening for requests
            }
        } catch (EOFException e) {
            // if the input stream has reached its end, as in the connection was closed from the other side
            this.client.displayMessage("INFO: Connection closed from the server"); // inform the user
        } catch (IOException e) {
            e.printStackTrace(); // quietly ignore any other IO Exception
        } finally { // after any of the exceptions has occurred, close the object streams.
            this.closeObjectStreams();
        }
    }

    /**
     * Method that attempts to connect to a chat server.
     */
    private void connect() {
        this.client.displayMessage("INFO: Attempting to connect...");
        try { // attempt to create a socket at the address and port given on object creation
            this.connection = new Socket(InetAddress.getByName(this.server), this.port); // attempt to open a socket
            this.client.displayMessage("INFO: Connected to: " + this.connection.getInetAddress().getHostName());
            this.connected = true;
            this.client.updateStatus("Connected");
        } catch (UnknownHostException | ConnectException e) { // if socket creation failed, update the interface
            this.client.displayMessage("ERROR: Chat Server at " + this.server + " not found");
            this.client.updateStatus("Not Connected");
        } catch (IOException e) { // if any other error happened, quietly ignore it
            e.printStackTrace();
        }
    }

    /**
     * Method that initializes the object streams with the chat server.
     * @throws NullPointerException
     *      when method is called without the client being connected to a server
     */
    private void setupObjectStreams() throws NullPointerException {
        // Check if there is a connection to a chat server
        if(this.connection == null) throw new NullPointerException("Not connected to a server");

        try { // attempt to set up object streams
            this.requestIn = new ObjectInputStream(this.connection.getInputStream());
            this.requestOut = new ObjectOutputStream(this.connection.getOutputStream());
            this.requestOut.flush();
        } catch (EOFException e) {
            this.client.displayMessage("INFO: Connection closed from the server. (Probably maximum client limit exceeded). Try again later!");
        } catch (IOException e) { // quietly ignore an IO Exception
            e.printStackTrace();
        }
    }

    /**
     * Method that closes the object streams. Essentially disconnects the client from the server.
     */
    public void closeObjectStreams() throws NullPointerException {
        // Check if there is a connection to a chat server
        if(this.connection != null) {
            this.client.displayMessage("INFO: Disconnecting...");
            this.client.allowInput(false);
            try { // attempt to close the socket (which closes its streams as well)
                this.connection.close();
                this.connected = false;
                this.client.updateStatus("Not Connected");
            } catch (IOException e) { // quietly ignore an IO Exception
                e.printStackTrace();
            }
        } // if it's not, do nothing, useful when pressing Disconnect on the GUI when not connected at all
    }

    /**
     * Method that sends a request over the socket.
     * @param req
     *      the request to send
     * @throws IllegalArgumentException
     *      when the request to send is null
     * @throws NullPointerException
     *      when method is called without there being an output stream
     */
    public void sendRequest(Request req) throws IllegalArgumentException, NullPointerException {
        // Check if there is an output stream
        if(this.requestOut == null) throw new NullPointerException("No output stream to a chat server");
        // Input validation
        if(req == null) throw new IllegalArgumentException("Request to send can't be null");

        try { // attempt to send the object over the output stream
            this.requestOut.writeObject(req);
            this.requestOut.flush();
        } catch (IOException e) { // quietly ignore an IO Exception
            e.printStackTrace();
        }
    }

    /**
     * Method that takes in a request and decides what needs to be done to execute it.
     * @param req
     *      the request to handle
     * @throws IllegalArgumentException
     *      when the request to handle is null
     */
    private void handleRequest(Request req) throws IllegalArgumentException {
        // Input validation
        if(req == null)  throw new IllegalArgumentException("Request to handle can't be null");

        switch(req.getCode()){ // handle the request based on its request code
            case NUMBERS: // if it's two integers, that means that a key exchange is being initialised
                // Store the two big integers
                BigInteger[] numbers = (BigInteger[]) req.getMessage();
                BigInteger publicBase = numbers[0]; // This one will not be useful in the future, a local variable is fine
                this.publicMod = numbers[1];

                // Update the user interface to reflect the procedure
                this.client.disableKeyExchangeButton(true);
                this.client.displayMessage("INFO: Attempting to establish secure connection with the other client, please be patient...");
                this.client.updateProgressIndicator(0.2);
                this.client.showProgressIndicator(true);

                // Generate the secret number for this connection via the Math instance
                this.mathHandler.setSecretNum(this.mathHandler.generateRandomNum(this.publicMod));

                // Send back the result of the euclid equation from the math handler back to the server
                this.sendRequest(new Request(RequestCode.NUMBER, this.mathHandler.euclid(publicBase, this.mathHandler.getSecretNum(), this.publicMod)));

                // Update the user interface again
                this.client.updateProgressIndicator(0.6);
                break;
            case NUMBER: // if it's a single integer, that means that it's the other connected client's equation value
                BigInteger publicK = (BigInteger) req.getMessage(); // store it in a local variable
                try { // attempt to generate encryption data
                    this.client.updateProgressIndicator(0.8);

                    // Generate the encryption key based on this new value, as well as the secret number generated earlier
                    this.cryptoHandler.generateAesKey(this.cryptoHandler.hash(this.mathHandler.euclid(publicK, this.mathHandler.getSecretNum(), this.publicMod)));

                    // Update the user interface and tracking booleans
                    this.client.updateProgressIndicator(1);
                    this.client.updateStatus("Connected, Securely connected");
                    this.secureConnected = true;
                    this.client.disableKeyExchangeButton(true);
                    this.client.allowInput(true);
                    this.client.displayMessage("INFO: Successfully established secure connection! You can now begin chatting");
                } catch(NoSuchAlgorithmException e) {
                    /* quietly ignore any NoSuchAlgorithmException which shouldn't occur,
                     * if the cryptographer class is set up properly */
                    e.printStackTrace();
                }
                break;
            case MESSAGE: // if it's a message
                try { // Try decrypting it and displaying it
                    this.client.displayMessage("PARTNER - " + this.cryptoHandler.decrypt((String) req.getMessage()));
                } catch (Exception e) { // quietly ignore any exception
                    e.printStackTrace();
                }
                break;
            case STATUS: // if it's a status update
                switch((String) req.getMessage()){ // then we have other cases
                    case "client_connect": // if another client has connected
                        this.anotherClientConnected = true; // update the tracker

                        // And the user interface
                        this.client.disableKeyExchangeButton(false);
                        this.client.updateStatus("Connected, Another client connected");
                        this.client.displayMessage("INFO: Another client connected! Press 'Establish Secure Connection' to start chatting");
                        break;
                    case "client_disconnect": // if the other client has disconnected
                        // Update the tracker booleans
                        this.anotherClientConnected = false;
                        this.secureConnected = false;

                        // And the user interface
                        this.client.disableKeyExchangeButton(true);
                        this.client.showProgressIndicator(false);
                        this.client.allowInput(false);
                        this.cryptoHandler.clearAesKey();
                        this.client.updateStatus("Connected");
                        this.client.displayMessage("INFO: The other party has disconnected");
                        break;
                    default: // in any other case
                        this.client.displayMessage("STATUS: " + req.getMessage()); // display it to the user
                }
                break;
            case ERROR: // if it's an error
                this.client.displayMessage("ERROR: " + req.getMessage()); // just display it to the user
                break;
        }
    }

    /**
     * Method that indefinitely accepts reads the input stream for requests.
     * @throws IOException
     *      If there was an exception when reading a request from the stream
     * @throws NullPointerException
     *      when method is called without there being an input stream
     */
    private void requestLoop() throws IOException, NullPointerException {
        if(this.requestIn == null) throw new NullPointerException("No input stream to a chat server");

        while(this.connected) { // while the client is connected to a server
            try { // attempt to read a request and handle it
                Request req = (Request) this.requestIn.readObject();
                this.handleRequest(req);
            } catch (ClassNotFoundException e) { // if the object sent wasn't a request
                e.printStackTrace(); // quietly ignore it
            }
        }
    }

}
