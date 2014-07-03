package sevenbits.it;

/**
 * Raises when there is some logical problems in formatter
 */

public class FormatterException extends Exception {
    FormatterException(){}
    FormatterException(String message, Throwable cause){
        super(message, cause);
        }
}
