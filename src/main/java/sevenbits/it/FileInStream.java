package sevenbits.it;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    public FileInStream(String str) throws StreamException {
        try {
            fileInputStream = new FileInputStream(str);
        }
        catch(FileNotFoundException ex){
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public boolean isEnd() throws StreamException{
        try {
            return fileInputStream.available() < 1;
        }
        catch(IOException ex)            {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public char readSymbol() throws StreamException{
        try {
            return (char)fileInputStream.read();
        }
        catch(IOException ex)            {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public void close() throws StreamException{
        try {
            fileInputStream.close();
        }
        catch(IOException ex){
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

}
