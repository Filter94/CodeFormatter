package it.sevenbits;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import it.sevenbits.CodeFormatter.CodeFormatter;
import it.sevenbits.CodeFormatter.FormatOptions;
import it.sevenbits.Streams.FileInStream;
import it.sevenbits.Streams.FileOutStream;

/**
 * Formates compiled java code
 */

final class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String DEFAULT_FORMATTER_PROPERTIES = "formatter.properties";

    private Main(){}

    /**
     * Formates java code in given file and writes sevenbits into another
     * @param args - args[0] - name of file with unformatted java code; args[1] - name of file to which will be written formatted java code
     */

    public static void main(final String[] args) {
        if (args.length > 1) {
            FileInStream fis;
            FileOutStream fos;
            CodeFormatter codeFormatter = new CodeFormatter();
            FormatOptions formatOptions = new FormatOptions(DEFAULT_FORMATTER_PROPERTIES);
            try {
                fis = new FileInStream(args[0]);
                fos = new FileOutStream(args[1]);
                codeFormatter.format(fis, fos, formatOptions);
            } catch (Exception ex) {
                if (LOGGER.isEnabledFor(Level.ERROR)) {
                    LOGGER.error(ex.getMessage());
                }
                assert false;
            }
        } else {
            LOGGER.error("Parameters: input_file_path output_file_path");
        }
    }
}
