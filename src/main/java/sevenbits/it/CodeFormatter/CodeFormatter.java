package sevenbits.it.CodeFormatter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import sevenbits.it.Streams.InStream;
import sevenbits.it.Streams.OutStream;
import sevenbits.it.Streams.StreamException;

import java.util.Set;
import java.util.TreeSet;

/**
 * Formates at least compiled java code
 */

public class CodeFormatter {
    Logger logger = Logger.getLogger(CodeFormatter.class.getName());
    int indentLength;
    Character indentChar;

    private final static int MODE_REGULAR = 0;
    private final static int MODE_DOUBLE_SLASH = 1;   // // - comment
    private final static int MODE_SLASH_ASTERISK = 2;
    private final static int MODE_STRING = 3;
    private final static int MODE_CHAR = 4;
    private final static int MODE_BACKSLASH = 5;

    /**
     * @param inStream - stream with unformatted java code
     * @param outStream - stream where formatted java code will be written
     * @param options - contains options for formatter
     * @throws FormatterException  - if there is delimiters mismatch
     * @throws StreamException - if there is problems with streams
     */

    public void format(InStream inStream, OutStream outStream, FormatOptions options) throws FormatterException, StreamException {
        indentLength = options.getIndentSize();
        indentChar = options.getIndent();

        String fileString = "";
        Character currentChar = 'a';
        Character previousChar;
        Set<Character> operations = new TreeSet<>();
        operations.add('+');
        operations.add('-');
        operations.add('=');
        operations.add('/');
        operations.add('*');
        operations.add('&');
        operations.add('|');
        operations.add('!');
        String buffer;
        int nestingLevel = 0;
        boolean thereWasOperation = false;
        boolean waitForClosingCurlyBracket = false;
        int mode = 0;
        boolean endOfStream = true;
        try{
            endOfStream = inStream.isEnd();
        } catch(StreamException ex){
            if (logger.isEnabledFor(Level.FATAL))
                logger.fatal(ex.getMessage());
        }
        while(!endOfStream){
            if (logger.isEnabledFor(Level.DEBUG))
                logger.debug("New symbol.");
            buffer = "";
            try{
                currentChar = inStream.readSymbol();
            } catch(StreamException ex){
                if (logger.isEnabledFor(Level.FATAL))
                    logger.fatal(ex.getMessage());
            }
            previousChar = fileString.charAt(fileString.length() - 1);
            switch (mode) {
                case MODE_REGULAR: {
                if (previousChar == '\n' && currentChar != ' ') {
                    int tabs = nestingLevel - 1;
                    waitForClosingCurlyBracket = true;
                    if(!operations.contains(currentChar)) {
                        for (int g = 0; g < tabs; g++)
                            for (int k = 0; k < indentLength; k++)
                                buffer += indentChar;
                        fileString += buffer;
                    }
                }
                    switch (currentChar) {
                        case '\r':break;
                        case '}': {
                            processClosingCurlyBracket(outStream, nestingLevel);
                            break;
                        }
                        case ' ': {
                            processSpaces(outStream,  previousChar);
                            break;
                        }
                        case '{': {
                            processOpeningCurlyBracket(outStream);
                            break;
                        }
                        case '"':
                        case '\'':
                        case '\\': {
                            mode = processCommentSymbol(outStream, currentChar, previousChar);
                        }
                            break;
                        case '/':
                        case '*':
                            if (fileString.charAt(fileString.length() - 2) == '/') {
                                if (currentChar == '*')
                                    mode = MODE_SLASH_ASTERISK;
                                    fileString = fileString.substring(0, fileString.length() - 1);
                                if (currentChar == '/') {
                                    fileString = fileString.substring(0, fileString.length() - 3);
                                    fileString += "    /";
                                    mode = MODE_DOUBLE_SLASH;
                                }
                                fileString += currentChar;
                                break;
                            }
                        case '=':
                        case '-':
                        case '&':
                        case '|':
                        case '+': {
                            if (thereWasOperation) {
                                fileString = fileString.substring(0, fileString.length() - 1);
                            }
                            if (previousChar != ' ' && !operations.contains(previousChar))
                                buffer += " ";
                            buffer += currentChar;
                            buffer += " ";
                            fileString += buffer;
                            thereWasOperation = true;
                            break;
                        }
                        case '(': {
                            processOpeningBracket(previousChar, outStream);
                            break;
                        }
                        case ')': {
                            processClosingBracket(outStream);
                            break;
                        }
                        case ';': {
                            processSemicolon(outStream);
                            break;
                        }
                        default: {
                            if(waitForClosingCurlyBracket) {
                                writeIndent(outStream);
                                waitForClosingCurlyBracket=false;
                            }
                            processSymbol(outStream, currentChar);
                            break;
                        }

                    }
                    break;
                }
                case MODE_DOUBLE_SLASH:
                    if(currentChar == '\n') {
                        if (logger.isEnabledFor(Level.DEBUG))
                            logger.debug("// comment ends");
                        mode = MODE_REGULAR;
                    }
                case MODE_SLASH_ASTERISK:
                    if(currentChar == '/' && previousChar == '*'  && mode == 2) {
                        if (logger.isEnabledFor(Level.DEBUG))
                            logger.debug("/* comment ends");
                        mode = MODE_REGULAR;
                    }
                case MODE_STRING:
                    if(currentChar == '"' && mode == 3) {
                        if (logger.isEnabledFor(Level.DEBUG))
                            logger.debug("string ends");
                        mode = MODE_REGULAR;
                    }
                case MODE_CHAR:
                    if(mode == 4 && (currentChar == '\'')) {
                        if (logger.isEnabledFor(Level.DEBUG))
                            logger.debug("symbol ends");
                        mode = MODE_REGULAR;
                    }
                case MODE_BACKSLASH:
                    if(mode == 5) {
                        if (logger.isEnabledFor(Level.DEBUG))
                            logger.debug("backslash");
                        mode = MODE_REGULAR;
                    }
                default:
                    outStream.writeSymbol(currentChar);
                    if(mode == 5){
                        currentChar = 'a';  // annuls meaning of current symbol
                    }
                    break;
                }
            if (logger.isEnabledFor(Level.DEBUG))
                logger.debug("Symbol parsed.");
            try{
                endOfStream = inStream.isEnd();
            } catch(StreamException ex){
                if (logger.isEnabledFor(Level.FATAL))
                    logger.fatal(ex.getMessage());
            }
        }
        if(nestingLevel != 0) {
            String message = "Delimiters does not match";
            Throwable ex = new Exception(message);
            if (logger.isEnabledFor(Level.ERROR))
                logger.fatal(ex.getMessage());
            throw new NotEnoughBracketsException(ex.getMessage(), ex.getCause());
        }
        try {
            inStream.close();
            writeString(outStream, fileString.substring(1));
            outStream.close();
        }
        catch(StreamException ex){
            if (logger.isEnabledFor(Level.FATAL))
                logger.fatal(ex.getMessage());
        }
    }

    /**
     * Writes string into stream
     * @param outStream - OutStream to which the String will be written
     * @param str - String that will be written into stream
     * @throws sevenbits.it.Streams.StreamException - throws when OutStream is not available or corrupted
     */

    private void writeString(OutStream outStream, String str) throws StreamException {
        for(int i = 0; i < str.length(); i++)
            outStream.writeSymbol(str.charAt(i));
    }

    private void processOpeningBracket(Character previousChar, OutStream outStream) throws StreamException{
        String buffer;
        if (previousChar != ' ')
            buffer = " (";
        else
            buffer = "(";
        writeString(outStream, buffer);
    }

    private void processClosingBracket(OutStream outStream) throws StreamException{
        writeString(outStream,  ") ");
    }

    private void processSemicolon(OutStream outStream) throws StreamException{
        writeString(outStream,  ";\n");
    }

    private void processSymbol(OutStream outStream, Character currentChar) throws StreamException{
        if (currentChar != ' ' && currentChar != '\n') {
            outStream.writeSymbol(currentChar);
        }
    }

    private void processOpeningCurlyBracket(OutStream outStream) throws StreamException{
        writeString(outStream, "{\n");
    }

    private void processClosingCurlyBracket(OutStream outStream, int nestingLevel ) throws StreamException, NotEnoughBracketsException{
        if(nestingLevel != 0)
            nestingLevel--;
        else{
            String message = "Delimiters does not match";
            NotEnoughBracketsException ex = new NotEnoughBracketsException();
            throw new NotEnoughBracketsException(message , ex);
        }
        String buffer;
        buffer = "}\n";
        if (nestingLevel == 0) {
            buffer += '\n';
        }
        try{
            writeString(outStream,buffer);
        }
        catch(StreamException ex){
            System.out.print("log");
        }
    }

    private void processSpaces(OutStream outStream, Character previousChar) throws StreamException{
        if (previousChar != ' ' && previousChar != '\n')
            outStream.writeSymbol(' ');
    }

    private int processCommentSymbol(OutStream outStream,Character currentChar, Character previousChar) throws StreamException {
        outStream.writeSymbol(currentChar);
        if (previousChar != '\\' && previousChar != '\'')
            switch (currentChar) {
                case '"':
                    return MODE_STRING;
                case '\'':
                    return MODE_CHAR;
                case '\\':
                    return MODE_BACKSLASH;
            }
        return MODE_REGULAR;
    }

    private void writeIndent(OutStream outStream)throws StreamException{
        String buffer = "";
        for (int k = 0; k < indentLength; k++)
            buffer += indentChar;
        writeString(outStream, buffer);
    }
}

