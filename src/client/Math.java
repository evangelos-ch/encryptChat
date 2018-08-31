/**
 * Math.java
 */
package client;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Singleton Class to handle most mathematical functions.
 * @author [ec00727]
 */
public class Math {
    /** Field to hold the current instance of the singleton class. */
    private static Math instance = null;
    /** The client's secret number. */
    private BigInteger secretNum = null;

    /** Empty private constructor for the class, since it will be a Singleton class. */
    private Math() {
    }

    /**
     * Method to return the current instance of this Singleton Class.
     * If it doesn't exist, it creates the instance and then returns it.
     * @return the instance of this singleton class
     */
    public static Math getInstance() {
        if(instance == null) instance = new Math();
        return instance;
    }

    /**
     * Accessor for the secretNum field.
     * @return this client's secret number
     */
    public BigInteger getSecretNum() {
        return this.secretNum;
    }

    /**
     * Mutator for the secretNum field.
     * @param secretNum
     *      the new secret number
     * @throws IllegalArgumentException
     *      if the new secret number is null
     */
    public void setSecretNum(BigInteger secretNum) throws IllegalArgumentException {
        if(secretNum != null) {
            this.secretNum = secretNum;
        } else {
            throw new IllegalArgumentException("Secret Number can't be null!");
        }
    }

    /**
     * Generates a random large prime integer between 1 and highLimit.
     * @param highLimit
     *      the highest number this number could be
     * @return
     *      the random large prime integer
     */
    public BigInteger generateRandomNum(BigInteger highLimit) {
        // Input Validation
        if(highLimit == null) throw new IllegalArgumentException("Upper limit can't be null");

        /* The random function to be used */
        SecureRandom random = new SecureRandom();

        /* The variable we will be generating numbers in.
         * Max bit length of the bit length of the high limit number
         * Certainty of 1/100. Lower certainty value = more likely to be prime
         * Using the SecureRandom object from above to generate numbers
         */
        BigInteger ret = new BigInteger(highLimit.bitLength(), 1/100, random);

        /* While the random number is not less or equal to the highLimit */
        while(ret.compareTo(highLimit) > 0 || ret.compareTo(BigInteger.ONE) < 0){
            ret = new BigInteger(highLimit.bitLength(), 1/100, random);
        }

        return ret;
    }

    /**
     * Calculates the formula of g^x mod n and returns the value.
     * @param g
     *      the variable g
     * @param x
     *      the variable x
     * @param n
     *      the variable n
     * @return
     *      the value of g^x mod n
     * @throws IllegalArgumentException
     *      when either of the arguments is null
     *
     */
    public BigInteger euclid(BigInteger g, BigInteger x, BigInteger n) throws IllegalArgumentException {
        // Input Validation
        if(g == null || x == null || n == null) throw new IllegalArgumentException("Arguments can't be null");

        return g.modPow(x, n);
    }
}
