package com.caesar.exception;

public class NotExistsDolphinLevelException extends Exception {

    private static final long serialVersionUID = 1L; // Unique identifier for serialization

    /**
     * Constructs a new GenTaskCodeFaildException with no detail message.
     */
    public NotExistsDolphinLevelException() {
        super();
    }

    /**
     * Constructs a new GenTaskCodeFaildException with the specified detail message.
     *
     * @param message the detail message
     */
    public NotExistsDolphinLevelException(String message) {
        super(message);
    }

    /**
     * Constructs a new GenTaskCodeFaildException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public NotExistsDolphinLevelException(String message, Throwable cause) {
        super(message, cause);
    }
}