/**
 * ServerConnectTest.java
 */
package client;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author [ec00727]
 */
public class ServerConnectTest {
    private static final String server = "127.0.0.1";
    private static final int port = 7890;

    /**
     * Tests the successful creation of the object.
     */
    @Test
    public void testSuccessfulConstruction() {
        MainController dummyController = new MainController();
        ServerConnect serverConnect = new ServerConnect(server, port, dummyController);
        assertFalse(serverConnect.isSecureConnected());
        assertFalse(serverConnect.isAnotherClientConnected());
    }

    /**
     * Tests that the creation fails when the client is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionFail() {
        ServerConnect serverConnect = new ServerConnect(server, port, null);
    }

    /**
     * Tests that the creation fails when the server IP is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionFail2() {
        MainController dummyController = new MainController();
        ServerConnect serverConnect = new ServerConnect(null, port, dummyController);
    }

    /**
     * Tests that the creation fails when the port is less than 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionFail3() {
        MainController dummyController = new MainController();
        ServerConnect serverConnect = new ServerConnect(server, -50, dummyController);
    }

}