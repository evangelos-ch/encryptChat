/**
 * AllTests.java
 */

import client.*;
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import requests.RequestCodeTest;
import requests.RequestTest;
import server.ClientConnectionTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        client.MainTest.class,
        ConnectControllerTest.class,
        MainControllerTest.class,
        ServerConnectTest.class,
        MathTest.class,
        CryptographerTest.class,
        RequestTest.class,
        RequestCodeTest.class,
        server.MainTest.class,
        ClientConnectionTest.class,
})

public class AllTests {
    public static Test suite() {
        return new JUnit4TestAdapter(AllTests.class);
    }
}
