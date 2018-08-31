/**
 * RequestCodeTest.java
 */
package requests;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author [ec00727]
 */
public class RequestCodeTest {

    @Test
    public void testValid() {
        assertEquals(6, RequestCode.values().length);
    }


}