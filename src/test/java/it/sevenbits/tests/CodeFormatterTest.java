package it.sevenbits.tests;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import it.sevenbits.codeformatter.CodeFormatter;
import it.sevenbits.codeformatter.FormatOptions;
import it.sevenbits.codeformatter.FormatterException;
import it.sevenbits.streams.StreamException;
import it.sevenbits.streams.StringInStream;
import it.sevenbits.streams.StringOutStream;

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

    @org.junit.Test(expected = FormatterException.class)
    public void testLessClosingDelimiters() throws Exception {
        javaCode = "{{{{{{{{{{{{{{{{{{{}}}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        makeTest(formatOptions);
    }

    @org.junit.Test(expected = FormatterException.class)
    public void testMoreClosingDelimiters()  throws Exception {
        javaCode = "{{{{{{{{{{{{{{{{{{{}}}}}}}}}}}}}}}}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        makeTest(formatOptions);
    }

    @org.junit.Test
    public void testOnlyDelimiters() throws Exception {
        javaCode = "{{{{{{{{{{{{{{{{{{{{}}}}}}}}}}}}}}}}}}}}";
        FormatOptions formatOptions = new FormatOptions();
        makeTest(formatOptions);
    }

    @org.junit.Test
    public void testUselessSpaces() throws Exception {
        javaCode = "{{{               a+b=3;}   }}";
        String expectedString = "{\n    {\n        {\n            a + b = 3;\n        }\n    }\n}\n";
        FormatOptions formatOptions = new FormatOptions();
        makeTest(formatOptions);
        System.out.println("x" + formattedCode + "x");
        assert expectedString.equals(formattedCode);
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
                assert j == currentString.length() - 1;
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
                assert j == currentString.length() - 1;
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
                assert j == currentString.length() - 1;
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