/**
 * CryptographerTest.java
 */
package client;

import org.junit.Test;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * @author [ec00727]
 */
public class CryptographerTest {
    private static final BigInteger number = BigInteger.valueOf(1482741235);
    private static final String message = "�n�߂܂��āI";
    private static final String encrypted_message = "ll2nH8/cCjn2GQZ5zlIHpWTEBZ329e2AWX5kuszWBWA=";

    /**
     * Tests the construction of the class.
     */
    @Test
    public void testConstruction() {
        // Get the instance of the Cryptographer class
        Cryptographer crypto = Cryptographer.getInstance();
    }

    /**
     * Tests that the hash function works correctly on a BigInteger and encodes properly in base64.
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void testHash() throws NoSuchAlgorithmException {
        // Get the instance of the Cryptographer class
        Cryptographer crypto = Cryptographer.getInstance();

        assertEquals("OWdorVCOVqZLikicfoChAtyx2Ax/U31eTdcYRBZQRsU=", crypto.hash(number));
    }

    /**
     * Tests that the hash function fails when the argument is null
     * @throws NoSuchAlgorithmException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testHashFail() throws NoSuchAlgorithmException {
        // Get the instance of the Cryptographer class
        Cryptographer crypto = Cryptographer.getInstance();

        crypto.hash(null);
    }

    /**
     * Tests that the AES Key generation method goes through without any errors.
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void testAesKeyGeneration() throws NoSuchAlgorithmException {
        // Get the instance of the Cryptographer class
        Cryptographer crypto = Cryptographer.getInstance();

        crypto.generateAesKey(crypto.hash(number));
    }

    /**
     * Tests that the AES Key generation method fails when the argument is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAesKeyGenerationFail() {
        // Get the instance of the Cryptographer class
        Cryptographer crypto = Cryptographer.getInstance();

        crypto.generateAesKey(null);
    }

    /**
     * Tests the successful encryption of a string.
     * @throws Exception
     */
    @Test
    public void testEncryption() throws Exception {
        // Get the instance of the Cryptographer class
        Cryptographer crypto = Cryptographer.getInstance();

        // Generating the AES Key
        crypto.generateAesKey(crypto.hash(number));

        assertEquals(encrypted_message, crypto.encrypt(message));
    }

    /**
     * Tests that the encryption method fails when there's no AES Encryption key.
     * This unit test also tests the "clearAesKey" method.
     * @throws Exception
     */
    @Test(expected = IllegalStateException.class)
    public void testEncryptionFail() throws Exception {
        // Get the instance of the Cryptographer class
        Cryptographer crypto = Cryptographer.getInstance();

        // Generating the AES Key
        crypto.generateAesKey(crypto.hash(number));

        // Clear the AES Key
        crypto.clearAesKey();

        assertEquals(encrypted_message, crypto.encrypt(message));
    }

    /**
     * Tests that the encryption method fails when the string to encrypt is null.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEncryptionFail2() throws Exception {
        // Get the instance of the Cryptographer class
        Cryptographer crypto = Cryptographer.getInstance();

        // Generating the AES Key
        crypto.generateAesKey(crypto.hash(number));

        crypto.encrypt(null);
    }

    /**
     * Tests the successful decryption of a string.
     * @throws Exception
     */
    @Test
    public void textDecryption() throws Exception {
        // Get the instance of the Cryptographer class
        Cryptographer crypto = Cryptographer.getInstance();

        // Generating the AES Key
        crypto.generateAesKey(crypto.hash(number));

        assertEquals(message, crypto.decrypt(encrypted_message));
    }

    /**
     * Tests that the decryption method fails when there's no AES Encryption key.
     * This unit test also tests the "clearAesKey" method.
     * @throws Exception
     */
    @Test(expected = IllegalStateException.class)
    public void testDecryptionFail() throws Exception {
        // Get the instance of the Cryptographer class
        Cryptographer crypto = Cryptographer.getInstance();

        // Generating the AES Key
        crypto.generateAesKey(crypto.hash(number));

        // Clear the AES Key
        crypto.clearAesKey();

        crypto.decrypt(encrypted_message);
    }

    /**
     * Tests that the decryption method fails when the string to decrypt is null.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDecryptionFail2() throws Exception {
        // Get the instance of the Cryptographer class
        Cryptographer crypto = Cryptographer.getInstance();

        // Generating the AES Key
        crypto.generateAesKey(crypto.hash(number));

        crypto.decrypt(null);
    }
}