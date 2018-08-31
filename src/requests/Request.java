/**
 * Request.java
 */
package requests;

import java.io.Serializable;

/**
 * Request class used to store an Object along with its purpose (in a RequestCode).
 * To be used to transfer objects over a network.
 * @author [ec00727]
 */
public class Request implements Serializable {
    /** Obligatory Serial Version ID for Serializable objects */
	private static final long serialVersionUID = 1L;
	/** The Request's code */
    private RequestCode code = null;
    /** The Object to be sent with the Request */
    private Object message = null;

    /**
     * Parameterised Constructor for the Request class.
     * @param code
     *      the request's code
     * @param message
     *      the request's message, the object to be sent, can be null
     * @throws IllegalArgumentException
     *      when the request code argument is null
     */
    public Request(RequestCode code, Object message) throws IllegalArgumentException {
        if(code != null){
            this.code = code;
        } else {
            throw new IllegalArgumentException("The Request's code can't be null!");
        }
        this.message = message;
    }

    /**
     * Accessor for the code field.
     * @return the Request's code
     */
    public RequestCode getCode() {
        return this.code;
    }

    /**
     * Accessor for the message field.
     * @return the Request's message
     */
    public Object getMessage() {
        return this.message;
    }
}
