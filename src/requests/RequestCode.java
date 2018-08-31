/**
 * RequestCode.java
 */
package requests;

import java.io.Serializable;

/**
 * Enumeration to define the different codes a Request can have.
 * @author [ec00727]
 */
public enum RequestCode implements Serializable {
    /** When a Number (BigInteger) is being sent. */
    NUMBER,
    /** When a pair of Numbers (BigIntegers) is being sent. */
    NUMBERS,
    /** When a simple message string is being sent. */
    MESSAGE,
    /** When an error string is being sent. */
    ERROR,
    /** When a status update is being sent. */
    STATUS,
    /** When a signal to initiate a key exchange is being sent. */
    INIT_KEY_EXCHANGE
}
