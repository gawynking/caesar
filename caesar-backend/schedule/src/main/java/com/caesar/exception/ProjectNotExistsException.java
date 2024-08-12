package com.caesar.exception;

public class ProjectNotExistsException extends Exception {

    private static final long serialVersionUID = 1L; // Unique identifier for serialization

    /**
     * Constructs a new ProjectNotExistsException with the specified detail message.
     *
     * @param message the detail message
     */
    public ProjectNotExistsException(String message) {
        super(message);
    }

    /**
     * Constructs a new ProjectNotExistsException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public ProjectNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}