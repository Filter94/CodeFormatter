package sevenbits.it.Streams;
import java.io.IOException;
import java.io.StringReader;

/**
 * Represents String as a Stream
 */

public class StringInStream implements InStream{
    private StringReader stringReader;

    /**
     * Creates stream from string
     * @param str - string that will be represented as stream
     */

    public StringInStream(String str){
        stringReader = new StringReader(str);
    }

    public boolean isEnd() throws StreamException{
        try {
            stringReader.mark(1);
            Character c = (char)stringReader.read();
            stringReader.reset();
            return c == (char)-1;
        }
        catch(IOException ex) {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public char readSymbol() throws StreamException{
        try {
            return (char)stringReader.read();
        }
        catch(IOException ex) {
            throw new StreamException(ex.getMessage(), ex.getCause());
        }
    }

    public void close(){
            stringReader.close();
    }

}
