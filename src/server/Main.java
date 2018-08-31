/**
 * Main.java
 */
package server;

import requests.Request;
import requests.RequestCode;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * The main class for the server component
 * @author [ec00727]
 */
public class Main {
    /** Constant with the PORT to run the server on */
    private static final int PORT = 7890;
    /** Constant with the name of the log file */
    private static final String LOG_FILE = "src/log.txt";
    /** Field to hold the instance of this class */
    private static Main instance = null;
    /** The Server Socket */
    private ServerSocket server = null;
    /** A large random integer to be used as part of the encryption process. Generated on server start */
    private BigInteger randG = null;
    /** A large random integer to be used as part of the encryption process. Generated on server start */
    private BigInteger randN = null;
    /** List of the clients connected to the server */
    private List<ClientConnection> clients = null;
    /** The logger to use to log system events */
    private Logger logger;

    /**
     * Constructor for the Main class
     */
    public Main() {
        // Setting up the logger
        File logFile = new File(Main.LOG_FILE); // opening the log file
        try {
            if(!logFile.exists()) logFile.createNewFile(); // if it doesn't exist, create it
            this.logger = Logger.getLogger("server"); // get logger for server subsystem
            this.logger.addHandler(new FileHandler(Main.LOG_FILE, true)); // open the log file in append mode
            this.logger.setLevel(Level.INFO); // setting the logger in INFO mode, so anything of info and up is logged
        } catch (IOException e) { // if there was an exception
            System.out.println("Logging failed to initialise");
            this.logger = null;
            e.printStackTrace();
        }

        // Initialising the client list
        this.clients = new ArrayList<ClientConnection>();

        // Initialising the random numbers
        this.generateRandNums();
    }

    /**
     * Main method to be run
     * @param args
     */
    public static void main(String[] args) {
        Main.instance = new Main(); // Creating the object
        Main.instance.clientConnectionListener(); // start the listener for the client connections
    }

    /**
     * Method to generate the two random numbers for encryption purposes
     */
    private void generateRandNums() {
        SecureRandom random = new SecureRandom(); // secure random function to be used to generate the numbers
        this.randG = new BigInteger(512, 1/100, random); // first random number, bit length of 512
        this.randN = new BigInteger(2048, 1/100, random); // second random number, bit length of 2048
    }

    /**
     * Method to log a message of a certain type
     * @param type
     *      the type of message to log
     * @param message
     *      the message to log
     * @throws IllegalArgumentException
     *      when either parameter is null
     */
    public void log(String type, String message) throws IllegalArgumentException {
        // Input validation
        if(type == null) throw new IllegalArgumentException("Type can't be null");
        if(message == null) throw new IllegalArgumentException("Message can't be null");

        if(this.logger != null){ // if there is a logger set up
            switch(type) { // log it based on the type
                case "severe":
                    this.logger.severe(message);
                    break;
                case "warning":
                    this.logger.warning(message);
                    break;
                default:
                    this.logger.info(message);
                    break;
            }
        } else { // if there isn't one set up, just print it to the java console
            System.out.println(type.toUpperCase() + ": " + message);
        }
    }

    /**
     * Accessor for the clients field
     * @return list of connected clients
     */
    public List<ClientConnection> getClients() {
        return this.clients;
    }

    /**
     * Method that returns the number of connected clients
     * @return amount of connected clients
     */
    public int clientAmount() {
        return this.clients.size();
    }

    /**
     * Method that constantly reads and handles incoming connections from clients
     */
    private void clientConnectionListener() {
        try {
            this.log("info", "Server starting up at port " + Main.PORT);
            this.server = new ServerSocket(Main.PORT, 100); // open a server socket at the specified port
            while(true) { // indefinitely
                Socket socket = this.server.accept(); // accept new connections
                if(this.clientAmount() < 2) { // if there are less than 2 clients currently connected
                    // Setting up the new client ID
                    int clientID = this.clientAmount() + 1;
                    if(this.clientAmount() == 1) clientID = this.clients.get(0).getID() + 1;

                    // Setting up the client connection
                    ClientConnection client = new ClientConnection(clientID, Main.instance, socket);

                    // Adding the client connection in the list and starting the connection
                    this.clients.add(client);
                    client.start();

                    // If this is the second client that connects
                    if(this.clientAmount() == 2) {
                        Request anotherClient = new Request(RequestCode.STATUS, "client_connect");
                        this.clients.get(0).sendRequest(anotherClient); // inform the first client
                        client.sendRequest(anotherClient);
                    }
                } else { // if there are 2 or more
                    socket.close(); // just close the connection
                    this.log("info","Refused connection with Client 3, due to already being max capacity");
                }
            }
        } catch (IOException e) {
            this.log("warning", "Exception occurred when receiving a connection or setting up the socket.");
            e.printStackTrace();
        }
    }

    /**
     * Method to initiate a key exchange
     * @throws IllegalStateException
     *      when there are less than two clients connected
     */
    public void startKeyExchange() throws IllegalStateException {
        // Checking if there are two clients connected
        if(this.clientAmount() != 2) {
            throw new IllegalStateException("Two clients need to be connected for a key exchange");
        } else {
            Request numbersRequest = new Request(RequestCode.NUMBERS, new BigInteger[]{this.randG, this.randN});
            for(ClientConnection client : this.clients) {
                client.sendRequest(numbersRequest);
            }
        }
    }

    /**
     * Method that sends a request to a client that does NOT match the given id
     * @param req
     *      the request to send
     * @param id
     *      the ID that is not the one of the client to send to
     * @throws IllegalArgumentException
     *      when the request to send is null
     * @throws IllegalStateException
     *      when there are less than two clients connected
     */
    public void sendRequestToOtherClient(Request req, int id) throws IllegalArgumentException, IllegalStateException {
        // Input validation
        if(req == null) throw new IllegalArgumentException("Request can't be null");
        // Checking if there are two clients connected
        if(this.clientAmount() != 2) {
            throw new IllegalStateException("Two clients need to be connected");
        } else { // if there are
            for(ClientConnection client : this.clients) { // loop through the clients array
                if(client.getID() != id) { // if we find the one that is not the one we're looking for
                    client.sendRequest(req); // send the request and break the loop
                    break;
                }
            }
        }
    }
}
