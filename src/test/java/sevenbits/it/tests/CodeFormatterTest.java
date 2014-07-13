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
import java.util.Set;
import java.util.TreeSet;

/**
 * Tests formatter
 */

public class CodeFormatterTest {
    private static final int MAX_STREAM_LENGTH = 8192;
    private String javaCode;
    private String formattedCode;
    private final Logger logger = Logger.getLogger(CodeFormatterTest.class.getName());

    static {
        String defaultLog4jProperties = "log4j.properties";
        try {
            PropertyConfigurator.configure(new FileInputStream(defaultLog4jProperties));
        } catch (FileNotFoundException ex) {
            BasicConfigurator.configure();
        }
    }

    private void makeTest(final FormatOptions formatOptions) throws StreamException, FormatterException {
        CodeFormatter codeFormatter = new CodeFormatter();

        StringOutStream stringOutStream = new StringOutStream(MAX_STREAM_LENGTH);
        codeFormatter.format(new StringInStream(javaCode), stringOutStream, formatOptions);
        formattedCode = stringOutStream.toString();
    }

    @org.junit.Test
    public void testEmptyStream() throws Exception {
        javaCode = "";
        FormatOptions formatOptions = new FormatOptions();
        try {
            makeTest(formatOptions);
        } catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ex.getMessage());
            }
            assert false;
        }
        assert true;
    }

    @org.junit.Test
    public void testLessClosingDelimiters() throws Exception {
        javaCode = "{{{{{{{{{{{{{{{{{{{}}}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        boolean testPassed = false;
        try {
            makeTest(formatOptions);
        } catch (FormatterException ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ex.getMessage());
            }
            testPassed = true;
        } catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ex.getMessage());
            }
        }
        assert testPassed;
    }

    @org.junit.Test
    public void testMoreClosingDelimiters() {
        javaCode = "{{{{{{{{{{{{{{{{{{{}}}}}}}}}}}}}}}}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        boolean testPassed = false;
        try {
            makeTest(formatOptions);
        } catch (FormatterException ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ex.getMessage());
            }
            testPassed = true;
        } catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ex.getMessage());
            }
        }
        assert testPassed;
    }

    @org.junit.Test
    public void testOnlyDelimiters() {
        javaCode = "{{{{{{{{{{{{{{{{{{{{}}}}}}}}}}}}}}}}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        try {
            makeTest(formatOptions);
        } catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ex.getMessage());
            }
            assert false;
        }
        assert true;
    }

    @org.junit.Test
    public void testIndents() {
        javaCode = "{{{ adasd;{sadadasda;{if(a){dasdasdasd;}{{{{{{{{{}}{{asdadsadda;{{{{}}dadasdasdasd;}}}}{{}}}}}}dadadasdad;}}}}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        try {
            makeTest(formatOptions);
        } catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ex.getMessage());
            }
            assert false;
        }
        int nestingLevel = 0;
        String buffer;
        String rughtfullyInedentent;
        String[] strings = formattedCode.split("\n");
        boolean firstInLine;
        for (String currentString : strings) {
            Character currentChar;
            int j;
            firstInLine = true;
            for (j = 0; j < currentString.length(); j++) {
                currentChar = currentString.charAt(j);
                if (firstInLine && currentChar != ' ') {
                    rughtfullyInedentent = "";
                    for (int g = 0; g < nestingLevel - 1; g++) {
                        rughtfullyInedentent += "    ";
                    }
                    if (currentChar != '}' && nestingLevel > 0) {
                        rughtfullyInedentent += "    ";
                    }
                    rughtfullyInedentent += currentChar;
                    buffer = currentString.substring(0, rughtfullyInedentent.length());
                    assert rughtfullyInedentent.equals(buffer);
                    firstInLine = false;
                }
                if (currentChar == '{') {
                    nestingLevel++;
                }
                if (currentChar == '}') {
                    nestingLevel--;
                }
            }
        }
        if (nestingLevel != 0) {
            assert false;
        }
    }

    @org.junit.Test
    public void testLineFeedAfterOpening() {
        javaCode = "{{{ adasd;{sadadasda;{if(a){dasdasdasd;}{{{{{{{{{}}{{asdadsadda;{{{{}}dadasdasdasd;}}}}{{}}}}}}dadadasdad;}}}}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        try {
            makeTest(formatOptions);
        } catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ex.getMessage());
            }
            assert false;
        }
        String[] strings = formattedCode.split("\n");
        for (String currentString : strings) {
            Character currentChar = '\n';
            int j;
            for (j = 0; j < currentString.length(); j++) {
                if (currentString.charAt(j) == '{') {
                    currentChar = currentString.charAt(j);
                    break;
                }
            }
            if (currentChar == '{') {
                try {
                    currentString.charAt(j + 1);
                } catch (Exception ex) {
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
        } catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ex.getMessage());
            }
            assert false;
        }
        String[] strings = formattedCode.split("\n");
        for (String currentString : strings) {
            Character currentChar = '\n';
            int j;
            for (j = 0; j < currentString.length(); j++) {
                if (currentString.charAt(j) == '}') {
                    currentChar = currentString.charAt(j);
                    break;
                }
            }
            if (currentChar == '}') {
                try {
                    currentString.charAt(j + 1);
                } catch (Exception ex) {
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
        } catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ex.getMessage());
            }
            assert false;
        }
        String[] strings = formattedCode.split("\n");
        for (String currentString : strings) {
            Character currentChar = '\n';
            int j;
            for (j = 0; j < currentString.length(); j++) {
                if (currentString.charAt(j) == ';') {
                    currentChar = currentString.charAt(j);
                    break;
                }
            }
            if (currentChar == ';') {
                try {
                    currentString.charAt(j + 1);
                } catch (Exception ex) {
                    assert true;
                }
            }
        }
    }

    @org.junit.Test
    public void testSpacesAroundOperations() {
        javaCode = "{{{{a+b=3;{{{{{{+++{{{{&&}}{{{int a=3-f;{{{!}||}}}}**a}{{}}}---}}}!!}}}/}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        try {
            makeTest(formatOptions);
        } catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ex.getMessage());
            }
            assert false;
        }
        String[] strings = formattedCode.split("\n");
        Set<Character> operations = new TreeSet<>();
        operations.add('+');
        operations.add('-');
        operations.add('=');
        operations.add('*');
        operations.add('&');
        operations.add('|');
        operations.add('!');
        for (String currentString : strings) {
            int j;
            for (j = 0; j < currentString.length(); j++) {
                if (operations.contains(currentString.charAt(j))) {
                    assert (currentString.charAt(j - 1) == ' ' && currentString.charAt(j + 1) == ' ');
                    assert currentString.charAt(j + 2) != ' ';
                }
            }
        }
    }
}