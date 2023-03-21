package fr.raconteur32.modpackconfigupdater.exceptions;

public class FileCreationException extends Exception {
    public FileCreationException(String message) {
        super(message);
    }

    public FileCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
