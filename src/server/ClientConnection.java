/**
 * ClientConnection.java
 */
package server;

import requests.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
/**
 * Class to handle the server's connection with an individual client. Runs in its own thread
 * @author [ec00727]
 */
public class ClientConnection extends Thread {
    /** The socket to the chat client */
    private Socket socket = null;
    /** The ID of the ClientConnection */
    private int id = 0;
    /** Input Stream to receive requests */
    private ObjectInputStream requestIn = null;
    /** Output Stream to send requests */
    private ObjectOutputStream requestOut = null;
    /** Boolean to track whether or not the server is still connected to this client */
    private boolean connected = false;
    /** The Main class that has created the ClientConnection. Used to send requests to other clients or log events */
    private Main server = null;

    /**
     * Parameterized Constructor for the ClientConnection class
     * @param id
     *      the id of the ClientConnection
     * @param server
     *      the Main class that has created the ClientConnection
     * @param socket
     *      the socket to the chat client for this ClientConnection
     * @throws IllegalArgumentException
     *      when any of the parameters are null or have an invalid value
     */
    public ClientConnection(int id, Main server, Socket socket) throws IllegalArgumentException {
        if(id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID can't be 0 or negative");
        }
        if(server != null) {
            this.server = server;
        } else {
            throw new IllegalArgumentException("Server can't be null");
        }
        if(socket != null) {
            this.socket = socket;
        } else {
            throw new IllegalArgumentException("Socket can't be null");
        }
    }

    /**
     * Accessor for the ID field
     * @return the ClientConnection's ID
     */
    public int getID() {
        return this.id;
    }

    /**
     * Overriding the run() method of the Thread superclass in order to add custom functionality.
     * Attempts set up object streams with the client, and then waits for requests indefinitely.
     */
    @Override
    public void run() {
        this.setupObjectStreams();
        this.server.log("info", "Listening for requests for Client " + this.id);
        while(this.connected) { // while connected to the client
            try { // attempt to read in requests and handle t hem
                Request req = (Request) this.requestIn.readObject();
                this.handleRequest(req);
            } catch (ClassNotFoundException e) { // if it's an invalid class, log an appropriate message
                this.server.log("warning", "Unrecognizable Request sent from Client " + this.id);
            } catch (IOException e) { // if there was an exception when trying to read, the client has disconnected
                this.server.log("warning", "Exception occurred when receiving Request from Client " + this.id + ". Likely disconnected.");
                this.connected = false; // close the loop
            }
        }
        this.closeObjectStreams();

    }

    /**
     * Method to set up the object streams with the connected client.
     */
    private void setupObjectStreams() {
        try {
            this.requestOut = new ObjectOutputStream(this.socket.getOutputStream());
            this.requestOut.flush();
            this.requestIn = new ObjectInputStream(this.socket.getInputStream());
            this.server.log("info", "Streams setup with Client " + this.id);
            this.connected = true;
        } catch (IOException e) { // if there was an error when setting up the streams
            this.server.log("warning", "Exception occurred when setting up streams with Client " + this.id);
            e.printStackTrace();
        }
    }

    /**
     * Method to close the object streams with the connected client.
     */
    private void closeObjectStreams() {
        this.server.log("info", "Closing streams with Client " + this.id);
        try {
            this.socket.close(); // close the socket which closes the associated streams
            if(this.server.clientAmount() == 2) { // if there is another client also connected
                // Send a request to let it know that the other client has disconnected
                this.server.sendRequestToOtherClient(new Request(RequestCode.STATUS, "client_disconnect"), this.id);
            }
            this.server.getClients().remove(this); // remove the ClientConnection from the list
        } catch (IOException e) { // if there was an exception, log it
            this.server.log("warning", "Exception occurred when closing the streams on Client " + this.id);
            e.printStackTrace();
        }
    }

    /**
     * Method to send a request to the connected client.
     * @param req
     *      the request to send
     * @throws IllegalArgumentException
     *      when the request to send is null
     * @throws NullPointerException
     *      when there is no output stream
     */
    public void sendRequest(Request req) throws IllegalArgumentException {
        // Check if there is an output stream to the client
        if(this.requestOut == null) throw new NullPointerException("No output stream to a chat server");
        // Input validation
        if(req == null) throw new IllegalArgumentException("Request to send can't be null");

        try { // attempt to send the object over the output stream
            this.server.log("info", "Sending Request to Client " + this.id + " with code " + req.getCode());
            this.requestOut.writeObject(req);
            this.requestOut.flush();
        } catch (IOException e) { // if there was any exception, log it
            this.server.log("warning", "Exception occurred when sending request to Client " + this.id);
            e.printStackTrace();
        }
    }

    /**
     * Method to handle a received request from a connected client
     * @param req
     *      the request to handle
     * @throws IllegalArgumentException
     *      when the request to handle is null
     */
    public void handleRequest(Request req) throws IllegalArgumentException {
        // Input validation
        if(req == null) throw new IllegalArgumentException("Request to handle can't be null");

        this.server.log("info", "Received Request: " + req.getCode());

        // Request to send in case there is no second client connected to the server
        Request noSecondClient = new Request(RequestCode.ERROR, "No second client connected");

        switch(req.getCode()) { // handle the request based on its request code
            case INIT_KEY_EXCHANGE: // if it's a signal to initiate a key exchange
                if(this.server.clientAmount() == 2) { // if there is another client
                    this.server.startKeyExchange(); // start the key exchange
                } else { // else notify the client
                    this.sendRequest(noSecondClient);
                }
                break;
            case NUMBER: // if it's a single large integer
                if(this.server.clientAmount() == 2) { // if there is another client connected
                    this.server.sendRequestToOtherClient(req, this.id); // send it to the other client
                } else { // else notify the client
                    this.sendRequest(noSecondClient);
                }
                break;
            case MESSAGE: // if it's a message
                if(this.server.clientAmount() == 2) { // if there is another client connected
                    this.server.sendRequestToOtherClient(req, this.id); // send it to the other client
                } else { // else notify the client
                    this.sendRequest(noSecondClient);
                }
                break;
            case STATUS: // if it's a status update
                this.server.log("info", (String) req.getMessage()); // log it
                break;
            case ERROR: // if it's an error
                this.server.log("warning", (String) req.getMessage()); // log it
                break;
        }
    }
}
