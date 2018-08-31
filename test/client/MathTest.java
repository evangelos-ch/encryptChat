/**
 * MathTest.java
 */
package client;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * @author [ec00727]
 */
public class MathTest {
    private static final BigInteger testSecretNum = BigInteger.valueOf(150);

    /**
     * Tests the construction of the class.
     */
    @Test
    public void testConstruction() {
        // Get the instance of the math class
        Math math = Math.getInstance();

        // Testing that the accessors and mutators work
        math.setSecretNum(testSecretNum);
        assertEquals(testSecretNum, math.getSecretNum());
    }

    /**
     * Tests that setSecretNum fails when the argument is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSecretNum() {
        // Get the instance of the math class
        Math math = Math.getInstance();
        math.setSecretNum(null);
    }

    /**
     * Tests the generation of a random large prime number.
     */
    @Test
    public void testGenerateRandomNum() {
        // Get the instance of the math class
        Math math = Math.getInstance();

        // Generate a new random number
        BigInteger rand = math.generateRandomNum(testSecretNum);

        // Make sure that it's prime and also that it's less or equal to the high limit given of testSecretNum
        assertTrue(rand.isProbablePrime(1/100));
        assertTrue(rand.compareTo(testSecretNum) <= 0);
    }

    /**
     * Tests that the euclid mod power method works correctly.
     */
    @Test
    public void testEuclid() {
        // Get the instance for the math class
        Math math = Math.getInstance();

        // Get 3 numbers to test
        BigInteger g = BigInteger.valueOf(2);
        BigInteger x = BigInteger.valueOf(10);
        BigInteger n = BigInteger.valueOf(100);

        // Test that the value returned by the euclid method is correct
        assertEquals(BigInteger.valueOf(24), math.euclid(g, x, n));
    }

    /**
     * Tests that the euclid mod power method fails when an argument is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEuclid() {
        // Get the instance for the math class
        Math math = Math.getInstance();

        // Get 2 numbers to test
        BigInteger g = BigInteger.valueOf(2);
        BigInteger x = BigInteger.valueOf(10);

        math.euclid(g, x, null);
    }
}