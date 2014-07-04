package sevenbits.it.Streams;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Used to represent file as output stream
 */

public class FileOutStream implements OutStream {

    FileOutputStream fileOutputStream;

    /**
     *
     * @param str - Name of file which will be opened as stream
     * @throws StreamException - if file is not available or corrupted
     */

    public FileOutStream(String str) throws StreamException {
        try {
            fileOutputStream = new FileOutputStream(str);
        }
        catch(FileNotFoundException ex){
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public void writeSymbol(char b) throws StreamException{
        try {
            fileOutputStream.write(b);
        }
        catch(IOException ex){
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }
    public void writeString(String str) throws StreamException{
        for(int i = 0; i < str.length(); i++)
            try {
                fileOutputStream.write(str.charAt(i));
            }
            catch(IOException ex){
                throw new StreamException(ex.getMessage(), ex.getCause());
            }
    }
    public void close() throws StreamException{
        try {
            fileOutputStream.close();
        }
        catch(IOException ex){
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }
}
