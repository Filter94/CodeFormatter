package sevenbits.it.Streams;

/**
 *  Simple input stream
 */

public interface InStream {

    /**
     * Checks if there is end of stream
     * @return  if stream it is the end of a stream
     * @throws StreamException - if stream is not available
     */

    boolean isEnd() throws StreamException;

    /**
     * Reads symbol from stream
     * @return next symbol of stream
     * @throws StreamException - if stream is not available
     */

    char readSymbol() throws StreamException;

    /**
     * Closes stream
     * @throws StreamException - if stream is not available
     */

    void close() throws StreamException;
}
