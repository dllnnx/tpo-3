package ru.itmo.exceptions;

public class InvalidFirefoxException extends InvalidCoreException {

    public InvalidFirefoxException(String message) {
        super(message);
    }

    public InvalidFirefoxException() {
        super.message = "Configuration for running Firefox not found.";
    }
}