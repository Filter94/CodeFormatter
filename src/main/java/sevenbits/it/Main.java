package sevenbits.it;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import sevenbits.it.CodeFormatter.CodeFormatter;
import sevenbits.it.CodeFormatter.FormatOptions;
import sevenbits.it.CodeFormatter.FormatterException;
import sevenbits.it.Streams.FileInStream;
import sevenbits.it.Streams.FileOutStream;
import sevenbits.it.Streams.StreamException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Formates compiled java code
 */

public class  Main{
    static Logger logger = Logger.getLogger(Main.class.getName());
    /**
     * Formates java code in given file and writes it into another
     * @param args - args[0] - name of file with unformatted java code; args[1] - name of file to which will be written formatted java code
     */

    public static void main(String[] args) {

        String defaultLog4jProperties = "log4j.properties";
        try {
            PropertyConfigurator.configure(new FileInputStream(defaultLog4jProperties));
        }
        catch(FileNotFoundException ex){
            BasicConfigurator.configure();
            if (logger.isEnabledFor(Level.WARN))
                logger.warn("Loaded default logger options.");
        }
        if (args.length > 1) {
            FileInStream fis;
            FileOutStream fos;
            String defaultFormatterProperties = "formatter.properties";
            CodeFormatter codeFormatter = new CodeFormatter();
            FormatOptions formatOptions = new FormatOptions(defaultFormatterProperties);
            try {
                fis = new FileInStream(args[0]);
                fos = new FileOutStream(args[1]);
                codeFormatter.format(fis, fos, formatOptions);
            } catch (FormatterException ex) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error(ex.getMessage());
                }
            } catch (StreamException ex) {
                if (logger.isEnabledFor(Level.ERROR))
                    logger.error(ex.getMessage());
            }
        }
        else{
            logger.error("Parameters: input_file_path output_file_path");
        }
    }
}
