package sevenbits.it.CodeFormatter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Used to configure formatter
 */

public class FormatOptions {
    int defaultIndentLength = 4;
    Character defaultIndentChar = ' ';
    int indentLength;
    Character indentChar;

    /**
     * Initializes options with default values
     */

    public FormatOptions(){
        setDefaultParams();
    }

    /**
     * Initializes options from config file name
     * @param configName nave of config file
     */

    public FormatOptions(String configName){
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configName));
            indentChar = properties.getProperty("indentChar").charAt(1);
            indentLength = Integer.parseInt(properties.getProperty("indentLength"));
        }
        catch (FileNotFoundException ex) {
            System.out.print("File not found.");
            setDefaultParams();
        }
        catch (IOException ex) {
            System.out.print("File not found.");
            setDefaultParams();
        }
    }

    /**
     * Initializes options from config file name
     * @return quantity of indents in options
     */

    public int getIndentSize(){
        return indentLength;
    }

    /**
     * Initializes options from config file name
     * @return indent symbol in options
     */

    public Character getIndent(){
        return indentChar;
    }

    /**
     * Sets default values for options
     */

    private void setDefaultParams(){
        indentLength = defaultIndentLength;
        indentChar = defaultIndentChar;
    }
}
