package it.sevenbits.formatter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import it.sevenbits.streams.InStream;
import it.sevenbits.streams.OutStream;
import it.sevenbits.streams.StreamException;

import java.util.Set;
import java.util.TreeSet;

/**
 * Formates at least compiled java code
 */

public class CodeFormatter {
    private final Logger logger = Logger.getLogger(CodeFormatter.class.getName());
    private static final int DEFAULT_INDENT_LENGTH = 4;
    private static final Character DEFAULT_INDENT_CHAR = ' ';

    private static final Set<Character> OPERATIONS = new TreeSet();
    static {
        OPERATIONS.add('+');
        OPERATIONS.add('-');
        OPERATIONS.add('=');
        OPERATIONS.add('*');
        OPERATIONS.add('&');
        OPERATIONS.add('|');
        OPERATIONS.add('!');
    }

    private static final int MODE_REGULAR = 0;
    private static final int MODE_DOUBLE_SLASH = 1;   // // - comment
    private static final int MODE_SLASH_ASTERISK = 2;
    private static final int MODE_STRING = 3;
    private static final int MODE_CHAR = 4;
    private static final int MODE_BACKSLASH = 5;

    /**
     * @param inStream - stream with unformatted java code
     * @param outStream - stream where formatted java code will be written
     * @param options - contains options for formatter
     * @throws FormatterException  - if there is delimiters mismatch
     */

    public void format(final InStream inStream, final OutStream outStream,
        final FormatOptions options) throws FormatterException {
        int indentLength = options.getIndentSize();
        if (indentLength == 0) {
            indentLength = DEFAULT_INDENT_LENGTH;
        }
        Character indentChar = options.getIndent();
        if (indentChar == null) {
            indentChar = DEFAULT_INDENT_CHAR;
        }
        Character currentChar = 'a';
        Character previousChar;
        int nestingLevel = 0;
        int mode = 0;
        boolean endOfStream;
        boolean newLine = false;
        try {
            endOfStream = inStream.isEnd();
            while (!endOfStream) {
                if (logger.isEnabledFor(Level.DEBUG)) {
                    logger.debug("New symbol.");
                }
                previousChar = currentChar;
                currentChar = inStream.readSymbol();
                switch (mode) {
                    case MODE_REGULAR:
                        if (previousChar == '/' && (currentChar != '/' && currentChar != '*')) {
                            outStream.writeSymbol(' ');
                            previousChar = ' ';
                        }
                        if (previousChar == '\n') {
                            newLine = true;
                        }
                        if (newLine && currentChar != ' ' && currentChar != '\n') {
                            int indents = nestingLevel - 1;
                            if (!OPERATIONS.contains(currentChar)) {
                                writeNIndents(outStream, indents, indentLength, indentChar);
                            }
                            if (currentChar != '}') {
                                writeNIndents(outStream, 1, indentLength, indentChar);
                            }
                            newLine = false;
                        }
                        switch (currentChar) {
                            case '\r':break;
                            case '}':
                                if (nestingLevel != 0) {
                                    nestingLevel--;
                                } else {
                                    String message = "Delimiters does not match";
                                    NotEnoughBracketsException ex = new NotEnoughBracketsException();
                                    throw new NotEnoughBracketsException(message , ex);
                                }
                                processClosingCurlyBracket(outStream, nestingLevel, inStream.isEnd());
                                currentChar = '\n';
                                break;
                            case ' ':
                                processSpaces(outStream,  previousChar);
                                break;
                            case '{':
                                nestingLevel++;
                                processOpeningCurlyBracket(outStream);
                                currentChar = '\n';
                                break;
                            case '"':
                            case '\'':
                            case '\\':
                                mode = processCommentSymbol(outStream, currentChar);
                                break;
                            case '/':
                                mode = processSlash(previousChar, currentChar, outStream);
                                currentChar = ' ';
                                break;
                            case '*':
                                mode = processAsterisk(previousChar, currentChar, outStream);
                                currentChar = ' ';
                                break;
                            case '=':
                            case '-':
                            case '&':
                            case '|':
                            case '!':
                            case '+':
                                processOperation(previousChar, currentChar, outStream);
                                currentChar = ' ';
                                break;
                            case '(':
                                processOpeningBracket(previousChar, outStream);
                                break;
                            case ')':
                                processClosingBracket(outStream);
                                break;
                            case ';':
                                processSemicolon(outStream);
                                currentChar = '\n';
                                break;
                            default:
                                processSymbol(outStream, currentChar);
                                break;
                        }
                        break;
                    case MODE_DOUBLE_SLASH:
                        if (currentChar == '\n') {
                            if (logger.isEnabledFor(Level.DEBUG)) {
                                logger.debug("// comment ends");
                            }
                            mode = MODE_REGULAR;
                        }
                    case MODE_SLASH_ASTERISK:
                        if (currentChar == '/' && previousChar == '*'  && mode == MODE_SLASH_ASTERISK) {
                            if (logger.isEnabledFor(Level.DEBUG)) {
                                logger.debug("/* comment ends");
                            }
                            mode = MODE_REGULAR;
                        }
                    case MODE_STRING:
                        if (currentChar == '"' && mode == MODE_STRING) {
                            if (logger.isEnabledFor(Level.DEBUG)) {
                                logger.debug("string ends");
                            }
                            mode = MODE_REGULAR;
                        }
                    case MODE_CHAR:
                        if (mode == MODE_CHAR && (currentChar == '\'')) {
                            if (logger.isEnabledFor(Level.DEBUG)) {
                                logger.debug("symbol ends");
                            }
                            mode = MODE_REGULAR;
                        }
                    case MODE_BACKSLASH:
                        if (mode == MODE_BACKSLASH) {
                            if (logger.isEnabledFor(Level.DEBUG)) {
                                logger.debug("backslash");
                            }
                            mode = MODE_REGULAR;
                        }
                    default:
                        outStream.writeSymbol(currentChar);
                        if (mode == MODE_BACKSLASH) {
                            currentChar = 'a';  // annuls meaning of current symbol
                        }
                        break;
                    }
                if (logger.isEnabledFor(Level.DEBUG)) {
                    logger.debug("Symbol parsed.");
                }
                endOfStream = inStream.isEnd();
            }
        } catch (StreamException ex) {
            throw new FormatterException(ex);
        }
        if (nestingLevel != 0) {
            throw new NotEnoughBracketsException("Delimiters does not match");
        }
    }

    int processAsterisk(final Character previousChar, final Character currentChar, final OutStream outStream) throws StreamException {
        int mode = MODE_REGULAR;
        String buffer = "";
        if (previousChar == '/') {
            mode = MODE_SLASH_ASTERISK;
        } else {
            if (previousChar != ' ') {
                buffer += " ";
            }
        }
        buffer += currentChar;
        if (mode == MODE_REGULAR) {
            buffer += " ";
        }
        writeString(outStream, buffer);
        return mode;
    }

    /**
     * Writes string into stream
     * @param outStream - OutStream to which the String will be written
     * @param str - String that will be written into stream
     * @throws it.sevenbits.streams.StreamException - throws when OutStream is not available or corrupted
     */

    private void writeString(final OutStream outStream, final String str) throws StreamException {
            for (int i = 0; i < str.length(); i++) {
                outStream.writeSymbol(str.charAt(i));
            }
    }

    void processOperation(final Character previousChar, final  Character currentChar, final  OutStream outStream)  throws StreamException {
        String buffer = "";
        if (previousChar != ' ') {
            buffer += " ";
        }
        buffer += currentChar + " ";
        writeString(outStream,  buffer);
    }

    int processSlash(final Character previousChar, final  Character currentChar, final  OutStream outStream) throws StreamException {
        String buffer = "";
        int mode = MODE_REGULAR;
        if (previousChar != '/') {
            if (previousChar != ' ') {
                buffer += ' ';
            }
        } else {
            mode = MODE_DOUBLE_SLASH;
        }
        buffer += currentChar;
        writeString(outStream, buffer);
        return mode;
    }

    private void processOpeningBracket(final Character previousChar, final OutStream outStream) throws StreamException {
        String buffer;
        if (previousChar != ' ') {
            buffer = " (";
        } else {
            buffer = "(";
        }
        writeString(outStream, buffer);
    }

    private void processClosingBracket(final OutStream outStream) throws StreamException {
        writeString(outStream,  ") ");
    }

    private void processSemicolon(final OutStream outStream) throws StreamException {
        writeString(outStream,  ";\n");
    }

    private void processSymbol(final OutStream outStream, final Character currentChar) throws StreamException {
        if (currentChar != ' ' && currentChar != '\n') {
            outStream.writeSymbol(currentChar);
        }
    }

    private void processOpeningCurlyBracket(final OutStream outStream) throws StreamException {
        writeString(outStream, "{\n");
    }

    private void processClosingCurlyBracket(final OutStream outStream,
    final int nestingLevel, final boolean endOfStream) throws StreamException {
        String buffer;
        buffer = "}\n";
        if (nestingLevel == 0 && !endOfStream) {
            buffer += '\n';
        }
        writeString(outStream, buffer);
    }

    private void processSpaces(final OutStream outStream, final Character previousChar) throws StreamException {
        if (previousChar != ' ' && previousChar != '\n') {
            outStream.writeSymbol(' ');
        }
    }

    private int processCommentSymbol(final OutStream outStream, final Character currentChar) throws StreamException {
        outStream.writeSymbol(currentChar);
        switch (currentChar) {
            case '"':
                return MODE_STRING;
            case '\'':
                return MODE_CHAR;
            case '\\':
                return MODE_BACKSLASH;
            default: return MODE_REGULAR;
        }
    }

    private void writeNIndents(final OutStream outStream, final int n, final int indentLength,
    final Character indentChar)throws StreamException {
        String buffer = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < indentLength; j++) {
                buffer += indentChar;
            }
        }
        writeString(outStream, buffer);
    }
}

