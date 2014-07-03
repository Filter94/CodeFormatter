package sevenbits.it;

/**
 * Simple output stream
 */

public interface OutStream {

    /**
     * Writes char into stream
     * @param b char which will be written into stream
     * @throws StreamException - if stream is not available
     */
    void writeSymbol(char b) throws StreamException;

    /**
     * Closes stream
     * @throws StreamException - if stream is not available
     */

    public void close() throws StreamException;
}

