/**
 * CryptographerTest.java
 */
package requests;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author [ec00727]
 */
public class RequestTest {

    /**
     * Tests the successful construction of the class.
     */
    @Test
    public void testSuccessfulConstruction() {
        // The message and code of the message
        String message = "hi";
        RequestCode code = RequestCode.MESSAGE;

        // Creating the  object
        Request request = new Request(code, message);

        // Checking if the accessors work correctly
        assertEquals(message, request.getMessage());
        assertEquals(code, request.getCode());
    }

    /**
     * Tests that the construction fails when teh request code is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionFail() {
        // The message and code of the message
        String message = "hi";

        // Creating the  object
        Request request = new Request(null, message);
    }

}