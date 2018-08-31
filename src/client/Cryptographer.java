/**
 * Cryptographer.java
 */
package client;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Singleton Class to handle most cryptographic functions
 * @author [ec00727]
 */
public class Cryptographer {
    /** Constant of the encryption algorithm being used */
    private static final String ALGORITHM = "AES";
    /** Constant of the hashing function being used */
    private static final String HASH_FUNCTION = "SHA-256";

    /** Field to hold the current instance of the singleton class */
    private static Cryptographer instance = null;
    /** Advanced Encryption Standard (AES) Key. Used for any encryption / decryption operations */
    private Key aesKey = null;

    /** Empty private constructor for the class, since it will be a Singleton class. */
    private Cryptographer() {
    }

    /**
     * Method to return the current instance of this Singleton Class.
     * If it doesn't exist, it creates the instance, stores it and then returns it
     * @return the instance of this singleton class
     */
    public static Cryptographer getInstance() {
        if(instance == null) Cryptographer.instance = new Cryptographer();
        return Cryptographer.instance;
    }


    /**
     * Method that encrypts a string using a cipher and the encryption key. The encrypted result is returned
     * as a string.
     * @param message
     *      the string to encrypt
     * @return encrypted string as a string
     * @throws Exception
     *      IllegalStateException if no AES Key has been generated and the method is called
     *      IllegalArgumentException if the given parameter message is null
     *      NoSuchAlgorithmException and other exceptions when initialising the cipher
     */
    public String encrypt(String message) throws Exception {
        // Input Validation
        if(this.aesKey == null) throw new IllegalStateException("No encryption key has been generated");
        if(message == null) throw new IllegalArgumentException("Message to encrypt can't be null");

        // Creating and initialising the cipher for the algorithm we're using to use when encrypting
        Cipher cipher = Cipher.getInstance(Cryptographer.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, this.aesKey); // initialising the cipher at encryption mode

        // Encrypting the message string with the cipher created above, saved into a byte array
        byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        // Encoding the byte array with Base64 back into a string and returning it
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Method that decrypts a string using a cipher and the encryption key. The decrypted result is returned
     * as a string.
     * @param message
     *      the encrypted string to decrypt
     * @return decrypted message as string
     * @throws Exception
     *      IllegalStateException if no AES Key has been generated and the method is called
     *      IllegalArgumentException if the given parameter message is null
     *      NoSuchAlgorithmException and other exceptions when initialising the cipher
     */
    public String decrypt(String message) throws Exception {
        // Input Validation
        if(this.aesKey == null) throw new IllegalStateException("No encryption key has been generated");
        if(message == null) throw new IllegalArgumentException("Message to encrypt can't be null");

        // Creating and initialising the cipher for the algorithm we're using to use when decrypting
        Cipher cipher = Cipher.getInstance(Cryptographer.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, this.aesKey); // initialising the cipher at decryption mode

        // Decoding the encrypted message string into a byte array using Base64, then decrypting that with the cipher
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(message));

        // Re-encoding the decrypted bytes into a string in UTF-8 and returning it
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Method to return the hash of a BigInteger, based on a hashing function defined as a constant in the class.
     * @param num
     *      the BigInteger to hash
     * @return the hash of the BigInteger as a string in base64
     * @throws NoSuchAlgorithmException
     *      when the hash function is invalid
     * @throws IllegalArgumentException
     *      when the num argument is null
     */
    public String hash(BigInteger num) throws IllegalArgumentException, NoSuchAlgorithmException {
        // Input Validation
        if(num == null) throw new IllegalArgumentException("Number to hash can not be null.");

        /* Message digest to use based on the hash function for this class */
        MessageDigest digest = MessageDigest.getInstance(Cryptographer.HASH_FUNCTION);

        // Store the hash in a byte array
        byte[] hashBytes = digest.digest(num.toByteArray());
        // return the byte array encoded in base64
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    /**
     * Method to generate AES Key based on a byte array.
     * @param key
     *      the byte array to be used to create the key
     * @throws IllegalArgumentException
     *      when key string given is null or more than 32 bytes long
     */
    public void generateAesKey(String key) throws IllegalArgumentException {
        // Input validation
        if(key == null) throw new IllegalArgumentException("Key can't be null.");
        /* Byte array of the bytes from decoding the string from base 64 */
        byte[] keyBytes = Base64.getDecoder().decode(key);

        // if the length of the key in bytes is too long
        if(keyBytes.length > 32) throw new IllegalArgumentException("Key can't be more than 32 bytes long.");

        // Use the byte array to generate an AES key
        this.aesKey = new SecretKeySpec(keyBytes, Cryptographer.ALGORITHM);
    }

    /**
     * Method that clears the encryption data from memory
     */
    public void clearAesKey() {
        this.aesKey = null;
    }

}
