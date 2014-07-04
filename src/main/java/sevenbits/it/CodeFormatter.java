package sevenbits.it;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Set;
import java.util.TreeSet;

/**
 * Formates at least compiled java code
 */

public class CodeFormatter {
    Logger logger = Logger.getLogger(CodeFormatter.class.getName());
    int indentLength;
    Character indentChar;

    /**
     * Writes string into stream
     * @param outStream - OutStream to which the String will be written
     * @param str - String that will be written into stream
     * @throws StreamException - throws when OutStream is not available or corrupted
     */

    private void writeString(OutStream outStream, String str) throws StreamException{
        try {
            for(int i = 0; i < str.length(); i++)
                outStream.writeSymbol(str.charAt(i));
        }
        catch(StreamException ex){
            throw ex;
        }
    }

    /**
     *
     * @param inStream - stream with unformatted java code
     * @param outStream - stream where formatted java code will be written
     * @param options - contains options for formatter
     * @throws FormatterException  - if there is delimiters mismatch
     * @throws StreamException - if there is problems with streams
     */

    void format(InStream inStream, OutStream outStream, FormatOptions options) throws FormatterException, StreamException {
        indentLength = options.getIndentSize();
        indentChar = options.getIndent();

        String fileString = new String("a");
        Character currentChar = 'a';
        Character previousChar;
        Set<Character> operations = new TreeSet<Character>();
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
        int forCount = 0;
        int mode = 0;  // 0- not code 1 = "//" - type comment 2 - "/**/" - type comment 3 = string 4- char 5- \
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
                case 0: {
                if (previousChar == '\n' && currentChar != ' ') {
                    int tabs = nestingLevel;
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
                            if(nestingLevel != 0)
                                nestingLevel--;
                            else{
                                String message = "Delimiters does not match";
                                NotEnoughBracketsException ex = new NotEnoughBracketsException();
                                if (logger.isEnabledFor(Level.ERROR))
                                    logger.fatal(ex.getMessage());
                                throw new NotEnoughBracketsException(message , ex);
                            }
                            fileString = fileString.substring(0, fileString.length() - 4);
                            buffer = "}\n";
                            if (nestingLevel == 0) {
                                buffer += '\n';
                            }
                            try{
                                fileString += buffer;
                                writeString(outStream, fileString.substring(1));
                                fileString = "";
                            }
                            catch(StreamException ex){
                                System.out.print("log");
                            }
                            fileString = "\n";
                            break;
                        }
                        case ' ': {
                            if (previousChar != ' ' && previousChar != '\n')
                                fileString += " ";
                            break;
                        }
                        case '{': {
                            buffer = "{\n";
                            nestingLevel++;
                            fileString += buffer;
                            break;
                        }
                        case '"':{
                                fileString += currentChar;
                                if(previousChar != '\\' && previousChar != '\'' )
                                    mode = 3;
                                break;
                            }
                        case '\'':{
                            fileString += currentChar;
                            if(previousChar != '\\')
                                mode = 4;
                            break;
                        }
                        case '\\': {
                            if (previousChar != '\\')
                                mode = 5;
                        }
                            mode = 4;
                            break;
                        case '/':
                        case '*':
                            if (fileString.charAt(fileString.length() - 2) == '/') {
                                if (currentChar == '*')
                                    mode = 2;
                                    fileString = fileString.substring(0, fileString.length() - 1);
                                if (currentChar == '/') {
                                    fileString = fileString.substring(0, fileString.length() - 3);
                                    fileString += "    /";
                                    mode = 1;
                                }
                                fileString += currentChar;
                                break;
                            }
                        case '=':
                        case '-':
                        case '&':
                        case '|':
                        case '+': {
                            if (operations.contains(fileString.charAt(fileString.length() - 2))) {
                                fileString = fileString.substring(0, fileString.length() - 1);
                            }
                            if (previousChar != ' ' && !operations.contains(previousChar))
                                buffer += " ";
                            buffer += currentChar;
                            buffer += " ";
                            fileString += buffer;
                            break;
                        }
                        case '(': {
                            if (previousChar != ' ')
                                buffer += " (";
                            else
                                buffer += "(";
                            fileString += buffer;
                            if (fileString.substring(fileString.length() - 6).equals(" for (")) {
                                forCount = 2;   // 2 ';' without \n after for
                            }
                            break;
                        }
                        case ')': {
                            buffer += ") ";
                            fileString += buffer;
                            break;
                        }
                        case ';': {
                            buffer = ";";
                            if (forCount == 0) {
                                buffer += "\n";
                            } else
                                forCount--;
                            fileString += buffer;
                            break;
                        }
                        default: {
                            if (currentChar != ' ' && currentChar != '\n') {
                                fileString += currentChar;
                            }
                            break;
                        }

                    }
                    break;
                }
                case 1:

                    if(currentChar == '\n') {
                        if (logger.isEnabledFor(Level.DEBUG))
                            logger.debug("// comment ends");
                        mode = 0;
                    }
                case 2:
                    if(currentChar == '/' && previousChar == '*'  && mode == 2) {
                        if (logger.isEnabledFor(Level.DEBUG))
                            logger.debug("/* comment ends");
                        mode = 0;
                    }
                case 3:
                    if(currentChar == '"' && mode == 3) {
                        if (logger.isEnabledFor(Level.DEBUG))
                            logger.debug("string ends");
                        mode = 0;
                    }
                case 4:
                    if(mode == 4 && (currentChar == '\'' && previousChar != '\\' && fileString.charAt(fileString.length() -2) != '\\')) {
                        if (logger.isEnabledFor(Level.DEBUG))
                            logger.debug("symbol ends");
                        mode = 0;
                    }
                case 5:
                    if(mode == 5) {
                        if (logger.isEnabledFor(Level.DEBUG))
                            logger.debug("symbol ends");
                        mode = 0;
                    }
                default:
                    if(currentChar != '\r' && currentChar != '\n')
                        fileString += currentChar;
                    if(currentChar == '\n' && (!(mode == 0 && (currentChar == '"' ||  currentChar == '\'')))){
                        fileString += '\n';
                    }
                    if(currentChar == '/' && mode == 0){
                        fileString += '\n';
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
        writeString(outStream, fileString.substring(1));
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
}