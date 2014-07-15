package it.sevenbits.streams;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Used to represent file as input stream
 */

public class FileInStream implements InStream {

    private FileInputStream fileInputStream;

    /**
     * @param str - Name of file which will be opened as stream
     * @throws StreamException if file is not available or corrupted
     */

    public FileInStream(final String str) throws StreamException {
        try {
            fileInputStream = new FileInputStream(str);
        } catch (IOException ex) {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public boolean isEnd() throws StreamException {
        try {
            return fileInputStream.available() < 1;
        } catch (IOException ex) {
            throw new StreamException(ex);
        }
    }

    public char readSymbol() throws StreamException {
        try {
            return (char) fileInputStream.read();
        } catch (IOException ex) {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public void close() throws StreamException {
        try {
            fileInputStream.close();
        } catch (IOException ex) {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

}
