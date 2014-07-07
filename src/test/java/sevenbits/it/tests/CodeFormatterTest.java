package sevenbits.it.tests;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import sevenbits.it.CodeFormatter.CodeFormatter;
import sevenbits.it.CodeFormatter.FormatOptions;
import sevenbits.it.CodeFormatter.FormatterException;
import sevenbits.it.Streams.StreamException;
import sevenbits.it.Streams.StringInStream;
import sevenbits.it.Streams.StringOutStream;

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

    private void makeTest(FormatOptions formatOptions) throws FormatterException, StreamException {
        CodeFormatter codeFormatter = new CodeFormatter();

        try {
            StringOutStream stringOutStream = new StringOutStream(MAX_STREAM_LENGTH);
            codeFormatter.format(new StringInStream(javaCode),stringOutStream, formatOptions);
            formattedCode = stringOutStream.toString();
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
        javaCode = "{{{ adasd;{sadadasda;{if(a){dasdasdasd;}{{{{{{{{{}}{{asdadsadda;{{{{}}dadasdasdasd;}}}}{{}}}}}}dadadasdad;}}}}}}}}";
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
        int nestingLevel = 0;
        String buffer = "";
        String rughtfullyInedentent = "";
        String [] strings = formattedCode.split("\n");
        boolean firstInLine = true;
        for (int i = 0; i < strings.length; i++){
            Character currentChar = '\n';
            int j;
            firstInLine = true;
            for(j = 0; j < strings[i].length() ; j++) {
                if (currentChar != '\n') {
                    if(firstInLine) {
                        rughtfullyInedentent = "";
                        int tabs = nestingLevel;
                        if (currentChar != '}')
                            for (int g = 0; g < tabs; g++)
                                rughtfullyInedentent += "    ";
                        else
                            for (int g = 0; g < tabs - 1; g++)
                                rughtfullyInedentent += "    ";
                        rughtfullyInedentent += currentChar;
                        buffer = strings[i].substring(0, rughtfullyInedentent.length());
                        assert rughtfullyInedentent.equals(buffer);
                        firstInLine = false;
                    }
                    if(currentChar == '{')
                        nestingLevel ++;
                    if(currentChar == '}')
                        nestingLevel --;
                }
            }
        }
    }

    @org.junit.Test
    public void testLineFeedAfterOpening() {
        javaCode = "{{{ adasd;{sadadasda;{if(a){dasdasdasd;}{{{{{{{{{}}{{asdadsadda;{{{{}}dadasdasdasd;}}}}{{}}}}}}dadadasdad;}}}}}}}}";
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
        String [] strings = formattedCode.split("\n");
        for (int i = 0; i < strings.length; i++){
            Character currentChar = '\n';
            int j;
            for(j = 0; j < strings[i].length() ; j++){
                if(strings[i].charAt(j) == '{') {
                    currentChar = strings[i].charAt(j);
                    break;
                }
            }
            if (currentChar == '{') {
                try {
                    strings[i].charAt(j + 1);
                }
                catch(Exception ex){
                    assert true;
                }
            }
        }
    }

    @org.junit.Test
    public void testLineFeedAfterClosing() {
        javaCode = "{{{{{{{{{{{{{{}}{{{{{{}}}}}}{{}}}}}}}}}}}}}}";
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
        String [] strings = formattedCode.split("\n");
        for (int i = 0; i < strings.length; i++){
            Character currentChar = '\n';
            int j;
            for(j = 0; j < strings[i].length() ; j++){
                if(strings[i].charAt(j) == '}') {
                    currentChar = strings[i].charAt(j);
                    break;
                }
            }
            if (currentChar == '}') {
                try {
                    strings[i].charAt(j + 1);
                }
                catch(Exception ex){
                    assert true;
                }
            }
        }
    }

    @org.junit.Test
    public void testSemicolons() {
        javaCode = "{{{{{{{{{{{{{{}}{{{{{{}}}}}}{{}}}}}}}}}}}}}}";
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
        String [] strings = formattedCode.split("\n");
        for (int i = 0; i < strings.length; i++){
            Character currentChar = '\n';
            int j;
            for(j = 0; j < strings[i].length() ; j++){
                if(strings[i].charAt(j) == ';') {
                    currentChar = strings[i].charAt(j);
                    break;
                }
            }
            if (currentChar == ';') {
                try {
                    strings[i].charAt(j + 1);
                }
                catch(Exception ex){
                    assert true;
                }
            }
        }
    }

}