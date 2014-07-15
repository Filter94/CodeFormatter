package it.sevenbits.streams;

/**
 * Represents String as a Stream
 */

public class StringInStream implements InStream {
    private final String inputString;
    private int index;

    /**
     * Creates stream from string
     * @param str - string that will be represented as stream
     */

    public StringInStream(final String str) {
        inputString = str;
        index = 0;
    }

    public boolean isEnd() throws StreamException {
        return index >= inputString.length();
    }

    public char readSymbol() throws StreamException {
        try {
            index++;
            return inputString.charAt(index - 1);
        } catch (StringIndexOutOfBoundsException ex) {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public void close() {
        index = inputString.length();
    }

}
