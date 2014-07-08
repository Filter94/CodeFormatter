package sevenbits.it.Streams;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Used to represent file as input stream
 */

public class FileInStream implements InStream {

    private ObjectInputStream objectInputStream;

    /**
     * @param str - Name of file which will be opened as stream
     * @throws StreamException if file is not available or corrupted
     */

    public FileInStream(String str) throws StreamException {
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(str));
        }
        catch(IOException ex){
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public boolean isEnd() throws StreamException{
        try {
            return objectInputStream.available() < 1;
        }
        catch(IOException ex)            {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public char readSymbol() throws StreamException{
        try {
            return objectInputStream.readChar();
        }
        catch(IOException ex)            {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public void close() throws StreamException{
        try {
            objectInputStream.close();
        }
        catch(IOException ex){
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

}
