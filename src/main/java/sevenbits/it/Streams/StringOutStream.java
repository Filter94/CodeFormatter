package sevenbits.it.Streams;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Provides string output stream
 */

public class StringOutStream implements OutStream {
    StringWriter stringWriter;

    /**
     * Creates stream on String with given size
     * @param size - size of String that will be stream
     */

    public StringOutStream(int size){
        stringWriter = new StringWriter(size);
    }

    /**
     * Represents stream as a String
     * @return - String which
     */

    public String toString(){
        return stringWriter.toString();
    }

    public void writeSymbol(char b) throws StreamException{
            stringWriter.write(b);
    }

    public void writeString(String str) throws StreamException{
        for(int i = 0; i < str.length(); i++)
            stringWriter.write(str.charAt(i));
    }

    public void close() throws StreamException{
        try {
            stringWriter.close();
        }
        catch(IOException ex){
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

}
