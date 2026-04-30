package ru.itmo.exceptions;

public class InvalidChromeException extends InvalidCoreException {

    public InvalidChromeException(String message) {
        super(message);
    }

    public InvalidChromeException() {
        super.message = "Configuration for running Chrome not found.";
    }

}