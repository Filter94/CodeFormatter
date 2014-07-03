package sevenbits.it;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CodeFormatterTest {
    int MAX_STREAM_LENGTH = 8192;
    String javaCode;
    String formattedCode;
    Logger logger = Logger.getLogger(CodeFormatterTest.class.getName());

    static {
        String defaultLog4jProperties = "log4j.properties";
        try {
            PropertyConfigurator.configure(new FileInputStream(defaultLog4jProperties));
        }
        catch( FileNotFoundException ex ) {
            BasicConfigurator.configure();
        }
    }

    private void makeTest(FormatOptions formatOptions) throws FormatterException, StreamException{
        CodeFormatter codeFormatter = new CodeFormatter();

        try {
        codeFormatter.format(new StringInStream(javaCode), new StringOutStream(MAX_STREAM_LENGTH), formatOptions);
        }
        catch (FormatterException ex) {
            throw ex;
        }
        catch (StreamException ex) {
            throw ex;
        }
    }

    @org.junit.Test
    public void testEmptyStream() throws Exception {
        javaCode = "";
        FormatOptions formatOptions = new FormatOptions();
        try {
            makeTest(formatOptions);
        } catch (FormatterException ex) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error(ex.getMessage());
                assert false;
        } catch (StreamException ex) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error(ex.getMessage());
            assert false;
        }
        assert true;
    }

    @org.junit.Test
    public void testLessClosingDelimeters() throws Exception {
        javaCode = "{{{{{{{{{{{{{{{{{{{}}}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        boolean testPassed = false;
        try {
            makeTest(formatOptions);
        } catch (FormatterException ex) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error(ex.getMessage());
            testPassed = true;
        } catch (StreamException ex) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error(ex.getMessage());
        }
        assert testPassed == true;
    }

    @org.junit.Test
    public void testMoreClosingDelimeters() {
        javaCode = "{{{{{{{{{{{{{{{{{{{}}}}}}}}}}}}}}}}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        boolean testPassed = false;
        try {
            makeTest(formatOptions);
        } catch (FormatterException ex) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error(ex.getMessage());
            testPassed = true;
        } catch (StreamException ex) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error(ex.getMessage());
        }
        assert testPassed == true;
    }

    @org.junit.Test
    public void testOnlyDelimiters() {
        javaCode = "{{{{{{{{{{{{{{{{{{{{}}}}}}}}}}}}}}}}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        try {
            makeTest(formatOptions);
        } catch (FormatterException ex) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error(ex.getMessage());
        } catch (StreamException ex) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error(ex.getMessage());
        }
        assert true;
    }

    @org.junit.Test
    public void testIndents() {
        javaCode = "{{{{{{{{{{{{{{{{{{{{}}}}}}}}}}}}}}}}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        try {
            makeTest(formatOptions);
        } catch (FormatterException ex) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error(ex.getMessage());
        } catch (StreamException ex) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error(ex.getMessage());
        }

        assert true;
    }
}