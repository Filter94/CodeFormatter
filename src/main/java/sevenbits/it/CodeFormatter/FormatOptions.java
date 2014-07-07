package sevenbits.it.CodeFormatter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Used to configure formatter
 */

public class FormatOptions {
    Logger logger = Logger.getLogger(FormatOptions.class.getName());
    static int DEFAULT_INDENT_LENGTH = 4;
    static Character DEFAULT_INDENT_CHAR = ' ';
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
            if (logger.isEnabledFor(Level.WARN))
                logger.warn(ex.getMessage());
            setDefaultParams();
        }
        catch (IOException ex) {
            if (logger.isEnabledFor(Level.WARN))
                logger.warn(ex.getMessage());
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
        indentLength = DEFAULT_INDENT_LENGTH;
        indentChar = DEFAULT_INDENT_CHAR;
        if (logger.isEnabledFor(Level.WARN))
            logger.warn("Default parameters for formatter.");
    }
}
