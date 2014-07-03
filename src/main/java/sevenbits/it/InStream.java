package sevenbits.it;

/**
 *  Simple input stream
 */

public interface InStream {

    /**
     * Checks if there is end of stream
     * @return  if stream it is the end of a stream
     * @throws StreamException - if stream is not available
     */

    public boolean isEnd() throws StreamException;

    /**
     * Reads symbol from stream
     * @return next symbol of stream
     * @throws StreamException - if stream is not available
     */

    public char readSymbol() throws StreamException;

    /**
     * Closes stream
     * @throws StreamException - if stream is not available
     */

    public void close() throws StreamException;
}
