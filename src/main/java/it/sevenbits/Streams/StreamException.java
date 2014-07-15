package it.sevenbits.Streams;

/**
 * Raises when there is some problems with stream
 */

public class StreamException extends Exception {
    StreamException(final String message, final Throwable cause) {
        super(message, cause);
    }
    StreamException(final Exception ex) {
        super(ex.getMessage(), ex.getCause());
    }
}