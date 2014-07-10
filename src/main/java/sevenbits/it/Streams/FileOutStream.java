package sevenbits.it.Streams;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Used to represent file as output stream
 */

public class FileOutStream implements OutStream {

    private FileOutputStream fileOutputStream;

    /**
     *
     * @param str - Name of file which will be opened as stream
     * @throws StreamException - if file is not available or corrupted
     */

    public FileOutStream(final String str) throws StreamException {
        try {
            fileOutputStream = new FileOutputStream(str);
        } catch (final FileNotFoundException ex) {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public void writeSymbol(final char b) throws StreamException {
        try {
            fileOutputStream.write(b);
        } catch (IOException ex) {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public void close() throws StreamException {
        try {
            fileOutputStream.close();
        } catch (IOException ex) {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }
}
