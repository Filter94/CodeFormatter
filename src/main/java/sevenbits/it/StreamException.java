package sevenbits.it;

/**
 * Raises when there is some problems with stream
 */

public class StreamException extends Exception {
    StreamException(String message, Throwable cause){
        super(message, cause);
    }
}