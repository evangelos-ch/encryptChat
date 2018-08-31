/**
 * ClientConnectionTest.java
 */
package server;

import org.junit.Test;

import java.net.Socket;

import static org.junit.Assert.*;

/**
 * @author [ec00727]
 */
public class ClientConnectionTest {
    private static final int id = 5;

    /**
     * Tests the successful creation of the object.
     */
    @Test
    public void testSuccessfulConstruction() {
        Main main = new Main();
        Socket socket = new Socket();
        ClientConnection clientConnection = new ClientConnection(id, main, socket);
    }

    /**
     * Tests that the construction fails when the ID is invalid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionFail() {
        Main main = new Main();
        Socket socket = new Socket();
        ClientConnection clientConnection = new ClientConnection(0, main, socket);
    }

    /**
     * Tests that the construction fails when the "Main" instance reference is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionFail2() {
        Socket socket = new Socket();
        ClientConnection clientConnection = new ClientConnection(id, null, socket);
    }

    /**
     * Tests that the construction fails when the socket reference is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionFail3() {
        Main main = new Main();
        ClientConnection clientConnection = new ClientConnection(id, main, null);
    }

}
