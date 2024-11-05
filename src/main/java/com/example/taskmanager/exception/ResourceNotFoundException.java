package com.example.taskmanager.exception;

public class ResourceNotFoundException extends RuntimeException {
    /**
     * Exception thrown when a requested resource cannot be found.
     *
     * @param message The error message providing details about the missing resource.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
